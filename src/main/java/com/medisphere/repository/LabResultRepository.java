package com.medisphere.repository;

import com.medisphere.domain.LabResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LabResultRepository extends MongoRepository<LabResult, String> {

    List<LabResult> findByPatientId(String patientId);

    List<LabResult> findByPatientIdOrderByResultedAtDesc(String patientId);

    Optional<LabResult> findByFhirObservationId(String fhirObservationId);

    @Query("{ 'interpretation': 'ABNORMAL' }")
    List<LabResult> findAbnormalResults();

    @Query("{ 'interpretation': 'CRITICAL' }")
    List<LabResult> findCriticalResults();

    @Query("{ 'patientId': ?0, 'testName': ?1 }")
    List<LabResult> findByPatientAndTestName(String patientId, String testName);

    List<LabResult> findByResultedAtAfter(LocalDateTime resultedAt);

    List<LabResult> findByStatus(String status);

    long countByPatientId(String patientId);

    long countByInterpretation(String interpretation);
}
