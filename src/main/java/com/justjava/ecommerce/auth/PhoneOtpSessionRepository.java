package com.justjava.ecommerce.auth;

import com.justjava.ecommerce.auth.PhoneOtpSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PhoneOtpSessionRepository extends JpaRepository<PhoneOtpSession, UUID> {
    Optional<PhoneOtpSession> findByPhone(String phone);
    void deleteByPhone(String phone);
}
