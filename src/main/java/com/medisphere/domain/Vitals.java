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
@Document(collection = "vitals")
public class Vitals {

    @Id
    private String id;

    private String patientId;
    private LocalDateTime timestamp;
    private String sourceDevice; // Wearable device ID

    // Vital Signs
    private float heartRate; // bpm
    private float systolicBP; // mmHg
    private float diastolicBP; // mmHg
    private float temperature; // Celsius
    private float respiratoryRate; // breaths/min
    private float oxygenSaturation; // SpO2 %
    private float bloodGlucose; // mg/dL

    // Additional Metrics
    private float weight; // kg
    private float height; // cm
    private float bmi;

    // Activity Data
    private int stepCount;
    private float caloriesBurned;
    private int sleepDuration; // minutes

    // Validation
    private boolean isValid;
    private String validationErrors;

    // Data Quality
    private float confidence; // 0-1 scale
    private String dataQuality; // GOOD, FAIR, POOR

    // Audit
    private LocalDateTime recordedAt;
    private LocalDateTime syncedAt;
    private String syncStatus; // PENDING, SYNCED, FAILED

    public boolean isWithinNormalRange() {
        return heartRate > 40 && heartRate < 150 &&
               systolicBP > 80 && systolicBP < 200 &&
               diastolicBP > 40 && diastolicBP < 130 &&
               temperature > 35 && temperature < 40 &&
               oxygenSaturation >= 95;
    }

    public boolean isAnomalous() {
        return heartRate > 140 || heartRate < 50 ||
               systolicBP > 180 ||
               diastolicBP > 120 ||
               oxygenSaturation < 90;
    }
}
