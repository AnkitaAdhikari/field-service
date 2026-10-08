package com.example.field_service.field_service.service;

import com.example.field_service.field_service.dto.NotificationResponse;
import com.example.field_service.field_service.entity.Notification;
import com.example.field_service.field_service.entity.NotificationType;
import com.example.field_service.field_service.entity.User;
import com.example.field_service.field_service.exception.NotificationNotFoundException;
import com.example.field_service.field_service.exception.UnauthorizedAccessException;
import com.example.field_service.field_service.repository.NotificationRepository;
import com.example.field_service.field_service.security.SecurityUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSender notificationSender;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationSender notificationSender) {
        this.notificationRepository = notificationRepository;
        this.notificationSender = notificationSender;
    }

    // Called from other services (Job, ServiceRequest, Payment) — never
    // exposed directly to the client.
    @Transactional
    public void send(User recipient, NotificationType type, String message) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setType(type);
        notification.setMessage(message);
        notificationRepository.save(notification);

        notificationSender.send(recipient, message);
    }

    public List<NotificationResponse> getMyNotifications() {
        String email = SecurityUtils.getCurrentUserEmail();
        return notificationRepository.findByRecipient_EmailOrderByCreatedAtDesc(email)
                .stream().map(this::toResponse).toList();
    }

    public long getMyUnreadCount() {
        String email = SecurityUtils.getCurrentUserEmail();
        return notificationRepository.countByRecipient_EmailAndReadFalse(email);
    }

    @Transactional
    public NotificationResponse markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found: " + id));

        String email = SecurityUtils.getCurrentUserEmail();
        if (!notification.getRecipient().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("This notification does not belong to you");
        }

        notification.setRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.getId(), n.getType(), n.getMessage(), n.isRead(), n.getCreatedAt());
    }
}