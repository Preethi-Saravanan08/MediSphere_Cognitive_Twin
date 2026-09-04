package com.medisphere.controller;

import com.medisphere.service.ConsentManagementService;
import com.medisphere.service.HIPAAAuditService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/consent")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ConsentController {

    private final ConsentManagementService consentManagementService;
    private final HIPAAAuditService hipaaAuditService;

    public ConsentController(
            ConsentManagementService consentManagementService,
            HIPAAAuditService hipaaAuditService) {
        this.consentManagementService = consentManagementService;
        this.hipaaAuditService = hipaaAuditService;
    }

    @PostMapping("/{patientId}/grant")
    public ResponseEntity<?> grantConsent(
            @PathVariable String patientId,
            @RequestBody ConsentRequest request) {
        try {
            boolean success = consentManagementService.grantConsent(patientId, request.getReason());
            
            if (success) {
                hipaaAuditService.logConsentChange(patientId, true, request.getReason());
                return ResponseEntity.ok(new ApiResponse(
                        true,
                        "Consent granted successfully",
                        null
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(false, "Failed to grant consent", null));
            }
        } catch (Exception e) {
            log.error("Error granting consent for patient: {}", patientId, e);
            hipaaAuditService.logError("CONSENT_GRANT_ERROR", patientId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error granting consent", null));
        }
    }

    @PostMapping("/{patientId}/revoke")
    public ResponseEntity<?> revokeConsent(
            @PathVariable String patientId,
            @RequestBody ConsentRequest request) {
        try {
            boolean success = consentManagementService.revokeConsent(patientId, request.getReason());
            
            if (success) {
                hipaaAuditService.logConsentChange(patientId, false, request.getReason());
                return ResponseEntity.ok(new ApiResponse(
                        true,
                        "Consent revoked successfully",
                        null
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(false, "Failed to revoke consent", null));
            }
        } catch (Exception e) {
            log.error("Error revoking consent for patient: {}", patientId, e);
            hipaaAuditService.logError("CONSENT_REVOKE_ERROR", patientId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error revoking consent", null));
        }
    }

    @GetMapping("/{patientId}/status")
    public ResponseEntity<?> getConsentStatus(@PathVariable String patientId) {
        try {
            ConsentManagementService.ConsentStatus status = consentManagementService.getConsentStatus(patientId);
            
            if (status == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "Patient not found", null));
            }
            
            hipaaAuditService.logAccess("CONSENT_STATUS_ACCESSED", patientId, "Retrieved consent status");
            
            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Consent status retrieved successfully",
                    status
            ));
        } catch (Exception e) {
            log.error("Error retrieving consent status for patient: {}", patientId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving consent status", null));
        }
    }

    @GetMapping("/{patientId}/verify")
    public ResponseEntity<?> verifyConsent(@PathVariable String patientId) {
        try {
            boolean hasConsent = consentManagementService.hasValidConsent(patientId);
            
            hipaaAuditService.logAccess("CONSENT_VERIFIED", patientId, "Verified consent status");
            
            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Consent verification completed",
                    hasConsent
            ));
        } catch (Exception e) {
            log.error("Error verifying consent for patient: {}", patientId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error verifying consent", null));
        }
    }

    @GetMapping("/count/consented")
    public ResponseEntity<?> getConsentedPatientCount() {
        try {
            long count = consentManagementService.getConsentedPatientCount();
            
            hipaaAuditService.logAccess("CONSENTED_COUNT_ACCESSED", "ALL", "Retrieved consented patient count");
            
            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Consented patient count retrieved",
                    count
            ));
        } catch (Exception e) {
            log.error("Error getting consented patient count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error getting count", null));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsentRequest {
        private String reason;
        private String version;
        private String signature;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
    }
}
