package com.justjava.ecommerce.vendor;

import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vendor/settings")
@PreAuthorize("hasRole('VENDOR')")
@RequiredArgsConstructor
public class VendorStoreSettingsController {

    private final UserRepository userRepository;

    @GetMapping
    public String settings(@AuthenticationPrincipal OidcUser principal, Model model) {
        User vendor = resolveVendor(principal);
        model.addAttribute("vendor", vendor);
        model.addAttribute("name", principal.getFullName() != null ? principal.getFullName() : "Vendor");
        return "vendor/settings";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @AuthenticationPrincipal OidcUser principal,
            @RequestParam String storeName,
            @RequestParam(required = false) String storeBio,
            @RequestParam(required = false) String phone,
            RedirectAttributes flash
    ) {
        User vendor = resolveVendor(principal);
        vendor.setStoreName(storeName.isBlank() ? null : storeName.trim());
        vendor.setStoreBio(storeBio == null || storeBio.isBlank() ? null : storeBio.trim());
        vendor.setPhone(phone == null || phone.isBlank() ? null : phone.trim());
        userRepository.save(vendor);
        flash.addFlashAttribute("profileSuccess", "Store profile updated successfully.");
        return "redirect:/vendor/settings";
    }

    @PostMapping("/bank")
    public String updateBank(
            @AuthenticationPrincipal OidcUser principal,
            @RequestParam String bankName,
            @RequestParam String bankAccountNumber,
            @RequestParam String bankAccountName,
            RedirectAttributes flash
    ) {
        User vendor = resolveVendor(principal);
        vendor.setBankName(bankName.trim());
        vendor.setBankAccountNumber(bankAccountNumber.trim());
        vendor.setBankAccountName(bankAccountName.trim());
        userRepository.save(vendor);
        flash.addFlashAttribute("bankSuccess", "Bank details updated successfully.");
        return "redirect:/vendor/settings";
    }

    private User resolveVendor(OidcUser principal) {
        return userRepository.findByKeycloakId(principal.getSubject())
                .orElseThrow(() -> new IllegalStateException("Vendor not found in local DB"));
    }
}
