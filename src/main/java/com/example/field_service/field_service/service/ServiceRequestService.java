package com.example.field_service.field_service.service;

import com.example.field_service.field_service.dto.*;
import com.example.field_service.field_service.entity.*;
import com.example.field_service.field_service.exception.*;
import com.example.field_service.field_service.repository.CustomerRepository;
import com.example.field_service.field_service.repository.ServiceRequestRepository;
import com.example.field_service.field_service.repository.TechnicianRepository;
import com.example.field_service.field_service.security.SecurityUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final CustomerRepository customerRepository;
    private final TechnicianRepository technicianRepository;
    private final JobService jobService;
    private final NotificationService notificationService;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
                                 CustomerRepository customerRepository,
                                 TechnicianRepository technicianRepository,
                                 JobService jobService,
                                 NotificationService notificationService) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.customerRepository = customerRepository;
        this.technicianRepository = technicianRepository;
        this.jobService = jobService;
        this.notificationService = notificationService;
    }

    @Transactional
    public ServiceRequestResponse create(ServiceRequestCreateRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();

        Customer customer = customerRepository.findByUser_Email(email)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Complete your customer profile before creating a service request"));

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setCustomer(customer);
        serviceRequest.setRequiredSkill(request.getRequiredSkill());
        serviceRequest.setDescription(request.getDescription());
        serviceRequest.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
        serviceRequest.setStatus(RequestStatus.PENDING);

        // Falls back to the customer's own address if no job-specific location given.
        serviceRequest.setAddress(request.getAddress() != null ? request.getAddress() : customer.getAddressLine());
        serviceRequest.setLatitude(request.getLatitude());
        serviceRequest.setLongitude(request.getLongitude());

        ServiceRequest saved = serviceRequestRepository.save(serviceRequest);

        // Attempt immediate auto-assignment. If nobody is free right now,
        // the request simply stays PENDING — it is not an error.
        tryAssignTechnician(saved);

        return toResponse(serviceRequestRepository.save(saved));
    }

    // Retries assignment for a request that stayed PENDING (e.g. admin
    // re-triggers after a technician comes back online).
    @Transactional
    public ServiceRequestResponse retryAssignment(Long requestId) {
        ServiceRequest serviceRequest = findByIdOrThrow(requestId);

        if (serviceRequest.getStatus() != RequestStatus.PENDING) {
            throw new InvalidStatusTransitionException(
                    "Only PENDING requests can be assigned; current status is " + serviceRequest.getStatus());
        }

        tryAssignTechnician(serviceRequest);
        return toResponse(serviceRequestRepository.save(serviceRequest));
    }

    private void tryAssignTechnician(ServiceRequest serviceRequest) {
        List<Technician> candidates =
                technicianRepository.findByAvailabilityAndSkill(Availability.AVAILABLE, serviceRequest.getRequiredSkill());

        if (candidates.isEmpty()) {
            // No skilled + available technician right now — leave PENDING.
            return;
        }

        // Simple pick-first strategy for now; nearest-technician-by-location
        // is a natural upgrade once distance calculation is added.
        Technician technician = candidates.get(0);

        serviceRequest.setTechnician(technician);
        serviceRequest.setStatus(RequestStatus.ASSIGNED);

        technician.setAvailability(Availability.BUSY);
        technicianRepository.save(technician);
        jobService.createForServiceRequest(serviceRequest, technician);
        notificationService.send(technician.getUser(), NotificationType.JOB_ASSIGNED,
                "You've been assigned a new " + serviceRequest.getRequiredSkill() + " job.");
    }

    public ServiceRequestResponse getById(Long id) {
        ServiceRequest serviceRequest = findByIdOrThrow(id);
        assertCallerCanView(serviceRequest);
        return toResponse(serviceRequest);
    }

    public List<ServiceRequestResponse> getMyRequestsAsCustomer() {
        String email = SecurityUtils.getCurrentUserEmail();
        return serviceRequestRepository.findByCustomer_User_EmailOrderByCreatedAtDesc(email)
                .stream().map(this::toResponse).toList();
    }

    public List<ServiceRequestResponse> getMyAssignedRequests() {
        String email = SecurityUtils.getCurrentUserEmail();
        return serviceRequestRepository.findByTechnician_User_EmailOrderByCreatedAtDesc(email)
                .stream().map(this::toResponse).toList();
    }

    // Technician-driven transitions: ASSIGNED -> IN_PROGRESS -> COMPLETED.
    @Transactional
    public ServiceRequestResponse updateStatus(Long id, StatusUpdateRequest request) {
        ServiceRequest serviceRequest = findByIdOrThrow(id);
        String email = SecurityUtils.getCurrentUserEmail();

        if (serviceRequest.getTechnician() == null ||
                !serviceRequest.getTechnician().getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("Only the assigned technician can update this request's status");
        }

        RequestStatus current = serviceRequest.getStatus();
        RequestStatus next = request.getStatus();

        boolean validTransition =
                (current == RequestStatus.ASSIGNED && next == RequestStatus.IN_PROGRESS) ||
                        (current == RequestStatus.IN_PROGRESS && next == RequestStatus.COMPLETED);

        if (!validTransition) {
            throw new InvalidStatusTransitionException(
                    "Cannot move from " + current + " to " + next);
        }

        serviceRequest.setStatus(next);

        // Job wrapped up — free the technician for the next assignment.
        if (next == RequestStatus.COMPLETED) {
            Technician technician = serviceRequest.getTechnician();
            technician.setAvailability(Availability.AVAILABLE);
            technicianRepository.save(technician);
        }

        return toResponse(serviceRequestRepository.save(serviceRequest));
    }

    // Customer-driven cancellation. Releases the technician's slot if one was assigned.
    @Transactional
    public ServiceRequestResponse cancel(Long id) {
        ServiceRequest serviceRequest = findByIdOrThrow(id);
        String email = SecurityUtils.getCurrentUserEmail();

        if (!serviceRequest.getCustomer().getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("Only the requesting customer can cancel this request");
        }

        if (serviceRequest.getStatus() == RequestStatus.COMPLETED ||
                serviceRequest.getStatus() == RequestStatus.CANCELLED) {
            throw new InvalidStatusTransitionException(
                    "Cannot cancel a request that is already " + serviceRequest.getStatus());
        }

        if (serviceRequest.getTechnician() != null) {
            Technician technician = serviceRequest.getTechnician();
            technician.setAvailability(Availability.AVAILABLE);
            technicianRepository.save(technician);
        }

        serviceRequest.setStatus(RequestStatus.CANCELLED);
        return toResponse(serviceRequestRepository.save(serviceRequest));
    }

    private void assertCallerCanView(ServiceRequest serviceRequest) {
        String email = SecurityUtils.getCurrentUserEmail();
        boolean isCustomer = serviceRequest.getCustomer().getUser().getEmail().equals(email);
        boolean isTechnician = serviceRequest.getTechnician() != null &&
                serviceRequest.getTechnician().getUser().getEmail().equals(email);

        if (!isCustomer && !isTechnician) {
            throw new UnauthorizedAccessException("You do not have access to this service request");
        }
    }

    private ServiceRequest findByIdOrThrow(Long id) {
        return serviceRequestRepository.findById(id)
                .orElseThrow(() -> new ServiceRequestNotFoundException("Service request not found: " + id));
    }

    private ServiceRequestResponse toResponse(ServiceRequest sr) {
        return new ServiceRequestResponse(
                sr.getId(),
                sr.getCustomer().getUser().getName(),
                sr.getCustomer().getUser().getEmail(),
                sr.getTechnician() != null ? sr.getTechnician().getUser().getName() : null,
                sr.getRequiredSkill(),
                sr.getDescription(),
                sr.getPriority(),
                sr.getStatus(),
                sr.getAddress(),
                sr.getLatitude(),
                sr.getLongitude(),
                sr.getCreatedAt(),
                sr.getUpdatedAt()
        );
    }
}