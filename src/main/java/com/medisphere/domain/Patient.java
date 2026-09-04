package com.medisphere.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "patients")
public class Patient {

    @Id
    private String id;

    private String patientId; // FHIR Patient Resource ID
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String contact;
    private String email;

    // Medical Information
    private List<String> conditions; // ICD codes
    private List<String> medications;
    private List<String> allergies;

    // FHIR Integration
    private String fhirResourceId;
    private LocalDateTime fhirLastSynced;
    private String ehrSystemId;

    // Consent & Privacy
    private boolean consentProvided;
    private LocalDateTime consentDate;
    private String consentVersion;
    private boolean hipaaAcknowledged;

    // Audit Trail
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Status
    private PatientStatus status;
    private boolean active;

    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", status=" + status +
                '}';
    }

    public enum PatientStatus {
        ONBOARDING, ACTIVE, INACTIVE, ARCHIVED
    }
}
