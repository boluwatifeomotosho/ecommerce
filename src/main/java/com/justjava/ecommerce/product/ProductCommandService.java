package com.justjava.ecommerce.product;

import com.justjava.ecommerce.product.dto.ProductDetailDto;
import com.justjava.ecommerce.product.dto.SaveProductRequest;

import java.util.UUID;

/**
 * Write contract for vendor product management.
 * Separated from query and approval operations (Interface Segregation Principle).
 */
public interface ProductCommandService {

    ProductDetailDto create(UUID vendorId, UUID createdByUserId, SaveProductRequest request);

    ProductDetailDto update(UUID productId, UUID vendorId, SaveProductRequest request);

    /** Enforces that the product was created by {@code enforceCreatorUserId} (pass null to skip). */
    ProductDetailDto update(UUID productId, UUID vendorId, UUID enforceCreatorUserId, SaveProductRequest request);

    void submitForReview(UUID productId, UUID vendorId);

    void submitForReview(UUID productId, UUID vendorId, UUID enforceCreatorUserId);

    void archive(UUID productId, UUID vendorId);

    void archive(UUID productId, UUID vendorId, UUID enforceCreatorUserId);

    void unarchive(UUID productId, UUID vendorId);

    void unarchive(UUID productId, UUID vendorId, UUID enforceCreatorUserId);
}
