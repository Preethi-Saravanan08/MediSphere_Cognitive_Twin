package com.medisphere.repository;

import com.medisphere.domain.HealthTwin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthTwinRepository extends MongoRepository<HealthTwin, String> {

    Optional<HealthTwin> findByPatientId(String patientId);

    Optional<HealthTwin> findByTwinId(String twinId);

    @Query("{ 'twinDataCompleteness': { $gte: ?0 } }")
    List<HealthTwin> findTwinsByMinimumCompleteness(int completeness);

    @Query("{ 'overallRiskLevel': ?0 }")
    List<HealthTwin> findByOverallRiskLevel(String riskLevel);

    @Query("{ 'overallRiskLevel': { $in: ['HIGH', 'CRITICAL'] } }")
    List<HealthTwin> findHighRiskPatients();

    List<HealthTwin> findByLastUpdatedAfter(LocalDateTime lastUpdated);

    List<HealthTwin> findByLastVitalUpdateBefore(LocalDateTime threshold);

    @Query("{ 'hipaaCompliant': true }")
    List<HealthTwin> findHipaaCompliantTwins();

    @Query("{ 'activeAlerts': { $ne: [] } }")
    List<HealthTwin> findTwinsWithActiveAlerts();

    long countByOverallRiskLevel(String riskLevel);
}
