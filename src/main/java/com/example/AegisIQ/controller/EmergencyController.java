package com.example.AegisIQ.controller;

import com.example.AegisIQ.dto.EmergencyCallRequest;
import com.example.AegisIQ.dto.EmergencyContactResponse;
import com.example.AegisIQ.service.EmergencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emergency")
@CrossOrigin(origins = "*")
public class EmergencyController {

    @Autowired
    private EmergencyService emergencyService;

    @GetMapping("/contacts")
    public ResponseEntity<List<EmergencyContactResponse>> getEmergencyContacts() {
        return ResponseEntity.ok(emergencyService.getEmergencyContacts());
    }

    @PostMapping("/call")
    public ResponseEntity<?> logEmergencyCall(@RequestBody EmergencyCallRequest request) {
        try {
            Map<String, Object> response = emergencyService.logEmergencyCall(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
}