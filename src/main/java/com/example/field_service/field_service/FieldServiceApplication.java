package com.example.field_service.field_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FieldServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FieldServiceApplication.class, args);
        System.out.println("This is my Field service");
    }
}
