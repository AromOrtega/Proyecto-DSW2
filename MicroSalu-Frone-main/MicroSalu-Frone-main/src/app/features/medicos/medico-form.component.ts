import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MedicoService } from './medico.service';
import { Medico, Horario } from './medico.model';
import { ToastService } from '../../core/ui/toast.service';

@Component({
  selector: 'app-medico-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h2>{{ isEdit ? '✏️ Editar' : '➕ Nuevo' }} Médico</h2>
        <button class="btn btn-outline" (click)="router.navigate(['/medicos'])">← Volver</button>
      </div>

      <div class="form-card">
        <form (ngSubmit)="onSave()">

          <h3 class="section-title">Datos Personales</h3>
          <div class="form-row">
            <div class="form-group">
              <label>Nombre *</label>
              <input type="text" [(ngModel)]="medico.nombre" name="nombre" required class="form-control" />
            </div>
            <div class="form-group">
              <label>Apellido *</label>
              <input type="text" [(ngModel)]="medico.apellido" name="apellido" required class="form-control" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>N° de Licencia (CMP) *</label>
              <input type="text" [(ngModel)]="medico.numeroLicencia" name="numeroLicencia" required class="form-control" />
            </div>
            <div class="form-group">
              <label>Especialidad *</label>
              <select [(ngModel)]="medico.especialidad" name="especialidad" required class="form-control">
                <option value="">Seleccione una...</option>
                <option value="CARDIOLOGIA">Cardiología</option>
                <option value="PEDIATRIA">Pediatría</option>
                <option value="NEUROLOGIA">Neurología</option>
                <option value="MEDICINA_GENERAL">Medicina General</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Email</label>
              <input type="email" [(ngModel)]="medico.email" name="email" class="form-control" />
            </div>
            <div class="form-group">
              <label>Teléfono</label>
              <input type="tel" [(ngModel)]="medico.telefono" name="telefono" class="form-control" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Tarifa por Consulta ($)</label>
              <input type="number" step="0.01" [(ngModel)]="medico.tarifaConsulta" name="tarifaConsulta" class="form-control" />
            </div>
          </div>

          <h3 class="section-title">Horarios de Atención</h3>
          <div class="schedule-section">
            @for (h of medico.horarios; track $index) {
              <div class="form-row align-items-end" style="margin-bottom: 10px; display: flex; gap: 10px;">
                <div class="form-group" style="flex: 1;">
                  <label>Día</label>
                  <select [(ngModel)]="h.dia" name="dia_{{$index}}" class="form-control" required>
                    <option value="LUNES">Lunes</option>
                    <option value="MARTES">Martes</option>
                    <option value="MIERCOLES">Miércoles</option>
                    <option value="JUEVES">Jueves</option>
                    <option value="VIERNES">Viernes</option>
                    <option value="SABADO">Sábado</option>
                    <option value="DOMINGO">Domingo</option>
                  </select>
                </div>
                <div class="form-group" style="flex: 1;">
                  <label>Hora Inicio</label>
                  <input type="time" [(ngModel)]="h.horaInicio" name="inicio_{{$index}}" class="form-control" required />
                </div>
                <div class="form-group" style="flex: 1;">
                  <label>Hora Fin</label>
                  <input type="time" [(ngModel)]="h.horaFin" name="fin_{{$index}}" class="form-control" required />
                </div>
                <div class="form-group">
                  <button type="button" class="btn btn-danger" (click)="eliminarHorario($index)">🗑️</button>
                </div>
              </div>
            }
            <button type="button" class="btn btn-outline mt-2" (click)="agregarHorario()">+ Añadir turno</button>
          </div>

          @if (!isEdit) {
            <h3 class="section-title" style="margin-top: 20px;">Credenciales de Acceso</h3>
            <div class="form-row">
              <div class="form-group">
                <label>Usuario *</label>
                <input type="text" [(ngModel)]="medico.usuario!.username" name="username" required class="form-control" />
              </div>
              <div class="form-group">
                <label>Contraseña *</label>
                <input type="password" [(ngModel)]="medico.usuario!.password" name="password" required class="form-control" />
              </div>
            </div>
          }

          @if (error()) { <div class="alert alert-error mt-3">{{ error() }}</div> }

          <div class="form-actions" style="margin-top: 20px;">
            <button type="button" class="btn btn-outline" (click)="router.navigate(['/medicos'])">Cancelar</button>
            <button type="submit" class="btn btn-primary" [disabled]="loading()">
              @if (loading()) { <span class="spinner"></span> }
              {{ isEdit ? 'Actualizar' : 'Guardar' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class MedicoFormComponent implements OnInit {
  medico: Medico = { nombre: '', apellido: '', especialidad: '', usuario: {}, horarios: [] };
  isEdit = false;
  loading = signal(false);
  error = signal('');

  constructor(
    private service: MedicoService,
    private route: ActivatedRoute,
    public router: Router,
    private toast: ToastService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.service.getById(+id).subscribe({
        next: (m) => {
          this.medico = m;
          if (!this.medico.horarios) {
            this.medico.horarios = [];
          }
        },
        error: () => this.error.set('Error al cargar el médico.')
      });
    }
  }

  agregarHorario(): void {
    if (!this.medico.horarios) this.medico.horarios = [];
    // ¡Acá cambiamos diaSemana por dia!
    this.medico.horarios.push({ dia: 'LUNES', horaInicio: '08:00', horaFin: '12:00' });
  }

  eliminarHorario(index: number): void {
    this.medico.horarios?.splice(index, 1);
  }

  onSave(): void {
    if (!this.medico.nombre || !this.medico.apellido || !this.medico.especialidad) {
      this.error.set('Nombre, apellido y especialidad son requeridos.');
      return;
    }

    if (!this.isEdit && (!this.medico.usuario?.username || !this.medico.usuario?.password)) {
      this.error.set('Debe asignarle un usuario y contraseña al médico.');
      return;
    }

    this.loading.set(true);
    const req = this.isEdit
      ? this.service.update(this.medico.id!, this.medico)
      : this.service.create(this.medico);

    req.subscribe({
      next: () => {
        this.toast.success(this.isEdit ? 'Médico actualizado.' : 'Médico creado.');
        this.router.navigate(['/medicos']);
      },
      error: (err) => {
        console.error(err);
        this.error.set('Error al guardar. Verifica la consola.');
        this.loading.set(false);
      }
    });
  }
}
