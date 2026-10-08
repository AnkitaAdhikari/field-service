package com.example.field_service.field_service.dto;

import java.time.LocalDateTime;

public class ReviewResponse {

    private Long id;
    private Long jobId;
    private String customerName;
    private String technicianName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewResponse(Long id, Long jobId, String customerName, String technicianName,
                          int rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.jobId = jobId;
        this.customerName = customerName;
        this.technicianName = technicianName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getJobId() { return jobId; }
    public String getCustomerName() { return customerName; }
    public String getTechnicianName() { return technicianName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}