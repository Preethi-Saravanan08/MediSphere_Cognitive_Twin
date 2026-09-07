import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PatientService, Patient } from '../../services/patient.service';

@Component({
  selector: 'app-patient-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="patient-list">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Patients</h2>
        <button class="btn btn-primary" (click)="openAddPatientModal()">
          <i class="fas fa-plus"></i> Add Patient
        </button>
      </div>

      <div class="card add-patient-card mb-4" *ngIf="showAddPatient">
        <div class="card-header bg-primary text-white">
          <div class="d-flex justify-content-between align-items-center">
            <h5 class="mb-0"><i class="fas fa-user-plus"></i> Add Patient</h5>
            <button type="button" class="btn-close btn-close-white" aria-label="Close" (click)="closeAddPatientModal()"></button>
          </div>
        </div>
        <form class="card-body" (ngSubmit)="saveNewPatient()">
          <div class="row g-3">
            <div class="col-md-3"><label class="form-label">First name</label><input class="form-control" name="firstName" [(ngModel)]="newPatient.firstName" required></div>
            <div class="col-md-3"><label class="form-label">Last name</label><input class="form-control" name="lastName" [(ngModel)]="newPatient.lastName" required></div>
            <div class="col-md-3"><label class="form-label">Date of birth</label><input type="date" class="form-control" name="dateOfBirth" [(ngModel)]="newPatient.dateOfBirth" required></div>
            <div class="col-md-3"><label class="form-label">Gender</label><select class="form-select" name="gender" [(ngModel)]="newPatient.gender"><option value="MALE">Male</option><option value="FEMALE">Female</option><option value="OTHER">Other</option></select></div>
            <div class="col-md-4"><label class="form-label">Email</label><input type="email" class="form-control" name="email" [(ngModel)]="newPatient.email" required></div>
            <div class="col-md-4"><label class="form-label">Phone</label><input type="tel" class="form-control" name="contact" [(ngModel)]="newPatient.contact" required></div>
            <div class="col-md-4 d-flex align-items-end gap-2"><button class="btn btn-primary" type="submit" [disabled]="isSaving"><i class="fas fa-save"></i> {{ isSaving ? 'Saving...' : 'Save to MongoDB' }}</button><button class="btn btn-outline-secondary" type="button" (click)="closeAddPatientModal()">Cancel</button></div>
          </div>
          <div class="alert alert-success mt-3 mb-0" *ngIf="successMessage">{{ successMessage }}</div>
          <div class="alert alert-danger mt-3 mb-0" *ngIf="errorMessage">{{ errorMessage }}</div>
        </form>
      </div>

      <div class="card">
        <div class="card-header">
          <div class="row">
            <div class="col-md-6">
              <input 
                type="text" 
                class="form-control" 
                placeholder="Search patient..."
                [(ngModel)]="searchTerm"
              >
            </div>
            <div class="col-md-6">
              <select class="form-select" [(ngModel)]="filterStatus">
                <option value="">All Status</option>
                <option value="ONBOARDING">Onboarding</option>
                <option value="ACTIVE">Active</option>
                <option value="INACTIVE">Inactive</option>
              </select>
            </div>
          </div>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>DOB</th>
                  <th>Gender</th>
                  <th>Email</th>
                  <th>Contact</th>
                  <th>Consent</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let patient of filteredPatients">
                  <td>
                    <strong>{{ patient.firstName }} {{ patient.lastName }}</strong>
                  </td>
                  <td>{{ patient.dateOfBirth }}</td>
                  <td>{{ patient.gender }}</td>
                  <td>{{ patient.email }}</td>
                  <td>{{ patient.contact }}</td>
                  <td>
                    <span *ngIf="patient.consentProvided" class="badge bg-success">
                      <i class="fas fa-check"></i> Yes
                    </span>
                    <span *ngIf="!patient.consentProvided" class="badge bg-warning">
                      <i class="fas fa-clock"></i> Pending
                    </span>
                  </td>
                  <td>
                    <span [ngClass]="getStatusClass(patient.status)" class="badge">
                      {{ patient.status }}
                    </span>
                  </td>
                  <td>
                    <button class="btn btn-sm btn-info" [routerLink]="['/patients', patient.id]">
                      <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-primary" 
                      [routerLink]="['/health-twin', patient.id]">
                      <i class="fas fa-heartbeat"></i>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .patient-list {
      padding: 20px;
    }
    .table {
      margin-bottom: 0;
    }
    .badge {
      padding: 0.5rem 0.75rem;
      font-size: 0.875rem;
    }
    .btn-sm {
      margin-right: 5px;
    }
    .add-patient-card { border-top: 3px solid #0d6efd; }
    .add-patient-card .form-label { font-weight: 600; font-size: .85rem; }
  `]
})
export class PatientListComponent implements OnInit {
  patients: Patient[] = [];
  filteredPatients: Patient[] = [];
  searchTerm = '';
  filterStatus = '';
  showAddPatient = false;
  isSaving = false;
  successMessage = '';
  errorMessage = '';
  newPatient: Patient = this.emptyPatient();

  constructor(private patientService: PatientService) { }

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.patientService.getAllActivePatients().subscribe(data => {
      this.patients = data;
      this.applyFilters();
    });
  }

  applyFilters(): void {
    this.filteredPatients = this.patients.filter(p => {
      const matchesSearch = !this.searchTerm || 
        p.firstName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        p.lastName.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesStatus = !this.filterStatus || p.status === this.filterStatus;
      
      return matchesSearch && matchesStatus;
    });
  }

  getStatusClass(status: string | undefined): string {
    switch (status) {
      case 'ACTIVE': return 'bg-success';
      case 'ONBOARDING': return 'bg-info';
      case 'INACTIVE': return 'bg-secondary';
      default: return 'bg-light';
    }
  }

  openAddPatientModal(): void {
    this.showAddPatient = true;
    this.successMessage = '';
    this.errorMessage = '';
  }

  closeAddPatientModal(): void {
    this.showAddPatient = false;
    this.isSaving = false;
  }

  saveNewPatient(): void {
    this.isSaving = true;
    this.successMessage = '';
    this.errorMessage = '';
    this.patientService.createPatient(this.newPatient).subscribe({
      next: patient => {
        this.patients = [patient, ...this.patients];
        this.applyFilters();
        this.newPatient = this.emptyPatient();
        this.isSaving = false;
        this.successMessage = `${patient.firstName} ${patient.lastName} was saved to MongoDB.`;
      },
      error: () => {
        this.isSaving = false;
        this.errorMessage = 'Patient could not be saved. Check that the API and MongoDB are running.';
      }
    });
  }

  private emptyPatient(): Patient {
    return { firstName: '', lastName: '', dateOfBirth: '', gender: 'OTHER', email: '', contact: '' };
  }
}
