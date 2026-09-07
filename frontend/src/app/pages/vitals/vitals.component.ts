import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { VitalsService, Vitals } from '../../services/vitals.service';

@Component({
  selector: 'app-vitals',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="vitals">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Vital Signs Recording</h2>
        <button class="btn btn-primary" (click)="toggleForm()">
          <i class="fas fa-plus"></i> Record Vitals
        </button>
      </div>

      <div class="row">
        <div class="col-md-6" *ngIf="showForm">
          <div class="card mb-4">
            <div class="card-header bg-primary text-white">
              <h5 class="mb-0"><i class="fas fa-heartbeat"></i> Record New Vitals</h5>
            </div>
            <div class="card-body">
              <form (ngSubmit)="recordVitals()">
                <div class="form-group mb-3">
                  <label>Wearable Device ID</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    [(ngModel)]="newVitals.sourceDevice"
                    name="sourceDevice"
                    placeholder="e.g., apple-watch-001"
                  >
                </div>

                <div class="row">
                  <div class="col-md-6">
                    <div class="form-group mb-3">
                      <label>Heart Rate (bpm)</label>
                      <input 
                        type="number" 
                        class="form-control" 
                        [(ngModel)]="newVitals.heartRate"
                        name="heartRate"
                      >
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="form-group mb-3">
                      <label>Temperature (°C)</label>
                      <input 
                        type="number" 
                        step="0.1"
                        class="form-control" 
                        [(ngModel)]="newVitals.temperature"
                        name="temperature"
                      >
                    </div>
                  </div>
                </div>

                <div class="row">
                  <div class="col-md-6">
                    <div class="form-group mb-3">
                      <label>Systolic BP (mmHg)</label>
                      <input 
                        type="number" 
                        class="form-control" 
                        [(ngModel)]="newVitals.systolicBP"
                        name="systolicBP"
                      >
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="form-group mb-3">
                      <label>Diastolic BP (mmHg)</label>
                      <input 
                        type="number" 
                        class="form-control" 
                        [(ngModel)]="newVitals.diastolicBP"
                        name="diastolicBP"
                      >
                    </div>
                  </div>
                </div>

                <div class="row">
                  <div class="col-md-6">
                    <div class="form-group mb-3">
                      <label>O2 Saturation (%)</label>
                      <input 
                        type="number" 
                        class="form-control" 
                        [(ngModel)]="newVitals.oxygenSaturation"
                        name="oxygenSaturation"
                      >
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="form-group mb-3">
                      <label>Blood Glucose (mg/dL)</label>
                      <input 
                        type="number" 
                        class="form-control" 
                        [(ngModel)]="newVitals.bloodGlucose"
                        name="bloodGlucose"
                      >
                    </div>
                  </div>
                </div>

                <button type="submit" class="btn btn-success w-100">
                  <i class="fas fa-save"></i> Save Vitals
                </button>
              </form>
            </div>
          </div>
        </div>

        <div [ngClass]="{'col-md-12': !showForm, 'col-md-6': showForm}">
          <div class="card">
            <div class="card-header bg-info text-white">
              <h5 class="mb-0"><i class="fas fa-history"></i> Recent Vitals</h5>
            </div>
            <div class="card-body">
              <div class="table-responsive">
                <table class="table table-sm">
                  <thead>
                    <tr>
                      <th>Date/Time</th>
                      <th>HR</th>
                      <th>BP</th>
                      <th>SpO2</th>
                      <th>Temp</th>
                      <th>Quality</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr *ngFor="let vital of vitals">
                      <td>{{ vital.timestamp | date:'short' }}</td>
                      <td>{{ vital.heartRate }} bpm</td>
                      <td>{{ vital.systolicBP }}/{{ vital.diastolicBP }}</td>
                      <td>{{ vital.oxygenSaturation }}%</td>
                      <td>{{ vital.temperature }}°C</td>
                      <td>
                        <span [ngClass]="getQualityClass(vital.dataQuality)" class="badge">
                          {{ vital.dataQuality }}
                        </span>
                      </td>
                      <td>
                        <span *ngIf="vital.isValid" class="badge bg-success">Valid</span>
                        <span *ngIf="!vital.isValid" class="badge bg-warning">Review</span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <p class="text-muted small" *ngIf="vitals.length === 0">
                No vitals recorded yet
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .vitals {
      padding: 20px;
    }
    .form-group label {
      font-weight: 600;
      margin-bottom: 0.5rem;
    }
    .table {
      font-size: 0.875rem;
    }
  `]
})
export class VitalsComponent implements OnInit {
  vitals: Vitals[] = [];
  showForm = false;
  patientId: string | null = null;

  newVitals: Vitals = {
    patientId: '',
    timestamp: new Date().toISOString(),
    sourceDevice: '',
    heartRate: 72,
    systolicBP: 120,
    diastolicBP: 80,
    temperature: 36.8,
    respiratoryRate: 16,
    oxygenSaturation: 98,
    bloodGlucose: 95,
    weight: 75,
    height: 180
  };

  constructor(
    private route: ActivatedRoute,
    private vitalsService: VitalsService
  ) { }

  ngOnInit(): void {
    this.patientId = this.route.snapshot.paramMap.get('patientId');
    if (this.patientId) {
      this.newVitals.patientId = this.patientId;
      this.loadVitals();
    }
  }

  loadVitals(): void {
    if (this.patientId) {
      this.vitalsService.getLatestVitals(this.patientId).subscribe(data => {
        this.vitals = data;
      });
    }
  }

  recordVitals(): void {
    this.vitalsService.recordVitals(this.newVitals).subscribe(() => {
      alert('Vitals recorded successfully');
      this.loadVitals();
      this.resetForm();
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
  }

  resetForm(): void {
    this.newVitals = {
      patientId: this.patientId || '',
      timestamp: new Date().toISOString(),
      sourceDevice: '',
      heartRate: 72,
      systolicBP: 120,
      diastolicBP: 80,
      temperature: 36.8,
      respiratoryRate: 16,
      oxygenSaturation: 98,
      bloodGlucose: 95,
      weight: 75,
      height: 180
    };
  }

  getQualityClass(quality: string | undefined): string {
    switch (quality) {
      case 'GOOD': return 'bg-success';
      case 'FAIR': return 'bg-warning';
      case 'POOR': return 'bg-danger';
      default: return 'bg-secondary';
    }
  }
}
