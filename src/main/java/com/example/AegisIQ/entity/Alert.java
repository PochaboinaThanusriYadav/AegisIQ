package com.example.AegisIQ.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertId;
    
    @Column(nullable = false)
    private LocalDateTime alertTime;
    
    @Column(nullable = false)
    private String status; // SENT, PENDING, FAILED
    
    @Column(nullable = false)
    private String alertType; // SMS, EMAIL, EMERGENCY
    
    private String recipientInfo;
    
    @ManyToOne
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;
    
    @ManyToOne
    @JoinColumn(name = "responder_id")
    private User responder;
    
    @PrePersist
    protected void onCreate() {
        alertTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getAlertId() {
        return alertId;
    }

    public void setAlertId(Long alertId) {
        this.alertId = alertId;
    }

    public LocalDateTime getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(LocalDateTime alertTime) {
        this.alertTime = alertTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getRecipientInfo() {
        return recipientInfo;
    }

    public void setRecipientInfo(String recipientInfo) {
        this.recipientInfo = recipientInfo;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public User getResponder() {
        return responder;
    }

    public void setResponder(User responder) {
        this.responder = responder;
    }
}
