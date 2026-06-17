package com.justjava.ecommerce.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewProductRequest(

        @NotNull(message = "Decision is required")
        Decision decision,

        @NotBlank(message = "Rejection reason is required when rejecting a product")
        String rejectionReason

) {
    public enum Decision { APPROVE, REJECT }

    public boolean isApproval() { return decision == Decision.APPROVE; }
}
