package com.justjava.ecommerce.auth;

import com.justjava.ecommerce.auth.KeycloakAdminService;
import com.justjava.ecommerce.auth.PhoneRegistrationService;
import com.justjava.ecommerce.auth.PhoneRegistrationService.OtpResult;
import com.justjava.ecommerce.auth.PhoneRegistrationService.RateLimitException;
import com.justjava.ecommerce.user.UserSyncService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Serializable;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/register/phone")
@RequiredArgsConstructor
public class PhoneRegistrationController {

    private static final String SESSION_KEY = "pendingPhoneReg";

    private final PhoneRegistrationService phoneRegService;
    private final KeycloakAdminService     keycloakAdminService;
    private final UserSyncService          userSyncService;

    @GetMapping
    public String showForm(Model model) {
        return "register-phone";
    }

    @PostMapping
    public String requestOtp(
            @RequestParam String phone,
            @RequestParam String firstName,
            @RequestParam String lastName,
            HttpSession session,
            RedirectAttributes flash
    ) {
        String normalised = normalisePhone(phone);

        if (phoneRegService.isPhoneAlreadyRegistered(normalised)) {
            flash.addFlashAttribute("error", "This phone number is already registered. Please log in.");
            return "redirect:/register/phone";
        }

        try {
            phoneRegService.sendOtp(normalised);
        } catch (RateLimitException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/register/phone";
        }

        session.setAttribute(SESSION_KEY, new PendingRegistration(normalised, firstName, lastName));
        return "redirect:/register/phone/verify";
    }

    @GetMapping("/verify")
    public String showVerify(HttpSession session, Model model) {
        PendingRegistration pending = (PendingRegistration) session.getAttribute(SESSION_KEY);
        if (pending == null) return "redirect:/register/phone";
        model.addAttribute("phone", pending.phone());
        return "verify-phone";
    }

    @PostMapping("/verify")
    public String verifyOtp(
            @RequestParam String otp,
            HttpSession session,
            RedirectAttributes flash
    ) {
        PendingRegistration pending = (PendingRegistration) session.getAttribute(SESSION_KEY);
        if (pending == null) return "redirect:/register/phone";

        OtpResult result = phoneRegService.verifyOtp(pending.phone(), otp.trim());

        switch (result) {
            case INVALID -> {
                flash.addFlashAttribute("error", "Incorrect code. Please try again.");
                return "redirect:/register/phone/verify";
            }
            case EXPIRED -> {
                flash.addFlashAttribute("error", "Your code has expired. Please request a new one.");
                session.removeAttribute(SESSION_KEY);
                return "redirect:/register/phone";
            }
            case MAX_ATTEMPTS -> {
                flash.addFlashAttribute("error", "Too many failed attempts. Please start registration again.");
                session.removeAttribute(SESSION_KEY);
                return "redirect:/register/phone";
            }
            case OK -> {
                try {
                    String systemPassword = UUID.randomUUID().toString() + UUID.randomUUID().toString();
                    String keycloakId = keycloakAdminService.createUser(
                            pending.phone(), pending.firstName(), pending.lastName(), systemPassword, false
                    );
                    userSyncService.syncPhoneUser(keycloakId, pending.phone(), pending.firstName(), pending.lastName());
                } catch (Exception e) {
                    log.error("Failed to create Keycloak user for phone {}: {}", pending.phone(), e.getMessage());
                    flash.addFlashAttribute("error", "Registration failed. Please try again later.");
                    session.removeAttribute(SESSION_KEY);
                    return "redirect:/register/phone";
                }
                session.removeAttribute(SESSION_KEY);
                return "redirect:/register/phone/success";
            }
        }
        return "redirect:/register/phone";
    }

    @GetMapping("/success")
    public String registrationSuccess() {
        return "register-phone-success";
    }

    @PostMapping("/resend")
    public String resendOtp(HttpSession session, RedirectAttributes flash) {
        PendingRegistration pending = (PendingRegistration) session.getAttribute(SESSION_KEY);
        if (pending == null) return "redirect:/register/phone";

        try {
            phoneRegService.sendOtp(pending.phone());
            flash.addFlashAttribute("info", "A new code has been sent to your phone.");
        } catch (RateLimitException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/register/phone/verify";
    }

    private String normalisePhone(String raw) {
        String digits = raw.replaceAll("[^\\d+]", "");
        if (digits.startsWith("0") && digits.length() == 11) {
            digits = "+234" + digits.substring(1);
        } else if (!digits.startsWith("+")) {
            digits = "+" + digits;
        }
        return digits;
    }

    record PendingRegistration(String phone, String firstName, String lastName)
            implements Serializable {}
}
