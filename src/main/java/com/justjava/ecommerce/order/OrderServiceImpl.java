package com.justjava.ecommerce.order;

import com.justjava.ecommerce.address.CustomerAddressService;
import com.justjava.ecommerce.cart.CartItemRepository;
import com.justjava.ecommerce.cart.CartService;
import com.justjava.ecommerce.product.Product;
import com.justjava.ecommerce.product.ProductRepository;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal DELIVERY_FEE = new BigDecimal("1500.00");
    private static final Set<OrderStatus> RETRYABLE = EnumSet.of(OrderStatus.PENDING_PAYMENT, OrderStatus.CANCELLED);
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.PAID,       Set.of(OrderStatus.PROCESSING),
            OrderStatus.PROCESSING, Set.of(OrderStatus.SHIPPED),
            OrderStatus.SHIPPED,    Set.of(OrderStatus.DELIVERED)
    );

    private final OrderRepository          orderRepository;
    private final CartItemRepository       cartItemRepository;
    private final CartService              cartService;
    private final UserRepository           userRepository;
    private final ProductRepository        productRepository;
    private final CustomerAddressService   addressService;

    @Override
    public Order placeOrder(UUID customerId, CheckoutRequest req) {
        var cartItems = cartItemRepository.findByCustomerIdWithProducts(customerId);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty cart");
        }

        User customer = userRepository.getReferenceById(customerId);

        BigDecimal subtotal = cartItems.stream()
                .map(ci -> ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = subtotal.add(DELIVERY_FEE);

        // Generate reference before save so it is included in the initial INSERT, not a subsequent UPDATE
        String paymentReference = UUID.randomUUID().toString();

        Order order = Order.builder()
                .customer(customer)
                .status(OrderStatus.PENDING_PAYMENT)
                .paymentReference(paymentReference)
                .subtotal(subtotal)
                .deliveryFee(DELIVERY_FEE)
                .total(total)
                .shippingName(req.getShippingName())
                .shippingPhone(req.getShippingPhone())
                .shippingAddress(req.getShippingAddress())
                .shippingCity(req.getShippingCity())
                .shippingState(req.getShippingState())
                .build();

        List<OrderItem> items = cartItems.stream().map(ci -> {
            var p = ci.getProduct();
            BigDecimal lineTotal = p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            String vendorName = p.getVendor() != null ? p.getVendor().getFullName() : null;
            return OrderItem.builder()
                    .order(order)
                    .product(p)
                    .productName(p.getName())
                    .vendorName(vendorName)
                    .unitPrice(p.getPrice())
                    .quantity(ci.getQuantity())
                    .lineTotal(lineTotal)
                    .build();
        }).toList();

        order.getItems().addAll(items);
        Order saved = orderRepository.save(order);

        // Cart is intentionally NOT cleared here — only cleared after payment succeeds
        return saved;
    }

    @Override
    public UUID confirmPayment(String paymentReference, String channel) {
        Order order = orderRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new IllegalArgumentException("Order not found for reference: " + paymentReference));

        if (!RETRYABLE.contains(order.getStatus())) return order.getId();

        order.setStatus(OrderStatus.PAID);
        order.setPaymentChannel(channel);
        order.setPaidAt(LocalDateTime.now());

        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                productRepository.decrementStock(item.getProduct().getId(), item.getQuantity());
            }
        }

        UUID customerId = order.getCustomer().getId();
        cartService.clearCart(customerId);

        // Auto-save shipping address as the customer's first saved address
        addressService.autoSaveFromCheckout(
                customerId,
                order.getShippingName(),
                order.getShippingPhone(),
                order.getShippingAddress(),
                order.getShippingCity(),
                order.getShippingState()
        );

        return order.getId();
    }

    @Override
    public UUID cancelPayment(String paymentReference) {
        Order order = orderRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new IllegalArgumentException("Order not found for reference: " + paymentReference));
        if (RETRYABLE.contains(order.getStatus())) {
            order.setStatus(OrderStatus.CANCELLED);
        }
        return order.getId();
    }

    @Override
    public void cancelOrder(UUID orderId, UUID customerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Order does not belong to this customer");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Only orders awaiting payment can be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
    }

    @Override
    public String reinitializePayment(UUID orderId, UUID customerId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Order does not belong to this customer");
        }
        if (!RETRYABLE.contains(order.getStatus())) {
            throw new IllegalStateException("Order cannot be retried in status: " + order.getStatus());
        }

        // Verify every item still has sufficient stock before allowing retry
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product == null || product.getStockQuantity() < item.getQuantity()) {
                String name = product != null ? product.getName() : item.getProductName();
                throw new IllegalStateException(
                        "\"" + name + "\" is out of stock and can no longer be ordered. Please contact support.");
            }
        }

        // Generate a fresh reference so Paystack accepts it as a new transaction
        String newReference = order.getId().toString() + "-r" + System.currentTimeMillis();
        order.setPaymentReference(newReference);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        return newReference;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(UUID orderId, UUID customerId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Order does not belong to this customer");
        }
        return toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getCustomerOrders(UUID customerId, OrderStatus statusFilter, Pageable pageable) {
        Page<Order> page = statusFilter != null
                ? orderRepository.findByCustomerIdAndStatusOrderByCreatedAtDesc(customerId, statusFilter, pageable)
                : orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId, pageable);
        return page.map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(OrderStatus statusFilter, Pageable pageable) {
        Page<Order> page = statusFilter != null
                ? orderRepository.findByStatus(statusFilter, pageable)
                : orderRepository.findAll(pageable);
        return page.map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderByIdForAdmin(UUID orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getVendorOrders(UUID vendorId, OrderStatus statusFilter, Pageable pageable) {
        Page<Order> page = statusFilter != null
                ? orderRepository.findByVendorIdAndStatus(vendorId, statusFilter, pageable)
                : orderRepository.findByVendorId(vendorId, pageable);
        return page.map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderByIdForVendor(UUID orderId, UUID vendorId) {
        Order order = orderRepository.findByIdAndVendorId(orderId, vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found or not associated with your products"));
        return toDto(order);
    }

    @Override
    public void updateOrderStatusByAdmin(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        validateTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);
    }

    @Override
    public void updateOrderStatusByVendor(UUID orderId, UUID vendorId, OrderStatus newStatus) {
        Order order = orderRepository.findByIdAndVendorId(orderId, vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found or not associated with your products"));
        validateTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);
    }

    private void validateTransition(OrderStatus current, OrderStatus target) {
        Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(target)) {
            throw new IllegalStateException("Cannot move order from " + current + " to " + target);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private OrderDto toDto(Order o) {
        List<OrderDto.ItemDto> items = o.getItems().stream()
                .map(i -> new OrderDto.ItemDto(
                        i.getProduct() != null ? i.getProduct().getId() : null,
                        i.getProductName(), i.getVendorName(),
                        i.getUnitPrice(), i.getQuantity(), i.getLineTotal()))
                .toList();
        String customerName = o.getCustomer() != null ? o.getCustomer().getFullName() : null;
        return new OrderDto(
                o.getId(), o.getStatus(),
                o.getSubtotal(), o.getDeliveryFee(), o.getTotal(),
                o.getShippingName(), o.getShippingPhone(),
                o.getShippingAddress(), o.getShippingCity(), o.getShippingState(),
                o.getPaymentReference(), o.getPaymentChannel(), o.getPaidAt(),
                o.getCreatedAt(), customerName, items);
    }
}
