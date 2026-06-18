package com.justjava.ecommerce.order;

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

import java.util.UUID;

@Controller
@RequestMapping("/vendor/orders")
@PreAuthorize("hasRole('VENDOR')")
@RequiredArgsConstructor
public class VendorOrderController {

    private final OrderService    orderService;
    private final UserRepository  userRepository;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        UUID vendorId = resolveVendorId(principal);
        OrderStatus statusFilter = null;
        if (status != null && !status.isBlank()) {
            try { statusFilter = OrderStatus.valueOf(status); } catch (IllegalArgumentException ignored) {}
        }
        var orders = orderService.getVendorOrders(vendorId, statusFilter,
                PageRequest.of(page, 20, Sort.by("createdAt").descending()));
        model.addAttribute("orders",       orders);
        model.addAttribute("name",         principal.getFullName() != null ? principal.getFullName() : "Vendor");
        model.addAttribute("activeStatus", status);
        return "vendor/orders/list";
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable UUID id,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        UUID vendorId = resolveVendorId(principal);
        OrderDto order = orderService.getOrderByIdForVendor(id, vendorId);
        model.addAttribute("order", order);
        model.addAttribute("name", principal.getFullName() != null ? principal.getFullName() : "Vendor");
        return "vendor/orders/detail";
    }

    private UUID resolveVendorId(OidcUser principal) {
        return userRepository.findByKeycloakId(principal.getSubject())
                .orElseThrow(() -> new IllegalStateException("Vendor user not found"))
                .getId();
    }
}
