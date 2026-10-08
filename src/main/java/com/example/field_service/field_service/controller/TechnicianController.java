package com.example.field_service.field_service.controller;

import com.example.field_service.field_service.dto.*;
import com.example.field_service.field_service.service.TechnicianService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {

    private final TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    public TechnicianResponse createMyProfile(@Valid @RequestBody TechnicianRequest request) {
        return technicianService.createMyProfile(request);
    }

    @GetMapping("/me")
    public TechnicianResponse getMyProfile() {
        return technicianService.getMyProfile();
    }

    @PutMapping("/me")
    public TechnicianResponse updateMyProfile(@Valid @RequestBody TechnicianRequest request) {
        return technicianService.updateMyProfile(request);
    }

    @PatchMapping("/me/availability")
    public TechnicianResponse updateMyAvailability(@Valid @RequestBody AvailabilityRequest request) {
        return technicianService.updateMyAvailability(request);
    }

    @PatchMapping("/me/location")
    public TechnicianResponse updateMyLocation(@Valid @RequestBody LocationRequest request) {
        return technicianService.updateMyLocation(request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyProfile() {
        technicianService.deleteMyProfile();
    }

    @GetMapping("/search")
    public List<TechnicianResponse> search(@RequestParam String skill) {
        return technicianService.searchBySkill(skill);
    }
}