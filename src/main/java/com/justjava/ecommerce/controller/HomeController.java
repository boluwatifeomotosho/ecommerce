package com.justjava.ecommerce.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String landing() {
        return "landing";
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard(@AuthenticationPrincipal OidcUser user, Model model) {
        model.addAttribute("name", user.getFullName());
        model.addAttribute("email", user.getEmail());
        return "customer/dashboard";
    }

    @GetMapping("/vendor/dashboard")
    public String vendorDashboard(@AuthenticationPrincipal OidcUser user, Model model) {
        model.addAttribute("name", user.getFullName());
        model.addAttribute("email", user.getEmail());
        return "vendor/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(@AuthenticationPrincipal OidcUser user, Model model) {
        model.addAttribute("name", user.getFullName());
        model.addAttribute("email", user.getEmail());
        return "admin/dashboard";
    }
}
