package com.justjava.ecommerce.dto;

import com.justjava.ecommerce.model.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProductDetailDto(
        UUID              id,
        String            name,
        String            slug,
        String            description,
        BigDecimal        price,
        BigDecimal        compareAtPrice,
        String            sku,
        int               stockQuantity,
        Integer           weightGrams,
        ProductStatus     status,
        String            rejectionReason,
        CategoryDto       category,
        List<ProductImageDto> images,
        UUID              vendorId,
        String            vendorName,
        LocalDateTime     createdAt,
        LocalDateTime     updatedAt,
        LocalDateTime     publishedAt
) {
    public boolean isOnSale() {
        return compareAtPrice != null && compareAtPrice.compareTo(price) > 0;
    }

    public boolean isInStock() {
        return stockQuantity > 0;
    }

    public String primaryImageUrl() {
        return images.stream()
                .filter(ProductImageDto::primary)
                .findFirst()
                .or(() -> images.stream().findFirst())
                .map(ProductImageDto::url)
                .orElse(null);
    }
}
