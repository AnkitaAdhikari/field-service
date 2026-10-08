package com.example.field_service.field_service.repository;

import com.example.field_service.field_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient_EmailOrderByCreatedAtDesc(String email);

    long countByRecipient_EmailAndReadFalse(String email);
}