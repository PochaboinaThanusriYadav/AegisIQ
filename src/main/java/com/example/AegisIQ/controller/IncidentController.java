package com.example.AegisIQ.controller;

import com.example.AegisIQ.dto.IncidentRequest;
import com.example.AegisIQ.dto.IncidentResponse;
import com.example.AegisIQ.entity.Incident;
import com.example.AegisIQ.entity.RiskAssessment;
import com.example.AegisIQ.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidents")
@CrossOrigin(origins = "*")
public class IncidentController {
    
    @Autowired
    private IncidentService incidentService;
    
    /**
     * Report a new incident
     */
    @PostMapping("/report")
    public ResponseEntity<?> reportIncident(
            @RequestBody IncidentRequest request,
            @RequestParam Long userId) {
        try {
            Incident incident = new Incident();
            incident.setDescription(request.getDescription());
            incident.setLocation(request.getLocation());
            incident.setImageUrl(request.getImageUrl());
            
            Incident savedIncident = incidentService.reportIncident(incident, userId);
            
            IncidentResponse response = convertToResponse(savedIncident);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all incidents
     */
    @GetMapping
    public ResponseEntity<List<IncidentResponse>> getAllIncidents() {
        List<Incident> incidents = incidentService.getAllIncidents();
        List<IncidentResponse> responses = new ArrayList<>();
        
        for (Incident incident : incidents) {
            responses.add(convertToResponse(incident));
        }
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Get incident by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getIncidentById(@PathVariable Long id) {
        return incidentService.getIncidentById(id)
            .map(incident -> ResponseEntity.ok(convertToResponse(incident)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get incidents by user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IncidentResponse>> getIncidentsByUser(@PathVariable Long userId) {
        List<Incident> incidents = incidentService.getIncidentsByUserId(userId);
        List<IncidentResponse> responses = new ArrayList<>();
        
        for (Incident incident : incidents) {
            responses.add(convertToResponse(incident));
        }
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Get incidents by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<IncidentResponse>> getIncidentsByStatus(@PathVariable String status) {
        List<Incident> incidents = incidentService.getIncidentsByStatus(status);
        List<IncidentResponse> responses = new ArrayList<>();
        
        for (Incident incident : incidents) {
            responses.add(convertToResponse(incident));
        }
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Update incident status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateIncidentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            Incident incident = incidentService.updateIncidentStatus(id, status);
            return ResponseEntity.ok(convertToResponse(incident));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Helper method to convert Incident to IncidentResponse
     */
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
