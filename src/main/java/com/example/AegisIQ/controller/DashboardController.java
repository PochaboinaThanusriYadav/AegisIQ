package com.example.AegisIQ.controller;

import com.example.AegisIQ.dto.IncidentResponse;
import com.example.AegisIQ.service.AlertService;
import com.example.AegisIQ.service.IncidentService;
import com.example.AegisIQ.entity.Incident;
import com.example.AegisIQ.entity.Alert;
import com.example.AegisIQ.entity.RiskAssessment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private IncidentService incidentService;
    
    @Autowired
    private AlertService alertService;
    
    /**
     * Get dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        List<Incident> allIncidents = incidentService.getAllIncidents();
        List<Alert> allAlerts = alertService.getAllAlerts();
        
        Map<String, Object> stats = new HashMap<>();
        
        // Total incidents
        stats.put("totalIncidents", allIncidents.size());
        
        // Incidents by status
        long pending = allIncidents.stream().filter(i -> i.getStatus().equals("PENDING")).count();
        long underReview = allIncidents.stream().filter(i -> i.getStatus().equals("UNDER_REVIEW")).count();
        long resolved = allIncidents.stream().filter(i -> i.getStatus().equals("RESOLVED")).count();
        
        Map<String, Long> statusBreakdown = new HashMap<>();
        statusBreakdown.put("pending", pending);
        statusBreakdown.put("underReview", underReview);
        statusBreakdown.put("resolved", resolved);
        stats.put("statusBreakdown", statusBreakdown);
        
        // Priority breakdown
        long highPriority = allIncidents.stream()
            .filter(i -> i.getRiskAssessment() != null && 
                        i.getRiskAssessment().getPriorityClassification().equals("HIGH"))
            .count();
        long mediumPriority = allIncidents.stream()
            .filter(i -> i.getRiskAssessment() != null && 
                        i.getRiskAssessment().getPriorityClassification().equals("MEDIUM"))
            .count();
        long lowPriority = allIncidents.stream()
            .filter(i -> i.getRiskAssessment() != null && 
                        i.getRiskAssessment().getPriorityClassification().equals("LOW"))
            .count();
        
        Map<String, Long> priorityBreakdown = new HashMap<>();
        priorityBreakdown.put("high", highPriority);
        priorityBreakdown.put("medium", mediumPriority);
        priorityBreakdown.put("low", lowPriority);
        stats.put("priorityBreakdown", priorityBreakdown);
        
        // Total alerts
        stats.put("totalAlerts", allAlerts.size());
        
        // Alert breakdown
        long sentAlerts = allAlerts.stream().filter(a -> a.getStatus().equals("SENT")).count();
        long pendingAlerts = allAlerts.stream().filter(a -> a.getStatus().equals("PENDING")).count();
        
        Map<String, Long> alertBreakdown = new HashMap<>();
        alertBreakdown.put("sent", sentAlerts);
        alertBreakdown.put("pending", pendingAlerts);
        stats.put("alertBreakdown", alertBreakdown);
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get recent incidents
     */
    @GetMapping("/recent-incidents")
    public ResponseEntity<List<IncidentResponse>> getRecentIncidents(@RequestParam(defaultValue = "10") int limit) {
        List<Incident> allIncidents = incidentService.getAllIncidents();
        
        List<IncidentResponse> recentIncidents = allIncidents.stream()
            .sorted((i1, i2) -> i2.getTimestamp().compareTo(i1.getTimestamp()))
            .limit(limit)
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(recentIncidents);
    }

    private IncidentResponse convertToResponse(Incident incident) {
        IncidentResponse response = new IncidentResponse();
        response.setIncidentId(incident.getIncidentId());
        response.setDescription(incident.getDescription());
        response.setLocation(incident.getLocation());
        response.setImageUrl(incident.getImageUrl());
        response.setStatus(incident.getStatus());
        response.setTimestamp(incident.getTimestamp());
        response.setUserId(incident.getUser().getUserId());
        response.setUserName(incident.getUser().getName());

        if (incident.getRiskAssessment() != null) {
            RiskAssessment ra = incident.getRiskAssessment();
            response.setCredibilityScore(ra.getCredibilityScore());
            response.setSeverityLevel(ra.getSeverityLevel());
            response.setPriorityClassification(ra.getPriorityClassification());
            response.setRecommendedAction(ra.getRecommendedAction());
        }

        return response;
    }
}
