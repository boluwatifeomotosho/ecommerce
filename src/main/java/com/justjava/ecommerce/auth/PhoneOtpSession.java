package com.justjava.ecommerce.auth;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "phone_otp_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneOtpSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 20)
    private String phone;

    @Column(name = "otp_hash", nullable = false, length = 64)
    private String otpHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "request_count", nullable = false)
    private int requestCount;

    @Column(name = "window_start", nullable = false)
    private LocalDateTime windowStart;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
