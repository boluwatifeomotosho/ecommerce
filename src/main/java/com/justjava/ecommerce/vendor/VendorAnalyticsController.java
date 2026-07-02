package com.justjava.ecommerce.vendor;

import com.justjava.ecommerce.order.OrderRepository;
import com.justjava.ecommerce.order.OrderStatus;
import com.justjava.ecommerce.product.ProductRepository;
import com.justjava.ecommerce.product.ProductStatus;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/vendor/analytics")
@PreAuthorize("hasRole('VENDOR')")
@RequiredArgsConstructor
public class VendorAnalyticsController {

    private static final Set<OrderStatus> REVENUE_STATUSES =
            EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED, OrderStatus.DELIVERED);
    private static final Set<OrderStatus> SETTLED_STATUSES = EnumSet.of(OrderStatus.DELIVERED);
    private static final Set<OrderStatus> PENDING_STATUSES =
            EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED);

    private final OrderRepository   orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository    userRepository;

    @GetMapping
    public String analytics(@AuthenticationPrincipal OidcUser principal, Model model) {
        UUID vendorId = resolveVendorId(principal);

        // Order counts
        model.addAttribute("totalOrders",      orderRepository.countByVendorId(vendorId));
        model.addAttribute("paidOrders",       orderRepository.countByVendorIdAndStatus(vendorId, OrderStatus.PAID));
        model.addAttribute("processingOrders", orderRepository.countByVendorIdAndStatus(vendorId, OrderStatus.PROCESSING));
        model.addAttribute("shippedOrders",    orderRepository.countByVendorIdAndStatus(vendorId, OrderStatus.SHIPPED));
        model.addAttribute("deliveredOrders",  orderRepository.countByVendorIdAndStatus(vendorId, OrderStatus.DELIVERED));
        model.addAttribute("cancelledOrders",  orderRepository.countByVendorIdAndStatus(vendorId, OrderStatus.CANCELLED));

        // Earnings
        model.addAttribute("totalEarned",     orderRepository.sumRevenueByVendorIdAndStatusIn(vendorId, REVENUE_STATUSES));
        model.addAttribute("settledEarnings", orderRepository.sumRevenueByVendorIdAndStatusIn(vendorId, SETTLED_STATUSES));
        model.addAttribute("pendingEarnings", orderRepository.sumRevenueByVendorIdAndStatusIn(vendorId, PENDING_STATUSES));

        // Product counts
        model.addAttribute("publishedProducts", productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.PUBLISHED));
        model.addAttribute("draftProducts",     productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.DRAFT));
        model.addAttribute("pendingProducts",
                productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.PENDING_REVIEW)
              + productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.PENDING_EDIT));
        model.addAttribute("rejectedProducts",  productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.REJECTED));
        model.addAttribute("archivedProducts",  productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.ARCHIVED));
        model.addAttribute("lowStockProducts",  productRepository.countByVendorIdAndStockQuantityLessThanEqual(vendorId, 5));

        // Top 5 products by units sold
        model.addAttribute("topProducts",
                orderRepository.findTopProductsByVendor(vendorId, REVENUE_STATUSES, PageRequest.of(0, 5)));

        model.addAttribute("name", principal.getFullName() != null ? principal.getFullName() : "Vendor");
        return "vendor/analytics";
    }

    private UUID resolveVendorId(OidcUser principal) {
        return userRepository.findByKeycloakId(principal.getSubject())
                .orElseThrow(() -> new IllegalStateException("Vendor user not found"))
                .getId();
    }
}
