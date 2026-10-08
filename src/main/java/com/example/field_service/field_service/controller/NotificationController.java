package com.example.field_service.field_service.controller;

import com.example.field_service.field_service.dto.NotificationResponse;
import com.example.field_service.field_service.service.NotificationService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/me")
    public List<NotificationResponse> getMyNotifications() {
        return notificationService.getMyNotifications();
    }

    @GetMapping("/me/unread-count")
    public Map<String, Long> getUnreadCount() {
        return Map.of("unread", notificationService.getMyUnreadCount());
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markAsRead(@PathVariable Long id) {
        return notificationService.markAsRead(id);
    }
}