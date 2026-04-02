package com.example.AegisIQ.repository;

import com.example.AegisIQ.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByIncidentIncidentId(Long incidentId);
    List<Alert> findByStatus(String status);
    List<Alert> findByResponderUserId(Long responderId);
}
