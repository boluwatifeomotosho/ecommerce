package com.justjava.ecommerce.order;

import com.justjava.ecommerce.cart.CartService;
import com.justjava.ecommerce.review.ReviewService;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/customer")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService     cartService;
    private final OrderService    orderService;
    private final PaystackService paystackService;
    private final UserRepository  userRepository;
    private final ReviewService   reviewService;

    @Value("${app.base-url}")
    private String baseUrl;

    // ── Checkout page ─────────────────────────────────────────────────────────

    @GetMapping("/checkout")
    public String checkoutPage(Authentication auth, Model model) {
        UUID customerId = resolveId(auth);
        var cart = cartService.getCart(customerId);

        if (cart.empty()) return "redirect:/customer/cart";

        model.addAttribute("cart",    cart);
        model.addAttribute("request", new CheckoutRequest());
        model.addAttribute("name",    resolveName(auth));
        return "customer/checkout";
    }

    // ── Place order → redirect to Paystack ───────────────────────────────────

    @PostMapping("/checkout")
    public String placeOrder(
            @Valid @ModelAttribute("request") CheckoutRequest request,
            BindingResult bindingResult,
            Authentication auth,
            Model model
    ) {
        UUID customerId = resolveId(auth);

        if (bindingResult.hasErrors()) {
            model.addAttribute("cart", cartService.getCart(customerId));
            model.addAttribute("name", resolveName(auth));
            return "customer/checkout";
        }

        Order order = orderService.placeOrder(customerId, request);
        return redirectToPaystack(order, auth);
    }

    // ── Paystack callback ─────────────────────────────────────────────────────

    @GetMapping("/checkout/callback")
    public String paystackCallback(
            @RequestParam String reference,
            RedirectAttributes flash
    ) {
        try {
            var verification = paystackService.verifyTransaction(reference);
            if (verification.success()) {
                UUID orderId = orderService.confirmPayment(reference, verification.channel());
                return "redirect:/customer/orders/" + orderId;
            } else {
                UUID orderId = orderService.cancelPayment(reference);
                flash.addFlashAttribute("paymentError", "Payment was declined or cancelled. You can retry below.");
                return "redirect:/customer/orders/" + orderId;
            }
        } catch (Exception e) {
            log.error("Payment callback failed for reference: {}", reference, e);
            flash.addFlashAttribute("paymentError", "Could not verify payment. Please check your orders or contact support.");
            return "redirect:/customer/orders";
        }
    }

    // ── Order detail (status-aware: paid / pending / cancelled) ──────────────

    @GetMapping("/orders/{id}")
    public String orderDetail(
            @PathVariable UUID id,
            Authentication auth,
            Model model
    ) {
        UUID customerId = resolveId(auth);
        OrderDto order = orderService.getOrderById(id, customerId);
        model.addAttribute("order", order);
        model.addAttribute("name",  resolveName(auth));
        if (order.status() == OrderStatus.DELIVERED) {
            model.addAttribute("reviewedProductIds",
                    reviewService.getReviewedProductIds(customerId, id));
        }
        return "customer/order-detail";
    }

    // ── Retry payment for PENDING_PAYMENT or CANCELLED orders ────────────────

    @PostMapping("/orders/{id}/pay")
    public String retryPayment(
            @PathVariable UUID id,
            Authentication auth,
            RedirectAttributes flash
    ) {
        UUID customerId = resolveId(auth);
        try {
            String newReference = orderService.reinitializePayment(id, customerId);
            // Fetch updated order to get current total
            OrderDto order = orderService.getOrderById(id, customerId);
            String callbackUrl = baseUrl + "/customer/checkout/callback";
            String paystackUrl = paystackService.initializeTransaction(
                    resolveEmail(auth), order.total(), newReference, callbackUrl);
            return "redirect:" + paystackUrl;
        } catch (Exception e) {
            flash.addFlashAttribute("paymentError", e.getMessage());
            return "redirect:/customer/orders/" + id;
        }
    }

    // ── Cancel a PENDING_PAYMENT order ────────────────────────────────────────

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(
            @PathVariable UUID id,
            @RequestHeader(value = "HX-Request", required = false) String htmxRequest,
            Authentication auth,
            Model model,
            RedirectAttributes flash
    ) {
        UUID customerId = resolveId(auth);

        if (htmxRequest != null) {
            // HTMX path: cancel (best-effort) then return the updated card fragment
            try { orderService.cancelOrder(id, customerId); } catch (Exception ignored) {}
            model.addAttribute("order", orderService.getOrderById(id, customerId));
            return "customer/order-card :: orderCard";
        }

        // Regular form submit path: redirect with flash
        try {
            orderService.cancelOrder(id, customerId);
            flash.addFlashAttribute("successMessage", "Your order has been cancelled.");
        } catch (Exception e) {
            flash.addFlashAttribute("paymentError", e.getMessage());
        }
        return "redirect:/customer/orders/" + id;
    }

    // ── Order history ─────────────────────────────────────────────────────────

    @GetMapping("/orders")
    public String orderHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            Authentication auth,
            Model model
    ) {
        UUID customerId = resolveId(auth);

        OrderStatus statusFilter = null;
        if (status != null && !status.isBlank()) {
            try { statusFilter = OrderStatus.valueOf(status); } catch (IllegalArgumentException ignored) {}
        }

        var orders = orderService.getCustomerOrders(customerId, statusFilter, PageRequest.of(page, 10));
        model.addAttribute("orders",       orders);
        model.addAttribute("name",         resolveName(auth));
        model.addAttribute("activeStatus", status);
        return "customer/orders";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String redirectToPaystack(Order order, Authentication auth) {
        String reference   = order.getPaymentReference();
        String callbackUrl = baseUrl + "/customer/checkout/callback";
        String paystackUrl = paystackService.initializeTransaction(
                resolveEmail(auth), order.getTotal(), reference, callbackUrl);
        return "redirect:" + paystackUrl;
    }

    private UUID resolveId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof OidcUser u) {
            return userRepository.findByKeycloakId(u.getSubject())
                    .orElseThrow(() -> new IllegalStateException("User not found"))
                    .getId();
        }
        if (principal instanceof User u) return u.getId();
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
}
