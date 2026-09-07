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

    @Query("{ 'syncStatus': 'FAILED' }")
    List<FHIRResource> findFailedSyncs();

    @Query("{ 'syncStatus': 'PENDING' }")
    List<FHIRResource> findPendingSyncs();

    @Query("{ 'validated': false }")
    List<FHIRResource> findUnvalidatedResources();

    @Query("{ 'syncStatus': ?0, 'retryCount': { $lt: 5 } }")
    List<FHIRResource> findRetryableResources(String syncStatus);

    List<FHIRResource> findByEhrSystemId(String ehrSystemId);

    List<FHIRResource> findBySyncedAtAfter(LocalDateTime syncedAt);

    List<FHIRResource> findByMappedToEntityType(String entityType);

    long countByResourceType(String resourceType);

    long countBySyncStatus(String syncStatus);

    long countByValidatedTrue();
}
