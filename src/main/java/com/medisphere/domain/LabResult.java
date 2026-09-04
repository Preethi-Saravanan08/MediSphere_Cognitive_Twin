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

    private String patientId;
    private String testName;
    private String testCode; // LOINC code
    private String value;
    private String unit;
    private float numericValue;

    // Reference Ranges
    private float referenceMin;
    private float referenceMax;
    private String referenceRange;

    // Interpretation
    private String interpretation; // NORMAL, LOW, HIGH, CRITICAL
    private boolean abnormal;
    private String clinicalNotes;

    // Metadata
    private LocalDateTime testDate;
    private LocalDateTime resultsDate;
    private String laboratory;
    private String laboratorian;

    // FHIR Integration
    private String fhirObservationId;
    private String fhirBundleId;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean validated;

    public boolean isAbnormal() {
        if (numericValue == 0) return false;
        return numericValue < referenceMin || numericValue > referenceMax;
    }

    public boolean isCritical() {
        return "CRITICAL".equals(interpretation);
    }

    public String getStatus() {
        if (!isAbnormal()) return "NORMAL";
        if (isCritical()) return "CRITICAL";
        return interpretation;
    }
}
