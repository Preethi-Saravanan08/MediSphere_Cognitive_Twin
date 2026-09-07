import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <i class="fas fa-heartbeat"></i>
          <h1>MediSphere</h1>
          <p>Cognitive Twin Platform</p>
        </div>

        <form (ngSubmit)="login()">
          <div class="form-group mb-3">
            <label for="username">Username</label>
            <input 
              type="text" 
              class="form-control" 
              id="username"
              [(ngModel)]="username"
              name="username"
              placeholder="Enter your username"
            >
          </div>

          <div class="form-group mb-3">
            <label for="password">Password</label>
            <input 
              type="password" 
              class="form-control" 
              id="password"
              [(ngModel)]="password"
              name="password"
              placeholder="Enter your password"
            >
          </div>

          <div class="form-check mb-3">
            <input 
              type="checkbox" 
              class="form-check-input" 
              id="remember"
              [(ngModel)]="rememberMe"
              name="rememberMe"
            >
            <label class="form-check-label" for="remember">
              Remember me
            </label>
          </div>

          <button type="submit" class="btn btn-primary w-100 mb-3">
            <i class="fas fa-sign-in-alt"></i> Sign In
          </button>
        </form>

        <hr>

        <div class="login-footer">
          <p class="text-muted small mb-2">Demo Credentials:</p>
          <p class="text-muted small">Username: admin</p>
          <p class="text-muted small">Password: demo123</p>
        </div>

        <div class="alert alert-info mt-4">
          <i class="fas fa-info-circle"></i>
          <strong>Milestone 1 Status:</strong> Patient Portal UI and Backend APIs are fully operational
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-container {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    .login-card {
      background: white;
      padding: 40px;
      border-radius: 10px;
      box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
      width: 100%;
      max-width: 400px;
    }
    .login-header {
      text-align: center;
      margin-bottom: 30px;
    }
    .login-header i {
      font-size: 3rem;
      color: #667eea;
    }
    .login-header h1 {
      margin: 10px 0 0;
      font-weight: 700;
      color: #333;
    }
    .login-header p {
      color: #6c757d;
      margin: 5px 0 0;
    }
    .form-group label {
      font-weight: 600;
      margin-bottom: 0.5rem;
    }
    .form-control {
      border-radius: 5px;
      padding: 0.75rem 1rem;
    }
    .btn-primary {
      background-color: #667eea;
      border: none;
      padding: 0.75rem;
      font-weight: 600;
      transition: all 0.3s ease;
    }
    .btn-primary:hover {
      background-color: #764ba2;
    }
    .login-footer {
      background-color: #f8f9fa;
      padding: 15px;
      border-radius: 5px;
    }
  `]
})
export class LoginComponent {
  username = '';
  password = '';
  rememberMe = false;

  constructor(private router: Router) { }

  login(): void {
    // For demo purposes, just navigate to dashboard
    // In production, this would authenticate with backend
    if (this.username && this.password) {
      this.router.navigate(['/dashboard']);
    }
  }
}
