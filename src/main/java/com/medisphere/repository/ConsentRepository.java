package com.medisphere.repository;

import com.medisphere.domain.Consent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentRepository extends MongoRepository<Consent, String> {

    Optional<Consent> findByPatientIdAndConsentType(String patientId, String consentType);

    List<Consent> findByPatientId(String patientId);

    List<Consent> findByStatus(String status);

    @Query("{ 'status': 'ACTIVE', 'expirationDate': { $gt: ?0 } }")
    List<Consent> findActiveConsents(LocalDateTime now);

    @Query("{ 'status': 'ACTIVE', 'expirationDate': { $lt: ?0 } }")
    List<Consent> findExpiredConsents(LocalDateTime now);

    @Query("{ 'hipaaAcknowledged': true }")
    List<Consent> findHipaaAcknowledgedConsents();

    @Query("{ 'consentType': 'AI_PREDICTION', 'status': 'ACTIVE' }")
    List<Consent> findActiveAIPredictionConsents();

    List<Consent> findByCreatedAtAfter(LocalDateTime createdAt);

    long countByPatientId(String patientId);

    long countByStatus(String status);

    long countByHipaaAcknowledgedTrue();
}
