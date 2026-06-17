package com.justjava.ecommerce.category;

import java.util.UUID;

public record CategoryDto(
        UUID   id,
        String name,
        String slug,
        String imageUrl,
        UUID   parentId,
        String parentName
) {}
