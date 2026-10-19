import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PacienteService } from './paciente.service';
import { Paciente } from './paciente.model';
import { ConfirmService } from '../../core/ui/confirm.service';
import { ToastService } from '../../core/ui/toast.service';

@Component({
  selector: 'app-paciente-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h2>🧑‍🤝‍🧑 Pacientes</h2>
        <a routerLink="/pacientes/nuevo" class="btn btn-primary">+ Nuevo Paciente</a>
      </div>

      @if (loading()) {
        <div class="loading">Cargando pacientes...</div>
      } @else if (pacientes().length === 0) {
        <div class="empty-state">No hay pacientes registrados.</div>
      } @else {
        <div class="table-responsive">
          <table class="table">
            <thead>
            <tr>
              <th>ID</th>
              <th>Nombre</th>
              <th>Apellido</th>
              <th>Doc. Identidad</th> <th>Teléfono</th>
              <th>F. Nacimiento</th>
              <th>Acciones</th>
            </tr>
            </thead>
            <tbody>
              @for (p of pacientes(); track p.id) {
                <tr>
                  <td>{{ p.id }}</td>
                  <td>{{ p.nombre }}</td>
                  <td>{{ p.apellido }}</td>
                  <td>{{ p.numeroIdentificacion }}</td> <td>{{ p.telefono }}</td>
                  <td>{{ p.fechaNacimiento }}</td>
                  <td class="actions">
                    <a [routerLink]="['/pacientes', p.id, 'editar']" class="btn btn-sm btn-outline">✏️ Editar</a>
                    <button class="btn btn-sm btn-danger" (click)="onDelete(p)">🗑️ Eliminar</button>
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
export class PacienteListComponent implements OnInit {
  pacientes = signal<Paciente[]>([]);
  loading = signal(true);

  constructor(
    private service: PacienteService,
    private confirm: ConfirmService,
    private toast: ToastService
  ) { }

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.service.getAll().subscribe({
      next: (data) => { this.pacientes.set(data); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  async onDelete(p: Paciente): Promise<void> {
    const ok = await this.confirm.confirm(`¿Eliminar al paciente ${p.nombre} ${p.apellido}?`);
    if (!ok) return;
    this.service.delete(p.id!).subscribe({
      next: () => { this.toast.success('Paciente eliminado.'); this.load(); }
    });
  }
}
