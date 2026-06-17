package com.justjava.ecommerce.model;

public enum ProductStatus {
    DRAFT,
    PENDING_REVIEW,
    PUBLISHED,
    REJECTED,
    ARCHIVED;

    public boolean isEditable() {
        return this == DRAFT || this == REJECTED;
    }

    public boolean canSubmitForReview() {
        return this == DRAFT || this == REJECTED;
    }

    public boolean isVisible() {
        return this == PUBLISHED;
    }
}
