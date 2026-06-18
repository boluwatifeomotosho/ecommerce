package com.justjava.ecommerce.cart;

import com.justjava.ecommerce.product.Product;
import com.justjava.ecommerce.product.ProductRepository;
import com.justjava.ecommerce.product.ProductStatus;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository  productRepository;
    private final UserRepository     userRepository;

    @Override
    @Transactional(readOnly = true)
    public CartDto getCart(UUID customerId) {
        List<CartItem> items = cartItemRepository.findByCustomerIdWithProducts(customerId);
        List<CartDto.CartItemDto> dtos = items.stream().map(this::toItemDto).toList();
        BigDecimal subtotal = dtos.stream()
                .map(CartDto.CartItemDto::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalItems = dtos.stream().mapToInt(CartDto.CartItemDto::quantity).sum();
        return new CartDto(dtos, subtotal, totalItems, dtos.isEmpty());
    }

    @Override
    public void addItem(UUID customerId, UUID productId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be at least 1");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getStatus() != ProductStatus.PUBLISHED) {
            throw new IllegalArgumentException("Product is not available for purchase");
        }
        if (product.getStockQuantity() <= 0) {
            throw new IllegalArgumentException("Product is out of stock");
        }

        cartItemRepository.findByCustomerIdAndProductId(customerId, productId)
                .ifPresentOrElse(
                        existing -> {
                            int newQty = Math.min(existing.getQuantity() + quantity, product.getStockQuantity());
                            existing.setQuantity(newQty);
                        },
                        () -> {
                            User customer = userRepository.getReferenceById(customerId);
                            CartItem item = CartItem.builder()
                                    .customer(customer)
                                    .product(product)
                                    .quantity(Math.min(quantity, product.getStockQuantity()))
                                    .build();
                            cartItemRepository.save(item);
                        }
                );
    }

    @Override
    public void updateQuantity(UUID customerId, UUID productId, int quantity) {
        if (quantity <= 0) {
            removeItem(customerId, productId);
            return;
        }
        CartItem item = cartItemRepository.findByCustomerIdAndProductId(customerId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        int maxQty = item.getProduct().getStockQuantity();
        item.setQuantity(Math.min(quantity, maxQty));
    }

    @Override
    public void removeItem(UUID customerId, UUID productId) {
        cartItemRepository.deleteByCustomerIdAndProductId(customerId, productId);
    }

    @Override
    public void clearCart(UUID customerId) {
        cartItemRepository.deleteAllByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(UUID customerId) {
        return cartItemRepository.countByCustomerId(customerId);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private CartDto.CartItemDto toItemDto(CartItem item) {
        Product p = item.getProduct();
        BigDecimal lineTotal = p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        String vendorName = p.getVendor() != null ? p.getVendor().getFullName() : "Unknown";
        return new CartDto.CartItemDto(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.getPrimaryImageUrl(),
                vendorName,
                p.getPrice(),
                item.getQuantity(),
                lineTotal,
                p.getStockQuantity(),
                p.getStockQuantity() > 0
        );
    }
}
