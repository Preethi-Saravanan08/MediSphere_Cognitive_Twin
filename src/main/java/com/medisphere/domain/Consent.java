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
@Document(collection = "consents")
public class Consent {

    @Id
    private String id;

    private String consentId;
    private String patientId;
    private String patientName;

    // Consent Details
    private String consentType; // FULL, HIPAA, DATA_PROCESSING, AI_PREDICTION
    private String status; // ACTIVE, REVOKED, EXPIRED, PENDING
    private String version;

    // Scope
    private List<String> dataCategories; // VITALS, LABS, MEDICATIONS, CONDITIONS, GENOMICS
    private List<String> purposes; // TREATMENT, RESEARCH, ANALYTICS, CARE_COORDINATION
    private List<String> authorizedProviders;

    // Dates
    private LocalDateTime effectiveDate;
    private LocalDateTime expirationDate;
    private LocalDateTime createdAt;
    private LocalDateTime revokedAt;

    // HIPAA Compliance
    private String hipaaVersion;
    private boolean hipaaAcknowledged;
    private LocalDateTime hipaaAcknowledgedDate;

    // Verification
    private String consentMethod; // DIGITAL, PAPER, VERBAL
    private String verificationMethod;
    private String verifiedBy;
    private LocalDateTime verifiedAt;

    // Audit Trail
    private String createdBy;
    private String ipAddress;
    private String deviceInfo;
    private List<ConsentAuditLog> auditLogs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConsentAuditLog {
        private String action; // CREATED, VIEWED, MODIFIED, REVOKED, VERIFIED
        private LocalDateTime timestamp;
        private String userId;
        private String description;
        private String ipAddress;
    }

    public boolean isActive() {
        return "ACTIVE".equals(status) &&
               (expirationDate == null || expirationDate.isAfter(LocalDateTime.now()));
    }

    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDateTime.now());
    }

    public void addAuditLog(ConsentAuditLog log) {
        if (auditLogs == null) {
            auditLogs = new java.util.ArrayList<>();
        }
        auditLogs.add(log);
    }
}
