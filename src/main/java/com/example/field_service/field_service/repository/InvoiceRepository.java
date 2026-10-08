package com.example.field_service.field_service.repository;

import com.example.field_service.field_service.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByJob_Id(Long jobId);

    List<Invoice> findByCustomer_User_EmailOrderByIssuedAtDesc(String email);

    List<Invoice> findByTechnician_User_EmailOrderByIssuedAtDesc(String email);
}