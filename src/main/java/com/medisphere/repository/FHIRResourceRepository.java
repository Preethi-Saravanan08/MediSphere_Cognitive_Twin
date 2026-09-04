package com.medisphere.repository;

import com.medisphere.domain.FHIRResource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FHIRResourceRepository extends MongoRepository<FHIRResource, String> {

    Optional<FHIRResource> findByFhirResourceId(String fhirResourceId);

    List<FHIRResource> findByPatientId(String patientId);

    List<FHIRResource> findByResourceType(String resourceType);

    List<FHIRResource> findByEhrSystemId(String ehrSystemId);

    @Query("{ 'syncStatus': ?0 }")
    List<FHIRResource> findBySyncStatus(String syncStatus);

    @Query("{ 'syncStatus': 'FAILED' }")
    List<FHIRResource> findFailedSyncs();

    @Query("{ 'syncStatus': 'PENDING' }")
    List<FHIRResource> findPendingSyncs();

    @Query("{ 'syncStatus': 'RETRY', 'retryCount': { $lt: 3 } }")
    List<FHIRResource> findRetryableSyncs();

    @Query("{ 'validated': false }")
    List<FHIRResource> findUnvalidatedResources();

    @Query("{ 'patientId': ?0, 'syncedAt': { $lt: ?1 } }")
    List<FHIRResource> findNotSyncedSince(String patientId, LocalDateTime dateTime);

    @Query("{ 'patientId': ?0, 'resourceType': ?1 }")
    List<FHIRResource> findByPatientAndResourceType(String patientId, String resourceType);

    long countByResourceType(String resourceType);

    long countBySyncStatus(String syncStatus);

    long countByPatientId(String patientId);

    long countByValidatedTrue();
}
