package com.example.field_service.field_service.dto;

import com.example.field_service.field_service.entity.Availability;

import java.time.LocalDateTime;
import java.util.Set;

public class TechnicianResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Set<String> skills;
    private Availability availability;
    private String address;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;

    public TechnicianResponse(Long id, String name, String email, String phone,
                              Set<String> skills, Availability availability,
                              String address, Double latitude, Double longitude,
                              LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.skills = skills;
        this.availability = availability;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Set<String> getSkills() { return skills; }
    public Availability getAvailability() { return availability; }
    public String getAddress() { return address; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}