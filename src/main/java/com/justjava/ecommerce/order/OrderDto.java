package com.justjava.ecommerce.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDto(
        UUID          id,
        OrderStatus   status,
        BigDecimal    subtotal,
        BigDecimal    deliveryFee,
        BigDecimal    total,
        String        shippingName,
        String        shippingPhone,
        String        shippingAddress,
        String        shippingCity,
        String        shippingState,
        String        paymentReference,
        String        paymentChannel,
        LocalDateTime paidAt,
        LocalDateTime createdAt,
        String        customerName,
        List<ItemDto> items
) {
    public record ItemDto(
            UUID       productId,
            String     productName,
            String     vendorName,
            BigDecimal unitPrice,
            int        quantity,
            BigDecimal lineTotal
    ) {}
}
