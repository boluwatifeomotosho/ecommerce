package com.justjava.ecommerce.mobile;

import com.justjava.ecommerce.address.AddressForm;
import com.justjava.ecommerce.address.CustomerAddressService;
import com.justjava.ecommerce.auth.KeycloakAdminService;
import com.justjava.ecommerce.cart.CartService;
import com.justjava.ecommerce.category.CategoryDto;
import com.justjava.ecommerce.category.CategoryService;
import com.justjava.ecommerce.order.*;
import com.justjava.ecommerce.product.ProductQueryService;
import com.justjava.ecommerce.product.dto.ProductFilter;
import com.justjava.ecommerce.review.ReviewService;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import com.justjava.ecommerce.wishlist.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/mobile")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class MobileController {

    private static final int PAGE_SIZE = 20;

    private final CartService            cartService;
    private final OrderService           orderService;
    private final PaystackService        paystackService;
    private final ProductQueryService    productQueryService;
    private final CategoryService        categoryService;
    private final ReviewService          reviewService;
    private final WishlistService        wishlistService;
    private final CustomerAddressService addressService;
    private final UserRepository         userRepository;
    private final KeycloakAdminService   keycloakAdminService;

    @Value("${app.base-url}")
    private String baseUrl;

    // ── Home ─────────────────────────────────────────────────────────────────

    @GetMapping({"", "/"})
    public String home(Authentication auth, Model model) {
        var featured = productQueryService.getPublishedProducts(
                ProductFilter.empty(),
                PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "publishedAt"))
        );
        model.addAttribute("featuredProducts", featured.getContent());
        model.addAttribute("categories",       categoryService.getRootCategories());
        addCommon(auth, model, null);
        return "mobile/home";
    }

    // ── Shop ─────────────────────────────────────────────────────────────────

    @GetMapping("/shop")
    public String shop(
            @ModelAttribute ProductFilter filter,
            @RequestParam(defaultValue = "0")      int    page,
            @RequestParam(defaultValue = "newest") String sort,
            Authentication auth, Model model
    ) {
        var pageable = PageRequest.of(page, PAGE_SIZE, resolveSort(sort));
        var products = productQueryService.getPublishedProducts(filter, pageable);

        List<CategoryDto> allCategories  = categoryService.getAllActive();
        List<CategoryDto> rootCategories = allCategories.stream().filter(c -> c.parentId() == null).toList();
        Map<UUID, List<CategoryDto>> subMap = allCategories.stream()
                .filter(c -> c.parentId() != null)
                .collect(Collectors.groupingBy(CategoryDto::parentId));

        UUID selectedRootId = null;
        if (filter.categoryId() != null) {
            if (rootCategories.stream().anyMatch(c -> c.id().equals(filter.categoryId()))) {
                selectedRootId = filter.categoryId();
            } else {
                CategoryDto sel = allCategories.stream()
                        .filter(c -> c.id().equals(filter.categoryId())).findFirst().orElse(null);
                if (sel != null) selectedRootId = sel.parentId();
            }
        }

        model.addAttribute("products",       products);
        model.addAttribute("filter",         filter);
        model.addAttribute("sort",           sort);
        model.addAttribute("rootCategories", rootCategories);
        model.addAttribute("subCategoryMap", subMap);
        model.addAttribute("selectedRootId", selectedRootId);
        addCommon(auth, model, null);
        return "mobile/shop";
    }

    @GetMapping("/shop/{slug}")
    public String productDetail(@PathVariable String slug, Authentication auth, Model model) {
        var product     = productQueryService.getPublishedProductBySlug(slug);
        UUID customerId = resolveId(auth);
        model.addAttribute("product",      product);
        model.addAttribute("reviews",      reviewService.getReviewsForProduct(product.id()));
        model.addAttribute("isWishlisted", wishlistService.isWishlisted(customerId, product.id()));
        addCommon(auth, model, null);
        return "mobile/product-detail";
    }

    // ── Cart ─────────────────────────────────────────────────────────────────

    @GetMapping("/cart")
    public String cart(Authentication auth, Model model) {
        model.addAttribute("cart", cartService.getCart(resolveId(auth)));
        addCommon(auth, model, null);
        return "mobile/cart";
    }

    @PostMapping("/cart/items")
    public String addToCart(
            @RequestParam UUID productId,
            @RequestParam(defaultValue = "1") int quantity,
            @RequestParam(defaultValue = "/mobile/cart") String returnUrl,
            Authentication auth, RedirectAttributes flash
    ) {
        try {
            cartService.addItem(resolveId(auth), productId, quantity);
            flash.addFlashAttribute("cartSuccess", "Added to cart.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("cartError", e.getMessage());
        }
        return "redirect:" + returnUrl;
    }

    @PostMapping("/cart/items/{productId}/quantity")
    public String updateQuantity(
            @PathVariable UUID productId,
            @RequestParam int quantity,
            Authentication auth, RedirectAttributes flash
    ) {
        try {
            cartService.updateQuantity(resolveId(auth), productId, quantity);
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("cartError", e.getMessage());
        }
        return "redirect:/mobile/cart";
    }

    @PostMapping("/cart/items/{productId}/remove")
    public String removeFromCart(@PathVariable UUID productId, Authentication auth) {
        cartService.removeItem(resolveId(auth), productId);
        return "redirect:/mobile/cart";
    }

    // ── Checkout ─────────────────────────────────────────────────────────────

    @GetMapping("/checkout")
    public String checkoutPage(Authentication auth, Model model) {
        UUID customerId = resolveId(auth);
        var cart = cartService.getCart(customerId);
        if (cart.empty()) return "redirect:/mobile/cart";

        var savedAddresses = addressService.getAddresses(customerId);
        var req = new CheckoutRequest();
        addressService.getDefaultAddress(customerId).ifPresent(a -> {
            req.setShippingName(a.getRecipientName());
            req.setShippingPhone(a.getPhone());
            req.setShippingAddress(a.getAddressLine());
            req.setShippingCity(a.getCity());
            req.setShippingState(a.getState());
        });

        model.addAttribute("cart",           cart);
        model.addAttribute("request",        req);
        model.addAttribute("savedAddresses", savedAddresses);
        addCommon(auth, model, null);
        return "mobile/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(
            @Valid @ModelAttribute("request") CheckoutRequest request,
            BindingResult br, Authentication auth, Model model
    ) {
        UUID customerId = resolveId(auth);
        if (br.hasErrors()) {
            model.addAttribute("cart",           cartService.getCart(customerId));
            model.addAttribute("savedAddresses", addressService.getAddresses(customerId));
            addCommon(auth, model, null);
            return "mobile/checkout";
        }

        Order order = orderService.placeOrder(customerId, request);

        if (request.isSaveAddress()) {
            try {
                AddressForm af = new AddressForm();
                af.setRecipientName(request.getShippingName());
                af.setPhone(request.getShippingPhone());
                af.setAddressLine(request.getShippingAddress());
                af.setCity(request.getShippingCity());
                af.setState(request.getShippingState());
                addressService.addAddress(customerId, af, false);
            } catch (Exception ignored) {}
        }

        String reference   = order.getPaymentReference();
        String callbackUrl = baseUrl + "/mobile/checkout/callback";
        String paystackUrl = paystackService.initializeTransaction(
                resolveEmail(auth), order.getTotal(), reference, callbackUrl);
        return "redirect:" + paystackUrl;
    }

    @GetMapping("/checkout/callback")
    public String paystackCallback(@RequestParam String reference, RedirectAttributes flash) {
        try {
            var verification = paystackService.verifyTransaction(reference);
            if (verification.success()) {
                UUID orderId = orderService.confirmPayment(reference, verification.channel());
                return "redirect:/mobile/orders/" + orderId;
            } else {
                UUID orderId = orderService.cancelPayment(reference);
                flash.addFlashAttribute("paymentError", "Payment was declined or cancelled. You can retry below.");
                return "redirect:/mobile/orders/" + orderId;
            }
        } catch (Exception e) {
            log.error("Mobile payment callback failed for reference: {}", reference, e);
            flash.addFlashAttribute("paymentError", "Could not verify payment. Please check your orders.");
            return "redirect:/mobile/orders";
        }
    }

    // ── Orders ────────────────────────────────────────────────────────────────

    @GetMapping("/orders")
    public String orders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            Authentication auth, Model model
    ) {
        UUID customerId = resolveId(auth);
        OrderStatus statusFilter = null;
        if (status != null && !status.isBlank()) {
            try { statusFilter = OrderStatus.valueOf(status); } catch (IllegalArgumentException ignored) {}
        }
        var orders = orderService.getCustomerOrders(customerId, statusFilter, PageRequest.of(page, 10));
        model.addAttribute("orders",       orders);
        model.addAttribute("activeStatus", status);
        addCommon(auth, model, null);
        return "mobile/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable UUID id, Authentication auth, Model model) {
        UUID customerId = resolveId(auth);
        OrderDto order = orderService.getOrderById(id, customerId);
        model.addAttribute("order", order);
        if (order.status() == OrderStatus.DELIVERED) {
            model.addAttribute("reviewedProductIds",
                    reviewService.getReviewedProductIds(customerId, id));
        }
        addCommon(auth, model, null);
        return "mobile/order-detail";
    }

    @PostMapping("/orders/{id}/pay")
    public String retryPayment(@PathVariable UUID id, Authentication auth, RedirectAttributes flash) {
        UUID customerId = resolveId(auth);
        try {
            String newRef  = orderService.reinitializePayment(id, customerId);
            OrderDto order = orderService.getOrderById(id, customerId);
            String callbackUrl = baseUrl + "/mobile/checkout/callback";
            String paystackUrl = paystackService.initializeTransaction(
                    resolveEmail(auth), order.total(), newRef, callbackUrl);
            return "redirect:" + paystackUrl;
        } catch (Exception e) {
            flash.addFlashAttribute("paymentError", e.getMessage());
            return "redirect:/mobile/orders/" + id;
        }
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable UUID id, Authentication auth, RedirectAttributes flash) {
        try {
            orderService.cancelOrder(id, resolveId(auth));
            flash.addFlashAttribute("successMessage", "Order cancelled.");
        } catch (Exception e) {
            flash.addFlashAttribute("paymentError", e.getMessage());
        }
        return "redirect:/mobile/orders/" + id;
    }

    // ── Wishlist ──────────────────────────────────────────────────────────────

    @GetMapping("/wishlist")
    public String wishlist(Authentication auth, Model model) {
        model.addAttribute("wishlistItems", wishlistService.getWishlist(resolveId(auth)));
        addCommon(auth, model, null);
        return "mobile/wishlist";
    }

    @PostMapping("/wishlist/toggle")
    public String toggleWishlist(
            @RequestParam UUID productId,
            @RequestParam(defaultValue = "/mobile/wishlist") String returnUrl,
            Authentication auth, RedirectAttributes flash
    ) {
        boolean added = wishlistService.toggle(resolveId(auth), productId);
        flash.addFlashAttribute("wishlistMessage", added ? "Added to wishlist!" : "Removed from wishlist.");
        return "redirect:" + returnUrl;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        UUID customerId = resolveId(auth);
        var activeStatuses = EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED);
        model.addAttribute("activeOrderCount", orderService.getCustomerOrders(customerId, null,
                PageRequest.of(0, 1)).getTotalElements());
        model.addAttribute("wishlistCount",    wishlistService.getWishlistCount(customerId));
        var recentOrders = orderService.getCustomerOrders(customerId, null, PageRequest.of(0, 5));
        model.addAttribute("recentOrders", recentOrders.getContent());
        addCommon(auth, model, null);
        return "mobile/dashboard";
    }

    // ── Settings ──────────────────────────────────────────────────────────────

    @GetMapping("/settings")
    public String settings(Authentication auth, Model model) {
        UUID customerId = resolveId(auth);
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        model.addAttribute("user",         user);
        model.addAttribute("addresses",    addressService.getAddresses(customerId));
        model.addAttribute("addressForm",  new AddressForm());
        model.addAttribute("maxAddresses", CustomerAddressService.MAX_ADDRESSES);
        addCommon(auth, model, null);
        return "mobile/settings";
    }

    @PostMapping("/settings/name")
    public String updateName(@RequestParam String fullName, Authentication auth, RedirectAttributes flash) {
        if (fullName == null || fullName.isBlank()) {
            flash.addFlashAttribute("nameError", "Name cannot be blank.");
            return "redirect:/mobile/settings";
        }
        String trimmed = fullName.trim();
        try {
            User user = userRepository.findById(resolveId(auth))
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            user.setFullName(trimmed);
            userRepository.save(user);
            int idx   = trimmed.indexOf(' ');
            String f  = idx > 0 ? trimmed.substring(0, idx) : trimmed;
            String l  = idx > 0 ? trimmed.substring(idx + 1) : "";
            keycloakAdminService.updateUserName(resolveKeycloakId(auth), f, l);
            flash.addFlashAttribute("nameSuccess", "Name updated.");
        } catch (Exception e) {
            flash.addFlashAttribute("nameError", "Could not update name. Please try again.");
        }
        return "redirect:/mobile/settings";
    }

    @PostMapping("/settings/addresses/add")
    public String addAddress(
            @Valid @ModelAttribute("addressForm") AddressForm form,
            BindingResult br, Authentication auth, RedirectAttributes flash
    ) {
        if (br.hasErrors()) {
            flash.addFlashAttribute("addressError", "Please fill in all address fields correctly.");
            return "redirect:/mobile/settings";
        }
        try {
            addressService.addAddress(resolveId(auth), form, false);
            flash.addFlashAttribute("addressSuccess", "Address added.");
        } catch (Exception e) {
            flash.addFlashAttribute("addressError", e.getMessage());
        }
        return "redirect:/mobile/settings";
    }

    @PostMapping("/settings/addresses/{id}/update")
    public String updateAddress(
            @PathVariable UUID id,
            @Valid @ModelAttribute("addressForm") AddressForm form,
            BindingResult br, Authentication auth, RedirectAttributes flash
    ) {
        if (br.hasErrors()) {
            flash.addFlashAttribute("addressError", "Please fill in all fields correctly.");
            return "redirect:/mobile/settings";
        }
        try {
            addressService.updateAddress(resolveId(auth), id, form);
            flash.addFlashAttribute("addressSuccess", "Address updated.");
        } catch (Exception e) {
            flash.addFlashAttribute("addressError", e.getMessage());
        }
        return "redirect:/mobile/settings";
    }

    @PostMapping("/settings/addresses/{id}/default")
    public String setDefaultAddress(@PathVariable UUID id, Authentication auth, RedirectAttributes flash) {
        try {
            addressService.setDefault(resolveId(auth), id);
            flash.addFlashAttribute("addressSuccess", "Default address updated.");
        } catch (Exception e) {
            flash.addFlashAttribute("addressError", e.getMessage());
        }
        return "redirect:/mobile/settings";
    }

    @PostMapping("/settings/addresses/{id}/delete")
    public String deleteAddress(@PathVariable UUID id, Authentication auth, RedirectAttributes flash) {
        try {
            addressService.delete(resolveId(auth), id);
            flash.addFlashAttribute("addressSuccess", "Address removed.");
        } catch (Exception e) {
            flash.addFlashAttribute("addressError", e.getMessage());
        }
        return "redirect:/mobile/settings";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void addCommon(Authentication auth, Model model, String activePage) {
        UUID customerId = resolveId(auth);
        model.addAttribute("name",          resolveName(auth));
        model.addAttribute("cartItemCount", cartService.getCartItemCount(customerId));
        if (activePage != null) model.addAttribute("activePage", activePage);
    }

    private Sort resolveSort(String sort) {
        return switch (sort) {
            case "price-asc"  -> Sort.by("price").ascending();
            case "price-desc" -> Sort.by("price").descending();
            case "name"       -> Sort.by("name").ascending();
            default           -> Sort.by("createdAt").descending();
        };
    }

    private UUID resolveId(Authentication auth) {
        Object p = auth.getPrincipal();
        if (p instanceof OidcUser u) {
            return userRepository.findByKeycloakId(u.getSubject())
                    .orElseThrow(() -> new IllegalStateException("User not found")).getId();
        }
        if (p instanceof User u) return u.getId();
        throw new IllegalStateException("Unsupported principal");
    }

    private String resolveName(Authentication auth) {
        Object p = auth.getPrincipal();
        if (p instanceof OidcUser u) return u.getFullName() != null ? u.getFullName() : u.getEmail();
        if (p instanceof User u)     return u.getFullName() != null ? u.getFullName() : "Customer";
        return "Customer";
    }

    private String resolveEmail(Authentication auth) {
        Object p = auth.getPrincipal();
        if (p instanceof OidcUser u) return u.getEmail();
        if (p instanceof User u)     return u.getEmail() != null ? u.getEmail() : u.getPhone() + "@naija.mart";
        return "customer@naija.mart";
    }

    private String resolveKeycloakId(Authentication auth) {
        Object p = auth.getPrincipal();
        if (p instanceof OidcUser u) return u.getSubject();
        if (p instanceof User u) return u.getKeycloakId();
        throw new IllegalStateException("Unsupported principal");
    }
}
