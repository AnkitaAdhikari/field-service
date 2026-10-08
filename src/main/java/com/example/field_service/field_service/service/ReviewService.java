package com.example.field_service.field_service.service;

import com.example.field_service.field_service.dto.RatingSummaryResponse;
import com.example.field_service.field_service.dto.ReviewRequest;
import com.example.field_service.field_service.dto.ReviewResponse;
import com.example.field_service.field_service.entity.Job;
import com.example.field_service.field_service.entity.JobStatus;
import com.example.field_service.field_service.entity.NotificationType;
import com.example.field_service.field_service.entity.Review;
import com.example.field_service.field_service.exception.InvalidStatusTransitionException;
import com.example.field_service.field_service.exception.JobNotFoundException;
import com.example.field_service.field_service.exception.ReviewAlreadyExistsException;
import com.example.field_service.field_service.exception.UnauthorizedAccessException;
import com.example.field_service.field_service.repository.JobRepository;
import com.example.field_service.field_service.repository.ReviewRepository;
import com.example.field_service.field_service.security.SecurityUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final JobRepository jobRepository;
    private final NotificationService notificationService;

    public ReviewService(ReviewRepository reviewRepository,
                         JobRepository jobRepository,
                         NotificationService notificationService) {
        this.reviewRepository = reviewRepository;
        this.jobRepository = jobRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public ReviewResponse create(Long jobId, ReviewRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found: " + jobId));

        String email = SecurityUtils.getCurrentUserEmail();
        if (!job.getServiceRequest().getCustomer().getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("Only the customer who requested this job can review it");
        }

        if (job.getStatus() != JobStatus.COMPLETED) {
            throw new InvalidStatusTransitionException(
                    "Can only review a completed job; current status is " + job.getStatus());
        }

        if (reviewRepository.existsByJob_Id(jobId)) {
            throw new ReviewAlreadyExistsException("This job has already been reviewed");
        }

        Review review = new Review();
        review.setJob(job);
        review.setCustomer(job.getServiceRequest().getCustomer());
        review.setTechnician(job.getTechnician());
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review saved = reviewRepository.save(review);

        notificationService.send(job.getTechnician().getUser(), NotificationType.JOB_COMPLETED,
                "You received a " + request.getRating() + "-star review.");

        return toResponse(saved);
    }

    public List<ReviewResponse> getForTechnician(Long technicianId) {
        return reviewRepository.findByTechnician_IdOrderByCreatedAtDesc(technicianId)
                .stream().map(this::toResponse).toList();
    }

    public RatingSummaryResponse getRatingSummary(Long technicianId) {
        Double avg = reviewRepository.findAverageRatingByTechnicianId(technicianId);
        long count = reviewRepository.countByTechnician_Id(technicianId);
        return new RatingSummaryResponse(technicianId, avg != null ? avg : 0.0, count);
    }

    public List<ReviewResponse> getMyReviews() {
        String email = SecurityUtils.getCurrentUserEmail();
        return reviewRepository.findByCustomer_User_EmailOrderByCreatedAtDesc(email)
                .stream().map(this::toResponse).toList();
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getJob().getId(),
                review.getCustomer().getUser().getName(),
                review.getTechnician().getUser().getName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}