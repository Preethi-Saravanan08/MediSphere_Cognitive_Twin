import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PatientService, Patient } from '../../services/patient.service';

@Component({
  selector: 'app-patient-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="patient-detail" *ngIf="patient">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2>{{ patient.firstName }} {{ patient.lastName }}</h2>
          <p class="text-muted">Patient ID: {{ patient.id }}</p>
        </div>
        <button class="btn btn-primary" (click)="savePatient()">
          <i class="fas fa-save"></i> Save Changes
        </button>
      </div>

      <div class="row">
        <div class="col-md-6">
          <div class="card mb-4">
            <div class="card-header bg-primary text-white">
              <h5 class="mb-0"><i class="fas fa-user"></i> Personal Information</h5>
            </div>
            <div class="card-body">
              <div class="form-group mb-3">
                <label>First Name</label>
                <input type="text" class="form-control" [(ngModel)]="patient.firstName">
              </div>
              <div class="form-group mb-3">
                <label>Last Name</label>
                <input type="text" class="form-control" [(ngModel)]="patient.lastName">
              </div>
              <div class="form-group mb-3">
                <label>Date of Birth</label>
                <input type="date" class="form-control" [(ngModel)]="patient.dateOfBirth">
              </div>
              <div class="form-group mb-3">
                <label>Gender</label>
                <select class="form-select" [(ngModel)]="patient.gender">
                  <option>MALE</option>
                  <option>FEMALE</option>
                  <option>OTHER</option>
                </select>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card mb-4">
            <div class="card-header bg-primary text-white">
              <h5 class="mb-0"><i class="fas fa-envelope"></i> Contact Information</h5>
            </div>
            <div class="card-body">
              <div class="form-group mb-3">
                <label>Email</label>
                <input type="email" class="form-control" [(ngModel)]="patient.email">
              </div>
              <div class="form-group mb-3">
                <label>Phone</label>
                <input type="tel" class="form-control" [(ngModel)]="patient.contact">
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-md-6">
          <div class="card">
            <div class="card-header bg-success text-white">
              <h5 class="mb-0"><i class="fas fa-file-contract"></i> Consent & Compliance</h5>
            </div>
            <div class="card-body">
              <div class="form-check mb-3">
                <input 
                  type="checkbox" 
                  class="form-check-input" 
                  [(ngModel)]="patient.consentProvided"
                  (change)="updateConsent()"
                >
                <label class="form-check-label">
                  Data Processing Consent
                </label>
              </div>
              <div class="form-check mb-3">
                <input 
                  type="checkbox" 
                  class="form-check-input" 
                  [(ngModel)]="patient.hipaaAcknowledged"
                  (change)="updateHipaa()"
                >
                <label class="form-check-label">
                  HIPAA Acknowledgment
                </label>
              </div>
              <p class="text-muted small" *ngIf="patient.consentProvided">
                ✓ Consent Provided - All data processing compliant
              </p>
              <p class="text-muted small" *ngIf="patient.hipaaAcknowledged">
                ✓ HIPAA Acknowledged - PHI protected
              </p>
            </div>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card">
            <div class="card-header bg-info text-white">
              <h5 class="mb-0"><i class="fas fa-info-circle"></i> Status</h5>
            </div>
            <div class="card-body">
              <div class="mb-3">
                <label>Status</label>
                <p>
                  <span [ngClass]="getStatusBadge()" class="badge">
                    {{ patient.status }}
                  </span>
                </p>
              </div>
              <div class="mb-3">
                <label>Active</label>
                <p>
                  <span *ngIf="patient.status === 'ACTIVE'" class="badge bg-success">
                    <i class="fas fa-check-circle"></i> Active
                  </span>
                  <span *ngIf="patient.status !== 'ACTIVE'" class="badge bg-warning">
                    <i class="fas fa-clock"></i> Inactive
                  </span>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .patient-detail {
      padding: 20px;
    }
    .card {
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    .form-group label {
      font-weight: 600;
      margin-bottom: 0.5rem;
    }
  `]
})
export class PatientDetailComponent implements OnInit {
  patient: Patient | null = null;

  constructor(
    private route: ActivatedRoute,
    private patientService: PatientService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.patientService.getPatient(id).subscribe(data => {
        this.patient = data;
      });
    }
  }

  savePatient(): void {
    if (this.patient && this.patient.id) {
      this.patientService.updatePatient(this.patient.id, this.patient).subscribe(() => {
        alert('Patient updated successfully');
      });
    }
  }

  updateConsent(): void {
    if (this.patient && this.patient.id) {
      this.patientService.updateConsent(this.patient.id, this.patient.consentProvided || false, '1.0').subscribe(() => {
        alert('Consent updated');
      });
    }
  }

  updateHipaa(): void {
    if (this.patient && this.patient.id) {
      this.patientService.updateHipaaStatus(this.patient.id, this.patient.hipaaAcknowledged || false).subscribe(() => {
        alert('HIPAA status updated');
      });
    }
  }

  getStatusBadge(): string {
    switch (this.patient?.status) {
      case 'ACTIVE': return 'bg-success';
      case 'ONBOARDING': return 'bg-info';
      case 'INACTIVE': return 'bg-secondary';
      default: return 'bg-light';
    }
  }
}
