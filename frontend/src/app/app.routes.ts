import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { PatientListComponent } from './pages/patients/patient-list.component';
import { PatientDetailComponent } from './pages/patients/patient-detail.component';
import { HealthTwinComponent } from './pages/health-twin/health-twin.component';
import { VitalsComponent } from './pages/vitals/vitals.component';
import { ConsentComponent } from './pages/consent/consent.component';
import { LoginComponent } from './pages/login/login.component';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'patients', component: PatientListComponent },
  { path: 'patients/:id', component: PatientDetailComponent },
  { path: 'health-twin/:patientId', component: HealthTwinComponent },
  { path: 'vitals/:patientId', component: VitalsComponent },
  { path: 'consent/:patientId', component: ConsentComponent }
];
