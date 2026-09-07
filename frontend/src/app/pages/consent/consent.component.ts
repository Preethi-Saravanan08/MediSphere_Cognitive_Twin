import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-consent',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="consent">
      <h2 class="mb-4">Consent Management</h2>

      <div class="row">
        <div class="col-md-6">
          <div class="card mb-4">
            <div class="card-header bg-primary text-white">
              <h5 class="mb-0"><i class="fas fa-file-contract"></i> HIPAA Notice</h5>
            </div>
            <div class="card-body">
              <div class="consent-notice">
                <h6>Privacy Notice</h6>
                <p>
                  I acknowledge that I have received and reviewed MediSphere's Privacy Notice. 
                  I understand that my health information may be used and disclosed as described 
                  in the Notice of Privacy Practices.
                </p>
                <h6>Consent to Treatment</h6>
                <p>
                  I authorize MediSphere to create and maintain a digital health twin of my health 
                  information. I understand this information will be used to:
                </p>
                <ul>
                  <li>Monitor my health in real-time</li>
                  <li>Predict health risks using AI models</li>
                  <li>Provide personalized care recommendations</li>
                  <li>Improve clinical decision-making</li>
                </ul>
                <h6>Data Security</h6>
                <p>
                  I understand that my information will be protected using industry-standard encryption 
                  and security measures. All access to my information will be logged and audited in 
                  accordance with HIPAA regulations.
                </p>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card mb-4">
            <div class="card-header bg-success text-white">
              <h5 class="mb-0"><i class="fas fa-check-circle"></i> Consent Acknowledgment</h5>
            </div>
            <div class="card-body">
              <div class="form-check mb-3">
                <input 
                  type="checkbox" 
                  class="form-check-input" 
                  id="hipaaConsent"
                  [(ngModel)]="hipaaAcknowledged"
                >
                <label class="form-check-label" for="hipaaConsent">
                  I acknowledge receipt of the Privacy Notice
                </label>
              </div>

              <div class="form-check mb-3">
                <input 
                  type="checkbox" 
                  class="form-check-input" 
                  id="dataConsent"
                  [(ngModel)]="dataProcessingConsent"
                >
                <label class="form-check-label" for="dataConsent">
                  I consent to the collection and use of my health data
                </label>
              </div>

              <div class="form-check mb-3">
                <input 
                  type="checkbox" 
                  class="form-check-input" 
                  id="aiConsent"
                  [(ngModel)]="aiPredictionConsent"
                >
                <label class="form-check-label" for="aiConsent">
                  I consent to AI-based analysis and predictions
                </label>
              </div>

              <div class="form-check mb-4">
                <input 
                  type="checkbox" 
                  class="form-check-input" 
                  id="allConsent"
                  [(ngModel)]="allConsentsGiven"
                >
                <label class="form-check-label" for="allConsent">
                  <strong>I authorize all uses described above</strong>
                </label>
              </div>

              <button 
                class="btn btn-success w-100 mb-2" 
                (click)="acceptConsent()"
                [disabled]="!allConsentsGiven"
              >
                <i class="fas fa-save"></i> Accept & Sign
              </button>

              <button class="btn btn-outline-secondary w-100">
                <i class="fas fa-download"></i> Download PDF
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-md-12">
          <div class="card">
            <div class="card-header bg-info text-white">
              <h5 class="mb-0"><i class="fas fa-history"></i> Consent History</h5>
            </div>
            <div class="card-body">
              <div class="table-responsive">
                <table class="table">
                  <thead>
                    <tr>
                      <th>Date</th>
                      <th>Type</th>
                      <th>Status</th>
                      <th>Expires</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>2025-09-06</td>
                      <td>HIPAA Notice</td>
                      <td><span class="badge bg-success">Active</span></td>
                      <td>2026-09-06</td>
                      <td>
                        <button class="btn btn-sm btn-danger">Revoke</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="alert alert-info mt-4">
        <i class="fas fa-info-circle"></i> 
        <strong>HIPAA Compliance Notice:</strong> This consent form is compliant with HIPAA Privacy 
        Rule and will be maintained for the required minimum retention period of 7 years. All consent 
        records are logged and audited for compliance purposes.
      </div>
    </div>
  `,
  styles: [`
    .consent {
      padding: 20px;
    }
    .consent-notice {
      background-color: #f8f9fa;
      padding: 15px;
      border-radius: 5px;
      font-size: 0.95rem;
      line-height: 1.6;
    }
    .consent-notice h6 {
      margin-top: 15px;
      margin-bottom: 10px;
      font-weight: 600;
    }
    .consent-notice ul {
      margin-left: 20px;
      margin-bottom: 10px;
    }
  `]
})
export class ConsentComponent implements OnInit {
  hipaaAcknowledged = false;
  dataProcessingConsent = false;
  aiPredictionConsent = false;
  allConsentsGiven = false;

  patientId: string | null = null;

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.patientId = this.route.snapshot.paramMap.get('patientId');
  }

  acceptConsent(): void {
    if (this.allConsentsGiven) {
      alert('Consent accepted and signed. HIPAA compliance recorded.');
    }
  }
}
