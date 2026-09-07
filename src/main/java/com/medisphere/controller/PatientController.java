package com.medisphere.controller;

import com.medisphere.domain.Patient;
import com.medisphere.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/patients")
@Slf4j
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        log.info("Creating new patient: {} {}", patient.getFirstName(), patient.getLastName());
        Patient createdPatient = patientService.createPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable String id) {
        log.debug("Fetching patient: {}", id);
        Optional<Patient> patient = patientService.getPatientById(id);
        return patient.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search/by-patient-id/{patientId}")
    public ResponseEntity<Patient> getPatientByPatientId(@PathVariable String patientId) {
        log.debug("Searching patient by patientId: {}", patientId);
        Optional<Patient> patient = patientService.getPatientByPatientId(patientId);
        return patient.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable String id, @RequestBody Patient patient) {
        log.info("Updating patient: {}", id);
        patient.setId(id);
        Patient updatedPatient = patientService.updatePatient(patient);
        return ResponseEntity.ok(updatedPatient);
    }

    @GetMapping("/list/active")
    public ResponseEntity<List<Patient>> getAllActivePatients() {
        log.debug("Fetching all active patients");
        List<Patient> patients = patientService.getAllActivePatients();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/list/with-consent")
    public ResponseEntity<List<Patient>> getPatientsWithConsent() {
        log.debug("Fetching patients with consent");
        List<Patient> patients = patientService.getPatientsWithConsent();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/stats/count")
    public ResponseEntity<Long> countActivePatients() {
        log.debug("Counting active patients");
        long count = patientService.countActivePatients();
        return ResponseEntity.ok(count);
    }

    @PostMapping("/{id}/consent")
    public ResponseEntity<Void> updateConsent(
            @PathVariable String id,
            @RequestParam boolean provided,
            @RequestParam String version) {
        log.info("Updating consent for patient: {} (provided: {})", id, provided);
        patientService.updateConsentStatus(id, provided, version);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/hipaa-acknowledgment")
    public ResponseEntity<Void> updateHipaaStatus(
            @PathVariable String id,
            @RequestParam boolean acknowledged) {
        log.info("Updating HIPAA status for patient: {} (acknowledged: {})", id, acknowledged);
        patientService.updateHipaaStatus(id, acknowledged);
        return ResponseEntity.ok().build();
    }
}
