package com.justjava.ecommerce.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminSettingsController {

    private static final List<String> ALLOWED_KEYS =
            List.of("platform.name", "support.email", "support.phone", "commission.rate");

    private final PlatformSettingRepository settingRepository;

    @GetMapping
    public String settings(Authentication auth, Model model) {
        Map<String, String> settings = settingRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        PlatformSetting::getKey,
                        s -> s.getValue() != null ? s.getValue() : ""
                ));
        model.addAttribute("settings", settings);
        enrichModel(auth, model);
        return "admin/settings";
    }

    @PostMapping
    public String updateSettings(@RequestParam Map<String, String> params, RedirectAttributes flash) {
        ALLOWED_KEYS.forEach(key -> {
            String val = params.get(key);
            if (val != null) {
                settingRepository.findById(key).ifPresent(s -> {
                    s.setValue(val.trim());
                    s.setUpdatedAt(LocalDateTime.now());
                    settingRepository.save(s);
                });
            }
        });
        flash.addFlashAttribute("success", "Settings updated successfully.");
        return "redirect:/admin/settings";
    }

    private void enrichModel(Authentication auth, Model model) {
        Object principal = auth.getPrincipal();
        if (principal instanceof OidcUser u) {
            model.addAttribute("name",  u.getFullName() != null ? u.getFullName() : u.getEmail());
            model.addAttribute("email", u.getEmail());
        }
    }
}
