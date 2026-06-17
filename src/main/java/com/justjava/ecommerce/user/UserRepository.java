package com.justjava.ecommerce.user;

import com.justjava.ecommerce.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByKeycloakId(String keycloakId);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Page<User> findByRole(String role, Pageable pageable);
    long countByRole(String role);
}
