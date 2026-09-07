package com.medisphere.repository;

import com.medisphere.domain.Vitals;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VitalsRepository extends MongoRepository<Vitals, String> {

    List<Vitals> findByPatientId(String patientId);

    List<Vitals> findByPatientIdOrderByTimestampDesc(String patientId);

    @Query("{ 'patientId': ?0, 'timestamp': { $gte: ?1, $lte: ?2 } }")
    List<Vitals> findVitalsByPatientAndDateRange(String patientId, LocalDateTime from, LocalDateTime to);

    List<Vitals> findBySourceDevice(String deviceId);

    @Query("{ 'isValid': false }")
    List<Vitals> findInvalidVitals();

    @Query("{ 'syncStatus': 'PENDING' }")
    List<Vitals> findPendingSyncVitals();

    @Query("{ 'dataQuality': 'POOR' }")
    List<Vitals> findPoorQualityVitals();

    @Query("{ 'heartRate': { $gt: 140 } } | { 'heartRate': { $lt: 50 } }")
    List<Vitals> findAnomalousHeartRates();

    @Query("{ 'oxygenSaturation': { $lt: 90 } }")
    List<Vitals> findLowOxygenSaturation();

    List<Vitals> findByTimestampAfter(LocalDateTime timestamp);

    long countByPatientId(String patientId);

    long countBySyncStatus(String syncStatus);
}
