package com.example.AegisIQ.service;

import com.example.AegisIQ.dto.AiPredictionRequest;
import com.example.AegisIQ.dto.AiPredictionResponse;
import com.example.AegisIQ.entity.Incident;
import com.example.AegisIQ.entity.RiskAssessment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class AIRiskAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AIRiskAnalysisService.class);

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

    private static final Set<String> HIGH_CREDIBILITY_INDICATORS = new HashSet<>(Arrays.asList(
        "witnessed", "saw", "observed", "currently happening", "right now", "at", "location",
        "address", "time", "date", "person", "vehicle", "description", "evidence"
    ));

    private static final Set<String> LOW_CREDIBILITY_INDICATORS = new HashSet<>(Arrays.asList(
        "maybe", "possibly", "might", "could be", "not sure", "think", "heard from",
        "someone said", "rumor", "supposedly", "allegedly"
    ));

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${ai.service.enabled:true}")
    private boolean aiServiceEnabled;

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    public AIRiskAnalysisService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();
    }

    public RiskAssessment analyzeIncident(Incident incident) {
        AiPredictionResponse prediction = fetchPrediction(incident);
        if (prediction != null) {
            return buildAssessmentFromPrediction(incident, prediction);
        }

        return buildFallbackAssessment(incident);
    }

    private AiPredictionResponse fetchPrediction(Incident incident) {
        if (!aiServiceEnabled || aiServiceUrl == null || aiServiceUrl.isBlank()) {
            return null;
        }

        try {
            AiPredictionRequest request = new AiPredictionRequest(
                incident.getDescription(),
                incident.getLocation(),
                incident.getImageUrl()
            );

            String requestBody = objectMapper.writeValueAsString(request);
            String endpoint = aiServiceUrl.endsWith("/") ? aiServiceUrl + "predict" : aiServiceUrl + "/predict";

            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                logger.warn("AI service returned status {}", response.statusCode());
                return null;
            }

            return objectMapper.readValue(response.body(), AiPredictionResponse.class);
        } catch (IOException ex) {
            logger.warn("AI prediction request failed, falling back to heuristic analysis", ex);
            return null;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            logger.warn("AI prediction request interrupted, falling back to heuristic analysis", ex);
            return null;
        } catch (Exception ex) {
            logger.warn("AI prediction request failed, falling back to heuristic analysis", ex);
            return null;
        }
    }

    private RiskAssessment buildAssessmentFromPrediction(Incident incident, AiPredictionResponse prediction) {
        String severityLevel = normalizeSeverity(prediction.getSeverity());
        double confidence = normalizeConfidence(prediction.getConfidence());
        String priorityClassification = normalizePriority(prediction.getPriorityClassification(), severityLevel, confidence);
        String recommendedAction = hasText(prediction.getRecommendedAction())
            ? prediction.getRecommendedAction()
            : generateRecommendedAction(severityLevel, priorityClassification);

        RiskAssessment riskAssessment = new RiskAssessment();
        riskAssessment.setCredibilityScore(confidence);
        riskAssessment.setAiConfidence(confidence);
        riskAssessment.setSeverityLevel(severityLevel);
        riskAssessment.setPriorityClassification(priorityClassification);
        riskAssessment.setRecommendedAction(recommendedAction);
        riskAssessment.setAnalysisDetails(buildAnalysisDetails(
            "External AI service",
            confidence,
            severityLevel,
            prediction.getCategory(),
            prediction.getSummary()
        ));
        riskAssessment.setIncident(incident);
        return riskAssessment;
    }

    private RiskAssessment buildFallbackAssessment(Incident incident) {
        String description = incident.getDescription().toLowerCase(Locale.ROOT);

        double credibilityScore = calculateCredibilityScore(description, incident);
        String severityLevel = determineSeverityLevel(description);
        String priorityClassification = determinePriority(credibilityScore, severityLevel);
        String recommendedAction = generateRecommendedAction(severityLevel, priorityClassification);
        String analysisDetails = buildAnalysisDetails(
            "Local heuristic fallback",
            credibilityScore,
            severityLevel,
            null,
            generateFallbackSummary(credibilityScore, severityLevel)
        );

        RiskAssessment riskAssessment = new RiskAssessment();
        riskAssessment.setCredibilityScore(credibilityScore);
        riskAssessment.setAiConfidence(credibilityScore);
        riskAssessment.setSeverityLevel(severityLevel);
        riskAssessment.setPriorityClassification(priorityClassification);
        riskAssessment.setRecommendedAction(recommendedAction);
        riskAssessment.setAnalysisDetails(analysisDetails);
        riskAssessment.setIncident(incident);
        return riskAssessment;
    }

    private double calculateCredibilityScore(String description, Incident incident) {
        double score = 50.0;

        int wordCount = description.split("\\s+").length;
        if (wordCount > 50) score += 20;
        else if (wordCount > 20) score += 15;
        else if (wordCount > 10) score += 10;
        else if (wordCount < 5) score -= 10;

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

        if (incident.getLocation() != null && !incident.getLocation().isEmpty()) {
            score += incident.getLocation().length() > 10 ? 10 : 5;
        }

        if (incident.getImageUrl() != null && !incident.getImageUrl().isEmpty()) {
            score += 10;
        }

        if (hasProperStructure(description)) {
            score += 10;
        }

        return Math.max(0, Math.min(100, score));
    }

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

        if (highCount > 0) {
            return "HIGH";
        } else if (mediumCount > 0) {
            return "MEDIUM";
        } else if (lowCount > 0) {
            return "LOW";
        } else {
            return "MEDIUM";
        }
    }

    private String determinePriority(double credibilityScore, String severityLevel) {
        if (severityLevel.equals("HIGH") && credibilityScore >= 60) {
            return "HIGH";
        } else if (severityLevel.equals("HIGH") || (severityLevel.equals("MEDIUM") && credibilityScore >= 70)) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    private String normalizeSeverity(String severity) {
        if (!hasText(severity)) {
            return "MEDIUM";
        }

        String normalized = severity.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "CRITICAL", "HIGH", "SEVERE" -> "HIGH";
            case "LOW", "MINOR" -> "LOW";
            case "MEDIUM", "MODERATE" -> "MEDIUM";
            default -> normalized;
        };
    }

    private String normalizePriority(String priorityClassification, String severityLevel, double confidence) {
        if (hasText(priorityClassification)) {
            return priorityClassification.trim().toUpperCase(Locale.ROOT);
        }

        if ("HIGH".equals(severityLevel) && confidence >= 60) {
            return "HIGH";
        }

        if ("HIGH".equals(severityLevel) || ("MEDIUM".equals(severityLevel) && confidence >= 70)) {
            return "MEDIUM";
        }

        return "LOW";
    }

    private double normalizeConfidence(Double confidence) {
        if (confidence == null) {
            return 50.0;
        }

        if (confidence <= 1.0) {
            return Math.max(0, Math.min(100, confidence * 100.0));
        }

        return Math.max(0, Math.min(100, confidence));
    }

    private String generateRecommendedAction(String severityLevel, String priorityClassification) {
        if (priorityClassification.equals("HIGH")) {
            return "IMMEDIATE RESPONSE REQUIRED: Dispatch emergency services and security team immediately. Alert all relevant authorities.";
        } else if (priorityClassification.equals("MEDIUM")) {
            return "PROMPT ACTION NEEDED: Dispatch security team for investigation. Monitor situation closely.";
        } else {
            return "STANDARD PROCEDURE: Review incident details and assign to appropriate personnel for follow-up.";
        }
    }

    private String buildAnalysisDetails(String source, double confidence, String severityLevel, String category, String summary) {
        StringBuilder details = new StringBuilder();
        details.append("Analysis source: ").append(source).append(". ");
        details.append("Confidence: ").append(Math.round(confidence)).append("%. ");
        details.append("Severity: ").append(severityLevel).append(". ");

        if (hasText(category)) {
            details.append("Category: ").append(category).append(". ");
        }

        if (hasText(summary)) {
            details.append("Summary: ").append(summary.trim());
        }

        return details.toString().trim();
    }

    private String generateFallbackSummary(double credibilityScore, String severityLevel) {
        StringBuilder details = new StringBuilder();
        details.append("The incident was evaluated locally because the AI service was unavailable. ");

        if (credibilityScore >= 70) {
            details.append("High credibility indicators were detected. ");
        } else if (credibilityScore >= 40) {
            details.append("Moderate credibility with some verifiable information present. ");
        } else {
            details.append("Low credibility due to limited detail or uncertain language. ");
        }

        details.append("Severity was classified as ").append(severityLevel).append(" based on keyword analysis.");
        return details.toString();
    }

    private boolean hasProperStructure(String description) {
        return description.contains(".") || description.contains("!") || description.contains("?");
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
