package com.justjava.ecommerce.order;

import com.justjava.ecommerce.agent.AgentService;
import com.justjava.ecommerce.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final AgentService agentService;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            Authentication auth,
            Model model
    ) {
        OrderStatus statusFilter = null;
        if (status != null && !status.isBlank()) {
            try { statusFilter = OrderStatus.valueOf(status); } catch (IllegalArgumentException ignored) {}
        }
        var orders = orderService.getAllOrders(statusFilter,
                PageRequest.of(page, 20, Sort.by("createdAt").descending()));
        model.addAttribute("orders",       orders);
        model.addAttribute("activeStatus", status);
        enrichModel(auth, model);
        return "admin/orders/list";
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable UUID id,
            Authentication auth,
            Model model
    ) {
        OrderDto order = orderService.getOrderByIdForAdmin(id);
        model.addAttribute("order", order);
        model.addAttribute("agents", agentService.listAllAgents());
        enrichModel(auth, model);
        return "admin/orders/detail";
    }

    @PostMapping("/{id}/assign-agent")
    public String assignAgent(
            @PathVariable UUID id,
            @RequestParam UUID agentId,
            RedirectAttributes flash
    ) {
        try {
            orderService.assignAgentAndDispatch(id, agentId);
            flash.addFlashAttribute("successMessage", "Order dispatched. Agent has been notified.");
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/{id}/reassign-agent")
    public String reassignAgent(
            @PathVariable UUID id,
            @RequestParam UUID agentId,
            RedirectAttributes flash
    ) {
        try {
            orderService.reassignAgent(id, agentId);
            flash.addFlashAttribute("successMessage", "Agent reassigned. New agent has been notified.");
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatus status,
            RedirectAttributes flash
    ) {
        try {
            orderService.updateOrderStatusByAdmin(id, status);
            flash.addFlashAttribute("successMessage", "Order status updated to " + status + ".");
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    private void enrichModel(Authentication auth, Model model) {
        Object principal = auth.getPrincipal();
        if (principal instanceof OidcUser u) {
            model.addAttribute("name",  u.getFullName() != null ? u.getFullName() : u.getEmail());
            model.addAttribute("email", u.getEmail());
        } else if (principal instanceof User u) {
            model.addAttribute("name",  u.getFullName() != null ? u.getFullName() : "Admin");
            model.addAttribute("email", u.getEmail() != null ? u.getEmail() : u.getPhone());
        }
    }
}
