package com.example.field_service.field_service.dto;

import com.example.field_service.field_service.entity.JobStatus;

import java.time.LocalDateTime;

public class JobResponse {

    private Long id;
    private Long serviceRequestId;
    private String technicianName;
    private String customerName;
    private JobStatus status;
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;

    public JobResponse(Long id, Long serviceRequestId, String technicianName, String customerName,
                       JobStatus status, LocalDateTime acceptedAt, LocalDateTime startedAt,
                       LocalDateTime completedAt, LocalDateTime cancelledAt) {
        this.id = id;
        this.serviceRequestId = serviceRequestId;
        this.technicianName = technicianName;
        this.customerName = customerName;
        this.status = status;
        this.acceptedAt = acceptedAt;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.cancelledAt = cancelledAt;
    }

    public Long getId() { return id; }
    public Long getServiceRequestId() { return serviceRequestId; }
    public String getTechnicianName() { return technicianName; }
    public String getCustomerName() { return customerName; }
    public JobStatus getStatus() { return status; }
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
}