package com.example.field_service.field_service.controller;

import com.example.field_service.field_service.dto.CustomerRequest;
import com.example.field_service.field_service.dto.CustomerResponse;
import com.example.field_service.field_service.service.CustomerService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createMyProfile(@Valid @RequestBody CustomerRequest request) {
        return customerService.createMyProfile(request);
    }

    @GetMapping("/me")
    public CustomerResponse getMyProfile() {
        return customerService.getMyProfile();
    }

    @PutMapping("/me")
    public CustomerResponse updateMyProfile(@Valid @RequestBody CustomerRequest request) {
        return customerService.updateMyProfile(request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyProfile() {
        customerService.deleteMyProfile();
    }

    @GetMapping("/search")
    public List<CustomerResponse> search(@RequestParam String query) {
        return customerService.search(query);
    }
}