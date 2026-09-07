package com.medisphere.controller;

import com.medisphere.domain.HealthTwin;
import com.medisphere.service.HealthTwinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/health-twins")
@Slf4j
public class HealthTwinController {

    @Autowired
    private HealthTwinService healthTwinService;

    @PostMapping("/patient/{patientId}")
    public ResponseEntity<HealthTwin> createHealthTwin(@PathVariable String patientId) {
        log.info("Creating health twin for patient: {}", patientId);
        HealthTwin twin = healthTwinService.createHealthTwin(patientId);
        return ResponseEntity.status(HttpStatus.CREATED).body(twin);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<HealthTwin> getHealthTwinByPatient(@PathVariable String patientId) {
        log.debug("Fetching health twin for patient: {}", patientId);
        Optional<HealthTwin> twin = healthTwinService.getHealthTwinByPatientId(patientId);
        return twin.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{twinId}")
    public ResponseEntity<HealthTwin> updateHealthTwin(
            @PathVariable String twinId,
            @RequestBody HealthTwin twin) {
        log.info("Updating health twin: {}", twinId);
        twin.setId(twinId);
        HealthTwin updatedTwin = healthTwinService.updateHealthTwin(twin);
        return ResponseEntity.ok(updatedTwin);
    }

    @GetMapping("/list/high-risk")
    public ResponseEntity<List<HealthTwin>> getHighRiskPatients() {
        log.debug("Fetching high-risk patients");
        List<HealthTwin> twins = healthTwinService.getHighRiskPatients();
        return ResponseEntity.ok(twins);
    }

    @GetMapping("/list/completeness/{minCompleteness}")
    public ResponseEntity<List<HealthTwin>> getTwinsByCompleteness(@PathVariable int minCompleteness) {
        log.debug("Fetching twins with minimum completeness: {}%", minCompleteness);
        List<HealthTwin> twins = healthTwinService.getTwinsByCompleteness(minCompleteness);
        return ResponseEntity.ok(twins);
    }

    @GetMapping("/list/alerts")
    public ResponseEntity<List<HealthTwin>> getTwinsWithAlerts() {
        log.debug("Fetching twins with active alerts");
        List<HealthTwin> twins = healthTwinService.getTwinsWithActiveAlerts();
        return ResponseEntity.ok(twins);
    }

    @PostMapping("/{twinId}/risk-score")
    public ResponseEntity<Void> updateRiskScore(
            @PathVariable String twinId,
            @RequestParam String riskType,
            @RequestParam float score) {
        log.info("Updating risk score for twin {}: {} = {}", twinId, riskType, score);
        healthTwinService.updateRiskScore(twinId, riskType, score);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{twinId}/alert")
    public ResponseEntity<Void> addAlert(
            @PathVariable String twinId,
            @RequestBody HealthTwin.Alert alert) {
        log.info("Adding alert to twin {}: {}", twinId, alert.getMessage());
        healthTwinService.addAlert(twinId, alert);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/high-risk-count")
    public ResponseEntity<Long> countHighRiskPatients() {
        log.debug("Counting high-risk patients");
        long count = healthTwinService.countHighRiskPatients();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/total-count")
    public ResponseEntity<Long> countTotalTwins() {
        log.debug("Counting total health twins");
        long count = healthTwinService.countTotalTwins();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<HealthTwin>> getAllHealthTwins() {
        log.debug("Fetching all health twins");
        List<HealthTwin> twins = healthTwinService.getAllHealthTwins();
        return ResponseEntity.ok(twins);
    }
}
