package com.medisphere.service;

import com.medisphere.domain.Patient;
import com.medisphere.repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class ConsentManagementService {

    private static final String CURRENT_CONSENT_VERSION = "1.0";

    private final PatientRepository patientRepository;
    private final HIPAAAuditService hipaaAuditService;

    public ConsentManagementService(
            PatientRepository patientRepository,
            HIPAAAuditService hipaaAuditService) {
        this.patientRepository = patientRepository;
        this.hipaaAuditService = hipaaAuditService;
    }

    public boolean grantConsent(String patientId, String consentReason) {
        try {
            Optional<Patient> patientOpt = patientRepository.findById(patientId);
            if (patientOpt.isEmpty()) {
                log.error("Patient not found: {}", patientId);
                hipaaAuditService.logError("CONSENT_GRANT", patientId, "Patient not found");
                return false;
            }

            Patient patient = patientOpt.get();
            patient.setConsentProvided(true);
            patient.setConsentDate(LocalDateTime.now());
            patient.setConsentVersion(CURRENT_CONSENT_VERSION);
            patient.setHipaaAcknowledged(true);
            patient.setUpdatedAt(LocalDateTime.now());
            patient.setUpdatedBy("PATIENT");

            patientRepository.save(patient);

            hipaaAuditService.logConsentChange(patientId, true, consentReason);
            log.info("Consent granted for patient: {}", patientId);

            return true;
        } catch (Exception e) {
            log.error("Error granting consent for patient: {}", patientId, e);
            hipaaAuditService.logError("CONSENT_GRANT_ERROR", patientId, e.getMessage());
            return false;
        }
    }

    public boolean revokeConsent(String patientId, String revokeReason) {
        try {
            Optional<Patient> patientOpt = patientRepository.findById(patientId);
            if (patientOpt.isEmpty()) {
                log.error("Patient not found: {}", patientId);
                hipaaAuditService.logError("CONSENT_REVOKE", patientId, "Patient not found");
                return false;
            }

            Patient patient = patientOpt.get();
            boolean previousConsent = patient.isConsentProvided();
            patient.setConsentProvided(false);
            patient.setUpdatedAt(LocalDateTime.now());
            patient.setUpdatedBy("PATIENT");

            patientRepository.save(patient);

            hipaaAuditService.logConsentChange(patientId, false, revokeReason);
            log.info("Consent revoked for patient: {}", patientId);

            return true;
        } catch (Exception e) {
            log.error("Error revoking consent for patient: {}", patientId, e);
            hipaaAuditService.logError("CONSENT_REVOKE_ERROR", patientId, e.getMessage());
            return false;
        }
    }

    public boolean hasValidConsent(String patientId) {
        try {
            Optional<Patient> patientOpt = patientRepository.findById(patientId);
            if (patientOpt.isEmpty()) {
                return false;
            }

            Patient patient = patientOpt.get();
            boolean hasConsent = patient.isConsentProvided() && patient.isHipaaAcknowledged();

            if (!hasConsent) {
                hipaaAuditService.logSecurityEvent("CONSENT_MISSING", "Patient " + patientId + " has no valid consent");
            }

            return hasConsent;
        } catch (Exception e) {
            log.error("Error checking consent for patient: {}", patientId, e);
            return false;
        }
    }

    public ConsentStatus getConsentStatus(String patientId) {
        try {
            Optional<Patient> patientOpt = patientRepository.findById(patientId);
            if (patientOpt.isEmpty()) {
                return null;
            }

            Patient patient = patientOpt.get();
            return ConsentStatus.builder()
                    .patientId(patientId)
                    .consentProvided(patient.isConsentProvided())
                    .hipaaAcknowledged(patient.isHipaaAcknowledged())
                    .consentDate(patient.getConsentDate())
                    .consentVersion(patient.getConsentVersion())
                    .currentVersion(CURRENT_CONSENT_VERSION)
                    .versionMatches(CURRENT_CONSENT_VERSION.equals(patient.getConsentVersion()))
                    .build();

        } catch (Exception e) {
            log.error("Error getting consent status for patient: {}", patientId, e);
            return null;
        }
    }

    public boolean verifyConsentVersion(String patientId) {
        try {
            Optional<Patient> patientOpt = patientRepository.findById(patientId);
            if (patientOpt.isEmpty()) return false;

            Patient patient = patientOpt.get();
            return CURRENT_CONSENT_VERSION.equals(patient.getConsentVersion());
        } catch (Exception e) {
            log.error("Error verifying consent version for patient: {}", patientId, e);
            return false;
        }
    }

    public long getConsentedPatientCount() {
        try {
            return patientRepository.countByConsentProvidedTrue();
        } catch (Exception e) {
            log.error("Error counting consented patients", e);
            return 0;
        }
    }

    public static class ConsentStatus {
        private String patientId;
        private boolean consentProvided;
        private boolean hipaaAcknowledged;
        private LocalDateTime consentDate;
        private String consentVersion;
        private String currentVersion;
        private boolean versionMatches;

        public ConsentStatus(String patientId, boolean consentProvided, boolean hipaaAcknowledged,
                           LocalDateTime consentDate, String consentVersion, String currentVersion,
                           boolean versionMatches) {
            this.patientId = patientId;
            this.consentProvided = consentProvided;
            this.hipaaAcknowledged = hipaaAcknowledged;
            this.consentDate = consentDate;
            this.consentVersion = consentVersion;
            this.currentVersion = currentVersion;
            this.versionMatches = versionMatches;
        }

        public static ConsentStatusBuilder builder() {
            return new ConsentStatusBuilder();
        }

        public static class ConsentStatusBuilder {
            private String patientId;
            private boolean consentProvided;
            private boolean hipaaAcknowledged;
            private LocalDateTime consentDate;
            private String consentVersion;
            private String currentVersion;
            private boolean versionMatches;

            public ConsentStatusBuilder patientId(String patientId) {
                this.patientId = patientId;
                return this;
            }

            public ConsentStatusBuilder consentProvided(boolean consentProvided) {
                this.consentProvided = consentProvided;
                return this;
            }

            public ConsentStatusBuilder hipaaAcknowledged(boolean hipaaAcknowledged) {
                this.hipaaAcknowledged = hipaaAcknowledged;
                return this;
            }

            public ConsentStatusBuilder consentDate(LocalDateTime consentDate) {
                this.consentDate = consentDate;
                return this;
            }

            public ConsentStatusBuilder consentVersion(String consentVersion) {
                this.consentVersion = consentVersion;
                return this;
            }

            public ConsentStatusBuilder currentVersion(String currentVersion) {
                this.currentVersion = currentVersion;
                return this;
            }

            public ConsentStatusBuilder versionMatches(boolean versionMatches) {
                this.versionMatches = versionMatches;
                return this;
            }

            public ConsentStatus build() {
                return new ConsentStatus(patientId, consentProvided, hipaaAcknowledged,
                        consentDate, consentVersion, currentVersion, versionMatches);
            }
        }

        // Getters
        public String getPatientId() { return patientId; }
        public boolean isConsentProvided() { return consentProvided; }
        public boolean isHipaaAcknowledged() { return hipaaAcknowledged; }
        public LocalDateTime getConsentDate() { return consentDate; }
        public String getConsentVersion() { return consentVersion; }
        public String getCurrentVersion() { return currentVersion; }
        public boolean isVersionMatches() { return versionMatches; }
    }
}
