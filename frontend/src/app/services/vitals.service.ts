import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Vitals {
  id?: string;
  patientId: string;
  timestamp: string;
  sourceDevice: string;
  heartRate: number;
  systolicBP: number;
  diastolicBP: number;
  temperature: number;
  respiratoryRate: number;
  oxygenSaturation: number;
  bloodGlucose: number;
  weight: number;
  height: number;
  bmi?: number;
  stepCount?: number;
  caloriesBurned?: number;
  sleepDuration?: number;
  isValid?: boolean;
  dataQuality?: string;
  confidence?: number;
}

@Injectable({
  providedIn: 'root'
})
export class VitalsService {
  private apiUrl = 'http://localhost:8080/api/v1/vitals';

  constructor(private http: HttpClient) { }

  recordVitals(vitals: Vitals): Observable<Vitals> {
    return this.http.post<Vitals>(this.apiUrl, vitals);
  }

  getLatestVitals(patientId: string): Observable<Vitals[]> {
    return this.http.get<Vitals[]>(`${this.apiUrl}/patient/${patientId}/latest`);
  }

  getVitalsByDateRange(patientId: string, from: string, to: string): Observable<Vitals[]> {
    return this.http.get<Vitals[]>(
      `${this.apiUrl}/patient/${patientId}/range?from=${from}&to=${to}`
    );
  }

  getAnomalousVitals(patientId: string): Observable<Vitals[]> {
    return this.http.get<Vitals[]>(`${this.apiUrl}/patient/${patientId}/anomalous`);
  }

  countVitalsByPatient(patientId: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/stats/count/${patientId}`);
  }

  validateVitals(id: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/validate`, {});
  }
}
