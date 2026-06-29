package com.justjava.ecommerce.product;

public enum ProductStatus {
    DRAFT,
    PENDING_REVIEW,
    PUBLISHED,
    PENDING_EDIT,
    REJECTED,
    ARCHIVED;

    public boolean isEditable() {
        return this == DRAFT || this == REJECTED || this == PUBLISHED || this == PENDING_EDIT;
    }

    public boolean canSubmitForReview() {
        return this == DRAFT || this == REJECTED;
    }

    public boolean isVisible() {
        return this == PUBLISHED;
    }

    /** True while the product is awaiting an admin decision (new submission or edit). */
    public boolean isAwaitingReview() {
        return this == PENDING_REVIEW || this == PENDING_EDIT;
    }
}
