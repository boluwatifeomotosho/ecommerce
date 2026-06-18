package com.justjava.ecommerce.cart;

import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.UUID;

@ControllerAdvice
@RequiredArgsConstructor
public class CartModelAdvice {

    private final CartService    cartService;
    private final UserRepository userRepository;

    @ModelAttribute("cartItemCount")
    public int cartItemCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return 0;
        boolean isCustomer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
        if (!isCustomer) return 0;
        try {
            UUID customerId = resolveCustomerId(authentication);
            return customerId != null ? cartService.getCartItemCount(customerId) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private UUID resolveCustomerId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof OidcUser oidcUser) {
            return userRepository.findByKeycloakId(oidcUser.getSubject())
                    .map(User::getId).orElse(null);
        }
        if (principal instanceof User user) {
            return user.getId();
        }
        return null;
    }
}
