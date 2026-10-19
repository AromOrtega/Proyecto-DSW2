import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/ui/toast.service';
import { PacienteService } from '../pacientes/paciente.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    template: `
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-logo">
          <span class="logo-icon">🏥</span>
          <h1>SaludVital</h1>
          <p>Plataforma de Gestión Médica</p>
        </div>

        <form (ngSubmit)="onLogin()" #f="ngForm">
          <div class="form-group">
            <label for="username">Usuario</label>
            <input
              id="username"
              type="text"
              name="username"
              [(ngModel)]="username"
              required
              placeholder="Ingresa tu usuario"
              class="form-control"
            />
          </div>

          <div class="form-group">
            <label for="password">Contraseña</label>
            <input
              id="password"
              type="password"
              name="password"
              [(ngModel)]="password"
              required
              placeholder="Ingresa tu contraseña"
              class="form-control"
            />
          </div>

          @if (error()) {
            <div class="alert alert-error">{{ error() }}</div>
          }

          <button type="submit" class="btn btn-primary btn-block" [disabled]="loading()">
            @if (loading()) { <span class="spinner"></span> Iniciando... }
            @else { Iniciar Sesión }
          </button>
        </form>

        <div class="auth-footer">
          <p>¿Eres paciente nuevo? <a routerLink="/register-paciente">Regístrate aquí</a></p>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {
    username = '';
    password = '';
    loading = signal(false);
    error = signal('');

    constructor(
        private auth: AuthService,
        private router: Router,
        private toast: ToastService,
        private pacienteService: PacienteService
    ) { }

    onLogin(): void {
        if (!this.username || !this.password) {
            this.error.set('Por favor completa todos los campos.');
            return;
        }
        this.loading.set(true);
        this.error.set('');

        this.auth.login({ username: this.username, password: this.password }).subscribe({
            next: () => {
                this.toast.success(`¡Bienvenido, ${this.auth.username()}!`);
                this.resolveUserProfile();
            },
            error: (err) => {
                this.loading.set(false);
                const msg = err?.error?.message ?? 'Credenciales inválidas.';
                this.error.set(typeof msg === 'string' ? msg : 'Error al iniciar sesión.');
            }
        });
    }

    private resolveUserProfile(): void {
        const userId = this.auth.userId();

        if (this.auth.isPaciente() && userId) {
            this.pacienteService.getByUserId(userId).subscribe({
                next: (p) => {
                    if (p?.id) this.auth.setPacienteId(p.id);
                    this.loading.set(false);
                    this.router.navigate(['/']);
                },
                error: () => {
                    this.loading.set(false);
                    this.router.navigate(['/']);
                }
            });
        } else {
            this.loading.set(false);
            this.router.navigate(['/']);
        }
    }
}
