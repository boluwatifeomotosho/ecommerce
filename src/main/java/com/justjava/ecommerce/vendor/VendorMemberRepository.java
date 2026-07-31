package com.justjava.ecommerce.vendor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendorMemberRepository extends JpaRepository<VendorMember, UUID> {

    Optional<VendorMember> findByUserId(UUID userId);

    List<VendorMember> findByVendorId(UUID vendorId);

    boolean existsByUserId(UUID userId);

    @Query("SELECT m FROM VendorMember m JOIN FETCH m.vendor WHERE m.user.id = :userId")
    Optional<VendorMember> findByUserIdWithVendor(@Param("userId") UUID userId);

    @Query("SELECT m FROM VendorMember m JOIN FETCH m.user WHERE m.vendor.id = :vendorId ORDER BY m.joinedAt DESC")
    List<VendorMember> findByVendorIdWithUser(@Param("vendorId") UUID vendorId);

    @Query("SELECT m FROM VendorMember m JOIN FETCH m.user JOIN FETCH m.vendor LEFT JOIN FETCH m.invitedBy WHERE m.id = :memberId")
    Optional<VendorMember> findByIdWithUser(@Param("memberId") UUID memberId);
}
