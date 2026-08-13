package com.justjava.ecommerce.agent;

import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Only VENDOR (subsidiary admin) can invite agents — SUB_VENDOR (team member) cannot.
 * Agents themselves are platform-global and available to every subsidiary.
 */
@Controller
@RequestMapping("/vendor/agents")
@PreAuthorize("hasRole('VENDOR')")
@RequiredArgsConstructor
public class VendorAgentController {

    private final AgentService   agentService;
    private final UserRepository userRepository;

    @GetMapping
    public String list(@AuthenticationPrincipal OidcUser principal, Model model) {
        model.addAttribute("agents", agentService.listAllAgents());
        model.addAttribute("name",   principal.getFullName() != null ? principal.getFullName() : "Vendor");
        return "vendor/agents";
    }

    @PostMapping("/invite")
    public String invite(
            @AuthenticationPrincipal OidcUser principal,
            @RequestParam String email,
            @RequestParam String firstName,
            @RequestParam String lastName,
            RedirectAttributes flash
    ) {
        User inviter = userRepository.findByKeycloakId(principal.getSubject()).orElse(null);
        AgentService.InviteResult result = agentService.inviteAgent(email, firstName, lastName, inviter);
        if (result.success()) {
            flash.addFlashAttribute("inviteSuccess", result.message());
        } else {
            flash.addFlashAttribute("inviteError", result.message());
        }
        return "redirect:/vendor/agents";
    }
}
