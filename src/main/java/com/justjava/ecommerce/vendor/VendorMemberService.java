package com.justjava.ecommerce.vendor;

import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Resolves the vendor tenant (company) for the current user.
 *
 * A vendor admin or sub-vendor is always a member of exactly one Vendor
 * (see the UNIQUE on vendor_members.user_id). Callers that used to key
 * queries by the user's own id must instead use the vendor company id
 * returned here.
 */
@Service
@RequiredArgsConstructor
public class VendorMemberService {

    private final UserRepository         userRepository;
    private final VendorMemberRepository vendorMemberRepository;

    @Transactional(readOnly = true)
    public Optional<VendorMember> currentMembership(OidcUser principal) {
        if (principal == null) return Optional.empty();
        return userRepository.findByKeycloakId(principal.getSubject())
                .flatMap(u -> vendorMemberRepository.findByUserId(u.getId()));
    }

    @Transactional(readOnly = true)
    public UUID currentVendorId(OidcUser principal) {
        return currentMembership(principal)
                .map(m -> m.getVendor().getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Current user is not a member of any vendor"));
    }

    @Transactional(readOnly = true)
    public UUID currentUserId(OidcUser principal) {
        return userRepository.findByKeycloakId(principal.getSubject())
                .orElseThrow(() -> new IllegalStateException("User not found in local DB"))
                .getId();
    }

    /**
     * VENDOR_ADMIN → company-wide scope (creatorUserId = null).
     * SUB_VENDOR   → creator-scoped (creatorUserId = own user id).
     */
    @Transactional(readOnly = true)
    public VendorScope currentScope(OidcUser principal) {
        VendorMember m = currentMembership(principal)
                .orElseThrow(() -> new IllegalStateException("Current user is not a member of any vendor"));
        UUID vendorId = m.getVendor().getId();
        UUID creatorFilter = m.getMemberRole() == VendorMemberRole.SUB_VENDOR
                ? m.getUser().getId()
                : null;
        return new VendorScope(vendorId, creatorFilter, m.getMemberRole());
    }
}
