package com.justjava.ecommerce.review;

import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/customer/reviews")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService  reviewService;
    private final UserRepository userRepository;

    @PostMapping
    public String submitReview(
            @RequestParam UUID orderId,
            @RequestParam UUID productId,
            @RequestParam int rating,
            @RequestParam(required = false) String comment,
            Authentication auth,
            RedirectAttributes flash
    ) {
        UUID customerId = resolveId(auth);
        try {
            reviewService.submitReview(customerId, orderId, productId, rating, comment);
            flash.addFlashAttribute("successMessage", "Your review has been submitted!");
        } catch (Exception e) {
            log.warn("Review submission failed: {}", e.getMessage());
            flash.addFlashAttribute("paymentError", e.getMessage());
        }
        return "redirect:/customer/orders/" + orderId;
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
}
