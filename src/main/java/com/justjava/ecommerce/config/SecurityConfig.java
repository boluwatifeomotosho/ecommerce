package com.justjava.ecommerce.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justjava.ecommerce.auth.RegistrationController;
import com.justjava.ecommerce.auth.KeycloakAdminService;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import com.justjava.ecommerce.user.UserSyncService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

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
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.base-url}")
    private String appBaseUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(tokenRepository)
                        .csrfTokenRequestHandler(requestHandler)
                )
                .anonymous(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .authorizationEndpoint(auth -> auth
                                .authorizationRequestResolver(registrationAwareResolver())
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(oidcUserService())
                        )
                        .successHandler(loginSuccessHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/products", "/products/**",
                                "/login", "/register",
                                "/register/phone", "/register/phone/verify", "/register/phone/resend", "/register/phone/success",
                                "/register/vendor", "/register/customer",
                                "/login/phone", "/login/phone/verify", "/login/phone/resend",
                                "/mobile/login",
                                "/uploads/**",
                                "/static/**", "/css/**", "/js/**", "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/vendor/**").hasRole("VENDOR")
                        .requestMatchers("/customer/**").hasRole("CUSTOMER")
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler())
                        .logoutUrl("/logout")
                )
                .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class);

        return http.build();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        OidcUserService delegate = new OidcUserService();
        return userRequest -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);
            Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());

            extractRealmRoles(userRequest.getAccessToken().getTokenValue())
                    .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));

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

            HttpSession session = request.getSession(false);
            String registrationMode = session != null
                    ? (String) session.getAttribute(RegistrationController.REGISTRATION_MODE_KEY)
                    : null;
            boolean isMobile = session != null && Boolean.TRUE.equals(session.getAttribute("mobile"));
            if (session != null) {
                session.removeAttribute(RegistrationController.REGISTRATION_MODE_KEY);
                session.removeAttribute("mobile");
            }

            // Cookie fallback: sessions can be dropped across the Keycloak round-trip
            // (new session on callback, cookie policy quirks). This is set by
            // RegistrationController and cleared once consumed.
            String cookieMode = readRegistrationModeCookie(request);
            clearRegistrationModeCookie(response);

            // Also honor the accountType attribute Keycloak captures on registration
            // (exposed as an OIDC claim via a client mapper).
            String accountTypeClaim = asString(oidcUser.getClaim("accountType"));
            boolean claimSaysVendor   = "vendor".equalsIgnoreCase(accountTypeClaim);
            boolean sessionSaysVendor = "VENDOR".equals(registrationMode);
            boolean cookieSaysVendor  = "VENDOR".equals(cookieMode);

            if (hasRole(authorities, "ROLE_ADMIN")) {
                role        = "ADMIN";
                redirectUrl = "/admin/dashboard";
            } else if (hasRole(authorities, "ROLE_VENDOR")) {
                role        = "VENDOR";
                redirectUrl = "/vendor/dashboard";
            } else if (sessionSaysVendor || claimSaysVendor || cookieSaysVendor) {
                keycloakAdminService.assignVendorRole(oidcUser.getSubject());
                role        = "VENDOR";
                redirectUrl = "/vendor/dashboard";

                Set<GrantedAuthority> updated = new HashSet<>(authorities);
                updated.add(new SimpleGrantedAuthority("ROLE_VENDOR"));
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                SecurityContextHolder.getContext().setAuthentication(
                        new OAuth2AuthenticationToken(
                                oauthToken.getPrincipal(), updated,
                                oauthToken.getAuthorizedClientRegistrationId()));
            } else if (hasRole(authorities, "ROLE_DELIVERY_AGENT")) {
                role = "DELIVERY_AGENT";
            } else if (hasRole(authorities, "ROLE_WAREHOUSE_OFFICER")) {
                role = "WAREHOUSE_OFFICER";
            } else if (!hasRole(authorities, "ROLE_CUSTOMER")) {
                keycloakAdminService.assignCustomerRole(oidcUser.getSubject());

                Set<GrantedAuthority> updated = new HashSet<>(authorities);
                updated.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                SecurityContextHolder.getContext().setAuthentication(
                        new OAuth2AuthenticationToken(
                                oauthToken.getPrincipal(), updated,
                                oauthToken.getAuthorizedClientRegistrationId()));
            }

            userSyncService.syncUser(oidcUser, role);

            if ("VENDOR".equals(role) && needsVendorOnboarding(oidcUser.getSubject())) {
                redirectUrl = "/vendor/onboarding";
            } else if (isMobile && "/customer/dashboard".equals(redirectUrl)) {
                redirectUrl = "/mobile";
            }

            response.sendRedirect(redirectUrl);
        };
    }

    private boolean needsVendorOnboarding(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .map(u -> {
                    String logo = u.getCompanyLogoUrl();
                    return logo == null || logo.isBlank();
                })
                .orElse(true);
    }

    // ── OAuth2 Authorization Request Resolver ────────────────────────────────

    private OAuth2AuthorizationRequestResolver registrationAwareResolver() {
        DefaultOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository, "/oauth2/authorization");

        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                return addRegistrationHint(defaultResolver.resolve(request), request);
            }

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                return addRegistrationHint(defaultResolver.resolve(request, clientRegistrationId), request);
            }
        };
    }

    private OAuth2AuthorizationRequest addRegistrationHint(
            OAuth2AuthorizationRequest authRequest, HttpServletRequest httpRequest) {
        if (authRequest == null) return null;

        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute(RegistrationController.REGISTRATION_MODE_KEY) == null) {
            return authRequest;
        }

        String mode = (String) session.getAttribute(RegistrationController.REGISTRATION_MODE_KEY);

        String authUri = authRequest.getAuthorizationUri()
                .replace("/protocol/openid-connect/auth",
                         "/protocol/openid-connect/registrations");

        Map<String, Object> additional = new HashMap<>(authRequest.getAdditionalParameters());
        additional.put("account_type", "VENDOR".equals(mode) ? "vendor" : "customer");

        return OAuth2AuthorizationRequest.from(authRequest)
                .authorizationUri(authUri)
                .additionalParameters(additional)
                .build();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Set<String> extractRealmRoles(String jwtTokenValue) {
        Set<String> roles = new HashSet<>();
        try {
            String[] parts = jwtTokenValue.split("\\.");
            if (parts.length < 2) return roles;

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

    private String readRegistrationModeCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (RegistrationController.REGISTRATION_MODE_COOKIE.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    private void clearRegistrationModeCookie(HttpServletResponse response) {
        Cookie clear = new Cookie(RegistrationController.REGISTRATION_MODE_COOKIE, "");
        clear.setPath("/");
        clear.setHttpOnly(true);
        clear.setMaxAge(0);
        response.addCookie(clear);
    }

    private String asString(Object value) {
        if (value == null) return null;
        if (value instanceof String s) return s;
        if (value instanceof Collection<?> c && !c.isEmpty()) {
            Object first = c.iterator().next();
            return first == null ? null : first.toString();
        }
        return value.toString();
    }

    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler desktopHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        desktopHandler.setPostLogoutRedirectUri(appBaseUrl);

        OidcClientInitiatedLogoutSuccessHandler mobileHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        mobileHandler.setPostLogoutRedirectUri(appBaseUrl + "/mobile/login");

        return (request, response, authentication) -> {
            if ("true".equals(request.getParameter("mobile"))) {
                mobileHandler.onLogoutSuccess(request, response, authentication);
            } else {
                desktopHandler.onLogoutSuccess(request, response, authentication);
            }
        };
    }
}
