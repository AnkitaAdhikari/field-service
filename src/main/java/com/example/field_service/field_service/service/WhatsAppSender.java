package com.example.field_service.field_service.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WhatsAppSender {

    @Value("${twilio.whatsapp-from-number}")
    private String fromNumber;

    public void send(String toPhoneNumber, String message) {
        try {
            Message.creator(
                    new PhoneNumber("whatsapp:" + toPhoneNumber),
                    new PhoneNumber(fromNumber),
                    message
            ).create();
        } catch (Exception e) {
            System.err.println("Failed to send WhatsApp message to " + toPhoneNumber + ": " + e.getMessage());
        }
    }
}