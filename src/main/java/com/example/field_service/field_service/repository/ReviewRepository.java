package com.example.field_service.field_service.repository;

import com.example.field_service.field_service.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByJob_Id(Long jobId);

    boolean existsByJob_Id(Long jobId);

    List<Review> findByTechnician_IdOrderByCreatedAtDesc(Long technicianId);

    List<Review> findByCustomer_User_EmailOrderByCreatedAtDesc(String email);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.technician.id = :technicianId")
    Double findAverageRatingByTechnicianId(@Param("technicianId") Long technicianId);

    long countByTechnician_Id(Long technicianId);
}