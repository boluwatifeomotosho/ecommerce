package com.justjava.ecommerce.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class RegistrationController {

    public static final String REGISTRATION_MODE_KEY = "REGISTRATION_MODE";
    public static final String REGISTRATION_MODE_COOKIE = "nm_reg_mode";

    @GetMapping("/login")
    public String loginChoice() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerChoice() {
        return "auth/register";
    }

    @GetMapping("/register/customer")
    public void registerCustomer(HttpSession session, HttpServletResponse response) throws IOException {
        session.setAttribute(REGISTRATION_MODE_KEY, "CUSTOMER");
        response.addCookie(modeCookie("CUSTOMER"));
        response.sendRedirect("/oauth2/authorization/keycloak#account_type=customer");
    }

    @GetMapping("/register/vendor")
    public void registerVendor(HttpSession session, HttpServletResponse response) throws IOException {
        session.setAttribute(REGISTRATION_MODE_KEY, "VENDOR");
        response.addCookie(modeCookie("VENDOR"));
        response.sendRedirect("/oauth2/authorization/keycloak#account_type=vendor");
    }

    private Cookie modeCookie(String value) {
        Cookie cookie = new Cookie(REGISTRATION_MODE_COOKIE, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(15 * 60); // 15 minutes — enough for the Keycloak round-trip
        return cookie;
    }
}
