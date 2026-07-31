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

    private final UserRepository         userRepository;
    private final VendorRepository       vendorRepository;
    private final VendorMemberRepository vendorMemberRepository;

    @ModelAttribute
    public void addVendorContext(@AuthenticationPrincipal OidcUser principal, org.springframework.ui.Model model) {
        if (principal == null) return;
        User user = userRepository.findByKeycloakId(principal.getSubject()).orElse(null);
        if (user == null) return;

        String role = user.getRole();
        if (!"VENDOR".equalsIgnoreCase(role) && !"SUB_VENDOR".equalsIgnoreCase(role)) return;

        vendorMemberRepository.findByUserIdWithVendor(user.getId()).ifPresent(member -> {
            Vendor vendor = member.getVendor();
            model.addAttribute("vendorLogoUrl", vendor.getCompanyLogoUrl());
            model.addAttribute("vendorStoreName", vendor.getName());
            model.addAttribute("vendorMemberRole", member.getMemberRole().name());
            model.addAttribute("isVendorAdmin", member.getMemberRole() == VendorMemberRole.VENDOR_ADMIN);
        });
    }
}
