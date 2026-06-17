package com.justjava.ecommerce.product;

import com.justjava.ecommerce.product.dto.ProductDetailDto;
import com.justjava.ecommerce.product.dto.SaveProductRequest;

import java.util.UUID;

/**
 * Write contract for vendor product management.
 * Separated from query and approval operations (Interface Segregation Principle).
 */
public interface ProductCommandService {

    ProductDetailDto create(UUID vendorId, SaveProductRequest request);

    ProductDetailDto update(UUID productId, UUID vendorId, SaveProductRequest request);

    void submitForReview(UUID productId, UUID vendorId);

    void archive(UUID productId, UUID vendorId);
}
