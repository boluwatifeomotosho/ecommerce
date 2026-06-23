package com.justjava.ecommerce.wishlist;

import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/customer/wishlist")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserRepository  userRepository;

    @GetMapping
    public String wishlistPage(Authentication auth, Model model) {
        UUID customerId = resolveId(auth);
        model.addAttribute("wishlistItems", wishlistService.getWishlist(customerId));
        model.addAttribute("name", resolveName(auth));
        return "customer/wishlist";
    }

    @PostMapping("/toggle")
    public String toggle(
            @RequestParam UUID productId,
            @RequestParam(defaultValue = "/customer/wishlist") String returnUrl,
            Authentication auth,
            RedirectAttributes flash
    ) {
        UUID customerId = resolveId(auth);
        boolean added = wishlistService.toggle(customerId, productId);
        flash.addFlashAttribute("wishlistMessage", added ? "Added to wishlist!" : "Removed from wishlist.");
        return "redirect:" + returnUrl;
    }

    private UUID resolveId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof OidcUser u) {
            return userRepository.findByKeycloakId(u.getSubject())
                    .orElseThrow(() -> new IllegalStateException("User not found"))
                    .getId();
        }
        if (principal instanceof User u) return u.getId();
        throw new IllegalStateException("Unsupported principal");
    }

    private String resolveName(Authentication auth) {
        Object p = auth.getPrincipal();
        if (p instanceof OidcUser u) return u.getFullName() != null ? u.getFullName() : u.getEmail();
        if (p instanceof User u)     return u.getFullName() != null ? u.getFullName() : "Customer";
        return "Customer";
    }
}
