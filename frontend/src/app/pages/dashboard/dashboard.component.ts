import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface DashboardMetric {
  label: string;
  value: string;
  detail: string;
  icon: string;
  tone: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="dashboard-shell">
      <div class="page-heading">
        <div>
          <p class="eyebrow">CLINICAL OVERVIEW</p>
          <h1>Patient 360 Dashboard</h1>
        </div>
        <div class="demo-pill"><span class="pulse"></span> Demo workspace</div>
      </div>

      <div class="metric-grid">
        <article class="metric-card" *ngFor="let metric of metrics" [class]="metric.tone">
          <div class="metric-icon"><i [class]="metric.icon"></i></div>
          <div>
            <p>{{ metric.label }}</p>
            <strong>{{ metric.value }}</strong>
            <small>{{ metric.detail }}</small>
          </div>
        </article>
      </div>

      <section class="patient-panel">
        <div class="panel-heading">
          <div>
            <p class="eyebrow">SELECTED PATIENT</p>
            <h2>Digital Health Twin <span>·</span> {{ patient.name }}</h2>
          </div>
          <span class="status-chip"><i class="fas fa-circle"></i> {{ patient.status }}</span>
        </div>

        <div class="patient-content">
          <div class="patient-summary">
            <div class="avatar">JD</div>
            <div>
              <h3>{{ patient.name }}</h3>
              <p>{{ patient.id }} · {{ patient.age }} years · {{ patient.sex }}</p>
              <span class="risk-badge"><i class="fas fa-shield-heart"></i> {{ patient.risk }} risk</span>
            </div>
          </div>

          <div class="vitals-strip">
            <div *ngFor="let vital of vitals" class="vital">
              <span>{{ vital.label }}</span>
              <strong>{{ vital.value }}</strong>
              <small>{{ vital.note }}</small>
            </div>
          </div>

          <div class="detail-grid">
            <div class="detail-block">
              <span class="detail-label">FHIR patient resource</span>
              <strong><i class="fas fa-check-circle"></i> Loaded from Epic EHR</strong>
              <p>Last synchronized 2 minutes ago · R4 compliant</p>
            </div>
            <div class="detail-block">
              <span class="detail-label">Active medications</span>
              <strong>Metformin 500mg · Lisinopril 10mg</strong>
              <p>Medication reconciliation completed today</p>
            </div>
            <div class="detail-block">
              <span class="detail-label">Conditions</span>
              <strong>Hypertension · Type 2 Diabetes</strong>
              <p>2 conditions tracked in the patient twin</p>
            </div>
            <div class="detail-block twin-readiness">
              <span class="detail-label">Twin readiness</span>
              <strong>{{ patient.completeness }}% complete</strong>
              <div class="progress-track"><span [style.width.%]="patient.completeness"></span></div>
            </div>
          </div>
        </div>

        <div class="panel-actions">
          <button class="primary-action"><i class="fas fa-eye"></i> View timeline</button>
          <button><i class="fas fa-chart-line"></i> Run prediction</button>
          <button><i class="fas fa-user-doctor"></i> Create care plan</button>
          <span class="updated"><i class="fas fa-clock"></i> Updated 2 min ago</span>
        </div>
      </section>

      <div class="lower-grid">
        <section class="info-card">
          <div class="card-heading"><h3><i class="fas fa-wave-square"></i> Streaming signals</h3><span>LIVE</span></div>
          <div class="signal-row"><span class="signal-dot green"></span><strong>Wearable vitals</strong><small>Synced 30 sec ago</small></div>
          <div class="signal-row"><span class="signal-dot blue"></span><strong>FHIR resources</strong><small>2.4M resources indexed</small></div>
          <div class="signal-row"><span class="signal-dot amber"></span><strong>Risk engine</strong><small>Next model run in 18 min</small></div>
        </section>
        <section class="info-card">
          <div class="card-heading"><h3><i class="fas fa-clipboard-check"></i> Validation status</h3><span>7 / 7</span></div>
          <p class="validation-copy">All Milestone 1 foundations are connected for this demo workspace.</p>
          <div class="validation-tags"><span>FHIR R4</span><span>HIPAA audit</span><span>SMART auth</span><span>Kafka stream</span></div>
        </section>
      </div>
    </section>
  `,
  styles: [`
    :host { display: block; }
    .dashboard-shell { max-width: 1180px; margin: 0 auto; padding: 30px 34px 54px; color: #e7edf5; }
    .page-heading, .panel-heading, .card-heading { display: flex; justify-content: space-between; align-items: center; gap: 20px; }
    .eyebrow { color: #6da7ed; font-size: .68rem; font-weight: 800; letter-spacing: .14em; margin: 0 0 7px; }
    h1, h2, h3, p { margin-top: 0; }
    h1 { font-size: clamp(1.75rem, 3vw, 2.35rem); margin-bottom: 26px; font-weight: 750; letter-spacing: 0; }
    h2 { font-size: 1.18rem; margin: 0; font-weight: 700; }
    h2 span { color: #617086; }
    .demo-pill, .status-chip { border: 1px solid #2d4058; background: #172231; color: #91a4bc; border-radius: 999px; padding: 8px 13px; font-size: .73rem; white-space: nowrap; }
    .pulse, .status-chip i { display: inline-block; width: 7px; height: 7px; border-radius: 50%; background: #45d18a; margin-right: 7px; }
    .metric-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; margin-bottom: 22px; }
    .metric-card { background: #121b27; border: 1px solid #273548; border-radius: 4px; padding: 18px; display: flex; gap: 14px; min-height: 112px; }
    .metric-card p, .metric-card small { display: block; color: #8292a8; font-size: .72rem; margin: 0 0 5px; }
    .metric-card strong { display: block; color: #f5f8fc; font-size: 1.62rem; line-height: 1.05; margin-bottom: 7px; }
    .metric-card small { color: #5ea6ed; margin: 0; }
    .metric-icon { width: 34px; height: 34px; border-radius: 4px; display: grid; place-items: center; color: #70b8fa; background: #172d45; }
    .metric-card.amber .metric-icon { color: #f4bd64; background: #392c1b; }
    .metric-card.green .metric-icon { color: #63dda0; background: #17362c; }
    .metric-card.purple .metric-icon { color: #be9cff; background: #2b2345; }
    .patient-panel, .info-card { background: #121b27; border: 1px solid #29384b; border-radius: 4px; }
    .panel-heading { padding: 22px 24px; border-bottom: 1px solid #29384b; }
    .status-chip { color: #65d999; border-color: #285a48; background: #142a25; }
    .status-chip i { background: #65d999; }
    .patient-content { padding: 24px; }
    .patient-summary { display: flex; align-items: center; gap: 15px; margin-bottom: 22px; }
    .avatar { width: 58px; height: 58px; display: grid; place-items: center; border-radius: 50%; background: #246bb1; color: white; font-weight: 800; font-size: 1.15rem; }
    .patient-summary h3 { font-size: 1.15rem; margin: 0 0 4px; }
    .patient-summary p { color: #8494aa; font-size: .78rem; margin-bottom: 8px; }
    .risk-badge { color: #f2c778; background: #3a2f1c; border: 1px solid #6b5229; padding: 4px 8px; border-radius: 3px; font-size: .7rem; }
    .vitals-strip { display: grid; grid-template-columns: repeat(4, 1fr); border-top: 1px solid #29384b; border-bottom: 1px solid #29384b; padding: 18px 0; margin-bottom: 22px; }
    .vital { padding: 0 18px; border-right: 1px solid #29384b; }
    .vital:first-child { padding-left: 0; } .vital:last-child { border: 0; }
    .vital span, .vital small, .detail-label { display: block; color: #8494aa; font-size: .68rem; }
    .vital strong { display: block; color: #f5f8fc; font-size: 1.18rem; margin: 4px 0; }
    .vital small { color: #59ce93; }
    .detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 18px 30px; }
    .detail-label { text-transform: uppercase; letter-spacing: .08em; margin-bottom: 6px; }
    .detail-block strong { display: block; font-size: .82rem; color: #dae5f2; }
    .detail-block strong i { color: #5fd69a; margin-right: 5px; }
    .detail-block p { color: #718198; font-size: .7rem; margin: 5px 0 0; }
    .progress-track { height: 5px; background: #26364a; margin-top: 10px; border-radius: 3px; overflow: hidden; }
    .progress-track span { display: block; height: 100%; background: #4aa8ef; }
    .panel-actions { display: flex; gap: 9px; align-items: center; padding: 15px 24px; border-top: 1px solid #29384b; }
    button { color: #aebdd0; background: #1a2635; border: 1px solid #34475d; border-radius: 3px; padding: 8px 11px; font-size: .7rem; cursor: pointer; }
    button:hover, button.primary-action { color: white; background: #1768ad; border-color: #258dd8; }
    button i { margin-right: 6px; }
    .updated { margin-left: auto; color: #6e8095; font-size: .68rem; }
    .lower-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; margin-top: 18px; }
    .info-card { padding: 19px 21px; }
    .card-heading h3 { font-size: .86rem; margin: 0 0 13px; } .card-heading h3 i { color: #5da9ea; margin-right: 7px; }
    .card-heading > span { color: #5dd394; font-size: .66rem; font-weight: 800; }
    .signal-row { display: grid; grid-template-columns: 10px 1fr auto; gap: 9px; align-items: center; padding: 9px 0; border-top: 1px solid #253448; font-size: .75rem; }
    .signal-row small, .validation-copy { color: #76869b; font-size: .68rem; }
    .signal-dot { width: 7px; height: 7px; border-radius: 50%; } .green { background: #58d595; } .blue { background: #58a9ec; } .amber { background: #efbb5f; }
    .validation-copy { line-height: 1.6; margin-bottom: 13px; }
    .validation-tags { display: flex; flex-wrap: wrap; gap: 6px; } .validation-tags span { color: #86bdf0; border: 1px solid #2d5273; background: #172c40; border-radius: 3px; padding: 5px 8px; font-size: .66rem; }
    @media (max-width: 800px) { .dashboard-shell { padding: 22px 16px 40px; } .metric-grid, .vitals-strip { grid-template-columns: repeat(2, 1fr); } .vital { margin-bottom: 14px; border: 0; } .lower-grid, .detail-grid { grid-template-columns: 1fr; } .panel-actions { flex-wrap: wrap; } .updated { width: 100%; margin-left: 0; } }
  `]
})
export class DashboardComponent {
  metrics: DashboardMetric[] = [
    { label: 'Patients onboarded', value: '1,247', detail: '+87 this week', icon: 'fas fa-users', tone: 'blue' },
    { label: 'FHIR resources', value: '2.4M', detail: 'Synced from Epic EHR', icon: 'fas fa-database', tone: 'green' },
    { label: 'Twins created', value: '1,247', detail: '100% coverage', icon: 'fas fa-brain', tone: 'purple' },
    { label: 'Needs attention', value: '18', detail: '5 new alerts today', icon: 'fas fa-triangle-exclamation', tone: 'amber' }
  ];

  patient = { name: 'John Doe', id: 'MRN-10482', age: 46, sex: 'Male', risk: 'Moderate', status: 'Twin active', completeness: 88 };
  vitals = [
    { label: 'Heart rate', value: '72 bpm', note: 'Normal range' },
    { label: 'Blood pressure', value: '130/85', note: 'Monitor closely' },
    { label: 'SpO2', value: '98%', note: 'Normal range' },
    { label: 'Last reading', value: '2 min ago', note: 'Wearable synced' }
  ];
}
