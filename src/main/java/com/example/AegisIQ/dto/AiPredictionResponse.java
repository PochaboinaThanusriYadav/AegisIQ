package com.example.AegisIQ.dto;

public class AiPredictionResponse {

    private String severity;
    private Double confidence;
    private String priorityClassification;
    private String recommendedAction;
    private String summary;
    private String category;

    public AiPredictionResponse() {
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}