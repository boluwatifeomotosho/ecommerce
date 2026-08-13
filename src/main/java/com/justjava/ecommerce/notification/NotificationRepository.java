package com.justjava.ecommerce.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId, Pageable pageable);

    long countByRecipientIdAndReadAtIsNull(UUID recipientId);

    @Modifying
    @Query("UPDATE Notification n SET n.readAt = :now WHERE n.recipient.id = :recipientId AND n.readAt IS NULL")
    int markAllRead(@Param("recipientId") UUID recipientId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Notification n SET n.readAt = :now WHERE n.id = :id AND n.recipient.id = :recipientId AND n.readAt IS NULL")
    int markRead(@Param("id") UUID id, @Param("recipientId") UUID recipientId, @Param("now") LocalDateTime now);
}
