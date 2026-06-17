package com.justjava.ecommerce.product;

import com.justjava.ecommerce.product.dto.ProductDetailDto;
import com.justjava.ecommerce.product.dto.ProductFilter;
import com.justjava.ecommerce.product.dto.ProductSummaryDto;
import com.justjava.ecommerce.product.ProductMapper;
import com.justjava.ecommerce.product.ProductStatus;
import com.justjava.ecommerce.product.ProductRepository;
import com.justjava.ecommerce.product.ProductQueryService;
import com.justjava.ecommerce.product.ProductSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository productRepository;
    private final ProductMapper     mapper;

    @Override
    public Page<ProductSummaryDto> getPublishedProducts(ProductFilter filter, Pageable pageable) {
        return productRepository
                .findAll(ProductSpecification.fromFilter(filter, ProductStatus.PUBLISHED), pageable)
                .map(mapper::toSummary);
    }

    @Override
    public ProductDetailDto getPublishedProductBySlug(String slug) {
        return productRepository.findPublishedBySlug(slug)
                .map(mapper::toDetail)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + slug));
    }

    @Override
    public Page<ProductSummaryDto> getVendorProducts(UUID vendorId, Pageable pageable) {
        return productRepository.findByVendorId(vendorId, pageable)
                .map(mapper::toSummary);
    }

    @Override
    public ProductDetailDto getVendorProductById(UUID productId, UUID vendorId) {
        return productRepository.findByIdAndVendorId(productId, vendorId)
                .map(mapper::toDetail)
                .orElseThrow(() -> new EntityNotFoundException("Product not found or not owned by this vendor"));
    }

    @Override
    public Page<ProductSummaryDto> getPendingProducts(Pageable pageable) {
        return productRepository.findByStatus(ProductStatus.PENDING_REVIEW, pageable)
                .map(mapper::toSummary);
    }

    @Override
    public ProductDetailDto getProductById(UUID productId) {
        return productRepository.findByIdWithDetails(productId)
                .map(mapper::toDetail)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
    }
}
