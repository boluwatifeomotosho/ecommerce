package com.justjava.ecommerce.product;

import com.justjava.ecommerce.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

    List<ProductImage> findByProductIdOrderBySortOrderAscIdAsc(UUID productId);

    @Modifying
    @Query("DELETE FROM ProductImage i WHERE i.product.id = :productId")
    void deleteAllByProductId(@Param("productId") UUID productId);
}
