package com.justjava.ecommerce.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final UserRepository userRepository;

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
                if (isVendor) {
                    if (companyName    != null && !companyName.isBlank())    existing.setStoreName(companyName);
                    if (websiteUrl     != null && !websiteUrl.isBlank())     existing.setWebsiteUrl(websiteUrl);
                    if (companyLogoUrl != null && !companyLogoUrl.isBlank()) existing.setCompanyLogoUrl(companyLogoUrl);
                }
                userRepository.save(existing);
                log.debug("Updated user {} in local DB", email);
            },
            () -> {
                User.UserBuilder builder = User.builder()
                        .keycloakId(keycloakId)
                        .email(email)
                        .fullName(fullName)
                        .phone(phoneNumber)
                        .role(role)
                        .status("ACTIVE");
                if (isVendor) {
                    builder.storeName(companyName)
                           .websiteUrl(websiteUrl)
                           .companyLogoUrl(companyLogoUrl);
                }
                userRepository.save(builder.build());
                log.info("Created new local user: {} ({})", email, role);
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

    private String claim(OidcUser oidcUser, String name) {
        Object raw = oidcUser.getClaim(name);
        if (raw == null) return null;
        String value = raw.toString().trim();
        return value.isEmpty() ? null : value;
    }
}
