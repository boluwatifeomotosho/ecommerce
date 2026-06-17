package com.justjava.ecommerce.product;

import java.util.UUID;

/**
 * Admin-only contract for approving or rejecting vendor products.
 * Kept separate so that neither vendors nor customers can inject it.
 */
public interface ProductApprovalService {

    void approve(UUID productId);

    void reject(UUID productId, String reason);
}
