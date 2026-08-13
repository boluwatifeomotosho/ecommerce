package com.justjava.ecommerce.notification;

import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService  notificationService;
    private final UserRepository       userRepository;

    @GetMapping
    public String inbox(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        User user = resolveUser(principal);
        model.addAttribute("notifications", notificationService.list(user.getId(), PageRequest.of(page, 30)));
        model.addAttribute("name",  principal.getFullName());
        model.addAttribute("email", principal.getEmail());
        return "notifications/inbox";
    }

    @PostMapping("/mark-all-read")
    public String markAllRead(@AuthenticationPrincipal OidcUser principal) {
        User user = resolveUser(principal);
        notificationService.markAllRead(user.getId());
        return "redirect:/notifications";
    }

    /**
     * Marks the notification read then bounces to its target URL (or the inbox
     * if none). Used as the "click a bell item" endpoint from sidebars.
     */
    @GetMapping("/{id}/go")
    public String open(
            @PathVariable UUID id,
            @RequestParam(required = false) String to,
            @AuthenticationPrincipal OidcUser principal
    ) {
        User user = resolveUser(principal);
        notificationService.markRead(id, user.getId());
        String target = (to != null && !to.isBlank()) ? to : "/notifications";
        return "redirect:" + target;
    }

    private User resolveUser(OidcUser principal) {
        return userRepository.findByKeycloakId(principal.getSubject())
                .orElseThrow(() -> new IllegalStateException("User not found in local DB"));
    }

    // Kept only so IDE tooling notices URLEncoder as intentional if we later swap
    // to redirect encoding; unused at runtime.
    @SuppressWarnings("unused")
    private String enc(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
}
