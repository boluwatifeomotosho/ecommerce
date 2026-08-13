package com.justjava.ecommerce.agent;

import com.justjava.ecommerce.auth.KeycloakAdminService;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

/**
 * Warehouse agents are global to the platform — not scoped to any vendor.
 * Both platform admins and subsidiary admins can invite them, and any
 * subsidiary can pick from the shared pool when dispatching orders.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {

    private final UserRepository       userRepository;
    private final KeycloakAdminService keycloakAdminService;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String keycloakClientId;

    @Value("${app.base-url}")
    private String appBaseUrl;

    @Value("${app.invite-link-lifespan-seconds:86400}")
    private int inviteLifespanSeconds;

    @Transactional(readOnly = true)
    public List<User> listAllAgents() {
        return userRepository.findAllByRole("AGENT");
    }

    @Transactional
    public InviteResult inviteAgent(String email, String firstName, String lastName, User inviter) {
        String cleanEmail = email == null ? "" : email.trim().toLowerCase();
        String cleanFirst = firstName == null ? "" : firstName.trim();
        String cleanLast  = lastName == null  ? "" : lastName.trim();

        if (cleanEmail.isBlank() || cleanFirst.isBlank() || cleanLast.isBlank()) {
            return InviteResult.failure("Email, first name, and last name are all required.");
        }
        if (userRepository.findByEmail(cleanEmail).isPresent()) {
            return InviteResult.failure("A user with that email already exists on this platform.");
        }
        if (keycloakAdminService.emailExists(cleanEmail)) {
            return InviteResult.failure("That email is already registered in the authentication system.");
        }

        String keycloakId;
        try {
            keycloakId = keycloakAdminService.createInvitedUser(cleanEmail, cleanFirst, cleanLast);
        } catch (Exception e) {
            log.error("Failed to create Keycloak user for agent invite {}", cleanEmail, e);
            return InviteResult.failure("Could not create the account in the authentication system.");
        }

        try {
            keycloakAdminService.assignAgentRole(keycloakId);

            String inviterLabel = inviter != null && inviter.getFullName() != null ? inviter.getFullName() : "";
            keycloakAdminService.updateUserAttributes(keycloakId, Map.of(
                    "invitedBy", inviterLabel,
                    "role", "AGENT"
            ));

            userRepository.save(User.builder()
                    .keycloakId(keycloakId)
                    .email(cleanEmail)
                    .fullName(cleanFirst + " " + cleanLast)
                    .role("AGENT")
                    .status("PENDING_ACTIVATION")
                    .build());

            keycloakAdminService.sendInvitationEmail(
                    keycloakId,
                    keycloakClientId,
                    appBaseUrl + "/oauth2/authorization/keycloak",
                    inviteLifespanSeconds
            );

            return InviteResult.success("Invitation sent to " + cleanEmail + ". They will receive an email to set their password.");
        } catch (Exception e) {
            log.error("Agent invite flow failed after Keycloak user creation for {}, rolling back Keycloak user", cleanEmail, e);
            keycloakAdminService.deleteUser(keycloakId);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return InviteResult.failure("Could not complete the invitation. Please try again or contact support.");
        }
    }

    public record InviteResult(boolean success, String message) {
        public static InviteResult success(String m) { return new InviteResult(true, m); }
        public static InviteResult failure(String m) { return new InviteResult(false, m); }
    }
}
