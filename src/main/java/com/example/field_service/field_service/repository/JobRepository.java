package com.example.field_service.field_service.repository;

import com.example.field_service.field_service.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    Optional<Job> findByServiceRequest_Id(Long serviceRequestId);

    List<Job> findByTechnician_User_EmailOrderByCreatedAtDesc(String email);
}