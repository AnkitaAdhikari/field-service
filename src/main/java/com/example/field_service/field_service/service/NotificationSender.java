package com.example.field_service.field_service.service;

import com.example.field_service.field_service.entity.User;
import com.example.field_service.field_service.repository.CustomerRepository;
import com.example.field_service.field_service.repository.TechnicianRepository;

import org.springframework.stereotype.Component;

@Component
public class NotificationSender {

    private final EmailSender emailSender;
    private final SmsSender smsSender;
    private final WhatsAppSender whatsAppSender;
    private final CustomerRepository customerRepository;
    private final TechnicianRepository technicianRepository;

    public NotificationSender(EmailSender emailSender,
                              SmsSender smsSender,
                              WhatsAppSender whatsAppSender,
                              CustomerRepository customerRepository,
                              TechnicianRepository technicianRepository) {
        this.emailSender = emailSender;
        this.smsSender = smsSender;
        this.whatsAppSender = whatsAppSender;
        this.customerRepository = customerRepository;
        this.technicianRepository = technicianRepository;
    }

    public void send(User recipient, String message) {
        emailSender.send(recipient.getEmail(), "Field Service Update", message);

        String phone = resolvePhone(recipient.getEmail());
        if (phone != null) {
            smsSender.send(phone, message);
            whatsAppSender.send(phone, message);
        }
    }

    private String resolvePhone(String email) {
        return customerRepository.findByUser_Email(email)
                .map(c -> c.getPhone())
                .or(() -> technicianRepository.findByUser_Email(email).map(t -> t.getPhone()))
                .orElse(null);
    }
}