package com.example.AegisIQ.repository;

import com.example.AegisIQ.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {
    Optional<RiskAssessment> findByIncidentIncidentId(Long incidentId);
    List<RiskAssessment> findBySeverityLevel(String severityLevel);
}
