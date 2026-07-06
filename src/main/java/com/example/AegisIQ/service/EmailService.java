package com.example.AegisIQ.service;

import com.example.AegisIQ.entity.Incident;
import com.example.AegisIQ.entity.RiskAssessment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendEmergencyAlertEmail(String toEmail, Incident incident, RiskAssessment assessment) {
        String subject = "🚨 AegisIQ EMERGENCY: " + assessment.getSeverityLevel() + " Severity Incident Reported";
        String body = String.format(
                "Hello Responder,\n\n" +
                        "A new high/medium priority incident has been reported:\n" +
                        "- Incident ID: %d\n" +
                        "- Location: %s\n" +
                        "- Severity Level: %s\n" +
                        "- Credibility Score: %.0f%%\n" +
                        "- Recommended Action: %s\n\n" +
                        "Description:\n%s\n\n" +
                        "Please review this case immediately in the AegisIQ Command Center.\n\n" +
                        "Safe regards,\nAegisIQ Automations",
                incident.getIncidentId(),
                incident.getLocation(),
                assessment.getSeverityLevel(),
                assessment.getCredibilityScore(),
                assessment.getRecommendedAction(),
                incident.getDescription());

        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(toEmail);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                logger.info("📧 Email alert sent successfully to {}", toEmail);
            } catch (Exception e) {
                logger.error("❌ Failed to send email alert: {}. Falling back to logs.", e.getMessage());
            }
        } else {
            logger.info("📧 [Mock Email] Target: {} | Subject: {} \nBody: {}", toEmail, subject, body);
        }
    }
}
