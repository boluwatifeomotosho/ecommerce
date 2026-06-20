package com.justjava.ecommerce.product.dto;

import com.justjava.ecommerce.product.ProductStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummaryDto(
        UUID          id,
        String        name,
        String        slug,
        BigDecimal    price,
        BigDecimal    compareAtPrice,
        String        primaryImageUrl,
        String        categoryName,
        String        vendorName,
        int           stockQuantity,
        ProductStatus status,
        String        rejectionReason,
        Double        averageRating,
        int           reviewCount
) {
    public boolean isOnSale() {
        return compareAtPrice != null && compareAtPrice.compareTo(price) > 0;
    }

    public boolean isInStock() {
        return stockQuantity > 0;
    }
}
