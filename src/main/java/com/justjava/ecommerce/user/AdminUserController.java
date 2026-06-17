package com.justjava.ecommerce.user;

import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        var users = userRepository.findByRole("CUSTOMER",
                PageRequest.of(page, 25, Sort.by("createdAt").descending()));

        model.addAttribute("users", users);
        model.addAttribute("name", principal.getFullName());
        model.addAttribute("email", principal.getEmail());
        return "admin/users/list";
    }

    @GetMapping("/vendors")
    public String listVendors(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        var vendors = userRepository.findByRole("VENDOR",
                PageRequest.of(page, 25, Sort.by("createdAt").descending()));

        model.addAttribute("vendors", vendors);
        model.addAttribute("name", principal.getFullName());
        model.addAttribute("email", principal.getEmail());
        return "admin/vendors/list";
    }
}
