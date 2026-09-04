package com.medisphere.repository;

import com.medisphere.domain.LabResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LabResultRepository extends MongoRepository<LabResult, String> {

    List<LabResult> findByPatientIdOrderByTestDateDesc(String patientId);

    List<LabResult> findByPatientIdAndTestDateBetweenOrderByTestDateDesc(
            String patientId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'patientId': ?0, 'abnormal': true }")
    List<LabResult> findAbnormalResults(String patientId);

    @Query("{ 'patientId': ?0, 'interpretation': 'CRITICAL' }")
    List<LabResult> findCriticalResults(String patientId);

    @Query("{ 'patientId': ?0, 'testCode': ?1 }")
    List<LabResult> findByPatientAndTestCode(String patientId, String testCode);

    @Query("{ 'testCode': ?0, 'testDate': { $gte: ?1 } }")
    List<LabResult> findRecentTestResults(String testCode, LocalDateTime since);

    @Query("{ 'patientId': ?0, 'validated': false }")
    List<LabResult> findUnvalidatedResults(String patientId);

    long countByPatientId(String patientId);

    long countByPatientIdAndAbnormalTrue(String patientId);

    long countByPatientIdAndInterpretation(String patientId, String interpretation);
}
