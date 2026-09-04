package com.medisphere.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "health_twins")
public class HealthTwin {

    @Id
    private String id;

    private String patientId;
    private String twinId;
    private String modelVersion;

    // Twin Status
    private double completeness; // Percentage 0-100
    private LocalDateTime lastUpdate;
    private LocalDateTime createdAt;

    // Latest Vitals (snapshot)
    private Vitals latestVitals;

    // Latest Lab Results (snapshot)
    private List<LabResult> recentLabResults;

    // Risk Heatmap Data
    private RiskHeatmap riskHeatmap;

    // 3D Body Model Reference
    private String bodyModelUrl;
    private String bodyModelVersion;

    // Historical Data References
    private int vitalsDataPoints;
    private int labsDataPoints;
    private LocalDateTime dataStartDate;
    private LocalDateTime dataEndDate;

    // Validation
    private boolean validatedForPrediction;
    private LocalDateTime validationDate;
    private String validationErrors;

    // Versioning
    private int version;
    private LocalDateTime modifiedAt;
    private String modifiedBy;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RiskHeatmap {
        private double cardiovascularRisk;
        private double diabetesRisk;
        private double readmissionRisk;
        private List<OrganRisk> organRisks;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OrganRisk {
            private String organ;
            private double riskScore;
            private String color; // red, yellow, green
        }
    }

    public double getCompletenessPercentage() {
        return completeness;
    }

    public boolean isReadyForPrediction() {
        return completeness >= 95.0 && validatedForPrediction;
    }
}
