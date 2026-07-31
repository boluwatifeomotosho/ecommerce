package com.justjava.ecommerce.product;

import java.util.UUID;

/**
 * Approve/reject contract for vendor products. Approvals live with the vendor admin
 * (owner of the store); the platform admin only has takedown authority.
 */
public interface ProductApprovalService {

    /**
     * Approve a product submitted by a member of the given vendor. Verifies the
     * product belongs to that vendor before flipping status to PUBLISHED.
     */
    void approve(UUID productId, UUID vendorId);

    /**
     * Reject a product submitted by a member of the given vendor. Verifies the
     * product belongs to that vendor before flipping status to REJECTED.
     */
    void reject(UUID productId, UUID vendorId, String reason);

    /**
     * Force-archive a published product. Used when the platform admin needs to take
     * a listing off the catalog (e.g. policy violation) regardless of who owns it.
     */
    void takedown(UUID productId, String reason);
}
