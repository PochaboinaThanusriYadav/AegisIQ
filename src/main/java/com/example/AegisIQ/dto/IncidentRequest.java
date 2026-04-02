package com.example.AegisIQ.dto;

public class IncidentRequest {
    private String description;
    private String location;
    private String imageUrl;

    public IncidentRequest() {}

    public IncidentRequest(String description, String location, String imageUrl) {
        this.description = description;
        this.location = location;
        this.imageUrl = imageUrl;
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
}
