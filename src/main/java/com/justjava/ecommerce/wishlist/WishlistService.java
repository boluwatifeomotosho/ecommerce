package com.justjava.ecommerce.wishlist;

import com.justjava.ecommerce.product.Product;
import com.justjava.ecommerce.product.ProductRepository;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository  productRepository;
    private final UserRepository     userRepository;

    /** Adds the product if not already wishlisted; removes it if it is. Returns true if now wishlisted. */
    @Transactional
    public boolean toggle(UUID customerId, UUID productId) {
        Optional<WishlistItem> existing = wishlistRepository.findByCustomerIdAndProductId(customerId, productId);
        if (existing.isPresent()) {
            wishlistRepository.delete(existing.get());
            return false;
        }
        User customer = userRepository.getReferenceById(customerId);
        Product product = productRepository.getReferenceById(productId);
        wishlistRepository.save(WishlistItem.builder().customer(customer).product(product).build());
        return true;
    }

    @Transactional(readOnly = true)
    public List<WishlistItem> getWishlist(UUID customerId) {
        return wishlistRepository.findByCustomerIdWithProduct(customerId);
    }

    @Transactional(readOnly = true)
    public boolean isWishlisted(UUID customerId, UUID productId) {
        return wishlistRepository.existsByCustomerIdAndProductId(customerId, productId);
    }

    @Transactional(readOnly = true)
    public Set<UUID> getWishlistedProductIds(UUID customerId) {
        return wishlistRepository.findProductIdsByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public long getWishlistCount(UUID customerId) {
        return wishlistRepository.countByCustomerId(customerId);
    }
}
