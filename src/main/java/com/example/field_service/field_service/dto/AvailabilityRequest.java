package com.example.field_service.field_service.dto;

import com.example.field_service.field_service.entity.Availability;
import jakarta.validation.constraints.NotNull;

public class AvailabilityRequest {

    @NotNull(message = "Availability is required")
    private Availability availability;

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }
}