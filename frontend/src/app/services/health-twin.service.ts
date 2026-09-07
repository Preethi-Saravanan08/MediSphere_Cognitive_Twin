import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface HealthTwin {
  id?: string;
  twinId?: string;
  patientId: string;
  firstName: string;
  lastName: string;
  modelVersion?: string;
  currentVitals?: any;
  riskScores?: { [key: string]: number };
  overallRiskLevel?: string;
  twinDataCompleteness?: number;
  activeAlerts?: any[];
  hipaaCompliant?: boolean;
  lastUpdated?: string;
}

@Injectable({
  providedIn: 'root'
})
export class HealthTwinService {
  private apiUrl = 'http://localhost:8080/api/v1/health-twins';

  constructor(private http: HttpClient) { }

  createHealthTwin(patientId: string): Observable<HealthTwin> {
    return this.http.post<HealthTwin>(`${this.apiUrl}/patient/${patientId}`, {});
  }

  getHealthTwin(patientId: string): Observable<HealthTwin> {
    return this.http.get<HealthTwin>(`${this.apiUrl}/patient/${patientId}`);
  }

  updateHealthTwin(twinId: string, twin: HealthTwin): Observable<HealthTwin> {
    return this.http.put<HealthTwin>(`${this.apiUrl}/${twinId}`, twin);
  }

  getHighRiskPatients(): Observable<HealthTwin[]> {
    return this.http.get<HealthTwin[]>(`${this.apiUrl}/list/high-risk`);
  }

  getTwinsByCompleteness(minCompleteness: number): Observable<HealthTwin[]> {
    return this.http.get<HealthTwin[]>(`${this.apiUrl}/list/completeness/${minCompleteness}`);
  }

  countHighRiskPatients(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/stats/high-risk-count`);
  }

  countTotalTwins(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/stats/total-count`);
  }

  updateRiskScore(twinId: string, riskType: string, score: number): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/${twinId}/risk-score?riskType=${riskType}&score=${score}`,
      {}
    );
  }
}
