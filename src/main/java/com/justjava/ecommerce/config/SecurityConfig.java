package com.justjava.ecommerce.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justjava.ecommerce.service.KeycloakAdminService;
import com.justjava.ecommerce.service.UserSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.*;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final UserSyncService userSyncService;
    private final KeycloakAdminService keycloakAdminService;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(tokenRepository)
                        .csrfTokenRequestHandler(requestHandler)
                )
                .anonymous(AnonymousConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(oidcUserService())
                        )
                        .successHandler(loginSuccessHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new MvcRequestMatcher(introspector, "/"),
                                new MvcRequestMatcher(introspector, "/products"),
                                new MvcRequestMatcher(introspector, "/products/**"),
                                new MvcRequestMatcher(introspector, "/register/phone"),
                                new MvcRequestMatcher(introspector, "/register/phone/verify"),
                                new MvcRequestMatcher(introspector, "/register/phone/resend"),
                                new MvcRequestMatcher(introspector, "/static/**"),
                                new MvcRequestMatcher(introspector, "/css/**"),
                                new MvcRequestMatcher(introspector, "/js/**"),
                                new MvcRequestMatcher(introspector, "/webjars/**")
                        ).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/admin/**"))
                                .hasRole("ADMIN")
                        .requestMatchers(new MvcRequestMatcher(introspector, "/vendor/**"))
                                .hasRole("VENDOR")
                        .requestMatchers(new MvcRequestMatcher(introspector, "/customer/**"))
                                .authenticated()
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler())
                        .logoutUrl("/logout")
                );

        return http.build();
    }

    /**
     * Custom OIDC user service that reads realm roles from the access token JWT.
     * Keycloak puts realm_access in the access token by default, not the ID token,
     * so the standard authorities mapper never finds them.
     */
    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        OidcUserService delegate = new OidcUserService();
        return userRequest -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);
            Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());

            // Extract realm roles from access token (primary source in Keycloak)
            extractRealmRoles(userRequest.getAccessToken().getTokenValue())
                    .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));

            // Fallback: try ID token claims (works if Keycloak mapper is configured for ID token)
            Map<String, Object> realmAccess = oidcUser.getClaimAsMap("realm_access");
            if (realmAccess != null) {
                List<?> roles = (List<?>) realmAccess.get("roles");
                if (roles != null) {
                    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                }
            }

            log.debug("Resolved authorities for {}: {}", oidcUser.getEmail(), authorities);
            return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
        };
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            String role        = "CUSTOMER";
            String redirectUrl = "/customer/dashboard";

            if (hasRole(authorities, "ROLE_ADMIN")) {
                role        = "ADMIN";
                redirectUrl = "/admin/dashboard";
            } else if (hasRole(authorities, "ROLE_VENDOR")) {
                role        = "VENDOR";
                redirectUrl = "/vendor/dashboard";
            } else if (hasRole(authorities, "ROLE_DELIVERY_AGENT")) {
                role = "DELIVERY_AGENT";
            } else if (hasRole(authorities, "ROLE_WAREHOUSE_OFFICER")) {
                role = "WAREHOUSE_OFFICER";
            } else if (!hasRole(authorities, "ROLE_CUSTOMER")) {
                // First-ever login: no app role in token yet — assign CUSTOMER in Keycloak
                keycloakAdminService.assignCustomerRole(oidcUser.getSubject());
            }

            userSyncService.syncUser(oidcUser, role);
            response.sendRedirect(redirectUrl);
        };
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Set<String> extractRealmRoles(String jwtTokenValue) {
        Set<String> roles = new HashSet<>();
        try {
            String[] parts = jwtTokenValue.split("\\.");
            if (parts.length < 2) return roles;

            // Base64url decode (add padding if needed)
            String encoded = parts[1];
            int pad = 4 - encoded.length() % 4;
            if (pad < 4) encoded = encoded + "=".repeat(pad);
            byte[] decoded = Base64.getUrlDecoder().decode(encoded);

            Map<?, ?> claims = objectMapper.readValue(decoded, Map.class);
            Object ra = claims.get("realm_access");
            if (ra instanceof Map) {
                Object r = ((Map<?, ?>) ra).get("roles");
                if (r instanceof List) {
                    ((List<?>) r).forEach(role -> roles.add(role.toString()));
                }
            }
        } catch (Exception e) {
            log.warn("Could not extract realm roles from access token: {}", e.getMessage());
        }
        return roles;
    }

    private boolean hasRole(Collection<? extends GrantedAuthority> authorities, String role) {
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(role));
    }

    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler handler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        handler.setPostLogoutRedirectUri("{baseUrl}");
        return handler;
    }
}
