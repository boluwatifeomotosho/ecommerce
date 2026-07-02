package com.justjava.ecommerce.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "keycloak_id", unique = true, nullable = false)
    private String keycloakId;

    @Column(unique = true)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(unique = true)
    private String phone;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String status;

    @Column(name = "store_name", length = 255)
    private String storeName;

    @Column(name = "store_bio", columnDefinition = "TEXT")
    private String storeBio;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_account_number", length = 20)
    private String bankAccountNumber;

    @Column(name = "bank_account_name", length = 255)
    private String bankAccountName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "ACTIVE";
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
