package com.justjava.ecommerce.vendor;

import com.justjava.ecommerce.order.OrderRepository;
import com.justjava.ecommerce.order.OrderStatus;
import com.justjava.ecommerce.product.ProductRepository;
import com.justjava.ecommerce.product.ProductStatus;
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
@PreAuthorize("hasAnyRole('VENDOR', 'SUB_VENDOR')")
@RequiredArgsConstructor
public class VendorAnalyticsController {

    private static final Set<OrderStatus> REVENUE_STATUSES =
            EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED, OrderStatus.DELIVERED, OrderStatus.CONFIRMED);
    private static final Set<OrderStatus> SETTLED_STATUSES = EnumSet.of(OrderStatus.CONFIRMED);
    private static final Set<OrderStatus> PENDING_STATUSES =
            EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED, OrderStatus.DELIVERED);

    private final OrderRepository       orderRepository;
    private final ProductRepository     productRepository;
    private final VendorMemberService   vendorMemberService;

    @GetMapping
    public String analytics(@AuthenticationPrincipal OidcUser principal, Model model) {
        VendorScope scope = vendorMemberService.currentScope(principal);

        if (scope.isCreatorScoped()) {
            populateCreatorScoped(model, scope.vendorId(), scope.creatorUserId());
        } else {
            populateVendorWide(model, scope.vendorId());
        }

        model.addAttribute("name", principal.getFullName() != null ? principal.getFullName() : "Vendor");
        return "vendor/analytics";
    }

    private void populateVendorWide(Model model, UUID vendorId) {
        model.addAttribute("totalOrders",      orderRepository.countByVendorId(vendorId));
        model.addAttribute("paidOrders",       orderRepository.countByVendorIdAndStatus(vendorId, OrderStatus.PAID));
        model.addAttribute("processingOrders", orderRepository.countByVendorIdAndStatus(vendorId, OrderStatus.PROCESSING));
        model.addAttribute("shippedOrders",    orderRepository.countByVendorIdAndStatus(vendorId, OrderStatus.SHIPPED));
        model.addAttribute("deliveredOrders",  orderRepository.countByVendorIdAndStatus(vendorId, OrderStatus.DELIVERED));
        model.addAttribute("cancelledOrders",  orderRepository.countByVendorIdAndStatus(vendorId, OrderStatus.CANCELLED));

        model.addAttribute("totalEarned",     orderRepository.sumRevenueByVendorIdAndStatusIn(vendorId, REVENUE_STATUSES));
        model.addAttribute("settledEarnings", orderRepository.sumRevenueByVendorIdAndStatusIn(vendorId, SETTLED_STATUSES));
        model.addAttribute("pendingEarnings", orderRepository.sumRevenueByVendorIdAndStatusIn(vendorId, PENDING_STATUSES));

        model.addAttribute("publishedProducts", productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.PUBLISHED));
        model.addAttribute("draftProducts",     productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.DRAFT));
        model.addAttribute("pendingProducts",
                productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.PENDING_REVIEW)
              + productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.PENDING_EDIT));
        model.addAttribute("rejectedProducts",  productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.REJECTED));
        model.addAttribute("archivedProducts",  productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.ARCHIVED));
        model.addAttribute("lowStockProducts",  productRepository.countByVendorIdAndStockQuantityLessThanEqual(vendorId, 5));

        model.addAttribute("topProducts",
                orderRepository.findTopProductsByVendor(vendorId, REVENUE_STATUSES, PageRequest.of(0, 5)));
    }

    private void populateCreatorScoped(Model model, UUID vendorId, UUID creatorId) {
        model.addAttribute("totalOrders",      orderRepository.countByVendorIdAndCreator(vendorId, creatorId));
        model.addAttribute("paidOrders",       orderRepository.countByVendorIdAndCreatorAndStatus(vendorId, creatorId, OrderStatus.PAID));
        model.addAttribute("processingOrders", orderRepository.countByVendorIdAndCreatorAndStatus(vendorId, creatorId, OrderStatus.PROCESSING));
        model.addAttribute("shippedOrders",    orderRepository.countByVendorIdAndCreatorAndStatus(vendorId, creatorId, OrderStatus.SHIPPED));
        model.addAttribute("deliveredOrders",  orderRepository.countByVendorIdAndCreatorAndStatus(vendorId, creatorId, OrderStatus.DELIVERED));
        model.addAttribute("cancelledOrders",  orderRepository.countByVendorIdAndCreatorAndStatus(vendorId, creatorId, OrderStatus.CANCELLED));

        model.addAttribute("totalEarned",     orderRepository.sumRevenueByVendorIdAndCreatorAndStatusIn(vendorId, creatorId, REVENUE_STATUSES));
        model.addAttribute("settledEarnings", orderRepository.sumRevenueByVendorIdAndCreatorAndStatusIn(vendorId, creatorId, SETTLED_STATUSES));
        model.addAttribute("pendingEarnings", orderRepository.sumRevenueByVendorIdAndCreatorAndStatusIn(vendorId, creatorId, PENDING_STATUSES));

        model.addAttribute("publishedProducts", productRepository.countByVendorIdAndCreatedByIdAndStatus(vendorId, creatorId, ProductStatus.PUBLISHED));
        model.addAttribute("draftProducts",     productRepository.countByVendorIdAndCreatedByIdAndStatus(vendorId, creatorId, ProductStatus.DRAFT));
        model.addAttribute("pendingProducts",
                productRepository.countByVendorIdAndCreatedByIdAndStatus(vendorId, creatorId, ProductStatus.PENDING_REVIEW)
              + productRepository.countByVendorIdAndCreatedByIdAndStatus(vendorId, creatorId, ProductStatus.PENDING_EDIT));
        model.addAttribute("rejectedProducts",  productRepository.countByVendorIdAndCreatedByIdAndStatus(vendorId, creatorId, ProductStatus.REJECTED));
        model.addAttribute("archivedProducts",  productRepository.countByVendorIdAndCreatedByIdAndStatus(vendorId, creatorId, ProductStatus.ARCHIVED));
        model.addAttribute("lowStockProducts",  productRepository.countByVendorIdAndCreatedByIdAndStockQuantityLessThanEqual(vendorId, creatorId, 5));

        model.addAttribute("topProducts",
                orderRepository.findTopProductsByVendorAndCreator(vendorId, creatorId, REVENUE_STATUSES, PageRequest.of(0, 5)));
    }
}
