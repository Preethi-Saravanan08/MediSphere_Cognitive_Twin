package com.medisphere.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medisphere.domain.FHIRResource;
import com.medisphere.repository.FHIRResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class FhirIntegrationService {

    @Autowired
    private FHIRResourceRepository fhirResourceRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${fhir.server-url}")
    private String fhirServerUrl;

    @Transactional
    public FHIRResource saveFhirResource(FHIRResource fhirResource) {
        log.info("Saving FHIR resource: {} for patient: {}", fhirResource.getResourceType(), fhirResource.getPatientId());

        if (fhirResource.getId() == null) {
            fhirResource.setId(UUID.randomUUID().toString());
        }

        fhirResource.setCreatedAt(LocalDateTime.now());
        fhirResource.setSyncedAt(LocalDateTime.now());
        fhirResource.setSyncStatus("SYNCED");

        FHIRResource savedResource = fhirResourceRepository.save(fhirResource);
        auditService.logAction("FHIR_RESOURCE_SYNCED", savedResource.getId(), fhirResource.getResourceType());

        return savedResource;
    }

    public Optional<FHIRResource> getFhirResourceById(String fhirResourceId) {
        log.debug("Fetching FHIR resource: {}", fhirResourceId);
        return fhirResourceRepository.findByFhirResourceId(fhirResourceId);
    }

    public List<FHIRResource> getFhirResourcesByPatient(String patientId) {
        log.debug("Fetching FHIR resources for patient: {}", patientId);
        return fhirResourceRepository.findByPatientId(patientId);
    }

    public List<FHIRResource> getFhirResourcesByType(String resourceType) {
        log.debug("Fetching FHIR resources by type: {}", resourceType);
        return fhirResourceRepository.findByResourceType(resourceType);
    }

    public List<FHIRResource> getFailedSyncResources() {
        log.debug("Fetching failed sync resources");
        return fhirResourceRepository.findFailedSyncs();
    }

    public List<FHIRResource> getPendingSyncResources() {
        log.debug("Fetching pending sync resources");
        return fhirResourceRepository.findPendingSyncs();
    }

    @Transactional
    public void updateSyncStatus(String resourceId, String status) {
        Optional<FHIRResource> optionalResource = fhirResourceRepository.findById(resourceId);
        if (optionalResource.isPresent()) {
            FHIRResource resource = optionalResource.get();
            resource.setSyncStatus(status);
            resource.setSyncedAt(LocalDateTime.now());

            if ("FAILED".equals(status)) {
                resource.setRetryCount(resource.getRetryCount() + 1);
            }

            fhirResourceRepository.save(resource);
            log.info("FHIR resource sync status updated: {} -> {}", resourceId, status);
        }
    }

    @Transactional
    public void updateValidationStatus(String resourceId, boolean validated, String errors) {
        Optional<FHIRResource> optionalResource = fhirResourceRepository.findById(resourceId);
        if (optionalResource.isPresent()) {
            FHIRResource resource = optionalResource.get();
            resource.setValidated(validated);
            resource.setValidationErrors(errors);
            fhirResourceRepository.save(resource);
            log.info("FHIR resource validation updated: {} -> {}", resourceId, validated);
        }
    }

    public List<FHIRResource> getRetryableResources() {
        log.debug("Fetching retryable FHIR resources");
        return fhirResourceRepository.findRetryableResources("FAILED");
    }

    public long countResourcesByType(String resourceType) {
        return fhirResourceRepository.countByResourceType(resourceType);
    }

    public long countValidatedResources() {
        return fhirResourceRepository.countByValidatedTrue();
    }

    public long countSyncedResources() {
        return fhirResourceRepository.countBySyncStatus("SYNCED");
    }

    @Transactional
    public void markResourceAsMapped(String resourceId, String entityType, String entityId) {
        Optional<FHIRResource> optionalResource = fhirResourceRepository.findById(resourceId);
        if (optionalResource.isPresent()) {
            FHIRResource resource = optionalResource.get();
            resource.setMappedToEntityType(entityType);
            resource.setMappedToEntityId(entityId);
            fhirResourceRepository.save(resource);
            log.info("FHIR resource mapped to {} ({})", entityType, entityId);
        }
    }

    public List<FHIRResource> getUnmappedResources() {
        log.debug("Fetching unmapped FHIR resources");
        List<FHIRResource> allResources = fhirResourceRepository.findAll();
        return allResources.stream()
                .filter(r -> r.getMappedToEntityId() == null)
                .toList();
    }

    @Transactional
    public FHIRResource syncResourceFromEhr(String ehrSystemId, String fhirResourceId, Object resourceData) {
        log.info("Syncing FHIR resource from EHR system: {}", ehrSystemId);

        FHIRResource fhirResource = FHIRResource.builder()
                .id(UUID.randomUUID().toString())
                .fhirResourceId(fhirResourceId)
                .ehrSystemId(ehrSystemId)
                .syncStatus("SYNCED")
                .validated(true)
                .createdAt(LocalDateTime.now())
                .syncedAt(LocalDateTime.now())
                .retryCount(0)
                .build();

        try {
            fhirResource.setResourceData(objectMapper.valueToTree(resourceData));
        } catch (Exception e) {
            log.error("Error mapping FHIR resource data", e);
            fhirResource.setValidated(false);
            fhirResource.setValidationErrors(e.getMessage());
        }

        return saveFhirResource(fhirResource);
    }
}
