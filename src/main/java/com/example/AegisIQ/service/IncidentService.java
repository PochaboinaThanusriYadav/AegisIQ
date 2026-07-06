package com.example.AegisIQ.service;

import com.example.AegisIQ.entity.Incident;
import com.example.AegisIQ.entity.RiskAssessment;
import com.example.AegisIQ.entity.User;
import com.example.AegisIQ.repository.IncidentRepository;
import com.example.AegisIQ.repository.RiskAssessmentRepository;
import com.example.AegisIQ.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {

    private static final Logger logger = LoggerFactory.getLogger(IncidentService.class);

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

    @Autowired
    private EmailService emailService;

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Report a new incident and perform AI analysis
     */
    @Transactional
    public Incident reportIncident(Incident incident, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        incident.setUser(user);
        incident.setStatus("PENDING");

        Incident savedIncident = incidentRepository.save(incident);

        // Perform AI risk analysis
        RiskAssessment riskAssessment = aiRiskAnalysisService.analyzeIncident(savedIncident);
        riskAssessmentRepository.save(riskAssessment);

        savedIncident.setRiskAssessment(riskAssessment);
        savedIncident.setStatus("UNDER_REVIEW");
        incidentRepository.save(savedIncident);

        // Broadcast via WebSocket
        if (messagingTemplate != null) {
            try {
                messagingTemplate.convertAndSend("/topic/incidents", savedIncident.getIncidentId());
            } catch (Exception e) {
                logger.warn("WebSocket broadcast failed: {}", e.getMessage());
            }
        }

        // Send alerts if priority is medium or high
        if ("HIGH".equals(riskAssessment.getPriorityClassification()) ||
                "MEDIUM".equals(riskAssessment.getPriorityClassification())) {
            alertService.sendAlert(savedIncident, riskAssessment);

            // Email responders
            if (user.getEmail() != null) {
                emailService.sendEmergencyAlertEmail(user.getEmail(), savedIncident, riskAssessment);
            }
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
     * Search incidents with pagination and optional filters
     */
    public Page<Incident> searchIncidents(String status, String search, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        String statusParam = (status == null || status.isBlank()) ? null : status;
        String searchParam = (search == null || search.isBlank()) ? null : search;
        return incidentRepository.searchIncidents(statusParam, searchParam, pageable);
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
