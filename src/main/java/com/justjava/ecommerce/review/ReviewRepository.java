package com.justjava.ecommerce.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findByProductIdOrderByCreatedAtDesc(UUID productId);

    boolean existsByProductIdAndCustomerId(UUID productId, UUID customerId);

    @Query("SELECT r.product.id FROM Review r WHERE r.customer.id = :customerId AND r.order.id = :orderId")
    Set<UUID> findReviewedProductIdsByCustomerAndOrder(UUID customerId, UUID orderId);
}
