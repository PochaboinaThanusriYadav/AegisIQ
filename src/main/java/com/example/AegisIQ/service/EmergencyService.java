package com.example.AegisIQ.service;

import com.example.AegisIQ.dto.EmergencyCallRequest;
import com.example.AegisIQ.dto.EmergencyContactResponse;
import com.example.AegisIQ.entity.Incident;
import com.example.AegisIQ.entity.User;
import com.example.AegisIQ.repository.IncidentRepository;
import com.example.AegisIQ.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmergencyService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private UserRepository userRepository;

    public List<EmergencyContactResponse> getEmergencyContacts() {
        List<EmergencyContactResponse> contacts = new ArrayList<>();
        contacts.add(new EmergencyContactResponse("Police Control", "112", "Police", "Primary police emergency response"));
        contacts.add(new EmergencyContactResponse("Ambulance Dispatch", "108", "Medical", "Emergency medical transport and paramedics"));
        contacts.add(new EmergencyContactResponse("Fire Control", "101", "Fire", "Fire brigade and rescue response"));
        contacts.add(new EmergencyContactResponse("Rescue Team", "100", "Rescue", "Search, extraction, and rescue support"));
        contacts.add(new EmergencyContactResponse("Incident Commander", "+91-90000-00001", "Command", "Incident command escalation line"));
        contacts.add(new EmergencyContactResponse("On-Call Doctor", "+91-90000-00002", "Medical", "Medical advisor and triage support"));
        return contacts;
    }

    public Map<String, Object> logEmergencyCall(EmergencyCallRequest request) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());

        User caller = null;
        if (request.getCallerUserId() != null) {
            caller = userRepository.findById(request.getCallerUserId()).orElse(null);
        }

        Incident incident = null;
        if (request.getIncidentId() != null) {
            incident = incidentRepository.findById(request.getIncidentId()).orElse(null);
        }

        String callerName = caller != null ? caller.getName() : "Unknown";
        String incidentLabel = incident != null ? "Incident #" + incident.getIncidentId() : "No incident linked";

        System.out.println("[EMERGENCY CALL] " + request.getContactLabel() + " " + request.getContactNumber()
            + " | Caller: " + callerName
            + " | " + incidentLabel
            + " | Notes: " + (request.getNotes() == null ? "" : request.getNotes()));

        response.put("status", "LOGGED");
        response.put("contactLabel", request.getContactLabel());
        response.put("contactNumber", request.getContactNumber());
        response.put("callerName", callerName);
        response.put("incidentId", request.getIncidentId());
        response.put("notes", request.getNotes());
        response.put("message", "Emergency call logged successfully");

        return response;
    }
}