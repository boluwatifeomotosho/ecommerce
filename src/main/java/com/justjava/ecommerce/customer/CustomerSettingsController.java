package com.justjava.ecommerce.customer;

import com.justjava.ecommerce.address.AddressForm;
import com.justjava.ecommerce.address.CustomerAddressService;
import com.justjava.ecommerce.auth.KeycloakAdminService;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/customer/settings")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CustomerSettingsController {

    private final UserRepository         userRepository;
    private final KeycloakAdminService   keycloakAdminService;
    private final CustomerAddressService addressService;

    @GetMapping
    public String settingsPage(Authentication auth, Model model) {
        UUID customerId = resolveId(auth);
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        model.addAttribute("user",         user);
        model.addAttribute("addresses",    addressService.getAddresses(customerId));
        model.addAttribute("addressForm",  new AddressForm());
        model.addAttribute("name",         resolveName(auth));
        model.addAttribute("maxAddresses", CustomerAddressService.MAX_ADDRESSES);
        return "customer/settings";
    }

    // ── Profile ────────────────────────────────────────────────────────────────

    @PostMapping("/name")
    public String updateName(
            @RequestParam String fullName,
            Authentication auth,
            RedirectAttributes flash
    ) {
        if (fullName == null || fullName.isBlank()) {
            flash.addFlashAttribute("nameError", "Name cannot be blank.");
            return "redirect:/customer/settings";
        }
        String trimmed = fullName.trim();
        UUID customerId = resolveId(auth);
        try {
            User user = userRepository.findById(customerId)
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            user.setFullName(trimmed);
            userRepository.save(user);

            int spaceIdx  = trimmed.indexOf(' ');
            String first  = spaceIdx > 0 ? trimmed.substring(0, spaceIdx) : trimmed;
            String last   = spaceIdx > 0 ? trimmed.substring(spaceIdx + 1) : "";
            keycloakAdminService.updateUserName(resolveKeycloakId(auth), first, last);

            flash.addFlashAttribute("nameSuccess", "Name updated successfully.");
        } catch (Exception e) {
            flash.addFlashAttribute("nameError", "Could not update name. Please try again.");
        }
        return "redirect:/customer/settings";
    }

    // ── Addresses ──────────────────────────────────────────────────────────────

    @PostMapping("/addresses/add")
    public String addAddress(
            @Valid @ModelAttribute("addressForm") AddressForm form,
            BindingResult br,
            Authentication auth,
            RedirectAttributes flash
    ) {
        if (br.hasErrors()) {
            flash.addFlashAttribute("addressError", "Please fill in all address fields correctly.");
            return "redirect:/customer/settings";
        }
        try {
            addressService.addAddress(resolveId(auth), form, false);
            flash.addFlashAttribute("addressSuccess", "Address added.");
        } catch (Exception e) {
            flash.addFlashAttribute("addressError", e.getMessage());
        }
        return "redirect:/customer/settings";
    }

    @PostMapping("/addresses/{id}/update")
    public String updateAddress(
            @PathVariable UUID id,
            @Valid @ModelAttribute("addressForm") AddressForm form,
            BindingResult br,
            Authentication auth,
            RedirectAttributes flash
    ) {
        if (br.hasErrors()) {
            flash.addFlashAttribute("addressError", "Please fill in all fields correctly.");
            return "redirect:/customer/settings";
        }
        try {
            addressService.updateAddress(resolveId(auth), id, form);
            flash.addFlashAttribute("addressSuccess", "Address updated.");
        } catch (Exception e) {
            flash.addFlashAttribute("addressError", e.getMessage());
        }
        return "redirect:/customer/settings";
    }

    @PostMapping("/addresses/{id}/default")
    public String setDefault(@PathVariable UUID id, Authentication auth, RedirectAttributes flash) {
        try {
            addressService.setDefault(resolveId(auth), id);
            flash.addFlashAttribute("addressSuccess", "Default address updated.");
        } catch (Exception e) {
            flash.addFlashAttribute("addressError", e.getMessage());
        }
        return "redirect:/customer/settings";
    }

    @PostMapping("/addresses/{id}/delete")
    public String deleteAddress(@PathVariable UUID id, Authentication auth, RedirectAttributes flash) {
        try {
            addressService.delete(resolveId(auth), id);
            flash.addFlashAttribute("addressSuccess", "Address removed.");
        } catch (Exception e) {
            flash.addFlashAttribute("addressError", e.getMessage());
        }
        return "redirect:/customer/settings";
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private UUID resolveId(Authentication auth) {
        Object p = auth.getPrincipal();
        if (p instanceof OidcUser u) {
            return userRepository.findByKeycloakId(u.getSubject())
                    .orElseThrow(() -> new IllegalStateException("User not found")).getId();
        }
        if (p instanceof User u) return u.getId();
        throw new IllegalStateException("Unsupported principal");
    }

    private String resolveKeycloakId(Authentication auth) {
        Object p = auth.getPrincipal();
        if (p instanceof OidcUser u) return u.getSubject();
        if (p instanceof User u) return u.getKeycloakId();
        throw new IllegalStateException("Unsupported principal");
    }

    private String resolveName(Authentication auth) {
        Object p = auth.getPrincipal();
        if (p instanceof OidcUser u) return u.getFullName() != null ? u.getFullName() : u.getEmail();
        if (p instanceof User u)     return u.getFullName() != null ? u.getFullName() : "Customer";
        return "Customer";
    }
}
