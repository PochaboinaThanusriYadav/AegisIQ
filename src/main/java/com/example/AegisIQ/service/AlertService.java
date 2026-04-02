package com.example.AegisIQ.service;

import com.example.AegisIQ.entity.Alert;
import com.example.AegisIQ.entity.Incident;
import com.example.AegisIQ.entity.RiskAssessment;
import com.example.AegisIQ.entity.User;
import com.example.AegisIQ.repository.AlertRepository;
import com.example.AegisIQ.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlertService {
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Send alert for an incident based on risk assessment
     */
    @Transactional
    public void sendAlert(Incident incident, RiskAssessment riskAssessment) {
        String alertType = determineAlertType(riskAssessment);
        
        // Create alert
        Alert alert = new Alert();
        alert.setIncident(incident);
        alert.setAlertType(alertType);
        alert.setStatus("SENT");
        
        // Get emergency responders (users with role RESPONDER or ADMIN)
        List<User> responders = userRepository.findAll().stream()
            .filter(user -> user.getRole().equals("RESPONDER") || user.getRole().equals("ADMIN"))
            .toList();
        
        // Send alert to each responder
        for (User responder : responders) {
            Alert responderAlert = new Alert();
            responderAlert.setIncident(incident);
            responderAlert.setAlertType(alertType);
            responderAlert.setStatus("SENT");
            responderAlert.setResponder(responder);
            responderAlert.setRecipientInfo(responder.getEmail() + ", " + responder.getPhone());
            
            alertRepository.save(responderAlert);
            
            // Simulate sending alert (in production, integrate with SMS/Email service)
            sendNotification(responder, incident, riskAssessment);
        }
    }
    
    /**
     * Determine alert type based on risk assessment
     */
    private String determineAlertType(RiskAssessment riskAssessment) {
        if (riskAssessment.getPriorityClassification().equals("HIGH")) {
            return "EMERGENCY";
        } else if (riskAssessment.getPriorityClassification().equals("MEDIUM")) {
            return "EMAIL";
        } else {
            return "SMS";
        }
    }
    
    /**
     * Simulate sending notification (In production, integrate with actual SMS/Email service)
     */
    private void sendNotification(User responder, Incident incident, RiskAssessment riskAssessment) {
        String message = String.format(
            "ALERT: New %s priority incident reported. Location: %s. Severity: %s. Credibility: %.0f%%. Action: %s",
            riskAssessment.getPriorityClassification(),
            incident.getLocation(),
            riskAssessment.getSeverityLevel(),
            riskAssessment.getCredibilityScore(),
            riskAssessment.getRecommendedAction()
        );
        
        // Log the notification (in production, send actual SMS/Email)
        System.out.println("📧 Sending alert to " + responder.getEmail() + ": " + message);
    }
    
    /**
     * Get all alerts for an incident
     */
    public List<Alert> getAlertsByIncidentId(Long incidentId) {
        return alertRepository.findByIncidentIncidentId(incidentId);
    }
    
    /**
     * Get all alerts for a responder
     */
    public List<Alert> getAlertsByResponderId(Long responderId) {
        return alertRepository.findByResponderUserId(responderId);
    }
    
    /**
     * Get all alerts
     */
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }
}
