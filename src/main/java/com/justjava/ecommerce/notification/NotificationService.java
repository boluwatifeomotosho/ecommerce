package com.justjava.ecommerce.notification;

import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final UserRepository         userRepository;

    @Transactional
    public void notify(UUID recipientUserId, String type, String title, String body, String url) {
        if (recipientUserId == null) return;
        try {
            User recipient = userRepository.getReferenceById(recipientUserId);
            repository.save(Notification.builder()
                    .recipient(recipient)
                    .type(type)
                    .title(title)
                    .body(body)
                    .url(url)
                    .build());
        } catch (Exception e) {
            log.warn("Failed to save notification for {}: {}", recipientUserId, e.getMessage());
        }
    }

    @Transactional
    public void notifyMany(Collection<UUID> recipientIds, String type, String title, String body, String url) {
        if (recipientIds == null || recipientIds.isEmpty()) return;
        for (UUID id : recipientIds) {
            notify(id, type, title, body, url);
        }
    }

    @Transactional(readOnly = true)
    public Page<Notification> list(UUID recipientId, Pageable pageable) {
        return repository.findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable);
    }

    @Transactional(readOnly = true)
    public long unreadCount(UUID recipientId) {
        if (recipientId == null) return 0;
        return repository.countByRecipientIdAndReadAtIsNull(recipientId);
    }

    @Transactional
    public void markAllRead(UUID recipientId) {
        if (recipientId == null) return;
        repository.markAllRead(recipientId, LocalDateTime.now());
    }

    @Transactional
    public void markRead(UUID id, UUID recipientId) {
        if (id == null || recipientId == null) return;
        repository.markRead(id, recipientId, LocalDateTime.now());
    }
}
