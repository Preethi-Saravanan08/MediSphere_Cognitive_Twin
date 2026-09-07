import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <aside class="sidebar">
      <nav class="nav flex-column">
        <a routerLink="/dashboard" routerLinkActive="active" class="nav-link">
          <i class="fas fa-dashboard"></i> Dashboard
        </a>
        <a routerLink="/patients" routerLinkActive="active" class="nav-link">
          <i class="fas fa-users"></i> Patients
        </a>
        <hr class="my-2">
        <h6 class="sidebar-heading px-3 mt-4 mb-1">PATIENT 360</h6>
        <a routerLink="#" class="nav-link">
          <i class="fas fa-user-circle"></i> Patient Profile
        </a>
        <a routerLink="#" class="nav-link">
          <i class="fas fa-heartbeat"></i> Health Twin
        </a>
        <a routerLink="#" class="nav-link">
          <i class="fas fa-heart"></i> Vitals
        </a>
        <a routerLink="#" class="nav-link">
          <i class="fas fa-flask"></i> Lab Results
        </a>
        <hr class="my-2">
        <h6 class="sidebar-heading px-3 mt-4 mb-1">MANAGEMENT</h6>
        <a routerLink="#" class="nav-link">
          <i class="fas fa-file-contract"></i> Consent
        </a>
        <a routerLink="#" class="nav-link">
          <i class="fas fa-bell"></i> Alerts
        </a>
        <a routerLink="#" class="nav-link">
          <i class="fas fa-clipboard-list"></i> Care Plans
        </a>
      </nav>
    </aside>
  `,
  styles: [`
    .sidebar {
      width: 250px;
      background-color: #111923;
      border-right: 1px solid #263548;
      overflow-y: auto;
      padding: 20px 0;
    }
    .nav-link {
      color: #8292a7;
      padding: 0.75rem 1rem;
      text-decoration: none;
      display: block;
      transition: all 0.3s ease;
    }
    .nav-link:hover {
      background-color: #1b2c40;
      color: #d9ebfb;
    }
    .nav-link.active { background: #1d66a8; color: #fff; border-left: 3px solid #68b9f4; padding-left: 13px; }
    .nav-link i {
      margin-right: 10px;
      width: 20px;
    }
    .sidebar-heading {
      font-size: 0.875rem;
      font-weight: 700;
      color: #60738b;
      letter-spacing: .12em;
      font-size: .65rem;
    }
  `]
})
export class SidebarComponent {
}
