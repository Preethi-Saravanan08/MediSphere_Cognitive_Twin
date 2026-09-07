import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HealthTwinService, HealthTwin } from '../../services/health-twin.service';

@Component({
  selector: 'app-health-twin',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="health-twin" *ngIf="twin">
      <h2 class="mb-4">Digital Health Twin - {{ twin.firstName }} {{ twin.lastName }}</h2>

      <div class="row mb-4">
        <div class="col-md-3">
          <div class="card">
            <div class="card-body text-center">
              <h5 class="card-title">Data Completeness</h5>
              <div class="progress mb-2" style="height: 25px;">
                <div 
                  class="progress-bar bg-success" 
                  [style.width.%]="twin.twinDataCompleteness"
                >
                  {{ twin.twinDataCompleteness }}%
                </div>
              </div>
              <p class="text-muted small">{{ twin.twinDataCompleteness }}% Complete</p>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="card">
            <div class="card-body text-center">
              <h5 class="card-title">Overall Risk Level</h5>
              <p [ngClass]="getRiskLevelClass(twin.overallRiskLevel)" class="risk-badge">
                {{ twin.overallRiskLevel }}
              </p>
            </div>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title mb-3"><i class="fas fa-heartbeat"></i> Current Vitals</h5>
              <div *ngIf="twin.currentVitals">
                <div class="vital-item">
                  <span>Heart Rate:</span>
                  <strong>{{ twin.currentVitals.heartRate }} bpm</strong>
                </div>
                <div class="vital-item">
                  <span>Blood Pressure:</span>
                  <strong>{{ twin.currentVitals.systolicBP }}/{{ twin.currentVitals.diastolicBP }} mmHg</strong>
                </div>
                <div class="vital-item">
                  <span>O2 Saturation:</span>
                  <strong>{{ twin.currentVitals.oxygenSaturation }}%</strong>
                </div>
                <div class="vital-item">
                  <span>Temperature:</span>
                  <strong>{{ twin.currentVitals.temperature }}°C</strong>
                </div>
              </div>
              <p class="text-muted small" *ngIf="!twin.currentVitals">No vitals recorded yet</p>
            </div>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-md-6">
          <div class="card">
            <div class="card-header bg-primary text-white">
              <h5 class="mb-0"><i class="fas fa-chart-bar"></i> Risk Scores</h5>
            </div>
            <div class="card-body">
              <div *ngFor="let score of getRiskScoreArray()" class="risk-score-item">
                <span>{{ score.key }}:</span>
                <div class="progress" style="height: 20px;">
                  <div 
                    class="progress-bar" 
                    [ngClass]="getProgressBarClass(score.value)"
                    [style.width.%]="score.value * 100"
                  >
                    {{ (score.value * 100).toFixed(1) }}%
                  </div>
                </div>
              </div>
              <p class="text-muted small mt-3" *ngIf="!hasRiskScores()">
                Risk assessment pending - Data will be populated with Milestone 2 (TensorFlow Federated)
              </p>
            </div>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card">
            <div class="card-header bg-warning text-dark">
              <h5 class="mb-0"><i class="fas fa-bell"></i> Active Alerts</h5>
            </div>
            <div class="card-body">
              <div *ngFor="let alert of twin.activeAlerts" class="alert-item">
                <div class="alert" [ngClass]="'alert-' + getSeverityClass(alert.severity)">
                  <strong>{{ alert.alertType }}</strong>
                  <p class="mb-0">{{ alert.message }}</p>
                  <small>{{ alert.createdAt | date:'short' }}</small>
                </div>
              </div>
              <p class="text-muted small" *ngIf="!twin.activeAlerts || twin.activeAlerts.length === 0">
                <i class="fas fa-check-circle text-success"></i> No active alerts
              </p>
            </div>
          </div>
        </div>
      </div>

      <div class="row mt-4">
        <div class="col-md-12">
          <div class="card">
            <div class="card-header bg-info text-white">
              <h5 class="mb-0"><i class="fas fa-info-circle"></i> Twin Information</h5>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-md-3">
                  <strong>Twin ID:</strong>
                  <p class="text-muted">{{ twin.twinId }}</p>
                </div>
                <div class="col-md-3">
                  <strong>Model Version:</strong>
                  <p class="text-muted">{{ twin.modelVersion }}</p>
                </div>
                <div class="col-md-3">
                  <strong>HIPAA Compliant:</strong>
                  <p class="text-success" *ngIf="twin.hipaaCompliant">
                    <i class="fas fa-check-circle"></i> Yes
                  </p>
                </div>
                <div class="col-md-3">
                  <strong>Last Updated:</strong>
                  <p class="text-muted">{{ twin.lastUpdated | date:'short' }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .health-twin {
      padding: 20px;
    }
    .risk-badge {
      font-size: 1.5rem;
      font-weight: bold;
      padding: 10px;
      border-radius: 5px;
    }
    .vital-item {
      display: flex;
      justify-content: space-between;
      padding: 10px 0;
      border-bottom: 1px solid #dee2e6;
    }
    .vital-item:last-child {
      border-bottom: none;
    }
    .risk-score-item {
      margin-bottom: 15px;
    }
    .alert-item {
      margin-bottom: 10px;
    }
  `]
})
export class HealthTwinComponent implements OnInit {
  twin: HealthTwin | null = null;

  constructor(
    private route: ActivatedRoute,
    private healthTwinService: HealthTwinService
  ) { }

  ngOnInit(): void {
    const patientId = this.route.snapshot.paramMap.get('patientId');
    if (patientId) {
      this.healthTwinService.getHealthTwin(patientId).subscribe(
        data => {
          this.twin = data;
        },
        error => {
          console.log('Health twin not found, creating one...');
          this.healthTwinService.createHealthTwin(patientId).subscribe(data => {
            this.twin = data;
          });
        }
      );
    }
  }

  getRiskLevelClass(level: string | undefined): string {
    switch (level) {
      case 'CRITICAL': return 'bg-danger text-white';
      case 'HIGH': return 'bg-warning text-dark';
      case 'MODERATE': return 'bg-info text-white';
      case 'LOW': return 'bg-success text-white';
      default: return 'bg-secondary text-white';
    }
  }

  getRiskScoreArray(): Array<{ key: string; value: number }> {
    if (!this.twin?.riskScores) return [];
    return Object.entries(this.twin.riskScores).map(([key, value]) => ({
      key,
      value
    }));
  }

  hasRiskScores(): boolean {
    return !!this.twin?.riskScores && Object.keys(this.twin.riskScores).length > 0;
  }

  getProgressBarClass(value: number): string {
    if (value >= 0.7) return 'bg-danger';
    if (value >= 0.5) return 'bg-warning';
    if (value >= 0.3) return 'bg-info';
    return 'bg-success';
  }

  getSeverityClass(severity: string): string {
    switch (severity) {
      case 'CRITICAL': return 'danger';
      case 'WARNING': return 'warning';
      case 'INFO': return 'info';
      default: return 'secondary';
    }
  }
}
