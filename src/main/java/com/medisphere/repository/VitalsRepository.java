package com.medisphere.repository;

import com.medisphere.domain.Vitals;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VitalsRepository extends MongoRepository<Vitals, String> {

    List<Vitals> findByPatientIdOrderByTimestampDesc(String patientId);

    List<Vitals> findByPatientIdAndTimestampBetweenOrderByTimestampDesc(
            String patientId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("{ 'patientId': ?0, 'timestamp': { $gte: ?1 } }")
    List<Vitals> findRecentVitals(String patientId, LocalDateTime since);

    @Query("{ 'patientId': ?0, 'isValid': false }")
    List<Vitals> findInvalidVitals(String patientId);

    @Query("{ 'patientId': ?0, 'syncStatus': 'PENDING' }")
    List<Vitals> findPendingSyncVitals(String patientId);

    @Query("{ 'syncStatus': 'FAILED' }")
    List<Vitals> findFailedSyncVitals();

    @Query("{ 'sourceDevice': ?0, 'timestamp': { $gte: ?1 } }")
    List<Vitals> findVitalsByDeviceSince(String sourceDevice, LocalDateTime since);

    long countByPatientId(String patientId);

    long countByPatientIdAndIsValidTrue(String patientId);

    long countByPatientIdAndIsAnomalous(String patientId);
}
