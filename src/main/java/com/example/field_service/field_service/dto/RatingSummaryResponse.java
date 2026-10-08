package com.example.field_service.field_service.dto;

public class RatingSummaryResponse {

    private Long technicianId;
    private double averageRating;
    private long totalReviews;

    public RatingSummaryResponse(Long technicianId, double averageRating, long totalReviews) {
        this.technicianId = technicianId;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    public Long getTechnicianId() { return technicianId; }
    public double getAverageRating() { return averageRating; }
    public long getTotalReviews() { return totalReviews; }
}