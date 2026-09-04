package com.medisphere.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class HIPAAAuditService {

    @Value("${audit.log-file:logs/hipaa-audit.log}")
    private String auditLogFile;

    @Value("${audit.enabled:true}")
    private boolean auditEnabled;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public void logAccess(String action, String resourceId, String details) {
        if (!auditEnabled) return;

        try {
            String auditEntry = String.format(
                    "[%s] | Event ID: %s | Action: %s | Resource: %s | Details: %s | User: %s | Timestamp: %s",
                    "HIPAA_AUDIT",
                    UUID.randomUUID(),
                    action,
                    resourceId,
                    details,
                    getCurrentUser(),
                    LocalDateTime.now().format(dateFormatter)
            );

            log.info(auditEntry);
            writeToAuditFile(auditEntry);

        } catch (Exception e) {
            log.error("Error writing audit log", e);
        }
    }

    public void logModification(String action, String resourceId, String oldValue, String newValue) {
        if (!auditEnabled) return;

        try {
            String auditEntry = String.format(
                    "[%s] | Event ID: %s | Action: %s | Resource: %s | Old: %s | New: %s | User: %s | Timestamp: %s",
                    "HIPAA_AUDIT",
                    UUID.randomUUID(),
                    action,
                    resourceId,
                    oldValue,
                    newValue,
                    getCurrentUser(),
                    LocalDateTime.now().format(dateFormatter)
            );

            log.info(auditEntry);
            writeToAuditFile(auditEntry);

        } catch (Exception e) {
            log.error("Error writing modification audit log", e);
        }
    }

    public void logError(String action, String resourceId, String errorMessage) {
        if (!auditEnabled) return;

        try {
            String auditEntry = String.format(
                    "[%s] | Event ID: %s | Action: %s | Resource: %s | Error: %s | User: %s | Timestamp: %s",
                    "HIPAA_ERROR",
                    UUID.randomUUID(),
                    action,
                    resourceId,
                    errorMessage,
                    getCurrentUser(),
                    LocalDateTime.now().format(dateFormatter)
            );

            log.error(auditEntry);
            writeToAuditFile(auditEntry);

        } catch (Exception e) {
            log.error("Error writing error audit log", e);
        }
    }

    public void logSecurityEvent(String eventType, String details) {
        if (!auditEnabled) return;

        try {
            String auditEntry = String.format(
                    "[%s] | Event ID: %s | Type: %s | Details: %s | User: %s | Timestamp: %s",
                    "HIPAA_SECURITY",
                    UUID.randomUUID(),
                    eventType,
                    details,
                    getCurrentUser(),
                    LocalDateTime.now().format(dateFormatter)
            );

            log.warn(auditEntry);
            writeToAuditFile(auditEntry);

        } catch (Exception e) {
            log.error("Error writing security audit log", e);
        }
    }

    public void logConsentChange(String patientId, boolean consentProvided, String reason) {
        if (!auditEnabled) return;

        try {
            String auditEntry = String.format(
                    "[%s] | Event ID: %s | Action: CONSENT_CHANGE | Patient: %s | Consent: %s | Reason: %s | User: %s | Timestamp: %s",
                    "HIPAA_CONSENT",
                    UUID.randomUUID(),
                    patientId,
                    consentProvided,
                    reason,
                    getCurrentUser(),
                    LocalDateTime.now().format(dateFormatter)
            );

            log.info(auditEntry);
            writeToAuditFile(auditEntry);

        } catch (Exception e) {
            log.error("Error writing consent audit log", e);
        }
    }

    private void writeToAuditFile(String auditEntry) {
        try {
            java.nio.file.Files.write(
                    java.nio.file.Paths.get(auditLogFile),
                    (auditEntry + System.lineSeparator()).getBytes(),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND
            );
        } catch (Exception e) {
            log.error("Failed to write to audit file: {}", auditLogFile, e);
        }
    }

    private String getCurrentUser() {
        try {
            return org.springframework.security.core.context.SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();
        } catch (Exception e) {
            return "SYSTEM";
        }
    }
}
