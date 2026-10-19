import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MedicamentoService } from './medicamento.service';
import { Medicamento } from './medicamento.model';
import { ConfirmService } from '../../core/ui/confirm.service';
import { ToastService } from '../../core/ui/toast.service';

@Component({
    selector: 'app-medicamento-list',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    template: `
    <div class="page-container">
      <div class="page-header">
        <h2>💊 Medicamentos</h2>
        <a routerLink="/medicamentos/nuevo" class="btn btn-primary">+ Nuevo Medicamento</a>
      </div>

      <div class="search-bar">
        <input type="text" [(ngModel)]="searchTerm" placeholder="🔍 Buscar por nombre..." class="form-control" (input)="onSearch()" />
      </div>

      @if (loading()) {
        <div class="loading">Cargando medicamentos...</div>
      } @else if (medicamentos().length === 0) {
        <div class="empty-state">No se encontraron medicamentos.</div>
      } @else {
        <div class="table-responsive">
          <table class="table">
            <thead>
              <tr>
                <th>ID</th><th>Nombre</th><th>Descripción</th>
                <th>Dosis</th><th>Stock</th><th>Precio</th><th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              @for (m of medicamentos(); track m.id) {
                <tr>
                  <td>{{ m.id }}</td>
                  <td><strong>{{ m.nombre }}</strong></td>
                  <td>{{ m.descripcion }}</td>
                  <td>{{ m.dosis }}</td>
                  <td><span class="badge" [class.badge-warning]="(m.stock ?? 0) < 10">{{ m.stock ?? '—' }}</span></td>
                  <td>{{ m.precio ? ('$' + m.precio) : '—' }}</td>
                  <td class="actions">
                    <a [routerLink]="['/medicamentos', m.id, 'editar']" class="btn btn-sm btn-outline">✏️ Editar</a>
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
export class MedicamentoListComponent implements OnInit {
    medicamentos = signal<Medicamento[]>([]);
    loading = signal(true);
    searchTerm = '';
    private debounce: ReturnType<typeof setTimeout> | null = null;

    constructor(
        private service: MedicamentoService,
        private confirm: ConfirmService,
        private toast: ToastService
    ) { }

    ngOnInit(): void { this.load(); }

    load(): void {
        this.loading.set(true);
        this.service.getAll().subscribe({
            next: (data) => { this.medicamentos.set(data); this.loading.set(false); },
            error: () => this.loading.set(false)
        });
    }

    onSearch(): void {
        if (this.debounce) clearTimeout(this.debounce);
        this.debounce = setTimeout(() => {
            if (this.searchTerm.trim()) {
                this.loading.set(true);
                this.service.search(this.searchTerm).subscribe({
                    next: (data) => { this.medicamentos.set(data); this.loading.set(false); },
                    error: () => this.loading.set(false)
                });
            } else {
                this.load();
            }
        }, 400);
    }

    async onDelete(m: Medicamento): Promise<void> {
        const ok = await this.confirm.confirm(`¿Eliminar medicamento ${m.nombre}?`);
        if (!ok) return;
        this.service.delete(m.id!).subscribe({
            next: () => { this.toast.success('Medicamento eliminado.'); this.load(); }
        });
    }
}
