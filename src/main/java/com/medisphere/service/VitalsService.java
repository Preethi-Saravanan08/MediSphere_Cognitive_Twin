package com.medisphere.service;

import com.medisphere.domain.Vitals;
import com.medisphere.repository.VitalsRepository;
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
public class VitalsService {

    @Autowired
    private VitalsRepository vitalsRepository;

    @Autowired
    private AuditService auditService;

    @Transactional
    public Vitals recordVitals(Vitals vitals) {
        log.debug("Recording vitals for patient: {}", vitals.getPatientId());

        vitals.setRecordedAt(LocalDateTime.now());
        vitals.setSyncStatus("PENDING");

        // Validate vitals
        boolean isValid = vitals.isWithinNormalRange();
        vitals.setValid(isValid);

        if (isValid) {
            vitals.setDataQuality("GOOD");
            vitals.setConfidence(0.95f);
        } else if (vitals.isAnomalous()) {
            vitals.setDataQuality("POOR");
            vitals.setConfidence(0.5f);
        } else {
            vitals.setDataQuality("FAIR");
            vitals.setConfidence(0.75f);
        }

        Vitals savedVitals = vitalsRepository.save(vitals);
        log.info("Vitals recorded: {} for patient: {}", savedVitals.getId(), vitals.getPatientId());

        return savedVitals;
    }

    public Optional<Vitals> getVitalsById(String id) {
        log.debug("Fetching vitals by ID: {}", id);
        return vitalsRepository.findById(id);
    }

    public List<Vitals> getLatestVitalsByPatient(String patientId) {
        log.debug("Fetching latest vitals for patient: {}", patientId);
        return vitalsRepository.findByPatientIdOrderByTimestampDesc(patientId);
    }

    public List<Vitals> getVitalsByDateRange(String patientId, LocalDateTime from, LocalDateTime to) {
        log.debug("Fetching vitals for patient {} from {} to {}", patientId, from, to);
        return vitalsRepository.findVitalsByPatientAndDateRange(patientId, from, to);
    }

    public List<Vitals> getInvalidVitals() {
        log.debug("Fetching invalid vitals");
        return vitalsRepository.findInvalidVitals();
    }

    public List<Vitals> getPendingSyncVitals() {
        log.debug("Fetching pending sync vitals");
        return vitalsRepository.findPendingSyncVitals();
    }

    public List<Vitals> getAnomalousVitals(String patientId) {
        log.debug("Fetching anomalous vitals for patient: {}", patientId);
        List<Vitals> allVitals = vitalsRepository.findByPatientIdOrderByTimestampDesc(patientId);
        return allVitals.stream()
                .filter(Vitals::isAnomalous)
                .toList();
    }

    public List<Vitals> getLowOxygenSaturationVitals() {
        log.debug("Fetching vitals with low oxygen saturation");
        return vitalsRepository.findLowOxygenSaturation();
    }

    @Transactional
    public void updateSyncStatus(String vitalsId, String syncStatus) {
        Optional<Vitals> optionalVitals = vitalsRepository.findById(vitalsId);
        if (optionalVitals.isPresent()) {
            Vitals vitals = optionalVitals.get();
            vitals.setSyncStatus(syncStatus);
            vitals.setSyncedAt(LocalDateTime.now());
            vitalsRepository.save(vitals);
            log.info("Vitals sync status updated: {} -> {}", vitalsId, syncStatus);
        }
    }

    public long countVitalsByPatient(String patientId) {
        return vitalsRepository.countByPatientId(patientId);
    }

    public long countPendingSyncVitals() {
        return vitalsRepository.countBySyncStatus("PENDING");
    }

    public List<Vitals> getVitalsFromDevice(String deviceId) {
        log.debug("Fetching vitals from device: {}", deviceId);
        return vitalsRepository.findBySourceDevice(deviceId);
    }

    @Transactional
    public void validateAndCorrectVitals(String vitalsId) {
        Optional<Vitals> optionalVitals = vitalsRepository.findById(vitalsId);
        if (optionalVitals.isPresent()) {
            Vitals vitals = optionalVitals.get();

            // Re-validate vitals
            boolean isValid = vitals.isWithinNormalRange();
            vitals.setValid(isValid);

            if (isValid) {
                vitals.setValidationErrors(null);
                vitals.setDataQuality("GOOD");
            } else {
                vitals.setValidationErrors("Vitals outside normal range");
                vitals.setDataQuality("FAIR");
            }

            vitalsRepository.save(vitals);
            log.info("Vitals validated: {}", vitalsId);
        }
    }
}
