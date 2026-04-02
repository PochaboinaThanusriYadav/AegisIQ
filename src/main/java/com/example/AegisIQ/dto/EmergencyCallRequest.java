package com.example.AegisIQ.dto;

public class EmergencyCallRequest {
    private Long incidentId;
    private Long callerUserId;
    private String contactNumber;
    private String contactLabel;
    private String notes;

    public Long getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Long incidentId) {
        this.incidentId = incidentId;
    }

    public Long getCallerUserId() {
        return callerUserId;
    }

    public void setCallerUserId(Long callerUserId) {
        this.callerUserId = callerUserId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactLabel() {
        return contactLabel;
    }

    public void setContactLabel(String contactLabel) {
        this.contactLabel = contactLabel;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}