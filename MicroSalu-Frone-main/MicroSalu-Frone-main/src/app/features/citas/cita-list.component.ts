import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CitaService } from './cita.service';
import { Cita } from './cita.model';
import { AuthService } from '../../core/auth/auth.service';
import { ConfirmService } from '../../core/ui/confirm.service';
import { ToastService } from '../../core/ui/toast.service';

@Component({
    selector: 'app-cita-list',
    standalone: true,
    imports: [CommonModule, RouterLink],
    template: `
    <div class="page-container">
      <div class="page-header">
        <h2>📅 {{ titulo() }}</h2>
        @if (auth.isPaciente()) {
          <a routerLink="/nueva-cita" class="btn btn-primary">+ Nueva Cita</a>
        }
      </div>

      @if (loading()) {
        <div class="loading">Cargando citas...</div>
      } @else if (citas().length === 0) {
        <div class="empty-state">No hay citas disponibles.</div>
      } @else {
        <div class="table-responsive">
          <table class="table">
            <thead>
              <tr>
                <th>ID</th><th>Paciente ID</th><th>Médico ID</th>
                <th>Fecha y Hora</th><th>Motivo</th><th>Estado</th><th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              @for (c of citas(); track c.id) {
                <tr>
                  <td>{{ c.id }}</td>
                  <td>{{ c.pacienteId }}</td>
                  <td>{{ c.medicoId }}</td>
                  <td>{{ c.fechaHora | date:'dd/MM/yyyy HH:mm' }}</td>
                  <td>{{ c.motivo }}</td>
                  <td>
                    <span class="badge"
                      [class.badge-success]="c.estado === 'PROGRAMADA'"
                      [class.badge-error]="c.estado === 'CANCELADA'"
                      [class.badge-info]="c.estado === 'COMPLETADA'">
                      {{ c.estado }}
                    </span>
                  </td>
                  <td class="actions">
                    @if (c.estado !== 'CANCELADA') {
                      <button class="btn btn-sm btn-warning" (click)="onCancelar(c)">⛔ Cancelar</button>
                    }
                    @if (auth.isAdmin()) {
                      <button class="btn btn-sm btn-danger" (click)="onDelete(c)">🗑️ Eliminar</button>
                    }
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      }
    </div>
  `
})
export class CitaListComponent implements OnInit {
    citas = signal<Cita[]>([]);
    loading = signal(true);

    titulo = computed(() => {
        if (this.auth.isAdmin()) return 'Todas las Citas';
        if (this.auth.isMedico()) return 'Mis Citas';
        return 'Mis Citas';
    });

    constructor(
        public auth: AuthService,
        private service: CitaService,
        private confirm: ConfirmService,
        private toast: ToastService
    ) { }

    ngOnInit(): void { this.load(); }

    load(): void {
        this.loading.set(true);
        if (this.auth.isAdmin()) {
            this.service.getAll().subscribe({
                next: (d) => { this.citas.set(d); this.loading.set(false); },
                error: () => this.loading.set(false)
            });
        } else if (this.auth.isMedico()) {
            const medicoId = this.auth.getMedicoId();
            if (!medicoId) {
                this.toast.warning('No se encontró tu ID de médico. Contacta al administrador.');
                this.loading.set(false);
                return;
            }
            this.service.getByMedico(medicoId).subscribe({
                next: (d) => { this.citas.set(d); this.loading.set(false); },
                error: () => this.loading.set(false)
            });
        } else if (this.auth.isPaciente()) {
            const pacienteId = this.auth.getPacienteId();
            if (!pacienteId) {
                this.toast.warning('No se encontró tu ID de paciente. Intenta volver a iniciar sesión.');
                this.loading.set(false);
                return;
            }
            this.service.getByPaciente(pacienteId).subscribe({
                next: (d) => { this.citas.set(d); this.loading.set(false); },
                error: () => this.loading.set(false)
            });
        }
    }

    async onCancelar(c: Cita): Promise<void> {
        const ok = await this.confirm.confirm(`¿Cancelar la cita del ${new Date(c.fechaHora).toLocaleString()}?`);
        if (!ok) return;
        this.service.cancelar(c.id!).subscribe({
            next: () => { this.toast.success('Cita cancelada.'); this.load(); }
        });
    }

    async onDelete(c: Cita): Promise<void> {
        const ok = await this.confirm.confirm('¿Eliminar esta cita permanentemente?');
        if (!ok) return;
        this.service.delete(c.id!).subscribe({
            next: () => { this.toast.success('Cita eliminada.'); this.load(); }
        });
    }
}
