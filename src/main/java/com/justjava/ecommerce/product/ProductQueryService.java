package com.justjava.ecommerce.product;

import com.justjava.ecommerce.product.dto.ProductDetailDto;
import com.justjava.ecommerce.product.dto.ProductFilter;
import com.justjava.ecommerce.product.dto.ProductSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Read-only contract for product data.
 * Used by customer-facing pages and admin read views.
 * Separated from write operations to honour the Interface Segregation Principle.
 */
public interface ProductQueryService {

    Page<ProductSummaryDto> getPublishedProducts(ProductFilter filter, Pageable pageable);

    ProductDetailDto getPublishedProductBySlug(String slug);

    Page<ProductSummaryDto> getVendorProducts(UUID vendorId, Pageable pageable);

    Page<ProductSummaryDto> getVendorProductsByCreator(UUID vendorId, UUID createdByUserId, Pageable pageable);

    ProductDetailDto getVendorProductById(UUID productId, UUID vendorId);

    ProductDetailDto getVendorProductByIdForCreator(UUID productId, UUID vendorId, UUID createdByUserId);

    Page<ProductSummaryDto> getPendingProducts(Pageable pageable);

    Page<ProductSummaryDto> getPendingProductsByVendor(UUID vendorId, Pageable pageable);

    /** Admin-only: fetch any product by ID regardless of vendor. */
    ProductDetailDto getProductById(UUID productId);
}
