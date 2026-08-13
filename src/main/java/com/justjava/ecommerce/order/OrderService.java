package com.justjava.ecommerce.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {

    /** Creates a PENDING_PAYMENT order from the customer's cart and returns it. */
    Order placeOrder(UUID customerId, CheckoutRequest request);

    /** Marks the order PAID, clears the cart, and returns the order ID. */
    UUID confirmPayment(String paymentReference, String channel);

    /** Marks the order CANCELLED and returns the order ID. */
    UUID cancelPayment(String paymentReference);

    /** Generates a new Paystack reference for a PENDING_PAYMENT or CANCELLED order and returns it. */
    String reinitializePayment(UUID orderId, UUID customerId);

    /** Cancels a PENDING_PAYMENT order by customer request. */
    void cancelOrder(UUID orderId, UUID customerId);

    /** Customer confirms that a DELIVERED order actually reached them, transitioning it to CONFIRMED. */
    void confirmDelivery(UUID orderId, UUID customerId);

    OrderDto getOrderById(UUID orderId, UUID customerId);

    Page<OrderDto> getCustomerOrders(UUID customerId, OrderStatus statusFilter, Pageable pageable);

    Page<OrderDto> getAllOrders(OrderStatus statusFilter, Pageable pageable);

    OrderDto getOrderByIdForAdmin(UUID orderId);

    Page<OrderDto> getVendorOrders(UUID vendorId, OrderStatus statusFilter, Pageable pageable);

    /** Same as {@link #getVendorOrders} but restricted to orders that contain a product created by {@code createdByUserId}. */
    Page<OrderDto> getVendorOrdersForCreator(UUID vendorId, UUID createdByUserId, OrderStatus statusFilter, Pageable pageable);

    OrderDto getOrderByIdForVendor(UUID orderId, UUID vendorId);

    /** Requires the order to contain at least one product created by {@code createdByUserId}. */
    OrderDto getOrderByIdForVendorCreator(UUID orderId, UUID vendorId, UUID createdByUserId);

    void updateOrderStatusByAdmin(UUID orderId, OrderStatus newStatus);

    /** Moves a SHIPPED order to IN_TRANSIT while assigning a warehouse agent. */
    void assignAgentAndDispatch(UUID orderId, UUID agentUserId);

    /** Reassign the agent on an IN_TRANSIT order (admin-only path). */
    void reassignAgent(UUID orderId, UUID agentUserId);

    /** Called by the agent portal to mark their own assigned order DELIVERED. */
    void markDeliveredByAgent(UUID orderId, UUID agentUserId);

    Page<OrderDto> getOrdersForAgent(UUID agentUserId, OrderStatus statusFilter, Pageable pageable);

    OrderDto getOrderByIdForAgent(UUID orderId, UUID agentUserId);

    long countForAgentByStatus(UUID agentUserId, OrderStatus status);

    void updateOrderStatusByVendor(UUID orderId, UUID vendorId, OrderStatus newStatus);

    /** Only permits the update if the order contains a product created by {@code createdByUserId}. */
    void updateOrderStatusByVendorCreator(UUID orderId, UUID vendorId, UUID createdByUserId, OrderStatus newStatus);
}
