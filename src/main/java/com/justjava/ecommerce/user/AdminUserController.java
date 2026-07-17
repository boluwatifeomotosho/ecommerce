package com.justjava.ecommerce.user;

import com.justjava.ecommerce.order.OrderRepository;
import com.justjava.ecommerce.order.OrderStatus;
import com.justjava.ecommerce.product.ProductRepository;
import com.justjava.ecommerce.product.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private static final Set<OrderStatus> REVENUE_STATUSES =
            EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED, OrderStatus.DELIVERED);
    private static final Set<OrderStatus> SETTLED_STATUSES = EnumSet.of(OrderStatus.DELIVERED);
    private static final Set<OrderStatus> PENDING_STATUSES =
            EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED);

    private final UserRepository    userRepository;
    private final OrderRepository   orderRepository;
    private final ProductRepository productRepository;

    @GetMapping("/users")
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        var users = userRepository.findByRole("CUSTOMER",
                PageRequest.of(page, 25, Sort.by("createdAt").descending()));

        model.addAttribute("users", users);
        model.addAttribute("name", principal.getFullName());
        model.addAttribute("email", principal.getEmail());
        return "admin/users/list";
    }

    @GetMapping("/vendors")
    public String listVendors(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        var vendors = userRepository.findByRole("VENDOR",
                PageRequest.of(page, 25, Sort.by("createdAt").descending()));

        model.addAttribute("vendors", vendors);
        model.addAttribute("name", principal.getFullName());
        model.addAttribute("email", principal.getEmail());
        return "admin/vendors/list";
    }

    @GetMapping("/vendors/{id}")
    public String vendorDetails(
            @PathVariable UUID id,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        User vendor = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));

        if (!"VENDOR".equalsIgnoreCase(vendor.getRole())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not a vendor");
        }

        UUID vendorId = vendor.getId();

        // Orders
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

        // Products
        model.addAttribute("publishedProducts", productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.PUBLISHED));
        model.addAttribute("draftProducts",     productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.DRAFT));
        model.addAttribute("pendingProducts",
                productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.PENDING_REVIEW)
              + productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.PENDING_EDIT));
        model.addAttribute("rejectedProducts",  productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.REJECTED));
        model.addAttribute("archivedProducts",  productRepository.countByVendorIdAndStatus(vendorId, ProductStatus.ARCHIVED));
        model.addAttribute("lowStockProducts",  productRepository.countByVendorIdAndStockQuantityLessThanEqual(vendorId, 5));

        // Top products & recent orders
        model.addAttribute("topProducts",
                orderRepository.findTopProductsByVendor(vendorId, REVENUE_STATUSES, PageRequest.of(0, 5)));
        model.addAttribute("recentOrders",
                orderRepository.findRecentByVendorId(vendorId, PageRequest.of(0, 10)));

        model.addAttribute("vendor", vendor);
        model.addAttribute("name", principal.getFullName());
        model.addAttribute("email", principal.getEmail());
        return "admin/vendors/details";
    }
}
