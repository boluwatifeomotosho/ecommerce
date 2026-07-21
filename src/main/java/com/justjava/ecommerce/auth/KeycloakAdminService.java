package com.justjava.ecommerce.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KeycloakAdminService {

    private final RestClient restClient;
    private final String realm;
    private final String adminUsername;
    private final String adminPassword;

    public KeycloakAdminService(
            RestClient.Builder restClientBuilder,
            @Value("${keycloak.admin.server-url}") String serverUrl,
            @Value("${keycloak.admin.realm}") String realm,
            @Value("${keycloak.admin.admin-username}") String adminUsername,
            @Value("${keycloak.admin.admin-password}") String adminPassword
    ) {
        this.realm = realm;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.restClient = restClientBuilder.baseUrl(serverUrl).build();
    }

    public void assignCustomerRole(String keycloakUserId) {
        assignRole(keycloakUserId, "CUSTOMER");
    }

    public void assignVendorRole(String keycloakUserId) {
        assignRole(keycloakUserId, "VENDOR");
    }

    private void assignRole(String keycloakUserId, String roleName) {
        try {
            String token = getAdminToken();
            Map<String, Object> role = getRealmRole(token, roleName);
            assignRealmRoleToUser(token, keycloakUserId, role);
            log.info("Assigned {} role to user {}", roleName, keycloakUserId);
        } catch (Exception e) {
            log.warn("Could not assign {} role to user {}: {}", roleName, keycloakUserId, e.getMessage());
        }
    }

    private String getAdminToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", "admin-cli");
        form.add("username", adminUsername);
        form.add("password", adminPassword);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
                .uri("/realms/master/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);

        if (response == null || !response.containsKey("access_token")) {
            throw new IllegalStateException("No access_token in admin token response");
        }
        return (String) response.get("access_token");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getRealmRole(String token, String roleName) {
        Map<String, Object> role = restClient.get()
                .uri("/admin/realms/{realm}/roles/{roleName}", realm, roleName)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(Map.class);

        if (role == null) {
            throw new IllegalStateException("Role '" + roleName + "' not found in realm '" + realm + "'");
        }
        return role;
    }

    public String createUser(String username, String firstName, String lastName, String password, boolean isEmail) {
        String token = getAdminToken();

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("enabled", true);
        user.put("credentials", List.of(Map.of("type", "password", "value", password, "temporary", false)));
        if (isEmail) {
            user.put("email", username);
            user.put("emailVerified", true);
        }

        ResponseEntity<Void> response = restClient.post()
                .uri("/admin/realms/{realm}/users", realm)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .retrieve()
                .toBodilessEntity();

        String location = response.getHeaders().getFirst("Location");
        if (location == null) throw new IllegalStateException("Keycloak did not return a Location header after user creation");
        String userId = location.substring(location.lastIndexOf('/') + 1);

        assignCustomerRole(userId);
        log.info("Created Keycloak user {} (id={})", username, userId);
        return userId;
    }

    @SuppressWarnings("unchecked")
    public void updateUserAttributes(String keycloakId, Map<String, String> attributes) {
        if (attributes == null || attributes.isEmpty()) return;
        try {
            String token = getAdminToken();

            Map<String, Object> current = restClient.get()
                    .uri("/admin/realms/{realm}/users/{userId}", realm, keycloakId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(Map.class);
            if (current == null) throw new IllegalStateException("Keycloak user not found: " + keycloakId);

            Map<String, List<String>> merged = new HashMap<>();
            Object existing = current.get("attributes");
            if (existing instanceof Map) {
                ((Map<String, Object>) existing).forEach((k, v) -> {
                    if (v instanceof List) merged.put(k, (List<String>) v);
                });
            }
            attributes.forEach((k, v) -> merged.put(k, v == null ? List.of() : List.of(v)));

            Map<String, Object> body = new HashMap<>();
            body.put("attributes", merged);

            restClient.put()
                    .uri("/admin/realms/{realm}/users/{userId}", realm, keycloakId)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Updated Keycloak attributes for user {}: {}", keycloakId, attributes.keySet());
        } catch (Exception e) {
            log.warn("Could not update Keycloak attributes for user {}: {}", keycloakId, e.getMessage());
            throw new RuntimeException("Failed to update profile in authentication system: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getUserAttributes(String keycloakId) {
        Map<String, String> flat = new HashMap<>();
        try {
            String token = getAdminToken();
            Map<String, Object> current = restClient.get()
                    .uri("/admin/realms/{realm}/users/{userId}", realm, keycloakId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(Map.class);
            if (current == null) return flat;

            Object attrs = current.get("attributes");
            if (attrs instanceof Map<?, ?> map) {
                map.forEach((k, v) -> {
                    if (v instanceof List<?> list && !list.isEmpty() && list.get(0) != null) {
                        flat.put(k.toString(), list.get(0).toString());
                    } else if (v != null) {
                        flat.put(k.toString(), v.toString());
                    }
                });
            }

            // Also surface top-level fields we care about
            if (current.get("firstName") instanceof String fn && !fn.isBlank()) flat.putIfAbsent("firstName", fn);
            if (current.get("lastName")  instanceof String ln && !ln.isBlank()) flat.putIfAbsent("lastName",  ln);
            if (current.get("email")     instanceof String em && !em.isBlank()) flat.putIfAbsent("email",     em);
        } catch (Exception e) {
            log.warn("Could not fetch Keycloak attributes for user {}: {}", keycloakId, e.getMessage());
        }
        return flat;
    }

    public void updateUserName(String keycloakId, String firstName, String lastName) {
        try {
            String token = getAdminToken();
            Map<String, Object> body = new HashMap<>();
            body.put("firstName", firstName);
            body.put("lastName", lastName);
            restClient.put()
                    .uri("/admin/realms/{realm}/users/{userId}", realm, keycloakId)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Updated Keycloak name for user {}", keycloakId);
        } catch (Exception e) {
            log.warn("Could not update Keycloak name for user {}: {}", keycloakId, e.getMessage());
            throw new RuntimeException("Failed to update name in authentication system: " + e.getMessage(), e);
        }
    }

    private void assignRealmRoleToUser(String token, String userId, Map<String, Object> role) {
        restClient.post()
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", realm, userId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(List.of(role))
                .retrieve()
                .toBodilessEntity();
    }
}
