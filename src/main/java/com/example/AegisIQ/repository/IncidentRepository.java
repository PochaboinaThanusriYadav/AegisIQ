package com.example.AegisIQ.repository;

import com.example.AegisIQ.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByUserUserId(Long userId);
    List<Incident> findByStatus(String status);
    List<Incident> findByLocationContaining(String location);
}
