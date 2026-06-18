package com.justjava.ecommerce.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product p LEFT JOIN FETCH p.images WHERE ci.customer.id = :customerId ORDER BY ci.addedAt ASC")
    List<CartItem> findByCustomerIdWithProducts(@Param("customerId") UUID customerId);

    Optional<CartItem> findByCustomerIdAndProductId(UUID customerId, UUID productId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.customer.id = :customerId AND ci.product.id = :productId")
    void deleteByCustomerIdAndProductId(@Param("customerId") UUID customerId, @Param("productId") UUID productId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.customer.id = :customerId")
    void deleteAllByCustomerId(@Param("customerId") UUID customerId);

    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.customer.id = :customerId")
    int countByCustomerId(@Param("customerId") UUID customerId);
}
