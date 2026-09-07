package com.medisphere.kafka;

import com.medisphere.domain.Vitals;
import com.medisphere.service.VitalsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VitalsConsumer {

    @Autowired
    private VitalsService vitalsService;

    @KafkaListener(
            topics = "medisphere-vitals",
            containerFactory = "vitalsListenerContainerFactory",
            groupId = "medisphere-vitals-consumer"
    )
    public void consumeVitals(Vitals vitals) {
        try {
            log.info("Received vitals from Kafka for patient: {}", vitals.getPatientId());

            // Record and validate vitals
            Vitals recordedVitals = vitalsService.recordVitals(vitals);

            // Update sync status
            vitalsService.updateSyncStatus(recordedVitals.getId(), "SYNCED");

            log.info("Vitals processed and stored: {}", recordedVitals.getId());
        } catch (Exception e) {
            log.error("Error processing vitals from Kafka", e);
        }
    }

    @KafkaListener(
            topics = "medisphere-fhir-sync",
            containerFactory = "stringListenerContainerFactory",
            groupId = "medisphere-vitals-consumer"
    )
    public void consumeFhirSync(String message) {
        try {
            log.info("Received FHIR sync message: {}", message);
            // Process FHIR sync messages
        } catch (Exception e) {
            log.error("Error processing FHIR sync message", e);
        }
    }
}
