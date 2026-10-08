package com.example.field_service.field_service.dto;

import com.example.field_service.field_service.entity.Priority;
import com.example.field_service.field_service.entity.RequestStatus;

import java.time.LocalDateTime;

public class ServiceRequestResponse {

    private Long id;
    private String customerName;
    private String customerEmail;
    private String technicianName;   // null until assigned
    private String requiredSkill;
    private String description;
    private Priority priority;
    private RequestStatus status;
    private String address;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ServiceRequestResponse(Long id, String customerName, String customerEmail,
                                  String technicianName, String requiredSkill, String description,
                                  Priority priority, RequestStatus status, String address,
                                  Double latitude, Double longitude,
                                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.technicianName = technicianName;
        this.requiredSkill = requiredSkill;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getTechnicianName() { return technicianName; }
    public String getRequiredSkill() { return requiredSkill; }
    public String getDescription() { return description; }
    public Priority getPriority() { return priority; }
    public RequestStatus getStatus() { return status; }
    public String getAddress() { return address; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}