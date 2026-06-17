package com.justjava.ecommerce.product;

import com.justjava.ecommerce.product.Product;
import com.justjava.ecommerce.product.ProductStatus;
import com.justjava.ecommerce.product.ProductRepository;
import com.justjava.ecommerce.product.ProductApprovalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductApprovalServiceImpl implements ProductApprovalService {

    private final ProductRepository productRepository;

    @Override
    public void approve(UUID productId) {
        Product product = getPendingProduct(productId);
        product.setStatus(ProductStatus.PUBLISHED);
        product.setRejectionReason(null);
        product.setPublishedAt(LocalDateTime.now());
        productRepository.save(product);
        log.info("Product {} approved and published", productId);
    }

    @Override
    public void reject(UUID productId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Rejection reason must be provided");
        }
        Product product = getPendingProduct(productId);
        product.setStatus(ProductStatus.REJECTED);
        product.setRejectionReason(reason.trim());
        productRepository.save(product);
        log.info("Product {} rejected: {}", productId, reason);
    }

    private Product getPendingProduct(UUID productId) {
        Product product = productRepository.findByIdWithDetails(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
        if (product.getStatus() != ProductStatus.PENDING_REVIEW) {
            throw new IllegalStateException(
                    "Product " + productId + " is not in PENDING_REVIEW (status=" + product.getStatus() + ")");
        }
        return product;
    }
}
