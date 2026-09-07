package com.medisphere.repository;

import com.medisphere.domain.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

    Optional<Patient> findByPatientId(String patientId);

    Optional<Patient> findByFhirResourceId(String fhirResourceId);

    List<Patient> findByActive(boolean active);

    List<Patient> findByStatus(Patient.PatientStatus status);

    List<Patient> findByConsentProvidedTrue();

    List<Patient> findByHipaaAcknowledgedTrue();

    @Query("{ 'fhirLastSynced': { $lt: ?0 } }")
    List<Patient> findPatientsNotSyncedSince(LocalDateTime dateTime);

    @Query("{ 'ehrSystemId': ?0 }")
    List<Patient> findByEhrSystemId(String ehrSystemId);

    List<Patient> findByCreatedAtAfter(LocalDateTime createdAt);

    long countByActive(boolean active);

    long countByStatus(Patient.PatientStatus status);

    long countByConsentProvidedTrue();
}
