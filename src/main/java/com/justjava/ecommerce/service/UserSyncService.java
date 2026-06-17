package com.justjava.ecommerce.service;

import com.justjava.ecommerce.model.User;
import com.justjava.ecommerce.repository.UserRepository;
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

        userRepository.findByKeycloakId(keycloakId).ifPresentOrElse(
            existing -> {
                existing.setEmail(email);
                existing.setFullName(fullName);
                userRepository.save(existing);
                log.debug("Updated user {} in local DB", email);
            },
            () -> {
                User user = User.builder()
                        .keycloakId(keycloakId)
                        .email(email)
                        .fullName(fullName)
                        .role(role)
                        .status("ACTIVE")
                        .build();
                userRepository.save(user);
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
}
