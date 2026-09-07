package com.medisphere.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class AuditService {

    @Value("${audit.log-file}")
    private String auditLogFile;

    @Value("${audit.enabled}")
    private boolean auditEnabled;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public void logAction(String action, String resourceId, String details) {
        if (!auditEnabled) {
            return;
        }

        try {
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String logEntry = String.format(
                    "%s | ACTION: %s | RESOURCE_ID: %s | DETAILS: %s%n",
                    timestamp, action, resourceId, details
            );

            Path auditPath = Paths.get(auditLogFile);
            Files.createDirectories(auditPath.getParent());
            Files.writeString(auditPath, logEntry, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            log.debug("Audit log written: {}", action);
        } catch (IOException e) {
            log.error("Error writing audit log", e);
        }
    }

    public void logHipaaAccess(String userId, String patientId, String accessType, String resourceType) {
        String details = String.format("HIPAA_ACCESS | USER: %s | PATIENT: %s | TYPE: %s | RESOURCE: %s",
                userId, patientId, accessType, resourceType);
        logAction("HIPAA_ACCESS", patientId, details);
    }

    public void logDataModification(String userId, String patientId, String entityType, String action) {
        String details = String.format("DATA_MODIFICATION | USER: %s | ACTION: %s | ENTITY: %s",
                userId, action, entityType);
        logAction("DATA_MODIFIED", patientId, details);
    }

    public void logConsentAction(String patientId, String consentId, String action) {
        String details = String.format("CONSENT_%s | CONSENT_ID: %s", action, consentId);
        logAction("CONSENT_ACTION", patientId, details);
    }

    public void logSecurityEvent(String eventType, String description) {
        String details = String.format("SECURITY_EVENT | TYPE: %s | DESC: %s", eventType, description);
        logAction("SECURITY_EVENT", "SYSTEM", details);
    }

    public void logFhirSync(String patientId, String resourceType, String status) {
        String details = String.format("FHIR_SYNC | RESOURCE_TYPE: %s | STATUS: %s", resourceType, status);
        logAction("FHIR_SYNC", patientId, details);
    }
}
