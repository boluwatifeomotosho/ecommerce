package com.justjava.ecommerce.auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegistrationController {

    public static final String REGISTRATION_MODE_KEY = "REGISTRATION_MODE";

    @GetMapping("/login")
    public String loginChoice() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerChoice() {
        return "auth/register";
    }

    @GetMapping("/register/customer")
    public String registerCustomer(HttpSession session) {
        session.setAttribute(REGISTRATION_MODE_KEY, "CUSTOMER");
        return "redirect:/oauth2/authorization/keycloak";
    }

    @GetMapping("/register/vendor")
    public String registerVendor(HttpSession session) {
        session.setAttribute(REGISTRATION_MODE_KEY, "VENDOR");
        return "redirect:/oauth2/authorization/keycloak";
    }
}
