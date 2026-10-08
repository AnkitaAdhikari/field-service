package com.example.field_service.field_service.controller;

import com.example.field_service.field_service.dto.*;
import com.example.field_service.field_service.service.ServiceRequestService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    public ServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceRequestResponse create(@Valid @RequestBody ServiceRequestCreateRequest request) {
        return serviceRequestService.create(request);
    }

    @GetMapping("/{id}")
    public ServiceRequestResponse getById(@PathVariable Long id) {
        return serviceRequestService.getById(id);
    }

    @GetMapping("/me")
    public List<ServiceRequestResponse> getMyRequests() {
        return serviceRequestService.getMyRequestsAsCustomer();
    }

    @GetMapping("/assigned-to-me")
    public List<ServiceRequestResponse> getMyAssignedRequests() {
        return serviceRequestService.getMyAssignedRequests();
    }

    @PatchMapping("/{id}/status")
    public ServiceRequestResponse updateStatus(@PathVariable Long id,
                                               @Valid @RequestBody StatusUpdateRequest request) {
        return serviceRequestService.updateStatus(id, request);
    }

    @PatchMapping("/{id}/cancel")
    public ServiceRequestResponse cancel(@PathVariable Long id) {
        return serviceRequestService.cancel(id);
    }

    // Admin-only retry when a request stayed PENDING (no technician was free at creation time).
    @PostMapping("/{id}/retry-assignment")
    public ServiceRequestResponse retryAssignment(@PathVariable Long id) {
        return serviceRequestService.retryAssignment(id);
    }
}