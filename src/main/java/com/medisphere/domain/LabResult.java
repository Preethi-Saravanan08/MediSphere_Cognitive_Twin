package com.medisphere.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "lab_results")
public class LabResult {

    @Id
    private String id;

    private String labResultId;
    private String patientId;
    private String testName;
    private String testCode;

    // Result Values
    private String value;
    private String unit;
    private String referenceRange;
    private String normalRange;

    // Result Status
    private String status; // FINAL, PRELIMINARY, AMENDED, CANCELLED
    private String interpretation; // NORMAL, ABNORMAL, CRITICAL

    // Test Details
    private String specimentType;
    private String laboratory;
    private String provider;

    // Dates
    private LocalDateTime collectedAt;
    private LocalDateTime analyzedAt;
    private LocalDateTime resultedAt;

    // FHIR Integration
    private String fhirObservationId;
    private LocalDateTime fhirSyncedAt;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;

    public boolean isAbnormal() {
        return "ABNORMAL".equals(interpretation) || "CRITICAL".equals(interpretation);
    }

    public boolean isCritical() {
        return "CRITICAL".equals(interpretation);
    }
}
