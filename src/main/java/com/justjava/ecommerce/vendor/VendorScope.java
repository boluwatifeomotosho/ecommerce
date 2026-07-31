package com.justjava.ecommerce.vendor;

import java.util.UUID;

/**
 * Query scope for the current vendor-side principal.
 *
 * VENDOR_ADMIN: sees all activity for their company — {@code creatorUserId} is null.
 * SUB_VENDOR:   only sees activity attributable to themselves — {@code creatorUserId}
 *               is set to the sub-vendor's own user id.
 *
 * Callers branch on {@link #isCreatorScoped()} to pick between the vendor-wide
 * and per-creator repository queries.
 */
public record VendorScope(UUID vendorId, UUID creatorUserId, VendorMemberRole role) {

    public boolean isCreatorScoped() {
        return creatorUserId != null;
    }

    public boolean isVendorAdmin() {
        return role == VendorMemberRole.VENDOR_ADMIN;
    }
}
