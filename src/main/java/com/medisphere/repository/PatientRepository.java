package com.medisphere.repository;

import com.medisphere.domain.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

    Optional<Patient> findByPatientId(String patientId);

    Optional<Patient> findByFhirResourceId(String fhirResourceId);

    List<Patient> findByStatus(Patient.PatientStatus status);

    List<Patient> findByActiveTrue();

    List<Patient> findByConsentProvidedTrue();

    List<Patient> findByEhrSystemId(String ehrSystemId);

    @Query("{ 'fhirLastSynced': { $lt: ?0 } }")
    List<Patient> findPatientsNotSyncedSince(java.time.LocalDateTime dateTime);

    long countByStatus(Patient.PatientStatus status);

    long countByActiveTrue();

    long countByConsentProvidedTrue();
}
