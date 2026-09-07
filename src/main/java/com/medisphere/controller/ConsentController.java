package com.medisphere.controller;

import com.medisphere.domain.Consent;
import com.medisphere.service.ConsentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/consents")
@Slf4j
public class ConsentController {

    @Autowired
    private ConsentService consentService;

    @PostMapping
    public ResponseEntity<Consent> createConsent(@RequestBody Consent consent) {
        log.info("Creating consent for patient: {} with type: {}", consent.getPatientId(), consent.getConsentType());
        Consent createdConsent = consentService.createConsent(consent);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConsent);
    }

    @GetMapping("/patient/{patientId}/type/{consentType}")
    public ResponseEntity<Consent> getConsentByPatientAndType(
            @PathVariable String patientId,
            @PathVariable String consentType) {
        log.debug("Fetching consent for patient: {} with type: {}", patientId, consentType);
        Optional<Consent> consent = consentService.getConsentByPatientAndType(patientId, consentType);
        return consent.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Consent>> getConsentsByPatient(@PathVariable String patientId) {
        log.debug("Fetching all consents for patient: {}", patientId);
        List<Consent> consents = consentService.getConsentsByPatient(patientId);
        return ResponseEntity.ok(consents);
    }

    @GetMapping("/list/active")
    public ResponseEntity<List<Consent>> getActiveConsents() {
        log.debug("Fetching active consents");
        List<Consent> consents = consentService.getActiveConsents();
        return ResponseEntity.ok(consents);
    }

    @GetMapping("/list/expired")
    public ResponseEntity<List<Consent>> getExpiredConsents() {
        log.debug("Fetching expired consents");
        List<Consent> consents = consentService.getExpiredConsents();
        return ResponseEntity.ok(consents);
    }

    @GetMapping("/check/active/{patientId}/{consentType}")
    public ResponseEntity<Boolean> hasActiveConsent(
            @PathVariable String patientId,
            @PathVariable String consentType) {
        log.debug("Checking active consent for patient: {} with type: {}", patientId, consentType);
        boolean hasConsent = consentService.hasActiveConsent(patientId, consentType);
        return ResponseEntity.ok(hasConsent);
    }

    @PostMapping("/{consentId}/revoke")
    public ResponseEntity<Void> revokeConsent(
            @PathVariable String consentId,
            @RequestParam String revokedBy) {
        log.info("Revoking consent: {} by {}", consentId, revokedBy);
        consentService.revokeConsent(consentId, revokedBy);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{consentId}/verify")
    public ResponseEntity<Void> verifyConsent(
            @PathVariable String consentId,
            @RequestParam String verifiedBy) {
        log.info("Verifying consent: {} by {}", consentId, verifiedBy);
        consentService.verifyConsent(consentId, verifiedBy);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{consentId}/hipaa-acknowledge")
    public ResponseEntity<Void> acknowledgeHipaa(
            @PathVariable String consentId,
            @RequestParam String acknowledgedBy) {
        log.info("HIPAA acknowledged for consent: {} by {}", consentId, acknowledgedBy);
        consentService.acknowledgeHipaa(consentId, acknowledgedBy);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/active-count")
    public ResponseEntity<Long> countActiveConsents() {
        log.debug("Counting active consents");
        long count = consentService.countActiveConsents();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/hipaa-acknowledged-count")
    public ResponseEntity<Long> countHipaaAcknowledgedConsents() {
        log.debug("Counting HIPAA acknowledged consents");
        long count = consentService.countHipaaAcknowledgedConsents();
        return ResponseEntity.ok(count);
    }
}
