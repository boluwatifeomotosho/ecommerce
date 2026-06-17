package com.justjava.ecommerce.specification;

import com.justjava.ecommerce.dto.ProductFilter;
import com.justjava.ecommerce.model.Product;
import com.justjava.ecommerce.model.ProductStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Static factory for {@link Specification}<{@link Product}> predicates.
 * Compose with {@code Specification.where(...).and(...)} in service layer.
 */
public final class ProductSpecification {

    private ProductSpecification() {}

    public static Specification<Product> hasStatus(ProductStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Product> inCategory(UUID categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> forVendor(UUID vendorId) {
        return (root, query, cb) -> cb.equal(root.get("vendor").get("id"), vendorId);
    }

    public static Specification<Product> nameOrDescriptionContains(String term) {
        return (root, query, cb) -> {
            String like = "%" + term.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("description")), like)
            );
        };
    }

    public static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min != null && max != null) return cb.between(root.get("price"), min, max);
            if (min != null)                return cb.greaterThanOrEqualTo(root.get("price"), min);
            if (max != null)                return cb.lessThanOrEqualTo(root.get("price"), max);
            return cb.conjunction();
        };
    }

    /** Builds a combined specification from a {@link ProductFilter}. */
    public static Specification<Product> fromFilter(ProductFilter filter, ProductStatus status) {
        Specification<Product> spec = hasStatus(status);

        if (filter.categoryId() != null)
            spec = spec.and(inCategory(filter.categoryId()));

        if (filter.search() != null && !filter.search().isBlank())
            spec = spec.and(nameOrDescriptionContains(filter.search().trim()));

        if (filter.minPrice() != null || filter.maxPrice() != null)
            spec = spec.and(priceBetween(filter.minPrice(), filter.maxPrice()));

        return spec;
    }
}
