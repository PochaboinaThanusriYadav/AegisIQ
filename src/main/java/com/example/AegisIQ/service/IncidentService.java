package com.example.AegisIQ.service;

import com.example.AegisIQ.entity.Incident;
import com.example.AegisIQ.entity.RiskAssessment;
import com.example.AegisIQ.entity.User;
import com.example.AegisIQ.repository.IncidentRepository;
import com.example.AegisIQ.repository.RiskAssessmentRepository;
import com.example.AegisIQ.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {
    
    @Autowired
    private IncidentRepository incidentRepository;
    
    @Autowired
    private RiskAssessmentRepository riskAssessmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AIRiskAnalysisService aiRiskAnalysisService;
    
    @Autowired
    private AlertService alertService;
    
    /**
     * Report a new incident and perform AI analysis
     */
    @Transactional
    public Incident reportIncident(Incident incident, Long userId) {
        // Find user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        incident.setUser(user);
        incident.setStatus("PENDING");
        
        // Save incident
        Incident savedIncident = incidentRepository.save(incident);
        
        // Perform AI risk analysis
        RiskAssessment riskAssessment = aiRiskAnalysisService.analyzeIncident(savedIncident);
        riskAssessmentRepository.save(riskAssessment);
        
        savedIncident.setRiskAssessment(riskAssessment);
        savedIncident.setStatus("UNDER_REVIEW");
        incidentRepository.save(savedIncident);
        
        // Send alerts if priority is medium or high
        if (riskAssessment.getPriorityClassification().equals("HIGH") || 
            riskAssessment.getPriorityClassification().equals("MEDIUM")) {
            alertService.sendAlert(savedIncident, riskAssessment);
        }
        
        return savedIncident;
    }
    
    /**
     * Get all incidents
     */
    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }
    
    /**
     * Get incident by ID
     */
    public Optional<Incident> getIncidentById(Long id) {
        return incidentRepository.findById(id);
    }
    
    /**
     * Get incidents by user
     */
    public List<Incident> getIncidentsByUserId(Long userId) {
        return incidentRepository.findByUserUserId(userId);
    }
    
    /**
     * Get incidents by status
     */
    public List<Incident> getIncidentsByStatus(String status) {
        return incidentRepository.findByStatus(status);
    }
    
    /**
     * Update incident status
     */
    @Transactional
    public Incident updateIncidentStatus(Long incidentId, String status) {
        Incident incident = incidentRepository.findById(incidentId)
            .orElseThrow(() -> new RuntimeException("Incident not found"));
        
        incident.setStatus(status);
        return incidentRepository.save(incident);
    }
    
    /**
     * Get risk assessment for incident
     */
    public Optional<RiskAssessment> getRiskAssessmentByIncidentId(Long incidentId) {
        return riskAssessmentRepository.findByIncidentIncidentId(incidentId);
    }
}
