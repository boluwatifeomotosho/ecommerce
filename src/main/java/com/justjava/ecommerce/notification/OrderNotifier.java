package com.justjava.ecommerce.notification;

import com.justjava.ecommerce.order.Order;
import com.justjava.ecommerce.order.OrderItem;
import com.justjava.ecommerce.product.Product;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import com.justjava.ecommerce.vendor.Vendor;
import com.justjava.ecommerce.vendor.VendorMember;
import com.justjava.ecommerce.vendor.VendorMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderNotifier {

    private final EmailService           emailService;
    private final UserRepository         userRepository;
    private final VendorMemberRepository vendorMemberRepository;
    private final NotificationService    notificationService;

    @Value("${app.base-url}") private String baseUrl;

    public void notifyOrderPaid(Order order) {
        try {
            List<Map<String, Object>> items = order.getItems().stream()
                    .map(this::toItemView)
                    .toList();

            Map<String, Object> base = new HashMap<>();
            String orderRef = order.getId().toString().substring(0, 8).toUpperCase();
            base.put("orderRef",        orderRef);
            base.put("items",           items);
            base.put("subtotal",        order.getSubtotal());
            base.put("deliveryFee",     order.getDeliveryFee());
            base.put("total",           order.getTotal());
            base.put("shippingName",    order.getShippingName());
            base.put("shippingPhone",   order.getShippingPhone());
            base.put("shippingAddress", order.getShippingAddress());
            base.put("shippingCity",    order.getShippingCity());
            base.put("shippingState",   order.getShippingState());
            base.put("customerName",    order.getCustomer() != null ? order.getCustomer().getFullName() : null);

            // ── Customer ─────────────────────────────────────────────────────
            User customer = order.getCustomer();
            String customerEmail = customer != null ? customer.getEmail() : null;
            String customerOrderUrl = baseUrl + "/customer/orders/" + order.getId();
            if (customerEmail != null && !customerEmail.isBlank()) {
                Map<String, Object> model = new HashMap<>(base);
                model.put("orderUrl", customerOrderUrl);
                emailService.sendHtml(customerEmail,
                        "Your order is confirmed — payment received",
                        "order-paid-customer", model);
            }
            if (customer != null) {
                notificationService.notify(customer.getId(),
                        "ORDER_PAID",
                        "Payment received for order #" + orderRef,
                        "We received your payment. Your order is being prepared.",
                        "/customer/orders/" + order.getId());
            }

            // ── Vendors' team (subsidiary admin + team members) ──────────────
            Set<UUID> vendorIds = new LinkedHashSet<>();
            for (OrderItem item : order.getItems()) {
                Product p = item.getProduct();
                if (p != null && p.getVendor() != null) vendorIds.add(p.getVendor().getId());
            }

            Set<String> teamRecipients = new LinkedHashSet<>();
            Set<UUID>   teamUserIds    = new LinkedHashSet<>();
            for (UUID vendorId : vendorIds) {
                for (VendorMember m : vendorMemberRepository.findByVendorIdWithUser(vendorId)) {
                    if (m.getUser() != null) {
                        if (m.getUser().getEmail() != null) teamRecipients.add(m.getUser().getEmail());
                        teamUserIds.add(m.getUser().getId());
                    }
                }
            }

            if (!teamRecipients.isEmpty()) {
                Map<String, Object> model = new HashMap<>(base);
                model.put("orderUrl",       baseUrl + "/vendor/orders/" + order.getId());
                model.put("recipientLabel", "Your team");
                emailService.sendHtmlToMany(teamRecipients,
                        "New paid order — " + orderRef,
                        "order-paid-team", model);
            }
            notificationService.notifyMany(teamUserIds,
                    "ORDER_PAID",
                    "New paid order — #" + orderRef,
                    "A customer just paid for an order containing your products.",
                    "/vendor/orders/" + order.getId());

            // ── App admins ───────────────────────────────────────────────────
            List<User> admins = userRepository.findAllByRole("ADMIN");
            List<String> adminEmails = admins.stream()
                    .map(User::getEmail)
                    .filter(Objects::nonNull)
                    .filter(e -> !e.isBlank())
                    .toList();

            if (!adminEmails.isEmpty()) {
                Map<String, Object> model = new HashMap<>(base);
                model.put("orderUrl",       baseUrl + "/admin/orders/" + order.getId());
                model.put("recipientLabel", "The platform");
                emailService.sendHtmlToMany(adminEmails,
                        "New paid order on Pinepetosan — " + orderRef,
                        "order-paid-team", model);
            }
            notificationService.notifyMany(
                    admins.stream().map(User::getId).toList(),
                    "ORDER_PAID",
                    "New paid order — #" + orderRef,
                    "A customer just paid for an order.",
                    "/admin/orders/" + order.getId());
        } catch (Exception e) {
            log.warn("Failed to enqueue order-paid notifications for order {}: {}", order.getId(), e.getMessage());
        }
    }

    public void notifyAgentAssigned(Order order) {
        try {
            User agent = order.getAssignedAgent();
            if (agent == null) return;

            String orderRef = order.getId().toString().substring(0, 8).toUpperCase();
            Map<String, Object> model = new HashMap<>();
            model.put("orderRef",        orderRef);
            model.put("agentName",       agent.getFullName());
            model.put("customerName",    order.getShippingName());
            model.put("customerPhone",   order.getShippingPhone());
            model.put("shippingAddress", order.getShippingAddress());
            model.put("shippingCity",    order.getShippingCity());
            model.put("shippingState",   order.getShippingState());
            model.put("total",           order.getTotal());
            model.put("items",           order.getItems().stream().map(this::toItemView).toList());
            model.put("orderUrl",        baseUrl + "/agent/deliveries/" + order.getId());

            if (agent.getEmail() != null && !agent.getEmail().isBlank()) {
                emailService.sendHtml(agent.getEmail(),
                        "New delivery assigned — order #" + orderRef,
                        "agent-assigned", model);
            }

            notificationService.notify(agent.getId(),
                    "DELIVERY_ASSIGNED",
                    "New delivery assigned — #" + orderRef,
                    "You have been assigned a new delivery to " + order.getShippingCity() + ".",
                    "/agent/deliveries/" + order.getId());
        } catch (Exception e) {
            log.warn("Failed to notify agent for order {}: {}", order.getId(), e.getMessage());
        }
    }

    /**
     * Fired when an in-transit order is handed off to a different agent, so
     * the previous agent knows their link to the delivery is now dead.
     */
    public void notifyAgentReassigned(Order order, User previousAgent) {
        if (previousAgent == null) return;
        try {
            String orderRef = order.getId().toString().substring(0, 8).toUpperCase();
            notificationService.notify(previousAgent.getId(),
                    "DELIVERY_REASSIGNED",
                    "Delivery reassigned — #" + orderRef,
                    "This delivery was handed off to another agent and is no longer on your route.",
                    "/agent/deliveries");
        } catch (Exception e) {
            log.warn("Failed to notify previous agent for order {}: {}", order.getId(), e.getMessage());
        }
    }

    /**
     * Fired when the assigned agent marks an order as physically delivered.
     * Prompts the customer to confirm receipt and lets the vendor team + admins know.
     */
    public void notifyOrderDelivered(Order order) {
        try {
            String orderRef = order.getId().toString().substring(0, 8).toUpperCase();

            User customer = order.getCustomer();
            if (customer != null) {
                notificationService.notify(customer.getId(),
                        "ORDER_DELIVERED",
                        "Order #" + orderRef + " delivered — please confirm",
                        "Your delivery agent marked this order as delivered. Confirm receipt to close it out.",
                        "/customer/orders/" + order.getId());
            }

            Set<UUID> teamUserIds = collectVendorTeamUserIds(order);
            notificationService.notifyMany(teamUserIds,
                    "ORDER_DELIVERED",
                    "Order #" + orderRef + " delivered",
                    "The assigned agent marked this order as delivered. Awaiting customer confirmation.",
                    "/vendor/orders/" + order.getId());

            List<User> admins = userRepository.findAllByRole("ADMIN");
            notificationService.notifyMany(
                    admins.stream().map(User::getId).toList(),
                    "ORDER_DELIVERED",
                    "Order #" + orderRef + " delivered",
                    "Agent marked delivered. Awaiting customer confirmation.",
                    "/admin/orders/" + order.getId());
        } catch (Exception e) {
            log.warn("Failed to enqueue order-delivered notifications for order {}: {}", order.getId(), e.getMessage());
        }
    }

    /**
     * Fired when the customer confirms they received the order.
     * Closes the loop for the agent, vendor team, and admins.
     */
    public void notifyOrderConfirmed(Order order) {
        try {
            String orderRef = order.getId().toString().substring(0, 8).toUpperCase();

            User agent = order.getAssignedAgent();
            if (agent != null) {
                notificationService.notify(agent.getId(),
                        "ORDER_CONFIRMED",
                        "Delivery confirmed — #" + orderRef,
                        "The customer confirmed they received this order. Nice work!",
                        "/agent/deliveries/" + order.getId());
            }

            Set<UUID> teamUserIds = collectVendorTeamUserIds(order);
            notificationService.notifyMany(teamUserIds,
                    "ORDER_CONFIRMED",
                    "Order #" + orderRef + " confirmed by customer",
                    "The customer confirmed receipt. This order is now complete.",
                    "/vendor/orders/" + order.getId());

            List<User> admins = userRepository.findAllByRole("ADMIN");
            notificationService.notifyMany(
                    admins.stream().map(User::getId).toList(),
                    "ORDER_CONFIRMED",
                    "Order #" + orderRef + " confirmed by customer",
                    "Customer confirmed receipt. Order complete.",
                    "/admin/orders/" + order.getId());
        } catch (Exception e) {
            log.warn("Failed to enqueue order-confirmed notifications for order {}: {}", order.getId(), e.getMessage());
        }
    }

    private Set<UUID> collectVendorTeamUserIds(Order order) {
        Set<UUID> vendorIds = new LinkedHashSet<>();
        for (OrderItem item : order.getItems()) {
            Product p = item.getProduct();
            if (p != null && p.getVendor() != null) vendorIds.add(p.getVendor().getId());
        }
        Set<UUID> teamUserIds = new LinkedHashSet<>();
        for (UUID vendorId : vendorIds) {
            for (VendorMember m : vendorMemberRepository.findByVendorIdWithUser(vendorId)) {
                if (m.getUser() != null) teamUserIds.add(m.getUser().getId());
            }
        }
        return teamUserIds;
    }

    private Map<String, Object> toItemView(OrderItem item) {
        Map<String, Object> m = new HashMap<>();
        m.put("productName", item.getProductName());
        m.put("vendorName",  vendorNameOf(item));
        m.put("unitPrice",   item.getUnitPrice());
        m.put("quantity",    item.getQuantity());
        m.put("lineTotal",   item.getLineTotal() != null ? item.getLineTotal()
                : (item.getUnitPrice() != null ? item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())) : BigDecimal.ZERO));
        return m;
    }

    private String vendorNameOf(OrderItem item) {
        if (item.getVendorName() != null) return item.getVendorName();
        Product p = item.getProduct();
        if (p != null && p.getVendor() != null) {
            Vendor v = p.getVendor();
            return v.getName();
        }
        return null;
    }
}
