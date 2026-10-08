package com.example.field_service.field_service.dto;

import com.example.field_service.field_service.entity.Priority;
import jakarta.validation.constraints.NotBlank;

public class ServiceRequestCreateRequest {

    @NotBlank(message = "Required skill is required")
    private String requiredSkill;

    @NotBlank(message = "Description is required")
    private String description;

    // Optional — defaults to MEDIUM in the service layer if not supplied.
    private Priority priority;

    private String address;
    private Double latitude;
    private Double longitude;

    public String getRequiredSkill() { return requiredSkill; }
    public void setRequiredSkill(String requiredSkill) { this.requiredSkill = requiredSkill; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}