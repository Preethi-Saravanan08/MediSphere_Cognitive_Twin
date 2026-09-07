package com.medisphere.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "health_twins")
public class HealthTwin {

    @Id
    private String id;

    private String twinId;
    private String patientId;
    private String modelVersion;

    // Patient Core Data
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;

    // Health Metrics
    private CurrentVitals currentVitals;
    private List<LabResult> recentLabResults;
    private List<String> activeConditions;
    private List<String> currentMedications;
    private List<String> allergies;

    // Risk Indicators
    private Map<String, Float> riskScores; // e.g., "cardiovascular": 0.24, "diabetes": 0.18
    private String overallRiskLevel; // LOW, MODERATE, HIGH, CRITICAL

    // Care Information
    private String carePlanId;
    private List<Alert> activeAlerts;
    private List<Provider> assignedProviders;

    // Historical Data
    private int twinDataCompleteness; // Percentage 0-100
    private LocalDateTime lastUpdated;
    private LocalDateTime createdAt;
    private LocalDateTime lastVitalUpdate;

    // Data Lineage
    private String primaryDataSource; // WEARABLE, EHR, MANUAL
    private List<String> integratedSources;

    // Audit & Compliance
    private boolean hipaaCompliant;
    private LocalDateTime lastAuditDate;
    private String auditStatus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CurrentVitals {
        private float heartRate;
        private float systolicBP;
        private float diastolicBP;
        private float oxygenSaturation;
        private float temperature;
        private float bloodGlucose;
        private LocalDateTime recordedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LabResult {
        private String testName;
        private String value;
        private String unit;
        private String status;
        private LocalDateTime testedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Alert {
        private String alertId;
        private String alertType;
        private String severity;
        private String message;
        private LocalDateTime createdAt;
        private boolean acknowledged;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Provider {
        private String providerId;
        private String name;
        private String specialty;
        private String role;
    }

    public int calculateDataCompleteness() {
        int completeness = 0;
        int totalFields = 0;

        if (currentVitals != null) {
            completeness += 2;
        }
        totalFields += 2;

        if (recentLabResults != null && !recentLabResults.isEmpty()) {
            completeness += 2;
        }
        totalFields += 2;

        if (activeConditions != null && !activeConditions.isEmpty()) {
            completeness += 1;
        }
        totalFields += 1;

        if (currentMedications != null && !currentMedications.isEmpty()) {
            completeness += 1;
        }
        totalFields += 1;

        return totalFields > 0 ? (completeness * 100) / totalFields : 0;
    }
}
