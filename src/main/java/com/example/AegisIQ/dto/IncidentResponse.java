package com.example.AegisIQ.dto;

import java.time.LocalDateTime;

public class IncidentResponse {
    private Long incidentId;
    private String description;
    private String location;
    private String imageUrl;
    private String status;
    private LocalDateTime timestamp;
    private Long userId;
    private String userName;
    
    // Risk Assessment Details
    private Double credibilityScore;
    private Double aiConfidence;
    private String severityLevel;
    private String priorityClassification;
    private String recommendedAction;
    private String analysisDetails;

    public IncidentResponse() {}

    public IncidentResponse(Long incidentId, String description, String location, String imageUrl, 
                            String status, LocalDateTime timestamp, Long userId, String userName,
                            Double credibilityScore, Double aiConfidence, String severityLevel, 
                            String priorityClassification, String recommendedAction, String analysisDetails) {
        this.incidentId = incidentId;
        this.description = description;
        this.location = location;
        this.imageUrl = imageUrl;
        this.status = status;
        this.timestamp = timestamp;
        this.userId = userId;
        this.userName = userName;
        this.credibilityScore = credibilityScore;
        this.aiConfidence = aiConfidence;
        this.severityLevel = severityLevel;
        this.priorityClassification = priorityClassification;
        this.recommendedAction = recommendedAction;
        this.analysisDetails = analysisDetails;
    }

    public Long getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Long incidentId) {
        this.incidentId = incidentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getCredibilityScore() {
        return credibilityScore;
    }

    public void setCredibilityScore(Double credibilityScore) {
        this.credibilityScore = credibilityScore;
    }

    public Double getAiConfidence() {
        return aiConfidence;
    }

    public void setAiConfidence(Double aiConfidence) {
        this.aiConfidence = aiConfidence;
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
}
