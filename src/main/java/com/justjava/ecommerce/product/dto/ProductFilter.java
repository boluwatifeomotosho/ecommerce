package com.justjava.ecommerce.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Bound from query parameters via {@code @ModelAttribute} in controllers.
 * All fields are optional; null means "no filter on this dimension".
 */
public record ProductFilter(
        UUID       categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String     search
) {
    public static ProductFilter empty() {
        return new ProductFilter(null, null, null, null);
    }
}
