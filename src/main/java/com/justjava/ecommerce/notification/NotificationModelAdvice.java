package com.justjava.ecommerce.notification;

import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * Exposes {@code notificationUnreadCount} and {@code recentNotifications}
 * to every rendered template, so sidebars/topbars can show the bell badge
 * without every controller having to inject them manually.
 */
@Slf4j
@ControllerAdvice(basePackages = "com.justjava.ecommerce")
@RequiredArgsConstructor
public class NotificationModelAdvice {

    private final NotificationRepository notificationRepository;
    private final UserRepository         userRepository;

    @ModelAttribute
    public void addUnreadCount(org.springframework.ui.Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) return;
            Object principal = auth.getPrincipal();
            if (!(principal instanceof OidcUser oidcUser)) return;

            User user = userRepository.findByKeycloakId(oidcUser.getSubject()).orElse(null);
            if (user == null) return;

            long unread = notificationRepository.countByRecipientIdAndReadAtIsNull(user.getId());
            model.addAttribute("notificationUnreadCount", unread);

            List<Notification> recent = notificationRepository
                    .findByRecipientIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, 5))
                    .getContent();
            model.addAttribute("recentNotifications", recent);
        } catch (Exception e) {
            log.debug("Notification advice skipped: {}", e.getMessage());
        }
    }
}
