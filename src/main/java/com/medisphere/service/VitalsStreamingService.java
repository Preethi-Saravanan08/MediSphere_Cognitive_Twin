package com.medisphere.service;

import com.medisphere.config.KafkaConfig;
import com.medisphere.domain.Vitals;
import com.medisphere.repository.VitalsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class VitalsStreamingService {

    private final VitalsRepository vitalsRepository;
    private final KafkaTemplate<String, Vitals> vitalsKafkaTemplate;
    private final KafkaTemplate<String, String> stringKafkaTemplate;
    private final HIPAAAuditService hipaaAuditService;

    public VitalsStreamingService(
            VitalsRepository vitalsRepository,
            KafkaTemplate<String, Vitals> vitalsKafkaTemplate,
            KafkaTemplate<String, String> stringKafkaTemplate,
            HIPAAAuditService hipaaAuditService) {
        this.vitalsRepository = vitalsRepository;
        this.vitalsKafkaTemplate = vitalsKafkaTemplate;
        this.stringKafkaTemplate = stringKafkaTemplate;
        this.hipaaAuditService = hipaaAuditService;
    }

    public void publishVitals(Vitals vitals) {
        try {
            // Validate vitals before publishing
            if (!vitals.isWithinNormalRange()) {
                log.warn("Vitals outside normal range for patient: {}", vitals.getPatientId());
                vitals.setValidationErrors("Outside normal range");
            }

            vitals.setValid(vitals.isWithinNormalRange());
            vitals.setSyncStatus("PENDING");
            vitals.setRecordedAt(LocalDateTime.now());

            vitalsKafkaTemplate.send(KafkaConfig.VITALS_TOPIC, vitals.getPatientId(), vitals);
            log.info("Vitals published for patient: {}", vitals.getPatientId());

            hipaaAuditService.logAccess("VITALS_PUBLISHED", vitals.getPatientId(), "Vitals streamed via Kafka");

        } catch (Exception e) {
            log.error("Error publishing vitals for patient: {}", vitals.getPatientId(), e);
            hipaaAuditService.logError("VITALS_PUBLISH_ERROR", vitals.getPatientId(), e.getMessage());
        }
    }

    @KafkaListener(
            topics = KafkaConfig.VITALS_TOPIC,
            groupId = "medisphere-vitals-consumer",
            containerFactory = "vitalsListenerContainerFactory")
    public void consumeVitals(Vitals vitals) {
        try {
            log.debug("Consuming vitals for patient: {}", vitals.getPatientId());

            // Persist to database
            vitals.setSyncedAt(LocalDateTime.now());
            vitals.setSyncStatus("SYNCED");
            vitals.setValid(vitals.isWithinNormalRange());

            Vitals savedVitals = vitalsRepository.save(vitals);
            log.info("Vitals persisted for patient: {}", vitals.getPatientId());

            // Check for anomalies
            if (vitals.isAnomalous()) {
                log.warn("Anomalous vitals detected for patient: {}", vitals.getPatientId());
                publishVitalsAlert(vitals);
            }

            hipaaAuditService.logAccess("VITALS_CONSUMED", vitals.getPatientId(), "Vitals consumed and persisted");

        } catch (Exception e) {
            log.error("Error consuming vitals for patient: {}", vitals.getPatientId(), e);
            vitals.setSyncStatus("FAILED");
            vitalsRepository.save(vitals);
            hipaaAuditService.logError("VITALS_CONSUME_ERROR", vitals.getPatientId(), e.getMessage());
        }
    }

    private void publishVitalsAlert(Vitals vitals) {
        try {
            String alertMessage = String.format(
                    "ALERT: Anomalous vitals detected for patient %s. HR: %.1f bpm, BP: %.1f/%.1f mmHg, SpO2: %.1f%%",
                    vitals.getPatientId(),
                    vitals.getHeartRate(),
                    vitals.getSystolicBP(),
                    vitals.getDiastolicBP(),
                    vitals.getOxygenSaturation()
            );

            stringKafkaTemplate.send(KafkaConfig.VITALS_ALERTS_TOPIC, vitals.getPatientId(), alertMessage);
            log.info("Alert published for patient: {}", vitals.getPatientId());

            hipaaAuditService.logSecurityEvent("VITALS_ANOMALY_ALERT", alertMessage);

        } catch (Exception e) {
            log.error("Error publishing vitals alert for patient: {}", vitals.getPatientId(), e);
        }
    }

    public long getVitalsCountForPatient(String patientId) {
        return vitalsRepository.countByPatientId(patientId);
    }

    public long getValidVitalsCountForPatient(String patientId) {
        return vitalsRepository.countByPatientIdAndIsValidTrue(patientId);
    }

    public long getAnomalousVitalsCountForPatient(String patientId) {
        return vitalsRepository.countByPatientIdAndIsAnomalous(patientId);
    }
}
