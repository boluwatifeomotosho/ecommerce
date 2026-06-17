package com.justjava.ecommerce.repository;

import com.justjava.ecommerce.model.Product;
import com.justjava.ecommerce.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    Optional<Product> findByIdAndVendorId(UUID id, UUID vendorId);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByVendorId(UUID vendorId, Pageable pageable);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID id);

    @Query("""
            SELECT p FROM Product p
            JOIN FETCH p.category
            JOIN FETCH p.vendor
            WHERE p.id = :id
            """)
    Optional<Product> findByIdWithDetails(@Param("id") UUID id);

    @Query("""
            SELECT p FROM Product p
            JOIN FETCH p.category
            JOIN FETCH p.vendor
            WHERE p.slug = :slug AND p.status = 'PUBLISHED'
            """)
    Optional<Product> findPublishedBySlug(@Param("slug") String slug);
}
