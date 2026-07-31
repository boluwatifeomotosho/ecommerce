package com.justjava.ecommerce.vendor;

import com.justjava.ecommerce.auth.KeycloakAdminService;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Controller
@PreAuthorize("hasRole('VENDOR')")
public class VendorOnboardingController {

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_LOGO_SIZE = 2 * 1024 * 1024;

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(\\+234\\d{10}|0\\d{10})$");
    private static final Pattern WEBSITE_PATTERN =
            Pattern.compile("^https?://[^\\s.]+\\.[^\\s]{2,}$");

    private final UserRepository       userRepository;
    private final VendorRepository     vendorRepository;
    private final KeycloakAdminService keycloakAdminService;
    private final Path uploadDir;

    public VendorOnboardingController(
            UserRepository userRepository,
            VendorRepository vendorRepository,
            KeycloakAdminService keycloakAdminService,
            @Value("${app.vendor-upload-dir:./uploads/vendors}") String uploadPath
    ) {
        this.userRepository = userRepository;
        this.vendorRepository = vendorRepository;
        this.keycloakAdminService = keycloakAdminService;
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create vendor upload directory: " + uploadPath, e);
        }
    }

    @GetMapping("/vendor/onboarding")
    @Transactional
    public String show(@AuthenticationPrincipal OidcUser principal, Model model) {
        User owner = requireVendorUser(principal);
        Vendor vendor = requireVendorCompany(owner);
        hydrateFromKeycloakIfMissing(owner, vendor, principal.getSubject());
        model.addAttribute("vendor", vendor);
        model.addAttribute("owner", owner);
        model.addAttribute("name", principal.getFullName());
        model.addAttribute("email", principal.getEmail());
        return "vendor/onboarding";
    }

    private void hydrateFromKeycloakIfMissing(User owner, Vendor vendor, String keycloakId) {
        boolean missingStore   = isBlank(vendor.getName());
        boolean missingPhone   = isBlank(owner.getPhone());
        boolean missingWebsite = isBlank(vendor.getWebsiteUrl());
        if (!missingStore && !missingPhone && !missingWebsite) return;

        Map<String, String> kc = keycloakAdminService.getUserAttributes(keycloakId);
        if (kc.isEmpty()) return;

        boolean vendorTouched = false;
        boolean userTouched   = false;
        if (missingStore) {
            String v = kc.get("companyName");
            if (!isBlank(v)) { vendor.setName(v); vendorTouched = true; }
        }
        if (missingPhone) {
            String v = kc.get("phoneNumber");
            if (!isBlank(v)) { owner.setPhone(v); userTouched = true; }
        }
        if (missingWebsite) {
            String v = kc.get("websiteUrl");
            if (!isBlank(v)) { vendor.setWebsiteUrl(v); vendorTouched = true; }
        }
        if (vendorTouched) vendorRepository.save(vendor);
        if (userTouched)   userRepository.save(owner);
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }

    @PostMapping("/vendor/onboarding")
    @Transactional
    public String submit(
            @AuthenticationPrincipal OidcUser principal,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "websiteUrl",  required = false) String websiteUrl,
            @RequestParam(value = "companyLogo", required = false) MultipartFile logo,
            RedirectAttributes redirect
    ) {
        User owner = requireVendorUser(principal);
        Vendor vendor = requireVendorCompany(owner);

        String company = trimToNull(companyName);
        String phone   = trimToNull(phoneNumber);
        String website = trimToNull(websiteUrl);

        if (company == null && (vendor.getName() == null || vendor.getName().isBlank())) {
            return fail(redirect, "Company name is required.");
        }
        if (phone != null && !PHONE_PATTERN.matcher(phone).matches()) {
            return fail(redirect, "Enter a valid Nigerian phone number.");
        }
        if (website != null && !WEBSITE_PATTERN.matcher(website).matches()) {
            return fail(redirect, "Enter a valid website URL.");
        }

        String logoUrl = vendor.getCompanyLogoUrl();
        if (logo != null && !logo.isEmpty()) {
            if (logo.getSize() > MAX_LOGO_SIZE) return fail(redirect, "Logo must be 2 MB or smaller.");
            String contentType = logo.getContentType();
            if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
                return fail(redirect, "Logo must be a PNG, JPEG, or WebP image.");
            }
            try {
                String ext = extension(logo.getOriginalFilename());
                String filename = UUID.randomUUID() + ext;
                Path target = uploadDir.resolve(filename).normalize();
                if (!target.startsWith(uploadDir)) return fail(redirect, "Invalid filename.");
                logo.transferTo(target);
                logoUrl = "/uploads/vendors/" + filename;
            } catch (IOException e) {
                log.error("Failed to save vendor logo", e);
                return fail(redirect, "Could not save logo. Try again.");
            }
        }

        if (logoUrl == null || logoUrl.isBlank()) {
            return fail(redirect, "Please upload a company logo.");
        }

        if (company != null) vendor.setName(company);
        if (website != null) vendor.setWebsiteUrl(website);
        vendor.setCompanyLogoUrl(logoUrl);
        vendorRepository.save(vendor);

        if (phone != null) {
            owner.setPhone(phone);
            userRepository.save(owner);
        }

        Map<String, String> kcAttrs = new HashMap<>();
        if (company != null) kcAttrs.put("companyName", company);
        if (phone   != null) kcAttrs.put("phoneNumber", phone);
        if (website != null) kcAttrs.put("websiteUrl",  website);
        kcAttrs.put("companyLogoUrl", logoUrl);
        try {
            keycloakAdminService.updateUserAttributes(principal.getSubject(), kcAttrs);
        } catch (Exception e) {
            log.warn("Vendor onboarding: local save succeeded but Keycloak sync failed for {}", principal.getEmail());
        }

        return "redirect:/vendor/dashboard";
    }

    private User requireVendorUser(OidcUser principal) {
        User user = userRepository.findByKeycloakId(principal.getSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor account not found"));
        if (!"VENDOR".equalsIgnoreCase(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vendors can access onboarding");
        }
        return user;
    }

    private Vendor requireVendorCompany(User owner) {
        return vendorRepository.findByOwnerId(owner.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor company not found"));
    }

    private String fail(RedirectAttributes redirect, String message) {
        redirect.addFlashAttribute("error", message);
        return "redirect:/vendor/onboarding";
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String extension(String filename) {
        if (filename == null) return ".png";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot).toLowerCase() : ".png";
    }
}
