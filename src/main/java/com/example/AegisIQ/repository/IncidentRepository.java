package com.example.AegisIQ.repository;

import com.example.AegisIQ.entity.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByUserUserId(Long userId);

    List<Incident> findByStatus(String status);

    List<Incident> findByLocationContaining(String location);

    // Paginated + filtered search
    @Query("SELECT i FROM Incident i WHERE " +
            "(:status IS NULL OR i.status = :status) AND " +
            "(:search IS NULL OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "  OR LOWER(i.location) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Incident> searchIncidents(
            @Param("status") String status,
            @Param("search") String search,
            Pageable pageable);
}
