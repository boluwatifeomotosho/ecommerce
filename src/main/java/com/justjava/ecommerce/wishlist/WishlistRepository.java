package com.justjava.ecommerce.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface WishlistRepository extends JpaRepository<WishlistItem, UUID> {

    @Query("SELECT DISTINCT w FROM WishlistItem w JOIN FETCH w.product p LEFT JOIN FETCH p.images LEFT JOIN FETCH p.category LEFT JOIN FETCH p.vendor WHERE w.customer.id = :customerId ORDER BY w.createdAt DESC")
    List<WishlistItem> findByCustomerIdWithProduct(UUID customerId);

    Optional<WishlistItem> findByCustomerIdAndProductId(UUID customerId, UUID productId);

    boolean existsByCustomerIdAndProductId(UUID customerId, UUID productId);

    long countByCustomerId(UUID customerId);

    @Query("SELECT w.product.id FROM WishlistItem w WHERE w.customer.id = :customerId")
    Set<UUID> findProductIdsByCustomerId(UUID customerId);
}
