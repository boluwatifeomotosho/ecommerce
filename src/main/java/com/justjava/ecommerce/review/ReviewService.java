package com.justjava.ecommerce.review;

import com.justjava.ecommerce.order.Order;
import com.justjava.ecommerce.order.OrderRepository;
import com.justjava.ecommerce.order.OrderStatus;
import com.justjava.ecommerce.product.Product;
import com.justjava.ecommerce.product.ProductRepository;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository  reviewRepository;
    private final OrderRepository   orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository    userRepository;

    @Transactional
    public void submitReview(UUID customerId, UUID orderId, UUID productId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Order does not belong to this customer");
        }
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("You can only review products from delivered orders");
        }

        boolean itemInOrder = order.getItems().stream()
                .anyMatch(item -> item.getProduct() != null && item.getProduct().getId().equals(productId));
        if (!itemInOrder) {
            throw new IllegalArgumentException("This product is not part of the specified order");
        }

        if (reviewRepository.existsByProductIdAndCustomerId(productId, customerId)) {
            throw new IllegalStateException("You have already reviewed this product");
        }

        Product product = productRepository.getReferenceById(productId);
        User customer = userRepository.getReferenceById(customerId);

        Review review = Review.builder()
                .product(product)
                .customer(customer)
                .order(order)
                .rating(rating)
                .comment(comment != null && !comment.isBlank() ? comment.trim() : null)
                .build();

        reviewRepository.save(review);
        recalculateProductRating(productId);
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsForProduct(UUID productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(r -> new ReviewDto(
                        r.getId(),
                        r.getCustomer().getFullName() != null ? r.getCustomer().getFullName() : "Customer",
                        r.getRating(),
                        r.getComment(),
                        r.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Set<UUID> getReviewedProductIds(UUID customerId, UUID orderId) {
        return reviewRepository.findReviewedProductIdsByCustomerAndOrder(customerId, orderId);
    }

    private void recalculateProductRating(UUID productId) {
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        if (reviews.isEmpty()) {
            productRepository.findById(productId).ifPresent(p -> {
                p.setAverageRating(null);
                p.setReviewCount(0);
            });
            return;
        }
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        double rounded = Math.round(avg * 100.0) / 100.0;
        productRepository.findById(productId).ifPresent(p -> {
            p.setAverageRating(rounded);
            p.setReviewCount(reviews.size());
        });
    }
}
