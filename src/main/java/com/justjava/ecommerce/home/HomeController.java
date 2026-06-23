package com.justjava.ecommerce.home;

import com.justjava.ecommerce.cart.CartService;
import com.justjava.ecommerce.order.Order;
import com.justjava.ecommerce.wishlist.WishlistService;
import com.justjava.ecommerce.order.OrderRepository;
import com.justjava.ecommerce.order.OrderStatus;
import com.justjava.ecommerce.product.ProductQueryService;
import com.justjava.ecommerce.product.ProductRepository;
import com.justjava.ecommerce.product.ProductStatus;
import com.justjava.ecommerce.product.dto.ProductFilter;
import com.justjava.ecommerce.category.CategoryService;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductQueryService productQueryService;
    private final CategoryService     categoryService;
    private final OrderRepository     orderRepository;
    private final ProductRepository   productRepository;
    private final UserRepository      userRepository;
    private final CartService         cartService;
    private final WishlistService     wishlistService;

    @GetMapping("/")
    public String landing(Model model) {
        var featuredProducts = productQueryService.getPublishedProducts(
                ProductFilter.empty(),
                PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "publishedAt"))
        );
        model.addAttribute("featuredProducts", featuredProducts.getContent());
        model.addAttribute("categories", categoryService.getRootCategories());
        return "landing";
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard(Authentication auth, Model model) {
        enrichModel(auth, model);
        UUID customerId = resolveId(auth);
        if (customerId != null) {
            var activeStatuses = EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED);
            model.addAttribute("activeOrderCount",   orderRepository.countByCustomerIdAndStatusIn(customerId, activeStatuses));
            model.addAttribute("totalOrderCount",    orderRepository.countByCustomerId(customerId));
            model.addAttribute("cartItemCount",      cartService.getCartItemCount(customerId));
            model.addAttribute("wishlistCount",      wishlistService.getWishlistCount(customerId));
            List<Order> recent = orderRepository.findRecentByCustomerId(customerId, PageRequest.of(0, 5));
            model.addAttribute("recentOrders", recent);
        }
        return "customer/dashboard";
    }

    @GetMapping("/vendor/dashboard")
    public String vendorDashboard(Authentication auth, Model model) {
        enrichModel(auth, model);
        UUID vendorId = resolveId(auth);
        if (vendorId != null) {
            model.addAttribute("totalOrders",   orderRepository.countByVendorId(vendorId));
            model.addAttribute("pendingOrders", orderRepository.countByVendorIdAndStatus(vendorId, OrderStatus.PENDING_PAYMENT));
            model.addAttribute("revenue",       orderRepository.sumRevenueByVendorId(vendorId));
            model.addAttribute("lowStockCount", productRepository.countByVendorIdAndStockQuantityLessThanEqual(vendorId, 5));
            List<Order> recent = orderRepository.findRecentByVendorId(vendorId, PageRequest.of(0, 5));
            model.addAttribute("recentOrders", recent);
        }
        return "vendor/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication auth, Model model) {
        enrichModel(auth, model);
        model.addAttribute("totalCustomers",    userRepository.countByRole("CUSTOMER"));
        model.addAttribute("totalVendors",      userRepository.countByRole("VENDOR"));
        model.addAttribute("totalOrders",       orderRepository.count());
        model.addAttribute("platformRevenue",   orderRepository.sumPlatformRevenue());
        model.addAttribute("pendingReviews",    productRepository.countByStatus(ProductStatus.PENDING_REVIEW));
        List<Order> recent = orderRepository.findRecentOrders(PageRequest.of(0, 5));
        model.addAttribute("recentOrders", recent);
        return "admin/dashboard";
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void enrichModel(Authentication auth, Model model) {
        Object principal = auth.getPrincipal();
        if (principal instanceof OidcUser u) {
            model.addAttribute("name",  u.getFullName() != null ? u.getFullName() : u.getEmail());
            model.addAttribute("email", u.getEmail());
        } else if (principal instanceof User u) {
            model.addAttribute("name",  u.getFullName() != null ? u.getFullName() : "User");
            model.addAttribute("email", u.getEmail() != null ? u.getEmail() : u.getPhone());
        }
    }

    private UUID resolveId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof OidcUser u) {
            return userRepository.findByKeycloakId(u.getSubject()).map(User::getId).orElse(null);
        }
        if (principal instanceof User u) return u.getId();
        return null;
    }
}
