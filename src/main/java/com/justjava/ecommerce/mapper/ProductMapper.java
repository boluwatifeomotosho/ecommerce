package com.justjava.ecommerce.mapper;

import com.justjava.ecommerce.dto.CategoryDto;
import com.justjava.ecommerce.dto.ProductDetailDto;
import com.justjava.ecommerce.dto.ProductImageDto;
import com.justjava.ecommerce.dto.ProductSummaryDto;
import com.justjava.ecommerce.model.Category;
import com.justjava.ecommerce.model.Product;
import com.justjava.ecommerce.model.ProductImage;
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

    public ProductSummaryDto toSummary(Product p) {
        return new ProductSummaryDto(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.getPrice(),
                p.getCompareAtPrice(),
                p.getPrimaryImageUrl(),
                p.getCategory().getName(),
                p.getVendor().getFullName(),
                p.getStockQuantity(),
                p.getStatus()
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
                p.getVendor().getFullName(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getPublishedAt()
        );
    }
}
