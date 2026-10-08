package com.example.field_service.field_service.repository;

import com.example.field_service.field_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByInvoice_Id(Long invoiceId);

    List<Payment> findByInvoice_Customer_User_EmailOrderByCreatedAtDesc(String email);
}