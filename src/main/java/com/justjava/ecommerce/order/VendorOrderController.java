package com.justjava.ecommerce.order;

import com.justjava.ecommerce.vendor.VendorMemberService;
import com.justjava.ecommerce.vendor.VendorScope;
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
@RequestMapping("/vendor/orders")
@PreAuthorize("hasAnyRole('VENDOR', 'SUB_VENDOR')")
@RequiredArgsConstructor
public class VendorOrderController {

    private final OrderService        orderService;
    private final VendorMemberService vendorMemberService;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        VendorScope scope = vendorMemberService.currentScope(principal);
        OrderStatus statusFilter = null;
        if (status != null && !status.isBlank()) {
            try { statusFilter = OrderStatus.valueOf(status); } catch (IllegalArgumentException ignored) {}
        }
        var pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending());
        var orders = scope.isCreatorScoped()
                ? orderService.getVendorOrdersForCreator(scope.vendorId(), scope.creatorUserId(), statusFilter, pageable)
                : orderService.getVendorOrders(scope.vendorId(), statusFilter, pageable);
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
        VendorScope scope = vendorMemberService.currentScope(principal);
        OrderDto order = scope.isCreatorScoped()
                ? orderService.getOrderByIdForVendorCreator(id, scope.vendorId(), scope.creatorUserId())
                : orderService.getOrderByIdForVendor(id, scope.vendorId());
        model.addAttribute("order", order);
        model.addAttribute("name", principal.getFullName() != null ? principal.getFullName() : "Vendor");
        return "vendor/orders/detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatus status,
            @AuthenticationPrincipal OidcUser principal,
            RedirectAttributes flash
    ) {
        VendorScope scope = vendorMemberService.currentScope(principal);
        try {
            if (scope.isCreatorScoped()) {
                orderService.updateOrderStatusByVendorCreator(id, scope.vendorId(), scope.creatorUserId(), status);
            } else {
                orderService.updateOrderStatusByVendor(id, scope.vendorId(), status);
            }
            flash.addFlashAttribute("successMessage", "Order status updated to " + status + ".");
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/vendor/orders/" + id;
    }
}
