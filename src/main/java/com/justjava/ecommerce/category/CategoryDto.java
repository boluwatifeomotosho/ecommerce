package com.justjava.ecommerce.category;

import java.util.UUID;

public record CategoryDto(
        UUID   id,
        String name,
        String slug,
        String imageUrl,
        String icon,
        UUID   parentId,
        String parentName
) {}
