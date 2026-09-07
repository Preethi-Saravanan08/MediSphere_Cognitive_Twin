package com.medisphere.controller;

import com.medisphere.domain.Vitals;
import com.medisphere.service.VitalsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/vitals")
@Slf4j
public class VitalsController {

    @Autowired
    private VitalsService vitalsService;

    @PostMapping
    public ResponseEntity<Vitals> recordVitals(@RequestBody Vitals vitals) {
        log.info("Recording vitals for patient: {}", vitals.getPatientId());
        Vitals recordedVitals = vitalsService.recordVitals(vitals);
        return ResponseEntity.status(HttpStatus.CREATED).body(recordedVitals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vitals> getVitals(@PathVariable String id) {
        log.debug("Fetching vitals: {}", id);
        Optional<Vitals> vitals = vitalsService.getVitalsById(id);
        return vitals.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}/latest")
    public ResponseEntity<List<Vitals>> getLatestVitalsByPatient(@PathVariable String patientId) {
        log.debug("Fetching latest vitals for patient: {}", patientId);
        List<Vitals> vitals = vitalsService.getLatestVitalsByPatient(patientId);
        return ResponseEntity.ok(vitals);
    }

    @GetMapping("/patient/{patientId}/range")
    public ResponseEntity<List<Vitals>> getVitalsByDateRange(
            @PathVariable String patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        log.debug("Fetching vitals for patient {} from {} to {}", patientId, from, to);
        List<Vitals> vitals = vitalsService.getVitalsByDateRange(patientId, from, to);
        return ResponseEntity.ok(vitals);
    }

    @GetMapping("/list/invalid")
    public ResponseEntity<List<Vitals>> getInvalidVitals() {
        log.debug("Fetching invalid vitals");
        List<Vitals> vitals = vitalsService.getInvalidVitals();
        return ResponseEntity.ok(vitals);
    }

    @GetMapping("/list/pending-sync")
    public ResponseEntity<List<Vitals>> getPendingSyncVitals() {
        log.debug("Fetching pending sync vitals");
        List<Vitals> vitals = vitalsService.getPendingSyncVitals();
        return ResponseEntity.ok(vitals);
    }

    @GetMapping("/patient/{patientId}/anomalous")
    public ResponseEntity<List<Vitals>> getAnomalousVitals(@PathVariable String patientId) {
        log.debug("Fetching anomalous vitals for patient: {}", patientId);
        List<Vitals> vitals = vitalsService.getAnomalousVitals(patientId);
        return ResponseEntity.ok(vitals);
    }

    @GetMapping("/list/low-oxygen")
    public ResponseEntity<List<Vitals>> getLowOxygenSaturation() {
        log.debug("Fetching vitals with low oxygen saturation");
        List<Vitals> vitals = vitalsService.getLowOxygenSaturationVitals();
        return ResponseEntity.ok(vitals);
    }

    @PostMapping("/{id}/validate")
    public ResponseEntity<Void> validateVitals(@PathVariable String id) {
        log.info("Validating vitals: {}", id);
        vitalsService.validateAndCorrectVitals(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/count/{patientId}")
    public ResponseEntity<Long> countVitalsByPatient(@PathVariable String patientId) {
        log.debug("Counting vitals for patient: {}", patientId);
        long count = vitalsService.countVitalsByPatient(patientId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/pending-count")
    public ResponseEntity<Long> countPendingSyncVitals() {
        log.debug("Counting pending sync vitals");
        long count = vitalsService.countPendingSyncVitals();
        return ResponseEntity.ok(count);
    }
}
