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

    @Query("{ 'completeness': { $gte: ?0 } }")
    List<HealthTwin> findByCompletenessGreaterThanOrEqual(double completeness);

    @Query("{ 'validatedForPrediction': true }")
    List<HealthTwin> findValidatedForPrediction();

    @Query("{ 'lastUpdate': { $lt: ?0 } }")
    List<HealthTwin> findByLastUpdateBefore(LocalDateTime dateTime);

    @Query("{ 'lastUpdate': { $gte: ?0, $lt: ?1 } }")
    List<HealthTwin> findByLastUpdateBetween(LocalDateTime startDate, LocalDateTime endDate);

    long countByValidatedForPrediction(boolean validated);

    long countByCompletenessGreaterThanOrEqual(double completeness);
}
