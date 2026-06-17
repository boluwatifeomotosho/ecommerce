package com.justjava.ecommerce.product.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SaveProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotNull(message = "Category is required")
    private UUID categoryId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 2 decimal places")
    private BigDecimal price;

    @DecimalMin(value = "0.01", message = "Compare-at price must be greater than zero")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal compareAtPrice;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;

    @Min(value = 1, message = "Weight must be at least 1 gram")
    private Integer weightGrams;

    private List<@NotBlank String> imageUrls;

    public SaveProductRequest(String name, UUID categoryId, String description,
                              BigDecimal price, BigDecimal compareAtPrice, Integer stockQuantity,
                              String sku, Integer weightGrams, List<String> imageUrls) {
        this.name = name;
        this.categoryId = categoryId;
        this.description = description;
        this.price = price;
        this.compareAtPrice = compareAtPrice;
        this.stockQuantity = stockQuantity;
        this.sku = sku;
        this.weightGrams = weightGrams;
        this.imageUrls = imageUrls;
    }
}
