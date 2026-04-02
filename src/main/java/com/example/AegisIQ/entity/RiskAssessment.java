package com.example.AegisIQ.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "risk_assessment")
public class RiskAssessment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long riskId;
    
    @Column(nullable = false)
    private Double credibilityScore; // 0-100
    
    @Column(nullable = false)
    private String severityLevel; // LOW, MEDIUM, HIGH
    
    private String priorityClassification; // LOW, MEDIUM, HIGH
    
    private String recommendedAction;
    
    @Column(length = 1000)
    private String analysisDetails;
    
    @OneToOne
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    // Getters and Setters
    public Long getRiskId() {
        return riskId;
    }

    public void setRiskId(Long riskId) {
        this.riskId = riskId;
    }

    public Double getCredibilityScore() {
        return credibilityScore;
    }

    public void setCredibilityScore(Double credibilityScore) {
        this.credibilityScore = credibilityScore;
    }

    public String getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(String severityLevel) {
        this.severityLevel = severityLevel;
    }

    public String getPriorityClassification() {
        return priorityClassification;
    }

    public void setPriorityClassification(String priorityClassification) {
        this.priorityClassification = priorityClassification;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
    }

    public String getAnalysisDetails() {
        return analysisDetails;
    }

    public void setAnalysisDetails(String analysisDetails) {
        this.analysisDetails = analysisDetails;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }
}
