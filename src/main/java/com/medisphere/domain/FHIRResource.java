package com.medisphere.domain;

import com.fasterxml.jackson.databind.JsonNode;
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
@Document(collection = "fhir_resources")
public class FHIRResource {

    @Id
    private String id;

    private String resourceType; // Patient, Observation, MedicationStatement, etc.
    private String fhirResourceId;
    private String patientId;
    private String ehrSystemId;

    // Raw FHIR Resource Data
    private JsonNode resourceData;

    // Metadata
    private String resourceUrl;
    private LocalDateTime resourceCreatedDate;
    private LocalDateTime resourceUpdatedDate;
    private String resourceStatus;

    // Sync Information
    private LocalDateTime syncedAt;
    private String syncStatus; // SYNCED, PENDING, FAILED, RETRY
    private int retryCount;
    private String lastError;

    // Validation
    private boolean validated;
    private String validationErrors;

    // Mapping Information
    private String mappedToEntityType; // Patient, Vitals, LabResult, etc.
    private String mappedToEntityId;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public boolean needsRetry() {
        return "FAILED".equals(syncStatus) || "RETRY".equals(syncStatus);
    }

    public boolean canBeProcessed() {
        return "SYNCED".equals(syncStatus) && validated;
    }
}
