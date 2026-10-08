package com.example.field_service.field_service.dto;

import java.time.LocalDateTime;

public class CustomerResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String addressLine;
    private String city;
    private String state;
    private String zipCode;
    private LocalDateTime createdAt;

    public CustomerResponse(Long id, String name, String email, String phone,
                            String addressLine, String city, String state,
                            String zipCode, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.addressLine = addressLine;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddressLine() { return addressLine; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}