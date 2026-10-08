package com.example.field_service.field_service.repository;

import com.example.field_service.field_service.entity.RequestStatus;
import com.example.field_service.field_service.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByCustomer_User_EmailOrderByCreatedAtDesc(String email);

    List<ServiceRequest> findByTechnician_User_EmailOrderByCreatedAtDesc(String email);

    List<ServiceRequest> findByStatus(RequestStatus status);
}