package com.justjava.ecommerce.repository;

import com.justjava.ecommerce.model.PhoneOtpSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PhoneOtpSessionRepository extends JpaRepository<PhoneOtpSession, UUID> {
    Optional<PhoneOtpSession> findByPhone(String phone);
    void deleteByPhone(String phone);
}
