package com.medisphere.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import com.medisphere.domain.*;
import com.medisphere.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class FHIRIntegrationService {

    private final FhirContext fhirContext;
    private final IGenericClient fhirClient;
    private final FHIRResourceRepository fhirResourceRepository;
    private final PatientRepository patientRepository;
    private final VitalsRepository vitalsRepository;
    private final LabResultRepository labResultRepository;
    private final HealthTwinRepository healthTwinRepository;
    private final HIPAAAuditService hipaaAuditService;

    @Value("${fhir.server-url}")
    private String fhirServerUrl;

    public FHIRIntegrationService(
            FHIRResourceRepository fhirResourceRepository,
            PatientRepository patientRepository,
            VitalsRepository vitalsRepository,
            LabResultRepository labResultRepository,
            HealthTwinRepository healthTwinRepository,
            HIPAAAuditService hipaaAuditService) {
        this.fhirResourceRepository = fhirResourceRepository;
        this.patientRepository = patientRepository;
        this.vitalsRepository = vitalsRepository;
        this.labResultRepository = labResultRepository;
        this.healthTwinRepository = healthTwinRepository;
        this.hipaaAuditService = hipaaAuditService;
        this.fhirContext = FhirContext.forR4();
        this.fhirClient = fhirContext.newRestfulGenericClient(fhirServerUrl);
    }

    public void syncPatientFromFHIR(String fhirPatientId, String ehrSystemId) {
        try {
            // Fetch Patient Resource from FHIR server
            Patient fhirPatient = fhirClient
                    .read()
                    .resource(Patient.class)
                    .withId(fhirPatientId)
                    .execute();

            if (fhirPatient == null) {
                log.error("Patient not found in FHIR: {}", fhirPatientId);
                return;
            }

            // Map FHIR Patient to MediSphere Patient
            com.medisphere.domain.Patient mediSpherePatient = mapFHIRPatientToMediSphere(fhirPatient, ehrSystemId);
            patientRepository.save(mediSpherePatient);

            log.info("Patient synced from FHIR: {}", mediSpherePatient.getPatientId());

            // Audit log
            hipaaAuditService.logAccess("FHIR_SYNC", mediSpherePatient.getId(), "Patient synced from FHIR");

            // Fetch and sync Observations (Vitals & Lab Results)
            syncObservationsForPatient(fhirPatientId, mediSpherePatient.getId());

            // Create or update Health Twin
            createOrUpdateHealthTwin(mediSpherePatient.getId());

        } catch (Exception e) {
            log.error("Error syncing patient from FHIR: {}", fhirPatientId, e);
            hipaaAuditService.logError("FHIR_SYNC_ERROR", fhirPatientId, e.getMessage());
        }
    }

    private void syncObservationsForPatient(String fhirPatientId, String patientId) {
        try {
            Bundle bundle = fhirClient
                    .search()
                    .forResource(Observation.class)
                    .where(Observation.SUBJECT.hasId(fhirPatientId))
                    .returnBundle(Bundle.class)
                    .execute();

            if (bundle == null || bundle.getEntry().isEmpty()) {
                log.warn("No observations found for patient: {}", fhirPatientId);
                return;
            }

            for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                if (entry.getResource() instanceof Observation) {
                    Observation observation = (Observation) entry.getResource();
                    processObservation(observation, patientId);
                }
            }

            log.info("Synced {} observations for patient: {}", bundle.getEntry().size(), patientId);

        } catch (Exception e) {
            log.error("Error syncing observations for patient: {}", fhirPatientId, e);
        }
    }

    private void processObservation(Observation observation, String patientId) {
        try {
            String code = observation.getCode().getCoding().get(0).getCode();
            Quantity value = (Quantity) observation.getValue();

            if (isVitalSign(code)) {
                mapFHIRObservationToVitals(observation, patientId, code, value);
            } else if (isLabResult(code)) {
                mapFHIRObservationToLabResult(observation, patientId, code, value);
            }
        } catch (Exception e) {
            log.error("Error processing observation: {}", observation.getId(), e);
        }
    }

    private boolean isVitalSign(String code) {
        Set<String> vitalCodes = Set.of(
                "8480-6", // Systolic BP
                "8462-4", // Diastolic BP
                "8867-4", // Heart Rate
                "9279-1", // Respiratory Rate
                "2708-6", // Oxygen Saturation
                "8310-5"  // Temperature
        );
        return vitalCodes.contains(code);
    }

    private boolean isLabResult(String code) {
        Set<String> labCodes = Set.of(
                "4548-4",  // HbA1c
                "2345-7",  // Glucose
                "2951-2",  // Sodium
                "2823-3",  // Potassium
                "3094-0"   // Urea Nitrogen
        );
        return labCodes.contains(code);
    }

    private void mapFHIRObservationToVitals(Observation observation, String patientId, String code, Quantity value) {
        Vitals vitals = Vitals.builder()
                .patientId(patientId)
                .timestamp(observation.getEffectiveDateTimeType().getValue().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime())
                .sourceDevice("FHIR")
                .build();

        // Map LOINC codes to vitals fields
        float numericValue = value.getValue().floatValue();
        switch (code) {
            case "8480-6":
                vitals.setSystolicBP(numericValue);
                break;
            case "8462-4":
                vitals.setDiastolicBP(numericValue);
                break;
            case "8867-4":
                vitals.setHeartRate(numericValue);
                break;
            case "9279-1":
                vitals.setRespiratoryRate(numericValue);
                break;
            case "2708-6":
                vitals.setOxygenSaturation(numericValue);
                break;
            case "8310-5":
                vitals.setTemperature(numericValue);
                break;
        }

        vitals.setValid(vitals.isWithinNormalRange());
        vitals.setValidationErrors(vitals.isValid() ? null : "Outside normal range");
        vitals.setSyncStatus("SYNCED");
        vitals.setSyncedAt(LocalDateTime.now());

        vitalsRepository.save(vitals);
    }

    private void mapFHIRObservationToLabResult(Observation observation, String patientId, String code, Quantity value) {
        LabResult labResult = LabResult.builder()
                .patientId(patientId)
                .testName(observation.getCode().getCoding().get(0).getDisplay())
                .testCode(code)
                .numericValue(value.getValue().floatValue())
                .unit(value.getUnit())
                .testDate(observation.getEffectiveDateTimeType().getValue().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime())
                .fhirObservationId(observation.getId())
                .validated(true)
                .build();

        // Set interpretation based on observation
        if (observation.getInterpretation() != null && !observation.getInterpretation().isEmpty()) {
            labResult.setInterpretation(observation.getInterpretation().get(0).getCoding().get(0).getCode());
        }

        labResult.setAbnormal(labResult.isAbnormal());
        labResult.setCreatedAt(LocalDateTime.now());

        labResultRepository.save(labResult);
    }

    private com.medisphere.domain.Patient mapFHIRPatientToMediSphere(Patient fhirPatient, String ehrSystemId) {
        com.medisphere.domain.Patient patient = com.medisphere.domain.Patient.builder()
                .patientId(fhirPatient.getId())
                .firstName(fhirPatient.getName().get(0).getGivenAsSingleString())
                .lastName(fhirPatient.getName().get(0).getFamily())
                .gender(fhirPatient.getGender().getDisplay())
                .dateOfBirth(fhirPatient.getBirthDate().toString())
                .fhirResourceId(fhirPatient.getId())
                .ehrSystemId(ehrSystemId)
                .fhirLastSynced(LocalDateTime.now())
                .consentProvided(false)
                .hipaaAcknowledged(false)
                .status(com.medisphere.domain.Patient.PatientStatus.ONBOARDING)
                .active(true)
                .createdAt(LocalDateTime.now())
                .createdBy("FHIR_SYNC")
                .build();

        return patient;
    }

    private void createOrUpdateHealthTwin(String patientId) {
        Optional<HealthTwin> existingTwin = healthTwinRepository.findByPatientId(patientId);

        HealthTwin healthTwin = existingTwin.orElseGet(() -> HealthTwin.builder()
                .patientId(patientId)
                .twinId(UUID.randomUUID().toString())
                .modelVersion("1.0")
                .createdAt(LocalDateTime.now())
                .build());

        // Update with latest data
        healthTwin.setLastUpdate(LocalDateTime.now());

        // Get latest vitals for snapshot
        List<Vitals> recentVitals = vitalsRepository.findByPatientIdOrderByTimestampDesc(patientId);
        if (!recentVitals.isEmpty()) {
            healthTwin.setLatestVitals(recentVitals.get(0));
            healthTwin.setVitalsDataPoints(recentVitals.size());
        }

        // Get recent lab results
        List<LabResult> recentLabs = labResultRepository.findByPatientIdOrderByTestDateDesc(patientId);
        if (!recentLabs.isEmpty()) {
            healthTwin.setRecentLabResults(recentLabs.size() > 10 ? recentLabs.subList(0, 10) : recentLabs);
            healthTwin.setLabsDataPoints(recentLabs.size());
        }

        // Calculate completeness
        double completeness = calculateTwinCompleteness(patientId);
        healthTwin.setCompleteness(completeness);
        healthTwin.setValidatedForPrediction(completeness >= 95.0);

        healthTwinRepository.save(healthTwin);
        log.info("Health twin updated for patient: {}, completeness: {}%", patientId, completeness);
    }

    private double calculateTwinCompleteness(String patientId) {
        List<Vitals> vitals = vitalsRepository.findByPatientIdOrderByTimestampDesc(patientId);
        List<LabResult> labs = labResultRepository.findByPatientIdOrderByTestDateDesc(patientId);

        int vitalsScore = Math.min(vitals.size(), 100);
        int labsScore = Math.min(labs.size() / 2, 50);

        return (vitalsScore + labsScore) / 2.0;
    }

    public void validateFHIRResource(String fhirResourceId) {
        try {
            Optional<FHIRResource> resource = fhirResourceRepository.findByFhirResourceId(fhirResourceId);
            if (resource.isEmpty()) return;

            FHIRResource fhir = resource.get();
            fhir.setValidated(true);
            fhir.setValidationErrors(null);
            fhirResourceRepository.save(fhir);

            log.info("FHIR resource validated: {}", fhirResourceId);
        } catch (Exception e) {
            log.error("Error validating FHIR resource: {}", fhirResourceId, e);
        }
    }
}
