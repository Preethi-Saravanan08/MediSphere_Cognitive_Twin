import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Patient {
  id?: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  email: string;
  contact: string;
  consentProvided?: boolean;
  hipaaAcknowledged?: boolean;
  status?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private apiUrl = 'http://localhost:8080/api/v1/patients';

  constructor(private http: HttpClient) { }

  createPatient(patient: Patient): Observable<Patient> {
    return this.http.post<Patient>(this.apiUrl, patient);
  }

  getPatient(id: string): Observable<Patient> {
    return this.http.get<Patient>(`${this.apiUrl}/${id}`);
  }

  updatePatient(id: string, patient: Patient): Observable<Patient> {
    return this.http.put<Patient>(`${this.apiUrl}/${id}`, patient);
  }

  getAllActivePatients(): Observable<Patient[]> {
    return this.http.get<Patient[]>(`${this.apiUrl}/list/active`);
  }

  countActivePatients(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/stats/count`);
  }

  updateConsent(id: string, provided: boolean, version: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/consent?provided=${provided}&version=${version}`, {});
  }

  updateHipaaStatus(id: string, acknowledged: boolean): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/hipaa-acknowledgment?acknowledged=${acknowledged}`, {});
  }
}
