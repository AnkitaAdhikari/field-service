package com.example.field_service.field_service.repository;

import com.example.field_service.field_service.entity.Availability;
import com.example.field_service.field_service.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TechnicianRepository extends JpaRepository<Technician, Long> {

    Optional<Technician> findByUser_Email(String email);

    boolean existsByUser_Email(String email);

    List<Technician> findByAvailability(Availability availability);

    // Used later by Job Assignment: find technicians who are AVAILABLE
    // and have the required skill, ready to be filtered further by location.
    @Query("SELECT t FROM Technician t JOIN t.skills s " +
            "WHERE t.availability = :availability AND LOWER(s) = LOWER(:skill)")
    List<Technician> findByAvailabilityAndSkill(
            @Param("availability") Availability availability,
            @Param("skill") String skill);

    @Query("SELECT t FROM Technician t JOIN t.skills s WHERE LOWER(s) = LOWER(:skill)")
    List<Technician> findBySkill(@Param("skill") String skill);
}