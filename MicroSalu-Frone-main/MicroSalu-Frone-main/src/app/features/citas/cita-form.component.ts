import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CitaService } from './cita.service';
import { MedicoService } from '../medicos/medico.service';
import { Medico } from '../medicos/medico.model';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/ui/toast.service';

@Component({
    selector: 'app-cita-form',
    standalone: true,
    imports: [CommonModule, FormsModule],
    template: `
    <div class="page-container">
      <div class="page-header">
        <h2>📅 Nueva Cita</h2>
        <button class="btn btn-outline" (click)="router.navigate(['/mis-citas'])">← Mis Citas</button>
      </div>

      <div class="form-card">
        <form (ngSubmit)="onSave()">
          <div class="form-group">
            <label>Médico *</label>
            <select [(ngModel)]="form.medicoId" name="medicoId" required class="form-control">
              <option [ngValue]="0" disabled>Selecciona un médico...</option>
              @for (m of medicos(); track m.id) {
                <option [ngValue]="m.id">{{ m.nombre }} {{ m.apellido }} — {{ m.especialidad }}</option>
              }
            </select>
          </div>

          <div class="form-group">
            <label>Fecha y Hora *</label>
            <input type="datetime-local" [(ngModel)]="fechaHoraLocal" name="fechaHora" required class="form-control" />
          </div>

          <div class="form-group">
            <label>Motivo de la consulta</label>
            <textarea [(ngModel)]="form.motivo" name="motivo" class="form-control" rows="4"
              placeholder="Describe el motivo de tu cita..."></textarea>
          </div>

          @if (error()) { <div class="alert alert-error">{{ error() }}</div> }

          <div class="form-actions">
            <button type="button" class="btn btn-outline" (click)="router.navigate(['/mis-citas'])">Cancelar</button>
            <button type="submit" class="btn btn-primary" [disabled]="loading()">
              @if (loading()) { <span class="spinner"></span> }
              Agendar Cita
            </button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class CitaFormComponent implements OnInit {
    form = { pacienteId: 0, medicoId: 0, motivo: '' };
    fechaHoraLocal = '';
    medicos = signal<Medico[]>([]);
    loading = signal(false);
    error = signal('');

    constructor(
        private citaService: CitaService,
        private medicoService: MedicoService,
        public auth: AuthService,
        public router: Router,
        private toast: ToastService
    ) { }

    ngOnInit(): void {
        this.medicoService.getAll().subscribe({
            next: (m) => this.medicos.set(m),
            error: () => this.error.set('No se pudieron cargar los médicos.')
        });
        const pid = this.auth.getPacienteId();
        if (pid) this.form.pacienteId = pid;
    }

    onSave(): void {
        if (!this.form.medicoId || !this.fechaHoraLocal) {
            this.error.set('Selecciona médico y fecha/hora.');
            return;
        }
        if (!this.form.pacienteId) {
            this.error.set('No se encontró tu perfil de paciente. Vuelve a iniciar sesión.');
            return;
        }
        this.loading.set(true);
        const cita = {
            ...this.form,
            fechaHora: new Date(this.fechaHoraLocal).toISOString()
        };
        this.citaService.create(cita).subscribe({
            next: () => {
                this.toast.success('Cita agendada exitosamente.');
                this.router.navigate(['/mis-citas']);
            },
            error: () => this.loading.set(false)
        });
    }
}
