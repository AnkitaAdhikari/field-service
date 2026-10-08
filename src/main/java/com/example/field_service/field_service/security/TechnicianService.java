package com.example.field_service.field_service.service;

import com.example.field_service.field_service.dto.*;
import com.example.field_service.field_service.entity.Availability;
import com.example.field_service.field_service.entity.Technician;
import com.example.field_service.field_service.entity.User;
import com.example.field_service.field_service.exception.TechnicianAlreadyExistsException;
import com.example.field_service.field_service.exception.TechnicianNotFoundException;
import com.example.field_service.field_service.repository.TechnicianRepository;
import com.example.field_service.field_service.repository.UserRepository;
import com.example.field_service.field_service.security.SecurityUtils;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechnicianService {

    private final TechnicianRepository technicianRepository;
    private final UserRepository userRepository;

    public TechnicianService(TechnicianRepository technicianRepository,
                             UserRepository userRepository) {
        this.technicianRepository = technicianRepository;
        this.userRepository = userRepository;
    }

    public TechnicianResponse createMyProfile(TechnicianRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();

        if (technicianRepository.existsByUser_Email(email)) {
            throw new TechnicianAlreadyExistsException(
                    "A technician profile already exists for this account");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found: " + email));

        Technician technician = new Technician();
        technician.setUser(user);
        applyRequest(technician, request);
        // New technicians start OFFLINE — they opt into AVAILABLE explicitly,
        // so nobody gets assigned jobs before they're actually ready to work.
        technician.setAvailability(Availability.OFFLINE);

        Technician saved = technicianRepository.save(technician);
        return toResponse(saved);
    }

    public TechnicianResponse getMyProfile() {
        return toResponse(findByEmailOrThrow(SecurityUtils.getCurrentUserEmail()));
    }

    public TechnicianResponse updateMyProfile(TechnicianRequest request) {
        Technician technician = findByEmailOrThrow(SecurityUtils.getCurrentUserEmail());
        applyRequest(technician, request);
        return toResponse(technicianRepository.save(technician));
    }

    public TechnicianResponse updateMyAvailability(AvailabilityRequest request) {
        Technician technician = findByEmailOrThrow(SecurityUtils.getCurrentUserEmail());
        technician.setAvailability(request.getAvailability());
        return toResponse(technicianRepository.save(technician));
    }

    public TechnicianResponse updateMyLocation(LocationRequest request) {
        Technician technician = findByEmailOrThrow(SecurityUtils.getCurrentUserEmail());
        technician.setLatitude(request.getLatitude());
        technician.setLongitude(request.getLongitude());
        return toResponse(technicianRepository.save(technician));
    }

    public void deleteMyProfile() {
        technicianRepository.delete(findByEmailOrThrow(SecurityUtils.getCurrentUserEmail()));
    }

    // Used by Job Assignment later: only AVAILABLE technicians with the skill.
    public List<Technician> findAvailableWithSkill(String skill) {
        return technicianRepository.findByAvailabilityAndSkill(Availability.AVAILABLE, skill);
    }

    // Admin search — any technician with a given skill, regardless of status.
    public List<TechnicianResponse> searchBySkill(String skill) {
        return technicianRepository.findBySkill(skill)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Technician findByEmailOrThrow(String email) {
        return technicianRepository.findByUser_Email(email)
                .orElseThrow(() -> new TechnicianNotFoundException(
                        "No technician profile found for this account"));
    }

    private void applyRequest(Technician technician, TechnicianRequest request) {
        technician.setPhone(request.getPhone());
        technician.setSkills(request.getSkills());
        technician.setAddress(request.getAddress());
        technician.setLatitude(request.getLatitude());
        technician.setLongitude(request.getLongitude());
    }

    private TechnicianResponse toResponse(Technician technician) {
        User user = technician.getUser();
        return new TechnicianResponse(
                technician.getId(),
                user.getName(),
                user.getEmail(),
                technician.getPhone(),
                technician.getSkills(),
                technician.getAvailability(),
                technician.getAddress(),
                technician.getLatitude(),
                technician.getLongitude(),
                technician.getCreatedAt()
        );
    }
}