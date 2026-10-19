import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MedicoService } from './medico.service';
import { Medico } from './medico.model';
import { ConfirmService } from '../../core/ui/confirm.service';
import { ToastService } from '../../core/ui/toast.service';

@Component({
  selector: 'app-medico-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h2>👨‍⚕️ Médicos</h2>
        <a routerLink="/medicos/nuevo" class="btn btn-primary">+ Nuevo Médico</a>
      </div>

      @if (loading()) {
        <div class="loading">Cargando médicos...</div>
      } @else if (medicos().length === 0) {
        <div class="empty-state">No hay médicos registrados.</div>
      } @else {
        <div class="table-responsive">
          <table class="table">
            <thead>
            <tr>
              <th>ID</th>
              <th>Nombre</th>
              <th>Apellido</th>
              <th>N° Licencia</th>
              <th>Especialidad</th>
              <th>Horarios</th> <th>Email</th>
              <th>Teléfono</th>
              <th>Acciones</th>
            </tr>
            </thead>
            <tbody>
              @for (m of medicos(); track m.id) {
                <tr>
                  <td>{{ m.id }}</td>
                  <td>{{ m.nombre }}</td>
                  <td>{{ m.apellido }}</td>
                  <td><strong>{{ m.numeroLicencia }}</strong></td>
                  <td><span class="badge badge-info">{{ m.especialidad }}</span></td>

                  <td>
                    <span class="badge badge-secondary">
                      {{ m.horarios?.length || 0 }} turnos
                    </span>
                  </td>

                  <td>{{ m.email }}</td>
                  <td>{{ m.telefono }}</td>
                  <td class="actions">
                    <a [routerLink]="['/medicos', m.id, 'editar']" class="btn btn-sm btn-outline">✏️ Editar</a>
                    <button class="btn btn-sm btn-danger" (click)="onDelete(m)">🗑️ Eliminar</button>
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
export class MedicoListComponent implements OnInit {
  medicos = signal<Medico[]>([]);
  loading = signal(true);

  constructor(
    private service: MedicoService,
    private confirm: ConfirmService,
    private toast: ToastService
  ) { }

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.service.getAll().subscribe({
      next: (data) => { this.medicos.set(data); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  async onDelete(m: Medico): Promise<void> {
    const ok = await this.confirm.confirm(`¿Eliminar al médico ${m.nombre} ${m.apellido}?`);
    if (!ok) return;
    this.service.delete(m.id!).subscribe({
      next: () => { this.toast.success('Médico eliminado.'); this.load(); },
      error: () => { }
    });
  }
}
