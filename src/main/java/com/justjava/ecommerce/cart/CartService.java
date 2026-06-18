package com.justjava.ecommerce.cart;

import java.util.UUID;

public interface CartService {
    CartDto getCart(UUID customerId);
    void addItem(UUID customerId, UUID productId, int quantity);
    void updateQuantity(UUID customerId, UUID productId, int quantity);
    void removeItem(UUID customerId, UUID productId);
    void clearCart(UUID customerId);
    int getCartItemCount(UUID customerId);
}
