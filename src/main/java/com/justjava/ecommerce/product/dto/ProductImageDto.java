package com.justjava.ecommerce.product.dto;

import java.util.UUID;

public record ProductImageDto(
        UUID    id,
        String  url,
        String  altText,
        int     sortOrder,
        boolean primary
) {}
