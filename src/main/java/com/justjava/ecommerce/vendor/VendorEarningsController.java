package com.justjava.ecommerce.vendor;

import com.justjava.ecommerce.order.OrderRepository;
import com.justjava.ecommerce.order.OrderStatus;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/vendor/earnings")
@PreAuthorize("hasRole('VENDOR')")
@RequiredArgsConstructor
public class VendorEarningsController {

    private static final Set<OrderStatus> EARNED_STATUSES =
            EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED, OrderStatus.DELIVERED);
    private static final Set<OrderStatus> SETTLED_STATUSES =
            EnumSet.of(OrderStatus.DELIVERED);
    private static final Set<OrderStatus> PENDING_STATUSES =
            EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED);

    private final OrderRepository orderRepository;
    private final UserRepository  userRepository;

    @GetMapping
    public String earnings(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        UUID vendorId = resolveVendorId(principal);

        var orders = orderRepository.findByVendorIdAndStatusIn(
                vendorId, EARNED_STATUSES,
                PageRequest.of(page, 20, Sort.by("createdAt").descending()));

        model.addAttribute("orders",          orders);
        model.addAttribute("totalEarned",     orderRepository.sumRevenueByVendorIdAndStatusIn(vendorId, EARNED_STATUSES));
        model.addAttribute("settledEarnings", orderRepository.sumRevenueByVendorIdAndStatusIn(vendorId, SETTLED_STATUSES));
        model.addAttribute("pendingEarnings", orderRepository.sumRevenueByVendorIdAndStatusIn(vendorId, PENDING_STATUSES));
        model.addAttribute("name", principal.getFullName() != null ? principal.getFullName() : "Vendor");
        return "vendor/earnings";
    }

    private UUID resolveVendorId(OidcUser principal) {
        return userRepository.findByKeycloakId(principal.getSubject())
                .orElseThrow(() -> new IllegalStateException("Vendor user not found"))
                .getId();
    }
}
