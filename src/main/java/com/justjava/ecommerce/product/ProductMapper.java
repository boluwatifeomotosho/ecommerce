package com.justjava.ecommerce.product;

import com.justjava.ecommerce.category.CategoryDto;
import com.justjava.ecommerce.product.dto.ProductDetailDto;
import com.justjava.ecommerce.product.dto.ProductImageDto;
import com.justjava.ecommerce.product.dto.ProductSummaryDto;
import com.justjava.ecommerce.category.Category;
import com.justjava.ecommerce.product.Product;
import com.justjava.ecommerce.product.ProductImage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    public CategoryDto toCategoryDto(Category c) {
        if (c == null) return null;
        Category parent = c.getParent();
        return new CategoryDto(
                c.getId(),
                c.getName(),
                c.getSlug(),
                c.getImageUrl(),
                c.getIcon(),
                parent != null ? parent.getId()   : null,
                parent != null ? parent.getName() : null
        );
    }

    public ProductImageDto toImageDto(ProductImage img) {
        return new ProductImageDto(
                img.getId(),
                img.getUrl(),
                img.getAltText(),
                img.getSortOrder(),
                img.isPrimary()
        );
    }

    private String vendorDisplayName(Product p) {
        return p.getVendor().getName();
    }

    public ProductSummaryDto toSummary(Product p) {
        return new ProductSummaryDto(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.getPrice(),
                p.getCompareAtPrice(),
                p.getPrimaryImageUrl(),
                p.getCategory().getName(),
                vendorDisplayName(p),
                p.getStockQuantity(),
                p.getStatus(),
                p.getRejectionReason(),
                p.getAverageRating(),
                p.getReviewCount()
        );
    }

    public ProductDetailDto toDetail(Product p) {
        List<ProductImageDto> images = p.getImages().stream()
                .map(this::toImageDto)
                .toList();

        return new ProductDetailDto(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.getDescription(),
                p.getPrice(),
                p.getCompareAtPrice(),
                p.getSku(),
                p.getStockQuantity(),
                p.getWeightGrams(),
                p.getStatus(),
                p.getRejectionReason(),
                toCategoryDto(p.getCategory()),
                images,
                p.getVendor().getId(),
                vendorDisplayName(p),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getPublishedAt(),
                p.getAverageRating(),
                p.getReviewCount()
        );
    }
}
