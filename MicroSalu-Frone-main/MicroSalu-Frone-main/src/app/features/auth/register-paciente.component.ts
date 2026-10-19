import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ToastService } from '../../core/ui/toast.service';

@Component({
  selector: 'app-register-paciente',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="auth-container">
      <div class="auth-card auth-card--wide">
        <div class="auth-logo">
          <span class="logo-icon">🏥</span>
          <h1>SaludVital</h1>
          <p>Registro de Paciente</p>
        </div>

        <form (ngSubmit)="onRegister()" #f="ngForm">
          <div class="form-row">
            <div class="form-group">
              <label>Usuario *</label>
              <input type="text" name="username" [(ngModel)]="form.username" required class="form-control" placeholder="nombre_usuario" />
            </div>
            <div class="form-group">
              <label>Email *</label>
              <input type="email" name="email" [(ngModel)]="form.email" required class="form-control" placeholder="email@ejemplo.com" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Contraseña *</label>
              <input type="password" name="password" [(ngModel)]="form.password" required minlength="6" class="form-control" placeholder="Mínimo 6 caracteres" />
            </div>
            <div class="form-group">
              <label>Nombre *</label>
              <input type="text" name="nombre" [(ngModel)]="form.nombre" required class="form-control" placeholder="Tu nombre" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Apellido</label>
              <input type="text" name="apellido" [(ngModel)]="form.apellido" class="form-control" placeholder="Tu apellido" />
            </div>
            <div class="form-group">
              <label>Doc. Identidad</label>
              <input type="text" name="numeroIdentificacion" [(ngModel)]="form.numeroIdentificacion" class="form-control" placeholder="DNI o Pasaporte" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Teléfono</label>
              <input type="tel" name="telefono" [(ngModel)]="form.telefono" class="form-control" placeholder="+1 234 567 8900" />
            </div>
            <div class="form-group">
              <label>Fecha de Nacimiento</label>
              <input type="date" name="fechaNacimiento" [(ngModel)]="form.fechaNacimiento" class="form-control" />
            </div>
          </div>

          <div class="form-group">
            <label>Dirección</label>
            <input type="text" name="direccion" [(ngModel)]="form.direccion" class="form-control" placeholder="Tu dirección" />
          </div>

          @if (error()) {
            <div class="alert alert-error">{{ error() }}</div>
          }
          @if (success()) {
            <div class="alert alert-success">{{ success() }}</div>
          }

          <button type="submit" class="btn btn-primary btn-block" [disabled]="loading()">
            @if (loading()) { <span class="spinner"></span> Registrando... }
            @else { Crear Cuenta }
          </button>
        </form>

        <div class="auth-footer">
          <p>¿Ya tienes cuenta? <a routerLink="/login">Inicia sesión</a></p>
        </div>
      </div>
    </div>
  `
})
export class RegisterPacienteComponent {
  // Agregamos numeroIdentificacion a la inicialización del formulario
  form = {
    username: '', email: '', password: '', nombre: '',
    apellido: '', numeroIdentificacion: '', telefono: '', fechaNacimiento: '', direccion: ''
  };
  loading = signal(false);
  error = signal('');
  success = signal('');

  constructor(
    private http: HttpClient,
    private router: Router,
    private toast: ToastService
  ) { }

  onRegister(): void {
    if (!this.form.username || !this.form.email || !this.form.password || !this.form.nombre) {
      this.error.set('Por favor completa los campos requeridos (*).');
      return;
    }
    this.loading.set(true);
    this.error.set('');

    this.http.post(`${environment.apiBaseUrl}/api/auth/register-paciente`, this.form).subscribe({
      next: () => {
        this.success.set('¡Cuenta creada exitosamente! Ahora puedes iniciar sesión.');
        this.loading.set(false);
        this.toast.success('Registro exitoso. Inicia sesión.');
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err?.error?.message ?? 'Error al registrar. Intenta de nuevo.';
        this.error.set(typeof msg === 'string' ? msg : JSON.stringify(msg));
      }
    });
  }
}
