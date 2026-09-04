package com.medisphere.controller;

import com.medisphere.domain.HealthTwin;
import com.medisphere.repository.HealthTwinRepository;
import com.medisphere.service.HIPAAAuditService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/twins")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HealthTwinController {

    private final HealthTwinRepository healthTwinRepository;
    private final HIPAAAuditService hipaaAuditService;

    public HealthTwinController(
            HealthTwinRepository healthTwinRepository,
            HIPAAAuditService hipaaAuditService) {
        this.healthTwinRepository = healthTwinRepository;
        this.hipaaAuditService = hipaaAuditService;
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getTwinByPatientId(@PathVariable String patientId) {
        try {
            Optional<HealthTwin> twin = healthTwinRepository.findByPatientId(patientId);
            
            hipaaAuditService.logAccess("HEALTH_TWIN_ACCESSED", patientId, "Retrieved health twin");
            
            return twin.map(value -> ResponseEntity.ok(new ApiResponse(
                    true,
                    "Health twin retrieved successfully",
                    value
            ))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Health twin not found", null)));
        } catch (Exception e) {
            log.error("Error retrieving health twin for patient: {}", patientId, e);
            hipaaAuditService.logError("HEALTH_TWIN_ERROR", patientId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving health twin", null));
        }
    }

    @GetMapping("/{twinId}")
    public ResponseEntity<?> getTwinById(@PathVariable String twinId) {
        try {
            Optional<HealthTwin> twin = healthTwinRepository.findByTwinId(twinId);
            
            hipaaAuditService.logAccess("HEALTH_TWIN_ACCESSED", twinId, "Retrieved health twin by ID");
            
            return twin.map(value -> ResponseEntity.ok(new ApiResponse(
                    true,
                    "Health twin retrieved successfully",
                    value
            ))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Health twin not found", null)));
        } catch (Exception e) {
            log.error("Error retrieving health twin: {}", twinId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving health twin", null));
        }
    }

    @GetMapping("/ready-for-prediction")
    public ResponseEntity<?> getValidatedTwins() {
        try {
            List<HealthTwin> twins = healthTwinRepository.findValidatedForPrediction();
            
            hipaaAuditService.logAccess("VALIDATED_TWINS_LIST", "ALL", "Retrieved validated twins");
            
            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Validated twins retrieved successfully",
                    twins
            ));
        } catch (Exception e) {
            log.error("Error retrieving validated twins", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving twins", null));
        }
    }

    @GetMapping("/count/validated")
    public ResponseEntity<?> getValidatedTwinCount() {
        try {
            long count = healthTwinRepository.countByValidatedForPrediction(true);
            
            hipaaAuditService.logAccess("VALIDATED_TWIN_COUNT", "ALL", "Retrieved validated twin count");
            
            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Validated twin count retrieved",
                    count
            ));
        } catch (Exception e) {
            log.error("Error getting validated twin count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving count", null));
        }
    }

    @GetMapping("/completeness/stats")
    public ResponseEntity<?> getCompletenessStats() {
        try {
            long above95 = healthTwinRepository.countByCompletenessGreaterThanOrEqual(95.0);
            long above80 = healthTwinRepository.countByCompletenessGreaterThanOrEqual(80.0);
            long above60 = healthTwinRepository.countByCompletenessGreaterThanOrEqual(60.0);
            long total = healthTwinRepository.count();
            
            hipaaAuditService.logAccess("TWIN_COMPLETENESS_STATS", "ALL", "Retrieved completeness stats");
            
            CompletenessStats stats = CompletenessStats.builder()
                    .above95Percent(above95)
                    .above80Percent(above80)
                    .above60Percent(above60)
                    .totalTwins(total)
                    .build();
            
            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Completeness statistics retrieved",
                    stats
            ));
        } catch (Exception e) {
            log.error("Error getting completeness stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving stats", null));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompletenessStats {
        private long above95Percent;
        private long above80Percent;
        private long above60Percent;
        private long totalTwins;

        public static CompletenessStatsBuilder builder() {
            return new CompletenessStatsBuilder();
        }

        public static class CompletenessStatsBuilder {
            private long above95Percent;
            private long above80Percent;
            private long above60Percent;
            private long totalTwins;

            public CompletenessStatsBuilder above95Percent(long above95Percent) {
                this.above95Percent = above95Percent;
                return this;
            }

            public CompletenessStatsBuilder above80Percent(long above80Percent) {
                this.above80Percent = above80Percent;
                return this;
            }

            public CompletenessStatsBuilder above60Percent(long above60Percent) {
                this.above60Percent = above60Percent;
                return this;
            }

            public CompletenessStatsBuilder totalTwins(long totalTwins) {
                this.totalTwins = totalTwins;
                return this;
            }

            public CompletenessStats build() {
                return new CompletenessStats(above95Percent, above80Percent, above60Percent, totalTwins);
            }
        }
    }
}
