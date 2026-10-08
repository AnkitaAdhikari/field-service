package com.example.field_service.field_service;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FieldServiceApplication {

    public static void main(String[] args) {
//        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(FieldServiceApplication.class, args);
        System.out.println("This is my Field service");
    }
}