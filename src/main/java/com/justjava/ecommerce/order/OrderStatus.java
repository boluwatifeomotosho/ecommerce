package com.justjava.ecommerce.order;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    PROCESSING,
    SHIPPED,
    IN_TRANSIT,
    DELIVERED,
    CONFIRMED,
    CANCELLED,
    REFUNDED
}
