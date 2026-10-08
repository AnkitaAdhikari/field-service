package com.example.field_service.field_service.dto;

import jakarta.validation.constraints.NotBlank;

public class CustomerRequest {

    @NotBlank(message = "Phone is required")
    private String phone;

    private String addressLine;
    private String city;
    private String state;
    private String zipCode;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
}