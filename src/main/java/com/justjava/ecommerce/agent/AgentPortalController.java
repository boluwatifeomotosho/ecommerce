package com.justjava.ecommerce.agent;

import com.justjava.ecommerce.order.OrderDto;
import com.justjava.ecommerce.order.OrderService;
import com.justjava.ecommerce.order.OrderStatus;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/agent")
@PreAuthorize("hasRole('AGENT')")
@RequiredArgsConstructor
public class AgentPortalController {

    private final OrderService   orderService;
    private final UserRepository userRepository;

    @GetMapping
    public String home() {
        return "redirect:/agent/deliveries";
    }

    @GetMapping("/deliveries")
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        User agent = resolveAgent(principal);
        OrderStatus statusFilter = null;
        if (status != null && !status.isBlank()) {
            try { statusFilter = OrderStatus.valueOf(status); } catch (IllegalArgumentException ignored) {}
        }
        var orders = orderService.getOrdersForAgent(agent.getId(), statusFilter,
                PageRequest.of(page, 20, Sort.by("createdAt").descending()));

        model.addAttribute("orders",         orders);
        model.addAttribute("activeStatus",   status);
        model.addAttribute("inTransitCount", orderService.countForAgentByStatus(agent.getId(), OrderStatus.IN_TRANSIT));
        model.addAttribute("deliveredCount", orderService.countForAgentByStatus(agent.getId(), OrderStatus.DELIVERED));
        model.addAttribute("confirmedCount", orderService.countForAgentByStatus(agent.getId(), OrderStatus.CONFIRMED));
        model.addAttribute("name",  principal.getFullName() != null ? principal.getFullName() : "Agent");
        model.addAttribute("email", principal.getEmail());
        return "agent/deliveries";
    }

    @GetMapping("/deliveries/{id}")
    public String detail(
            @PathVariable UUID id,
            @AuthenticationPrincipal OidcUser principal,
            RedirectAttributes flash,
            Model model
    ) {
        User agent = resolveAgent(principal);
        try {
            OrderDto order = orderService.getOrderByIdForAgent(id, agent.getId());
            model.addAttribute("order", order);
            model.addAttribute("name",  principal.getFullName() != null ? principal.getFullName() : "Agent");
            model.addAttribute("email", principal.getEmail());
            return "agent/delivery-detail";
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("errorMessage",
                    "This delivery is no longer assigned to you. It may have been reassigned to another agent.");
            return "redirect:/agent/deliveries";
        }
    }

    @PostMapping("/deliveries/{id}/deliver")
    public String markDelivered(
            @PathVariable UUID id,
            @AuthenticationPrincipal OidcUser principal,
            RedirectAttributes flash
    ) {
        User agent = resolveAgent(principal);
        try {
            orderService.markDeliveredByAgent(id, agent.getId());
            flash.addFlashAttribute("successMessage", "Marked as delivered. Waiting on customer confirmation.");
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/agent/deliveries/" + id;
    }

    private User resolveAgent(OidcUser principal) {
        return userRepository.findByKeycloakId(principal.getSubject())
                .orElseThrow(() -> new IllegalStateException("Agent user not found in local DB"));
    }
}
