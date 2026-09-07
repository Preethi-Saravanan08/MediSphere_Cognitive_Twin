import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar navbar-dark">
      <div class="container-fluid">
        <span class="navbar-brand mb-0 h1">
          <span class="brand-mark"><i class="fas fa-wave-square"></i></span> MediSphere
        </span>
        <div class="d-flex align-items-center">
          <span class="portal-label">Milestone 1: FHIR Integration & Twin Foundation</span>
          <span class="clinician-label">Clinician <span class="divider">|</span> Logout</span>
        </div>
      </div>
    </nav>
  `,
  styles: [`
    .navbar {
      background: #1769b7;
      min-height: 58px;
      padding: 0 24px;
    }
    .navbar-brand {
      font-size: 1.1rem;
      font-weight: bold;
      letter-spacing: .01em;
    }
    .brand-mark { color: #99d2ff; margin-right: 7px; }
    .portal-label { margin: auto; color: #c9e3fb; font-size: .78rem; }
    .clinician-label { color: #d9ebfb; font-size: .7rem; }
    .divider { margin: 0 8px; opacity: .5; }
    @media (max-width: 650px) { .portal-label { display: none; } .navbar { padding: 0 14px; } }
  `]
})
export class NavbarComponent implements OnInit {
  constructor() { }

  ngOnInit(): void {
  }
}
