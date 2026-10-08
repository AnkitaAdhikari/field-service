package com.example.field_service.field_service.service;

import com.example.field_service.field_service.dto.JobResponse;
import com.example.field_service.field_service.entity.*;
import com.example.field_service.field_service.exception.InvalidStatusTransitionException;
import com.example.field_service.field_service.exception.JobNotFoundException;
import com.example.field_service.field_service.exception.UnauthorizedAccessException;
import com.example.field_service.field_service.repository.JobRepository;
import com.example.field_service.field_service.repository.ServiceRequestRepository;
import com.example.field_service.field_service.repository.TechnicianRepository;
import com.example.field_service.field_service.security.SecurityUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final TechnicianRepository technicianRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final InvoiceService invoiceService;
    private final NotificationService notificationService;

    public JobService(JobRepository jobRepository,
                      TechnicianRepository technicianRepository,
                      ServiceRequestRepository serviceRequestRepository,
                      InvoiceService invoiceService,
                      NotificationService notificationService) {
        this.jobRepository = jobRepository;
        this.technicianRepository = technicianRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.invoiceService = invoiceService;
        this.notificationService = notificationService;
    }

    // Called by ServiceRequestService right after a technician is assigned.
    @Transactional
    public Job createForServiceRequest(ServiceRequest serviceRequest, Technician technician) {
        Job job = new Job();
        job.setServiceRequest(serviceRequest);
        job.setTechnician(technician);
        job.setStatus(JobStatus.ASSIGNED);
        return jobRepository.save(job);
    }

    @Transactional
    public JobResponse accept(Long jobId) {
        Job job = requireOwnTechnician(jobId);
        requireStatus(job, JobStatus.ASSIGNED, JobStatus.ACCEPTED);
        job.setStatus(JobStatus.ACCEPTED);
        job.setAcceptedAt(LocalDateTime.now());

        notificationService.send(job.getServiceRequest().getCustomer().getUser(), NotificationType.JOB_ACCEPTED,
                "Your technician has accepted the job.");

        return toResponse(jobRepository.save(job));
    }

    @Transactional
    public JobResponse start(Long jobId) {
        Job job = requireOwnTechnician(jobId);
        requireStatus(job, JobStatus.ACCEPTED, JobStatus.IN_PROGRESS);
        job.setStatus(JobStatus.IN_PROGRESS);
        job.setStartedAt(LocalDateTime.now());

        ServiceRequest sr = job.getServiceRequest();
        sr.setStatus(RequestStatus.IN_PROGRESS);
        serviceRequestRepository.save(sr);

        notificationService.send(sr.getCustomer().getUser(), NotificationType.JOB_STARTED,
                "Your technician has started the job.");

        return toResponse(jobRepository.save(job));
    }

    @Transactional
    public JobResponse complete(Long jobId) {
        Job job = requireOwnTechnician(jobId);
        requireStatus(job, JobStatus.IN_PROGRESS, JobStatus.COMPLETED);
        job.setStatus(JobStatus.COMPLETED);
        job.setCompletedAt(LocalDateTime.now());
        Job saved = jobRepository.save(job);

        ServiceRequest sr = job.getServiceRequest();
        sr.setStatus(RequestStatus.COMPLETED);
        serviceRequestRepository.save(sr);

        // Free the technician for the next job.
        Technician technician = job.getTechnician();
        technician.setAvailability(Availability.AVAILABLE);
        technicianRepository.save(technician);

        // Generate the invoice the moment work is confirmed done.
        invoiceService.generateForJob(saved);

        notificationService.send(sr.getCustomer().getUser(), NotificationType.JOB_COMPLETED,
                "Your job is complete. Invoice has been generated.");

        return toResponse(saved);
    }

    // Technician-initiated cancel (e.g. can't make it) — different from the
    // customer's cancel on ServiceRequest. Releases the technician AND puts
    // the request back to PENDING so it can be reassigned, instead of dead-ending it.
    @Transactional
    public JobResponse cancel(Long jobId) {
        Job job = requireOwnTechnician(jobId);

        if (job.getStatus() == JobStatus.COMPLETED || job.getStatus() == JobStatus.CANCELLED) {
            throw new InvalidStatusTransitionException(
                    "Cannot cancel a job that is already " + job.getStatus());
        }

        job.setStatus(JobStatus.CANCELLED);
        job.setCancelledAt(LocalDateTime.now());
        Job saved = jobRepository.save(job);

        Technician technician = job.getTechnician();
        technician.setAvailability(Availability.AVAILABLE);
        technicianRepository.save(technician);

        ServiceRequest sr = job.getServiceRequest();
        sr.setTechnician(null);
        sr.setStatus(RequestStatus.PENDING);
        serviceRequestRepository.save(sr);

        return toResponse(saved);
    }

    public List<JobResponse> getMyJobs() {
        String email = SecurityUtils.getCurrentUserEmail();
        return jobRepository.findByTechnician_User_EmailOrderByCreatedAtDesc(email)
                .stream().map(this::toResponse).toList();
    }

    private Job requireOwnTechnician(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found: " + jobId));

        String email = SecurityUtils.getCurrentUserEmail();
        if (!job.getTechnician().getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("Only the assigned technician can act on this job");
        }
        return job;
    }

    private void requireStatus(Job job, JobStatus expectedCurrent, JobStatus target) {
        if (job.getStatus() != expectedCurrent) {
            throw new InvalidStatusTransitionException(
                    "Cannot move job from " + job.getStatus() + " to " + target +
                            " (expected current status " + expectedCurrent + ")");
        }
    }

    private JobResponse toResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getServiceRequest().getId(),
                job.getTechnician().getUser().getName(),
                job.getServiceRequest().getCustomer().getUser().getName(),
                job.getStatus(),
                job.getAcceptedAt(),
                job.getStartedAt(),
                job.getCompletedAt(),
                job.getCancelledAt()
        );
    }
}