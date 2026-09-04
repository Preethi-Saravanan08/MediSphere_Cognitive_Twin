package com.medisphere.controller;

import com.medisphere.domain.Patient;
import com.medisphere.repository.PatientRepository;
import com.medisphere.service.HIPAAAuditService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/patients")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PatientController {

    private final PatientRepository patientRepository;
    private final HIPAAAuditService hipaaAuditService;

    public PatientController(
            PatientRepository patientRepository,
            HIPAAAuditService hipaaAuditService) {
        this.patientRepository = patientRepository;
        this.hipaaAuditService = hipaaAuditService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPatients() {
        try {
            long count = patientRepository.count();
            List<Patient> patients = patientRepository.findAll();
            
            hipaaAuditService.logAccess("PATIENT_LIST_ACCESSED", "ALL", "Retrieved patient list");
            
            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Patients retrieved successfully",
                    patients,
                    count
            ));
        } catch (Exception e) {
            log.error("Error retrieving patients", e);
            hipaaAuditService.logError("PATIENT_LIST_ERROR", "ALL", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving patients", null, 0));
        }
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<?> getPatientById(@PathVariable String patientId) {
        try {
            Optional<Patient> patient = patientRepository.findById(patientId);
            
            hipaaAuditService.logAccess("PATIENT_ACCESSED", patientId, "Retrieved patient record");
            
            return patient.map(value -> ResponseEntity.ok(new ApiResponse(
                    true,
                    "Patient retrieved successfully",
                    value,
                    1L
            ))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Patient not found", null, 0)));
        } catch (Exception e) {
            log.error("Error retrieving patient: {}", patientId, e);
            hipaaAuditService.logError("PATIENT_RETRIEVAL_ERROR", patientId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving patient", null, 0));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getPatientsByStatus(@PathVariable String status) {
        try {
            Patient.PatientStatus patientStatus = Patient.PatientStatus.valueOf(status.toUpperCase());
            List<Patient> patients = patientRepository.findByStatus(patientStatus);
            
            hipaaAuditService.logAccess("PATIENT_STATUS_LIST", status, "Retrieved patients by status");
            
            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Patients retrieved successfully",
                    patients,
                    (long) patients.size()
            ));
        } catch (Exception e) {
            log.error("Error retrieving patients by status: {}", status, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Invalid status or error retrieving patients", null, 0));
        }
    }

    @PostMapping
    public ResponseEntity<?> createPatient(@RequestBody PatientRequest request) {
        try {
            Patient patient = Patient.builder()
                    .patientId(request.getPatientId())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .dateOfBirth(request.getDateOfBirth())
                    .gender(request.getGender())
                    .contact(request.getContact())
                    .email(request.getEmail())
                    .status(Patient.PatientStatus.ONBOARDING)
                    .active(true)
                    .consentProvided(false)
                    .hipaaAcknowledged(false)
                    .createdAt(LocalDateTime.now())
                    .createdBy("API")
                    .build();

            Patient savedPatient = patientRepository.save(patient);
            
            hipaaAuditService.logAccess("PATIENT_CREATED", savedPatient.getId(), "New patient created");
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Patient created successfully", savedPatient, 1L));
        } catch (Exception e) {
            log.error("Error creating patient", e);
            hipaaAuditService.logError("PATIENT_CREATION_ERROR", "NEW", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error creating patient", null, 0));
        }
    }

    @PutMapping("/{patientId}")
    public ResponseEntity<?> updatePatient(@PathVariable String patientId, @RequestBody PatientRequest request) {
        try {
            Optional<Patient> patientOpt = patientRepository.findById(patientId);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "Patient not found", null, 0));
            }

            Patient patient = patientOpt.get();
            patient.setFirstName(request.getFirstName());
            patient.setLastName(request.getLastName());
            patient.setGender(request.getGender());
            patient.setContact(request.getContact());
            patient.setEmail(request.getEmail());
            patient.setUpdatedAt(LocalDateTime.now());
            patient.setUpdatedBy("API");

            Patient updatedPatient = patientRepository.save(patient);
            
            hipaaAuditService.logModification("PATIENT_UPDATED", patientId, "OLD_VALUES", "NEW_VALUES");
            
            return ResponseEntity.ok(new ApiResponse(true, "Patient updated successfully", updatedPatient, 1L));
        } catch (Exception e) {
            log.error("Error updating patient: {}", patientId, e);
            hipaaAuditService.logError("PATIENT_UPDATE_ERROR", patientId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error updating patient", null, 0));
        }
    }

    @GetMapping("/count/active")
    public ResponseEntity<?> getActivePatientCount() {
        try {
            long count = patientRepository.countByActiveTrue();
            
            hipaaAuditService.logAccess("PATIENT_COUNT_ACCESSED", "ACTIVE", "Retrieved active patient count");
            
            return ResponseEntity.ok(new ApiResponse(true, "Active patient count", count, 1L));
        } catch (Exception e) {
            log.error("Error getting active patient count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error getting patient count", null, 0));
        }
    }

    @GetMapping("/count/consented")
    public ResponseEntity<?> getConsentedPatientCount() {
        try {
            long count = patientRepository.countByConsentProvidedTrue();
            
            hipaaAuditService.logAccess("CONSENTED_PATIENT_COUNT", "ALL", "Retrieved consented patient count");
            
            return ResponseEntity.ok(new ApiResponse(true, "Consented patient count", count, 1L));
        } catch (Exception e) {
            log.error("Error getting consented patient count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error getting consented patient count", null, 0));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientRequest {
        private String patientId;
        private String firstName;
        private String lastName;
        private String dateOfBirth;
        private String gender;
        private String contact;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
        private long count;
    }
}
