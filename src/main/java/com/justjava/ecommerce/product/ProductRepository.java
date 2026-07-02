package com.justjava.ecommerce.product;

import com.justjava.ecommerce.product.Product;
import com.justjava.ecommerce.product.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    Optional<Product> findByIdAndVendorId(UUID id, UUID vendorId);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByStatusIn(java.util.Collection<ProductStatus> statuses, Pageable pageable);

    Page<Product> findByVendorId(UUID vendorId, Pageable pageable);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID id);

    long countByStatus(ProductStatus status);

    long countByVendorIdAndStockQuantityLessThanEqual(UUID vendorId, int threshold);

    long countByVendorIdAndStatus(UUID vendorId, ProductStatus status);

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

    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = CASE WHEN p.stockQuantity >= :qty THEN p.stockQuantity - :qty ELSE 0 END WHERE p.id = :id")
    void decrementStock(@Param("id") UUID id, @Param("qty") int qty);
}
