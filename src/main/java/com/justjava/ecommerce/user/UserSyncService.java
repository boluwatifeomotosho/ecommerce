package com.justjava.ecommerce.user;

import com.justjava.ecommerce.vendor.Vendor;
import com.justjava.ecommerce.vendor.VendorMember;
import com.justjava.ecommerce.vendor.VendorMemberRepository;
import com.justjava.ecommerce.vendor.VendorMemberRole;
import com.justjava.ecommerce.vendor.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final UserRepository         userRepository;
    private final VendorRepository       vendorRepository;
    private final VendorMemberRepository vendorMemberRepository;

    @Transactional
    public void syncUser(OidcUser oidcUser, String role) {
        String keycloakId = oidcUser.getSubject();
        String email      = oidcUser.getEmail();
        String fullName   = oidcUser.getFullName();

        boolean isVendor = "VENDOR".equalsIgnoreCase(role);

        String companyName    = claim(oidcUser, "companyName");
        String phoneNumber    = claim(oidcUser, "phoneNumber");
        String websiteUrl     = claim(oidcUser, "websiteUrl");
        String companyLogoUrl = claim(oidcUser, "companyLogoUrl");

        userRepository.findByKeycloakId(keycloakId).ifPresentOrElse(
            existing -> {
                existing.setEmail(email);
                existing.setFullName(fullName);
                if (phoneNumber != null && !phoneNumber.isBlank()) existing.setPhone(phoneNumber);
                if ("PENDING_ACTIVATION".equals(existing.getStatus())) {
                    existing.setStatus("ACTIVE");
                }
                userRepository.save(existing);
                if (isVendor) {
                    updateVendorCompany(existing, companyName, websiteUrl, companyLogoUrl);
                }
                log.debug("Updated user {} in local DB", email);
            },
            () -> {
                User saved = userRepository.save(User.builder()
                        .keycloakId(keycloakId)
                        .email(email)
                        .fullName(fullName)
                        .phone(phoneNumber)
                        .role(role)
                        .status("ACTIVE")
                        .build());
                log.info("Created new local user: {} ({})", email, role);

                if (isVendor) {
                    createVendorCompany(saved, companyName, websiteUrl, companyLogoUrl);
                }
            }
        );
    }

    @Transactional
    public void syncPhoneUser(String keycloakId, String phone, String firstName, String lastName) {
        userRepository.findByKeycloakId(keycloakId).ifPresentOrElse(
            existing -> log.debug("Phone user {} already in local DB", phone),
            () -> {
                User user = User.builder()
                        .keycloakId(keycloakId)
                        .phone(phone)
                        .fullName(firstName + " " + lastName)
                        .role("CUSTOMER")
                        .status("ACTIVE")
                        .build();
                userRepository.save(user);
                log.info("Created phone-registered user: {} (CUSTOMER)", phone);
            }
        );
    }

    private void createVendorCompany(User owner, String companyName, String websiteUrl, String companyLogoUrl) {
        String name = (companyName != null && !companyName.isBlank())
                ? companyName
                : (owner.getFullName() != null ? owner.getFullName() : "Vendor");
        String slug = slugify(name) + "-" + owner.getId().toString().substring(0, 8);

        Vendor vendor = vendorRepository.save(Vendor.builder()
                .name(name)
                .slug(slug)
                .owner(owner)
                .websiteUrl(websiteUrl)
                .companyLogoUrl(companyLogoUrl)
                .build());

        vendorMemberRepository.save(VendorMember.builder()
                .vendor(vendor)
                .user(owner)
                .memberRole(VendorMemberRole.VENDOR_ADMIN)
                .build());

        log.info("Created vendor company '{}' (slug={}) for user {}", name, slug, owner.getEmail());
    }

    private void updateVendorCompany(User owner, String companyName, String websiteUrl, String companyLogoUrl) {
        vendorRepository.findByOwnerId(owner.getId()).ifPresent(vendor -> {
            boolean changed = false;
            if (companyName != null && !companyName.isBlank() && !companyName.equals(vendor.getName())) {
                vendor.setName(companyName);
                changed = true;
            }
            if (websiteUrl != null && !websiteUrl.isBlank() && !websiteUrl.equals(vendor.getWebsiteUrl())) {
                vendor.setWebsiteUrl(websiteUrl);
                changed = true;
            }
            if (companyLogoUrl != null && !companyLogoUrl.isBlank() && !companyLogoUrl.equals(vendor.getCompanyLogoUrl())) {
                vendor.setCompanyLogoUrl(companyLogoUrl);
                changed = true;
            }
            if (changed) vendorRepository.save(vendor);
        });
    }

    private String slugify(String value) {
        String s = value.toLowerCase().replaceAll("[^a-z0-9]+", "-");
        return s.replaceAll("^-|-$", "");
    }

    private String claim(OidcUser oidcUser, String name) {
        Object raw = oidcUser.getClaim(name);
        if (raw == null) return null;
        String value = raw.toString().trim();
        return value.isEmpty() ? null : value;
    }
}
