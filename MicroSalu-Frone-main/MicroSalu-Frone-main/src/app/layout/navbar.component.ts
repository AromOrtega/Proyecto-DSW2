import { Component, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';

@Component({
    selector: 'app-navbar',
    standalone: true,
    imports: [CommonModule, RouterLink, RouterLinkActive],
    template: `
    <nav class="navbar">
      <div class="navbar-brand">
        <span class="logo-icon">🏥</span>
        <span class="brand-name">SaludVital</span>
      </div>

      <ul class="nav-links">
        @if (auth.isAdmin()) {
          <li><a routerLink="/medicos" routerLinkActive="active">Médicos</a></li>
          <li><a routerLink="/pacientes" routerLinkActive="active">Pacientes</a></li>
          <li><a routerLink="/medicamentos" routerLinkActive="active">Medicamentos</a></li>
          <li><a routerLink="/citas" routerLinkActive="active">Citas</a></li>
          <li><a routerLink="/reportes" routerLinkActive="active">Reportes</a></li>
          <li><a routerLink="/notificaciones" routerLinkActive="active">Notificaciones</a></li>
        }
        @if (auth.isMedico()) {
          <li><a routerLink="/mis-citas" routerLinkActive="active">Mis Citas</a></li>
          <li><a routerLink="/mis-notificaciones" routerLinkActive="active">Notificaciones</a></li>
        }
        @if (auth.isPaciente()) {
          <li><a routerLink="/mis-citas" routerLinkActive="active">Mis Citas</a></li>
          <li><a routerLink="/nueva-cita" routerLinkActive="active">Nueva Cita</a></li>
          <li><a routerLink="/mis-notificaciones" routerLinkActive="active">Notificaciones</a></li>
        }
      </ul>

      <div class="navbar-user">
        <span class="user-badge">{{ roleBadge() }}</span>
        <span class="username">{{ auth.username() }}</span>
        <button class="btn btn-outline btn-sm" (click)="auth.logout()">Salir</button>
      </div>
    </nav>
  `
})
export class NavbarComponent {
    roleBadge = computed(() => {
        if (this.auth.isAdmin()) return 'ADMIN';
        if (this.auth.isMedico()) return 'MÉDICO';
        if (this.auth.isPaciente()) return 'PACIENTE';
        return '';
    });

    constructor(public auth: AuthService) { }
}
