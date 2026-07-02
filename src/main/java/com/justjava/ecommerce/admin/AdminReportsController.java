package com.justjava.ecommerce.admin;

import com.justjava.ecommerce.order.OrderRepository;
import com.justjava.ecommerce.order.OrderStatus;
import com.justjava.ecommerce.product.ProductRepository;
import com.justjava.ecommerce.product.ProductStatus;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.EnumSet;
import java.util.Set;

@Controller
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminReportsController {

    private static final Set<OrderStatus> REVENUE_STATUSES =
            EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED, OrderStatus.DELIVERED);

    private final OrderRepository   orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository    userRepository;

    @GetMapping
    public String reports(Authentication auth, Model model) {
        // Top-level stats
        model.addAttribute("platformRevenue", orderRepository.sumPlatformRevenue());
        model.addAttribute("totalOrders",     orderRepository.count());
        model.addAttribute("totalCustomers",  userRepository.countByRole("CUSTOMER"));
        model.addAttribute("totalVendors",    userRepository.countByRole("VENDOR"));

        // Order status breakdown
        model.addAttribute("pendingPaymentOrders", orderRepository.countByStatus(OrderStatus.PENDING_PAYMENT));
        model.addAttribute("paidOrders",           orderRepository.countByStatus(OrderStatus.PAID));
        model.addAttribute("processingOrders",     orderRepository.countByStatus(OrderStatus.PROCESSING));
        model.addAttribute("shippedOrders",        orderRepository.countByStatus(OrderStatus.SHIPPED));
        model.addAttribute("deliveredOrders",      orderRepository.countByStatus(OrderStatus.DELIVERED));
        model.addAttribute("cancelledOrders",      orderRepository.countByStatus(OrderStatus.CANCELLED));

        // Product stats
        model.addAttribute("publishedProducts", productRepository.countByStatus(ProductStatus.PUBLISHED));
        model.addAttribute("pendingProducts",
                productRepository.countByStatus(ProductStatus.PENDING_REVIEW)
              + productRepository.countByStatus(ProductStatus.PENDING_EDIT));
        model.addAttribute("rejectedProducts",  productRepository.countByStatus(ProductStatus.REJECTED));
        model.addAttribute("totalProducts",     productRepository.count());

        // Top 5 vendors by revenue
        model.addAttribute("topVendors",
                orderRepository.findTopVendorsByRevenue(REVENUE_STATUSES, PageRequest.of(0, 5)));

        enrichModel(auth, model);
        return "admin/reports";
    }

    private void enrichModel(Authentication auth, Model model) {
        Object principal = auth.getPrincipal();
        if (principal instanceof OidcUser u) {
            model.addAttribute("name",  u.getFullName() != null ? u.getFullName() : u.getEmail());
            model.addAttribute("email", u.getEmail());
        }
    }
}
