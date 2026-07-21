package com.justjava.ecommerce.vendor;

import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = {
        "com.justjava.ecommerce.vendor",
        "com.justjava.ecommerce.home",
        "com.justjava.ecommerce.product",
        "com.justjava.ecommerce.order"
})
@RequiredArgsConstructor
public class VendorModelAdvice {

    private final UserRepository userRepository;

    @ModelAttribute
    public void addVendorContext(@AuthenticationPrincipal OidcUser principal, org.springframework.ui.Model model) {
        if (principal == null) return;
        User user = userRepository.findByKeycloakId(principal.getSubject()).orElse(null);
        if (user == null) return;
        if (!"VENDOR".equalsIgnoreCase(user.getRole())) return;

        model.addAttribute("vendorLogoUrl", user.getCompanyLogoUrl());
        model.addAttribute("vendorStoreName", user.getStoreName());
    }
}
