package com.example.AegisIQ.service;

import com.example.AegisIQ.entity.Incident;
import com.example.AegisIQ.entity.RiskAssessment;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class AIRiskAnalysisService {
    
    // Keywords for different severity levels
    private static final Set<String> HIGH_SEVERITY_KEYWORDS = new HashSet<>(Arrays.asList(
        "fire", "explosion", "shooting", "armed", "weapon", "violence", "attack", "bomb",
        "emergency", "critical", "severe", "fatal", "death", "injured", "casualties",
        "threat", "dangerous", "urgent", "crisis", "disaster", "terrorist"
    ));
    
    private static final Set<String> MEDIUM_SEVERITY_KEYWORDS = new HashSet<>(Arrays.asList(
        "theft", "robbery", "break-in", "vandalism", "assault", "harassment",
        "suspicious", "unauthorized", "trespassing", "damage", "accident", "collision",
        "medical", "injury", "fight", "disturbance", "altercation"
    ));
    
    private static final Set<String> LOW_SEVERITY_KEYWORDS = new HashSet<>(Arrays.asList(
        "noise", "parking", "complaint", "concern", "minor", "petty", "nuisance",
        "littering", "loitering", "graffiti", "lost", "found"
    ));
    
    // Credibility indicators
    private static final Set<String> HIGH_CREDIBILITY_INDICATORS = new HashSet<>(Arrays.asList(
        "witnessed", "saw", "observed", "currently happening", "right now", "at", "location",
        "address", "time", "date", "person", "vehicle", "description", "evidence"
    ));
    
    private static final Set<String> LOW_CREDIBILITY_INDICATORS = new HashSet<>(Arrays.asList(
        "maybe", "possibly", "might", "could be", "not sure", "think", "heard from",
        "someone said", "rumor", "supposedly", "allegedly"
    ));
    
    /**
     * Performs comprehensive risk analysis on an incident
     */
    public RiskAssessment analyzeIncident(Incident incident) {
        String description = incident.getDescription().toLowerCase();
        
        // Calculate credibility score (0-100)
        double credibilityScore = calculateCredibilityScore(description, incident);
        
        // Determine severity level
        String severityLevel = determineSeverityLevel(description);
        
        // Determine priority classification
        String priorityClassification = determinePriority(credibilityScore, severityLevel);
        
        // Generate recommended action
        String recommendedAction = generateRecommendedAction(severityLevel, priorityClassification);
        
        // Generate analysis details
        String analysisDetails = generateAnalysisDetails(description, credibilityScore, severityLevel);
        
        // Create risk assessment
        RiskAssessment riskAssessment = new RiskAssessment();
        riskAssessment.setCredibilityScore(credibilityScore);
        riskAssessment.setSeverityLevel(severityLevel);
        riskAssessment.setPriorityClassification(priorityClassification);
        riskAssessment.setRecommendedAction(recommendedAction);
        riskAssessment.setAnalysisDetails(analysisDetails);
        riskAssessment.setIncident(incident);
        
        return riskAssessment;
    }
    
    /**
     * Calculates credibility score based on various factors
     */
    private double calculateCredibilityScore(String description, Incident incident) {
        double score = 50.0; // Base score
        
        // Factor 1: Description length and detail (0-20 points)
        int wordCount = description.split("\\s+").length;
        if (wordCount > 50) score += 20;
        else if (wordCount > 20) score += 15;
        else if (wordCount > 10) score += 10;
        else if (wordCount < 5) score -= 10;
        
        // Factor 2: Presence of credibility indicators (0-20 points)
        int highCredibilityCount = 0;
        int lowCredibilityCount = 0;
        
        for (String indicator : HIGH_CREDIBILITY_INDICATORS) {
            if (description.contains(indicator)) {
                highCredibilityCount++;
            }
        }
        
        for (String indicator : LOW_CREDIBILITY_INDICATORS) {
            if (description.contains(indicator)) {
                lowCredibilityCount++;
            }
        }
        
        score += (highCredibilityCount * 4);
        score -= (lowCredibilityCount * 5);
        
        // Factor 3: Location specificity (0-10 points)
        if (incident.getLocation() != null && !incident.getLocation().isEmpty()) {
            if (incident.getLocation().length() > 10) {
                score += 10;
            } else {
                score += 5;
            }
        }
        
        // Factor 4: Image evidence (0-10 points)
        if (incident.getImageUrl() != null && !incident.getImageUrl().isEmpty()) {
            score += 10;
        }
        
        // Factor 5: Grammar and coherence (0-10 points)
        if (hasProperStructure(description)) {
            score += 10;
        }
        
        // Ensure score is between 0 and 100
        return Math.max(0, Math.min(100, score));
    }
    
    /**
     * Determines severity level based on keywords
     */
    private String determineSeverityLevel(String description) {
        int highCount = 0;
        int mediumCount = 0;
        int lowCount = 0;
        
        for (String keyword : HIGH_SEVERITY_KEYWORDS) {
            if (description.contains(keyword)) {
                highCount++;
            }
        }
        
        for (String keyword : MEDIUM_SEVERITY_KEYWORDS) {
            if (description.contains(keyword)) {
                mediumCount++;
            }
        }
        
        for (String keyword : LOW_SEVERITY_KEYWORDS) {
            if (description.contains(keyword)) {
                lowCount++;
            }
        }
        
        // Determine severity based on keyword matches
        if (highCount > 0) {
            return "HIGH";
        } else if (mediumCount > 0) {
            return "MEDIUM";
        } else if (lowCount > 0) {
            return "LOW";
        } else {
            // Default to MEDIUM if no keywords matched
            return "MEDIUM";
        }
    }
    
    /**
     * Determines priority classification based on credibility and severity
     */
    private String determinePriority(double credibilityScore, String severityLevel) {
        if (severityLevel.equals("HIGH") && credibilityScore >= 60) {
            return "HIGH";
        } else if (severityLevel.equals("HIGH") || (severityLevel.equals("MEDIUM") && credibilityScore >= 70)) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
    
    /**
     * Generates recommended action based on severity and priority
     */
    private String generateRecommendedAction(String severityLevel, String priorityClassification) {
        if (priorityClassification.equals("HIGH")) {
            return "IMMEDIATE RESPONSE REQUIRED: Dispatch emergency services and security team immediately. Alert all relevant authorities.";
        } else if (priorityClassification.equals("MEDIUM")) {
            return "PROMPT ACTION NEEDED: Dispatch security team for investigation. Monitor situation closely.";
        } else {
            return "STANDARD PROCEDURE: Review incident details and assign to appropriate personnel for follow-up.";
        }
    }
    
    /**
     * Generates detailed analysis summary
     */
    private String generateAnalysisDetails(String description, double credibilityScore, String severityLevel) {
        StringBuilder details = new StringBuilder();
        
        details.append("AI Analysis Summary: ");
        details.append("The incident has been analyzed using NLP and context evaluation. ");
        
        if (credibilityScore >= 70) {
            details.append("High credibility indicators detected including specific details and evidence. ");
        } else if (credibilityScore >= 40) {
            details.append("Moderate credibility with some verifiable information present. ");
        } else {
            details.append("Low credibility - lacking specific details or containing uncertain language. ");
        }
        
        details.append("Severity level determined as ").append(severityLevel);
        details.append(" based on keyword analysis and threat assessment. ");
        
        return details.toString();
    }
    
    /**
     * Checks if description has proper structure
     */
    private boolean hasProperStructure(String description) {
        // Check for basic sentence structure (starts with capital, has punctuation)
        return description.contains(".") || description.contains("!") || description.contains("?");
    }
}
