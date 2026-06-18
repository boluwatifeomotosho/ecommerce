package com.justjava.ecommerce.auth;

import com.justjava.ecommerce.auth.PhoneRegistrationService.OtpResult;
import com.justjava.ecommerce.auth.PhoneRegistrationService.RateLimitException;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/login/phone")
@RequiredArgsConstructor
public class PhoneLoginController {

    private static final String SESSION_KEY = "pendingPhoneLogin";

    private final PhoneRegistrationService phoneRegService;
    private final UserRepository           userRepository;

    @GetMapping
    public String showPhoneEntry(Model model) {
        return "auth/login-phone";
    }

    @PostMapping
    public String requestLoginOtp(
            @RequestParam String phone,
            HttpSession session,
            RedirectAttributes flash
    ) {
        String normalised = normalisePhone(phone);

        if (!phoneRegService.isPhoneAlreadyRegistered(normalised)) {
            flash.addFlashAttribute("error",
                    "No account found with this number. Please register first or use Email & Password.");
            return "redirect:/login/phone";
        }

        try {
            phoneRegService.sendOtp(normalised);
        } catch (RateLimitException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/login/phone";
        }

        session.setAttribute(SESSION_KEY, normalised);
        return "redirect:/login/phone/verify";
    }

    @GetMapping("/verify")
    public String showVerify(HttpSession session, Model model) {
        String phone = (String) session.getAttribute(SESSION_KEY);
        if (phone == null) return "redirect:/login/phone";
        model.addAttribute("phone", phone);
        return "auth/login-phone-verify";
    }

    @PostMapping("/verify")
    public String verifyOtp(
            @RequestParam String otp,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes flash
    ) {
        String phone = (String) session.getAttribute(SESSION_KEY);
        if (phone == null) return "redirect:/login/phone";

        OtpResult result = phoneRegService.verifyOtp(phone, otp.trim());

        switch (result) {
            case INVALID -> {
                flash.addFlashAttribute("error", "Incorrect code. Please try again.");
                return "redirect:/login/phone/verify";
            }
            case EXPIRED -> {
                flash.addFlashAttribute("error", "Your code has expired. Please request a new one.");
                session.removeAttribute(SESSION_KEY);
                return "redirect:/login/phone";
            }
            case MAX_ATTEMPTS -> {
                flash.addFlashAttribute("error", "Too many failed attempts. Please try again later.");
                session.removeAttribute(SESSION_KEY);
                return "redirect:/login/phone";
            }
            case OK -> {
                User user = userRepository.findByPhone(phone).orElse(null);
                if (user == null) {
                    flash.addFlashAttribute("error", "Account not found. Please contact support.");
                    session.removeAttribute(SESSION_KEY);
                    return "redirect:/login/phone";
                }
                session.removeAttribute(SESSION_KEY);
                establishSession(user, request);
                return "redirect:" + dashboardFor(user.getRole());
            }
        }
        return "redirect:/login/phone";
    }

    @PostMapping("/resend")
    public String resendOtp(HttpSession session, RedirectAttributes flash) {
        String phone = (String) session.getAttribute(SESSION_KEY);
        if (phone == null) return "redirect:/login/phone";

        try {
            phoneRegService.sendOtp(phone);
            flash.addFlashAttribute("info", "A new code has been sent to your phone.");
        } catch (RateLimitException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/login/phone/verify";
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void establishSession(User user, HttpServletRequest request) {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        var auth        = new UsernamePasswordAuthenticationToken(user, null, authorities);

        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctx);

        log.info("Phone OTP login successful for {}", user.getPhone());
    }

    private String dashboardFor(String role) {
        return switch (role) {
            case "ADMIN"  -> "/admin/dashboard";
            case "VENDOR" -> "/vendor/dashboard";
            default       -> "/customer/dashboard";
        };
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
}
