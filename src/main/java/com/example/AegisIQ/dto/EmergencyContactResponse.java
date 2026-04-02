package com.example.AegisIQ.dto;

public class EmergencyContactResponse {
    private String label;
    private String number;
    private String role;
    private String description;

    public EmergencyContactResponse() {
    }

    public EmergencyContactResponse(String label, String number, String role, String description) {
        this.label = label;
        this.number = number;
        this.role = role;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}