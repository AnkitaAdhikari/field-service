package com.example.field_service.field_service.controller;

import com.example.field_service.field_service.dto.RatingSummaryResponse;
import com.example.field_service.field_service.dto.ReviewRequest;
import com.example.field_service.field_service.dto.ReviewResponse;
import com.example.field_service.field_service.service.ReviewService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/api/jobs/{jobId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(@PathVariable Long jobId, @Valid @RequestBody ReviewRequest request) {
        return reviewService.create(jobId, request);
    }

    @GetMapping("/api/technicians/{technicianId}/reviews")
    public List<ReviewResponse> getForTechnician(@PathVariable Long technicianId) {
        return reviewService.getForTechnician(technicianId);
    }

    @GetMapping("/api/technicians/{technicianId}/rating-summary")
    public RatingSummaryResponse getRatingSummary(@PathVariable Long technicianId) {
        return reviewService.getRatingSummary(technicianId);
    }

    @GetMapping("/api/reviews/me")
    public List<ReviewResponse> getMyReviews() {
        return reviewService.getMyReviews();
    }
}