package com.justjava.ecommerce.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartDto(
        List<CartItemDto> items,
        BigDecimal subtotal,
        int totalItems,
        boolean empty
) {
    public record CartItemDto(
            UUID productId,
            String productName,
            String productSlug,
            String primaryImageUrl,
            String vendorName,
            BigDecimal price,
            int quantity,
            BigDecimal lineTotal,
            int maxStock,
            boolean inStock
    ) {}
}
