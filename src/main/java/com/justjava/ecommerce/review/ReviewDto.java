package com.justjava.ecommerce.review;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewDto(
        UUID          id,
        String        customerName,
        int           rating,
        String        comment,
        LocalDateTime createdAt
) {}
