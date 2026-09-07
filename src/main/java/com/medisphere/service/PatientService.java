package com.medisphere.service;

import com.medisphere.domain.Patient;
import com.medisphere.repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AuditService auditService;

    @Transactional
    public Patient createPatient(Patient patient) {
        log.info("Creating new patient: {}", patient.getFirstName() + " " + patient.getLastName());

        patient.setId(null);
        patient.setStatus(Patient.PatientStatus.ONBOARDING);
        patient.setCreatedAt(LocalDateTime.now());
        patient.setUpdatedAt(LocalDateTime.now());
        patient.setActive(true);

        Patient savedPatient = patientRepository.save(patient);
        auditService.logAction("PATIENT_CREATED", savedPatient.getId(), patient.getFirstName());
        log.info("Patient created successfully: {}", savedPatient.getId());

        return savedPatient;
    }

    public Optional<Patient> getPatientById(String id) {
        log.debug("Fetching patient by ID: {}", id);
        return patientRepository.findById(id);
    }

    public Optional<Patient> getPatientByPatientId(String patientId) {
        log.debug("Fetching patient by patientId: {}", patientId);
        return patientRepository.findByPatientId(patientId);
    }

    @Transactional
    public Patient updatePatient(Patient patient) {
        log.info("Updating patient: {}", patient.getId());

        patient.setUpdatedAt(LocalDateTime.now());
        Patient updatedPatient = patientRepository.save(patient);
        auditService.logAction("PATIENT_UPDATED", patient.getId(), patient.getFirstName());

        return updatedPatient;
    }

    public List<Patient> getAllActivePatients() {
        log.debug("Fetching all active patients");
        return patientRepository.findByActive(true);
    }

    public List<Patient> getPatientsByStatus(Patient.PatientStatus status) {
        log.debug("Fetching patients by status: {}", status);
        return patientRepository.findByStatus(status);
    }

    public List<Patient> getPatientsWithConsent() {
        log.debug("Fetching patients with consent");
        return patientRepository.findByConsentProvidedTrue();
    }

    public List<Patient> getPatientsWithHipaaAcknowledgment() {
        log.debug("Fetching patients with HIPAA acknowledgment");
        return patientRepository.findByHipaaAcknowledgedTrue();
    }

    @Transactional
    public void updateFhirSync(String patientId, String fhirResourceId) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            patient.setFhirResourceId(fhirResourceId);
            patient.setFhirLastSynced(LocalDateTime.now());
            patientRepository.save(patient);
            log.info("FHIR sync updated for patient: {}", patientId);
        }
    }

    @Transactional
    public void updateConsentStatus(String patientId, boolean consentProvided, String version) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            patient.setConsentProvided(consentProvided);
            patient.setConsentDate(LocalDateTime.now());
            patient.setConsentVersion(version);
            patientRepository.save(patient);
            auditService.logAction("CONSENT_UPDATED", patientId, patient.getFirstName());
            log.info("Consent status updated for patient: {}", patientId);
        }
    }

    @Transactional
    public void updateHipaaStatus(String patientId, boolean acknowledged) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            patient.setHipaaAcknowledged(acknowledged);
            patientRepository.save(patient);
            auditService.logAction("HIPAA_ACKNOWLEDGED", patientId, patient.getFirstName());
            log.info("HIPAA acknowledgment updated for patient: {}", patientId);
        }
    }

    public long countActivePatients() {
        return patientRepository.countByActive(true);
    }

    public long countPatientsWithConsent() {
        return patientRepository.countByConsentProvidedTrue();
    }

    public List<Patient> getPatientsNotSyncedSince(LocalDateTime dateTime) {
        log.debug("Fetching patients not synced since: {}", dateTime);
        return patientRepository.findPatientsNotSyncedSince(dateTime);
    }
}
