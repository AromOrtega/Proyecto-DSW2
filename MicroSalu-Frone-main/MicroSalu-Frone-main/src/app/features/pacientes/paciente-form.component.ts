import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PacienteService } from './paciente.service';
import { Paciente } from './paciente.model';
import { ToastService } from '../../core/ui/toast.service';
// 1. IMPORTANTE: Importa tu AuthService (ajusta la ruta según tu proyecto)
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-paciente-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h2>{{ isEdit ? '✏️ Editar' : '➕ Nuevo' }} Paciente</h2>
        <button class="btn btn-outline" (click)="router.navigate(['/pacientes'])">← Volver</button>
      </div>

      <div class="form-card">
        <form (ngSubmit)="onSave()">
          <div class="form-row">
            <div class="form-group">
              <label>Nombre *</label>
              <input type="text" [(ngModel)]="paciente.nombre" name="nombre" required class="form-control" />
            </div>
            <div class="form-group">
              <label>Apellido</label>
              <input type="text" [(ngModel)]="paciente.apellido" name="apellido" class="form-control" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Número de Identificación</label>
              <input type="text" [(ngModel)]="paciente.numeroIdentificacion" name="numeroIdentificacion" class="form-control" />
            </div>
            <div class="form-group">
              <label>Teléfono</label>
              <input type="tel" [(ngModel)]="paciente.telefono" name="telefono" class="form-control" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Fecha de Nacimiento</label>
              <input type="date" [(ngModel)]="paciente.fechaNacimiento" name="fechaNacimiento" class="form-control" />
            </div>
            <div class="form-group">
              <label>Dirección</label>
              <input type="text" [(ngModel)]="paciente.direccion" name="direccion" class="form-control" />
            </div>
          </div>

          @if (error()) { <div class="alert alert-error">{{ error() }}</div> }

          <div class="form-actions">
            <button type="button" class="btn btn-outline" (click)="router.navigate(['/pacientes'])">Cancelar</button>
            <button type="submit" class="btn btn-primary" [disabled]="loading()">
              {{ isEdit ? 'Actualizar' : 'Guardar' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class PacienteFormComponent implements OnInit {
  paciente: Paciente = { nombre: '', apellido: '' };
  isEdit = false;
  loading = signal(false);
  error = signal('');

  constructor(
    private service: PacienteService,
    private route: ActivatedRoute,
    public router: Router,
    private toast: ToastService,
    private authService: AuthService // 2. Inyectamos el AuthService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.service.getById(+id).subscribe({ next: (p) => (this.paciente = p) });
    }
  }

  onSave(): void {
    if (!this.paciente.nombre) {
      this.error.set('El nombre es requerido.');
      return;
    }

    // 3. LA MAGIA: Asignamos el usuarioId antes de enviar la petición
    // Solo lo hacemos si es un paciente nuevo (no edición)
    if (!this.isEdit) {
      const idUsuario = this.authService.userId();
      if (idUsuario) {
        this.paciente.usuarioId = idUsuario;
      } else {
        this.error.set('Error crítico: No se pudo identificar tu sesión.');
        return;
      }
    }

    this.loading.set(true);
    const req = this.isEdit
      ? this.service.update(this.paciente.id!, this.paciente)
      : this.service.create(this.paciente);

    req.subscribe({
      next: () => {
        this.toast.success(this.isEdit ? 'Paciente actualizado.' : 'Paciente creado.');
        this.router.navigate(['/pacientes']);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set('Ocurrió un error en el servidor al intentar guardar.');
        console.error(err);
      }
    });
  }
}
