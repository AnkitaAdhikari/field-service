package com.example.field_service.field_service.repository;

import com.example.field_service.field_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUser_Email(String email);

    boolean existsByUser_Email(String email);

    List<Customer> findByUser_NameContainingIgnoreCaseOrCityContainingIgnoreCase(
            String name, String city);
}