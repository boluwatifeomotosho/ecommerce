package com.justjava.ecommerce.vendor;

import com.justjava.ecommerce.order.OrderRepository;
import com.justjava.ecommerce.order.OrderStatus;
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

@Controller
@RequestMapping("/vendor/earnings")
@PreAuthorize("hasAnyRole('VENDOR', 'SUB_VENDOR')")
@RequiredArgsConstructor
public class VendorEarningsController {

    private static final Set<OrderStatus> EARNED_STATUSES =
            EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED, OrderStatus.DELIVERED, OrderStatus.CONFIRMED);
    private static final Set<OrderStatus> SETTLED_STATUSES =
            EnumSet.of(OrderStatus.CONFIRMED);
    private static final Set<OrderStatus> PENDING_STATUSES =
            EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED, OrderStatus.DELIVERED);

    private final OrderRepository     orderRepository;
    private final VendorMemberService vendorMemberService;

    @GetMapping
    public String earnings(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        VendorScope scope = vendorMemberService.currentScope(principal);
        var pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending());

        if (scope.isCreatorScoped()) {
            var orders = orderRepository.findByVendorIdAndCreatorAndStatusIn(
                    scope.vendorId(), scope.creatorUserId(), EARNED_STATUSES, pageable);
            model.addAttribute("orders",          orders);
            model.addAttribute("totalEarned",     orderRepository.sumRevenueByVendorIdAndCreatorAndStatusIn(scope.vendorId(), scope.creatorUserId(), EARNED_STATUSES));
            model.addAttribute("settledEarnings", orderRepository.sumRevenueByVendorIdAndCreatorAndStatusIn(scope.vendorId(), scope.creatorUserId(), SETTLED_STATUSES));
            model.addAttribute("pendingEarnings", orderRepository.sumRevenueByVendorIdAndCreatorAndStatusIn(scope.vendorId(), scope.creatorUserId(), PENDING_STATUSES));
        } else {
            var orders = orderRepository.findByVendorIdAndStatusIn(
                    scope.vendorId(), EARNED_STATUSES, pageable);
            model.addAttribute("orders",          orders);
            model.addAttribute("totalEarned",     orderRepository.sumRevenueByVendorIdAndStatusIn(scope.vendorId(), EARNED_STATUSES));
            model.addAttribute("settledEarnings", orderRepository.sumRevenueByVendorIdAndStatusIn(scope.vendorId(), SETTLED_STATUSES));
            model.addAttribute("pendingEarnings", orderRepository.sumRevenueByVendorIdAndStatusIn(scope.vendorId(), PENDING_STATUSES));
        }
        model.addAttribute("name", principal.getFullName() != null ? principal.getFullName() : "Vendor");
        return "vendor/earnings";
    }
}
