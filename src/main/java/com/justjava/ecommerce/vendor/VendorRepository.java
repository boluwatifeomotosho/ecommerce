package com.justjava.ecommerce.vendor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface VendorRepository extends JpaRepository<Vendor, UUID> {

    Optional<Vendor> findBySlug(String slug);

    Optional<Vendor> findByOwnerId(UUID ownerId);

    boolean existsBySlug(String slug);

    @Query(value = "SELECT v FROM Vendor v JOIN FETCH v.owner",
           countQuery = "SELECT COUNT(v) FROM Vendor v")
    Page<Vendor> findAllWithOwner(Pageable pageable);

    @Query("SELECT v FROM Vendor v JOIN FETCH v.owner WHERE v.id = :id")
    Optional<Vendor> findByIdWithOwner(@Param("id") UUID id);
}
