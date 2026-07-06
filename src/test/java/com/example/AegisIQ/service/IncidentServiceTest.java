package com.example.AegisIQ.service;

import com.example.AegisIQ.entity.Incident;
import com.example.AegisIQ.entity.RiskAssessment;
import com.example.AegisIQ.entity.User;
import com.example.AegisIQ.repository.IncidentRepository;
import com.example.AegisIQ.repository.RiskAssessmentRepository;
import com.example.AegisIQ.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;
    @Mock
    private RiskAssessmentRepository riskAssessmentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AIRiskAnalysisService aiRiskAnalysisService;
    @Mock
    private AlertService alertService;
    @Mock
    private EmailService emailService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private IncidentService incidentService;

    private User testUser;
    private Incident testIncident;
    private RiskAssessment testAssessment;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testIncident = new Incident();
        testIncident.setDescription("Fire alarm in building A");
        testIncident.setLocation("Block A, Floor 1");

        testAssessment = new RiskAssessment();
        testAssessment.setSeverityLevel("HIGH");
        testAssessment.setPriorityClassification("HIGH");
        testAssessment.setCredibilityScore(90.0);
        testAssessment.setRecommendedAction("Evacuate immediately");
        testAssessment.setAnalysisDetails("High risk detected.");
    }

    @Test
    void reportIncident_savesIncidentWithRiskAssessment() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(incidentRepository.save(any())).thenAnswer(inv -> {
            Incident i = inv.getArgument(0);
            i.setIncidentId(1L);
            return i;
        });
        when(aiRiskAnalysisService.analyzeIncident(any())).thenReturn(testAssessment);
        when(riskAssessmentRepository.save(any())).thenReturn(testAssessment);

        Incident result = incidentService.reportIncident(testIncident, 1L);

        assertNotNull(result);
        assertEquals("UNDER_REVIEW", result.getStatus());
        verify(incidentRepository, times(2)).save(any());
        verify(riskAssessmentRepository, times(1)).save(testAssessment);
    }

    @Test
    void reportIncident_triggersAlertForHighPriority() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(incidentRepository.save(any())).thenAnswer(inv -> {
            Incident i = inv.getArgument(0);
            i.setIncidentId(1L);
            return i;
        });
        when(aiRiskAnalysisService.analyzeIncident(any())).thenReturn(testAssessment);
        when(riskAssessmentRepository.save(any())).thenReturn(testAssessment);

        incidentService.reportIncident(testIncident, 1L);

        verify(alertService, times(1)).sendAlert(any(), eq(testAssessment));
        verify(emailService, times(1)).sendEmergencyAlertEmail(anyString(), any(), eq(testAssessment));
    }

    @Test
    void reportIncident_throwsExceptionWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> incidentService.reportIncident(testIncident, 99L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getIncidentById_returnsEmptyWhenNotFound() {
        when(incidentRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Incident> result = incidentService.getIncidentById(999L);
        assertTrue(result.isEmpty());
    }
}
