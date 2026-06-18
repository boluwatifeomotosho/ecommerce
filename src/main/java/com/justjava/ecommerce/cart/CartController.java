package com.justjava.ecommerce.cart;

import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/customer/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService    cartService;
    private final UserRepository userRepository;

    @GetMapping
    public String viewCart(Authentication auth, Model model) {
        UUID customerId = resolveCustomerId(auth);
        model.addAttribute("cart", cartService.getCart(customerId));
        model.addAttribute("name", resolveName(auth));
        return "customer/cart";
    }

    @PostMapping("/items")
    public String addItem(
            @RequestParam UUID productId,
            @RequestParam(defaultValue = "1") int quantity,
            @RequestParam(defaultValue = "/customer/cart") String returnUrl,
            Authentication auth,
            RedirectAttributes flash
    ) {
        try {
            UUID customerId = resolveCustomerId(auth);
            cartService.addItem(customerId, productId, quantity);
            flash.addFlashAttribute("cartSuccess", "Item added to your cart.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("cartError", e.getMessage());
        }
        return "redirect:" + returnUrl;
    }

    @PostMapping("/items/{productId}/quantity")
    public String updateQuantity(
            @PathVariable UUID productId,
            @RequestParam int quantity,
            Authentication auth,
            RedirectAttributes flash
    ) {
        try {
            UUID customerId = resolveCustomerId(auth);
            cartService.updateQuantity(customerId, productId, quantity);
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("cartError", e.getMessage());
        }
        return "redirect:/customer/cart";
    }

    @PostMapping("/items/{productId}/remove")
    public String removeItem(
            @PathVariable UUID productId,
            Authentication auth
    ) {
        UUID customerId = resolveCustomerId(auth);
        cartService.removeItem(customerId, productId);
        return "redirect:/customer/cart";
    }

    @PostMapping("/clear")
    public String clearCart(Authentication auth) {
        UUID customerId = resolveCustomerId(auth);
        cartService.clearCart(customerId);
        return "redirect:/customer/cart";
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String resolveName(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof OidcUser u) {
            return u.getFullName() != null ? u.getFullName() : u.getEmail();
        }
        if (principal instanceof User u) {
            return u.getFullName() != null ? u.getFullName() : "User";
        }
        return "User";
    }

    private UUID resolveCustomerId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof OidcUser oidcUser) {
            return userRepository.findByKeycloakId(oidcUser.getSubject())
                    .orElseThrow(() -> new IllegalStateException("User not found in local DB"))
                    .getId();
        }
        if (principal instanceof User user) {
            return user.getId();
        }
        throw new IllegalStateException("Unsupported principal type: " + principal.getClass());
    }
}
