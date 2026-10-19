import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MedicamentoService } from './medicamento.service';
import { Medicamento } from './medicamento.model';
import { ToastService } from '../../core/ui/toast.service';

@Component({
    selector: 'app-medicamento-form',
    standalone: true,
    imports: [CommonModule, FormsModule],
    template: `
    <div class="page-container">
      <div class="page-header">
        <h2>{{ isEdit ? '✏️ Editar' : '➕ Nuevo' }} Medicamento</h2>
        <button class="btn btn-outline" (click)="router.navigate(['/medicamentos'])">← Volver</button>
      </div>
      <div class="form-card">
        <form (ngSubmit)="onSave()">
          <div class="form-row">
            <div class="form-group">
              <label>Nombre *</label>
              <input type="text" [(ngModel)]="med.nombre" name="nombre" required class="form-control" />
            </div>
            <div class="form-group">
              <label>Dosis</label>
              <input type="text" [(ngModel)]="med.dosis" name="dosis" class="form-control" placeholder="Ej: 500mg" />
            </div>
          </div>
          <div class="form-group">
            <label>Descripción</label>
            <textarea [(ngModel)]="med.descripcion" name="descripcion" class="form-control" rows="3"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Stock</label>
              <input type="number" [(ngModel)]="med.stock" name="stock" class="form-control" min="0" />
            </div>
            <div class="form-group">
              <label>Precio</label>
              <input type="number" [(ngModel)]="med.precio" name="precio" class="form-control" min="0" step="0.01" />
            </div>
          </div>
          @if (error()) { <div class="alert alert-error">{{ error() }}</div> }
          <div class="form-actions">
            <button type="button" class="btn btn-outline" (click)="router.navigate(['/medicamentos'])">Cancelar</button>
            <button type="submit" class="btn btn-primary" [disabled]="loading()">{{ isEdit ? 'Actualizar' : 'Guardar' }}</button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class MedicamentoFormComponent implements OnInit {
    med: Medicamento = { nombre: '' };
    isEdit = false;
    loading = signal(false);
    error = signal('');

    constructor(
        private service: MedicamentoService,
        private route: ActivatedRoute,
        public router: Router,
        private toast: ToastService
    ) { }

    ngOnInit(): void {
        const id = this.route.snapshot.paramMap.get('id');
        if (id) {
            this.isEdit = true;
            this.service.getById(+id).subscribe({ next: (m) => (this.med = m) });
        }
    }

    onSave(): void {
        if (!this.med.nombre) { this.error.set('El nombre es requerido.'); return; }
        this.loading.set(true);
        const req = this.isEdit ? this.service.update(this.med.id!, this.med) : this.service.create(this.med);
        req.subscribe({
            next: () => { this.toast.success(this.isEdit ? 'Medicamento actualizado.' : 'Medicamento creado.'); this.router.navigate(['/medicamentos']); },
            error: () => this.loading.set(false)
        });
    }
}
