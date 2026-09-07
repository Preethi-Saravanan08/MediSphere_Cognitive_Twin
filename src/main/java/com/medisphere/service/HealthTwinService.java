package com.medisphere.service;

import com.medisphere.domain.HealthTwin;
import com.medisphere.domain.Patient;
import com.medisphere.repository.HealthTwinRepository;
import com.medisphere.repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class HealthTwinService {

    @Autowired
    private HealthTwinRepository healthTwinRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AuditService auditService;

    @Transactional
    public HealthTwin createHealthTwin(String patientId) {
        log.info("Creating health twin for patient: {}", patientId);

        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        if (optionalPatient.isEmpty()) {
            throw new IllegalArgumentException("Patient not found: " + patientId);
        }

        Patient patient = optionalPatient.get();

        HealthTwin twin = HealthTwin.builder()
                .twinId(UUID.randomUUID().toString())
                .patientId(patientId)
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .modelVersion("1.0.0")
                .createdAt(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .twinDataCompleteness(0)
                .overallRiskLevel("UNKNOWN")
                .hipaaCompliant(true)
                .build();

        HealthTwin savedTwin = healthTwinRepository.save(twin);
        auditService.logAction("HEALTH_TWIN_CREATED", savedTwin.getId(), patient.getFirstName());
        log.info("Health twin created: {}", savedTwin.getId());

        return savedTwin;
    }

    public Optional<HealthTwin> getHealthTwinByPatientId(String patientId) {
        log.debug("Fetching health twin for patient: {}", patientId);
        return healthTwinRepository.findByPatientId(patientId);
    }

    @Transactional
    public HealthTwin updateHealthTwin(HealthTwin twin) {
        log.info("Updating health twin: {}", twin.getId());
        twin.setLastUpdated(LocalDateTime.now());
        twin.setTwinDataCompleteness(twin.calculateDataCompleteness());

        HealthTwin updatedTwin = healthTwinRepository.save(twin);
        auditService.logAction("HEALTH_TWIN_UPDATED", twin.getId(), twin.getFirstName());

        return updatedTwin;
    }

    public List<HealthTwin> getHighRiskPatients() {
        log.debug("Fetching high-risk patients");
        return healthTwinRepository.findHighRiskPatients();
    }

    public List<HealthTwin> getTwinsByCompleteness(int minimumCompleteness) {
        log.debug("Fetching twins with minimum completeness: {}%", minimumCompleteness);
        return healthTwinRepository.findTwinsByMinimumCompleteness(minimumCompleteness);
    }

    public List<HealthTwin> getTwinsWithActiveAlerts() {
        log.debug("Fetching twins with active alerts");
        return healthTwinRepository.findTwinsWithActiveAlerts();
    }

    @Transactional
    public void updateRiskScore(String twinId, String riskType, float score) {
        Optional<HealthTwin> optionalTwin = healthTwinRepository.findById(twinId);
        if (optionalTwin.isPresent()) {
            HealthTwin twin = optionalTwin.get();
            twin.getRiskScores().put(riskType, score);
            updateOverallRiskLevel(twin);
            healthTwinRepository.save(twin);
            log.info("Risk score updated for twin {}: {} = {}", twinId, riskType, score);
        }
    }

    private void updateOverallRiskLevel(HealthTwin twin) {
        if (twin.getRiskScores() == null || twin.getRiskScores().isEmpty()) {
            twin.setOverallRiskLevel("UNKNOWN");
            return;
        }

        float maxRisk = twin.getRiskScores().values().stream()
                .max(Float::compare)
                .orElse(0.0f);

        if (maxRisk >= 0.7f) {
            twin.setOverallRiskLevel("CRITICAL");
        } else if (maxRisk >= 0.5f) {
            twin.setOverallRiskLevel("HIGH");
        } else if (maxRisk >= 0.3f) {
            twin.setOverallRiskLevel("MODERATE");
        } else {
            twin.setOverallRiskLevel("LOW");
        }
    }

    @Transactional
    public void addAlert(String twinId, HealthTwin.Alert alert) {
        Optional<HealthTwin> optionalTwin = healthTwinRepository.findById(twinId);
        if (optionalTwin.isPresent()) {
            HealthTwin twin = optionalTwin.get();
            if (twin.getActiveAlerts() == null) {
                twin.setActiveAlerts(new java.util.ArrayList<>());
            }
            twin.getActiveAlerts().add(alert);
            healthTwinRepository.save(twin);
            log.info("Alert added to twin {}: {}", twinId, alert.getMessage());
        }
    }

    public long countHighRiskPatients() {
        return healthTwinRepository.countByOverallRiskLevel("HIGH") +
               healthTwinRepository.countByOverallRiskLevel("CRITICAL");
    }

    public List<HealthTwin> getAllHealthTwins() {
        log.debug("Fetching all health twins");
        return healthTwinRepository.findAll();
    }

    public long countTotalTwins() {
        return healthTwinRepository.count();
    }
}
