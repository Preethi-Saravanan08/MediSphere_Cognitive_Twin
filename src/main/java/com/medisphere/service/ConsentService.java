package com.medisphere.service;

import com.medisphere.domain.Consent;
import com.medisphere.repository.ConsentRepository;
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
public class ConsentService {

    @Autowired
    private ConsentRepository consentRepository;

    @Autowired
    private AuditService auditService;

    @Transactional
    public Consent createConsent(Consent consent) {
        log.info("Creating consent for patient: {} with type: {}", consent.getPatientId(), consent.getConsentType());

        consent.setConsentId(UUID.randomUUID().toString());
        consent.setStatus("ACTIVE");
        consent.setCreatedAt(LocalDateTime.now());
        consent.setEffectiveDate(LocalDateTime.now());

        if (consent.getExpirationDate() == null) {
            // Default to 1 year expiration
            consent.setExpirationDate(LocalDateTime.now().plusYears(1));
        }

        Consent savedConsent = consentRepository.save(consent);

        Consent.ConsentAuditLog log = Consent.ConsentAuditLog.builder()
                .action("CREATED")
                .timestamp(LocalDateTime.now())
                .userId(consent.getCreatedBy())
                .description("Consent created")
                .build();
        consent.addAuditLog(log);

        auditService.logAction("CONSENT_CREATED", savedConsent.getId(), consent.getPatientId());
        log.info("Consent created: {}", savedConsent.getId());

        return savedConsent;
    }

    public Optional<Consent> getConsentByPatientAndType(String patientId, String consentType) {
        log.debug("Fetching consent for patient: {} with type: {}", patientId, consentType);
        return consentRepository.findByPatientIdAndConsentType(patientId, consentType);
    }

    public List<Consent> getConsentsByPatient(String patientId) {
        log.debug("Fetching all consents for patient: {}", patientId);
        return consentRepository.findByPatientId(patientId);
    }

    public List<Consent> getActiveConsents() {
        log.debug("Fetching active consents");
        return consentRepository.findActiveConsents(LocalDateTime.now());
    }

    public List<Consent> getExpiredConsents() {
        log.debug("Fetching expired consents");
        return consentRepository.findExpiredConsents(LocalDateTime.now());
    }

    public List<Consent> getHipaaAcknowledgedConsents() {
        log.debug("Fetching HIPAA acknowledged consents");
        return consentRepository.findHipaaAcknowledgedConsents();
    }

    public boolean hasActiveConsent(String patientId, String consentType) {
        Optional<Consent> consent = consentRepository.findByPatientIdAndConsentType(patientId, consentType);
        return consent.isPresent() && consent.get().isActive();
    }

    public boolean hasHipaaConsent(String patientId) {
        return hasActiveConsent(patientId, "HIPAA");
    }

    public boolean hasAIPredictionConsent(String patientId) {
        return hasActiveConsent(patientId, "AI_PREDICTION");
    }

    @Transactional
    public void revokeConsent(String consentId, String revokedBy) {
        Optional<Consent> optionalConsent = consentRepository.findById(consentId);
        if (optionalConsent.isPresent()) {
            Consent consent = optionalConsent.get();
            consent.setStatus("REVOKED");
            consent.setRevokedAt(LocalDateTime.now());

            Consent.ConsentAuditLog log = Consent.ConsentAuditLog.builder()
                    .action("REVOKED")
                    .timestamp(LocalDateTime.now())
                    .userId(revokedBy)
                    .description("Consent revoked by patient")
                    .build();
            consent.addAuditLog(log);

            consentRepository.save(consent);
            auditService.logAction("CONSENT_REVOKED", consentId, consent.getPatientId());
            log.info("Consent revoked: {}", consentId);
        }
    }

    @Transactional
    public void verifyConsent(String consentId, String verifiedBy) {
        Optional<Consent> optionalConsent = consentRepository.findById(consentId);
        if (optionalConsent.isPresent()) {
            Consent consent = optionalConsent.get();
            consent.setVerifiedBy(verifiedBy);
            consent.setVerifiedAt(LocalDateTime.now());

            Consent.ConsentAuditLog log = Consent.ConsentAuditLog.builder()
                    .action("VERIFIED")
                    .timestamp(LocalDateTime.now())
                    .userId(verifiedBy)
                    .description("Consent verified")
                    .build();
            consent.addAuditLog(log);

            consentRepository.save(consent);
            log.info("Consent verified: {}", consentId);
        }
    }

    @Transactional
    public void acknowledgeHipaa(String consentId, String acknowledgedBy) {
        Optional<Consent> optionalConsent = consentRepository.findById(consentId);
        if (optionalConsent.isPresent()) {
            Consent consent = optionalConsent.get();
            consent.setHipaaAcknowledged(true);
            consent.setHipaaAcknowledgedDate(LocalDateTime.now());

            Consent.ConsentAuditLog log = Consent.ConsentAuditLog.builder()
                    .action("HIPAA_ACKNOWLEDGED")
                    .timestamp(LocalDateTime.now())
                    .userId(acknowledgedBy)
                    .description("HIPAA notice acknowledged")
                    .build();
            consent.addAuditLog(log);

            consentRepository.save(consent);
            auditService.logAction("HIPAA_ACKNOWLEDGED", consentId, consent.getPatientId());
            log.info("HIPAA acknowledged for consent: {}", consentId);
        }
    }

    public long countActiveConsents() {
        return consentRepository.findActiveConsents(LocalDateTime.now()).size();
    }

    public long countHipaaAcknowledgedConsents() {
        return consentRepository.countByHipaaAcknowledgedTrue();
    }
}
