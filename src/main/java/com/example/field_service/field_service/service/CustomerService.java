package com.example.field_service.field_service.service;

import com.example.field_service.field_service.dto.CustomerRequest;
import com.example.field_service.field_service.dto.CustomerResponse;
import com.example.field_service.field_service.entity.Customer;
import com.example.field_service.field_service.entity.User;
import com.example.field_service.field_service.exception.CustomerAlreadyExistsException;
import com.example.field_service.field_service.exception.CustomerNotFoundException;
import com.example.field_service.field_service.repository.CustomerRepository;
import com.example.field_service.field_service.repository.UserRepository;
import com.example.field_service.field_service.security.SecurityUtils;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerService(CustomerRepository customerRepository,
                           UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    public CustomerResponse createMyProfile(CustomerRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();

        if (customerRepository.existsByUser_Email(email)) {
            throw new CustomerAlreadyExistsException(
                    "A customer profile already exists for this account");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found: " + email));

        Customer customer = new Customer();
        customer.setUser(user);
        applyRequest(customer, request);

        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    public CustomerResponse getMyProfile() {
        String email = SecurityUtils.getCurrentUserEmail();
        Customer customer = findByEmailOrThrow(email);
        return toResponse(customer);
    }

    public CustomerResponse updateMyProfile(CustomerRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        Customer customer = findByEmailOrThrow(email);
        applyRequest(customer, request);
        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    public void deleteMyProfile() {
        String email = SecurityUtils.getCurrentUserEmail();
        Customer customer = findByEmailOrThrow(email);
        customerRepository.delete(customer);
    }

    public List<CustomerResponse> search(String query) {
        return customerRepository
                .findByUser_NameContainingIgnoreCaseOrCityContainingIgnoreCase(query, query)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Customer findByEmailOrThrow(String email) {
        return customerRepository.findByUser_Email(email)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "No customer profile found for this account"));
    }

    private void applyRequest(Customer customer, CustomerRequest request) {
        customer.setPhone(request.getPhone());
        customer.setAddressLine(request.getAddressLine());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setZipCode(request.getZipCode());
    }

    private CustomerResponse toResponse(Customer customer) {
        User user = customer.getUser();
        return new CustomerResponse(
                customer.getId(),
                user.getName(),
                user.getEmail(),
                customer.getPhone(),
                customer.getAddressLine(),
                customer.getCity(),
                customer.getState(),
                customer.getZipCode(),
                customer.getCreatedAt()
        );
    }
}