import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportesService, ReporteItem } from './reportes.service';
import { ToastService } from '../../core/ui/toast.service';

type ReporteType = 'citas' | 'medicos' | 'pacientes' | 'medicamentos';

@Component({
    selector: 'app-reportes',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="page-container">
      <div class="page-header">
        <h2>📊 Reportes</h2>
      </div>

      <div class="report-buttons">
        <button class="btn btn-primary" (click)="load('citas')" [disabled]="loading()">
          📅 Reporte de Citas
        </button>
        <button class="btn btn-primary" (click)="load('medicos')" [disabled]="loading()">
          👨‍⚕️ Reporte de Médicos
        </button>
        <button class="btn btn-primary" (click)="load('pacientes')" [disabled]="loading()">
          🧑 Reporte de Pacientes
        </button>
        <button class="btn btn-primary" (click)="load('medicamentos')" [disabled]="loading()">
          💊 Reporte de Medicamentos
        </button>
      </div>

      @if (loading()) {
        <div class="loading">Generando reporte...</div>
      }

      @if (titulo()) {
        <div class="report-section">
          <h3>{{ titulo() }}</h3>
          <p class="report-count">Total registros: <strong>{{ data().length }}</strong></p>

          @if (data().length === 0) {
            <div class="empty-state">El reporte no contiene datos.</div>
          } @else {
            <div class="table-responsive">
              <table class="table">
                <thead>
                  <tr>
                    @for (key of headers(); track key) {
                      <th>{{ key }}</th>
                    }
                  </tr>
                </thead>
                <tbody>
                  @for (row of data(); track $index) {
                    <tr>
                      @for (key of headers(); track key) {
                        <td>{{ row[key] }}</td>
                      }
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          }
        </div>
      }
    </div>
  `
})
export class ReportesComponent {
    data = signal<ReporteItem[]>([]);
    headers = signal<string[]>([]);
    titulo = signal('');
    loading = signal(false);

    private readonly nombres: Record<ReporteType, string> = {
        citas: '📅 Reporte de Citas',
        medicos: '👨‍⚕️ Reporte de Médicos',
        pacientes: '🧑 Reporte de Pacientes',
        medicamentos: '💊 Reporte de Medicamentos'
    };

    constructor(private service: ReportesService, private toast: ToastService) { }

    load(tipo: ReporteType): void {
        this.loading.set(true);
        this.data.set([]);
        this.headers.set([]);
        this.titulo.set('');

        const req = tipo === 'citas' ? this.service.getCitas()
            : tipo === 'medicos' ? this.service.getMedicos()
                : tipo === 'pacientes' ? this.service.getPacientes()
                    : this.service.getMedicamentos();

        req.subscribe({
            next: (rows) => {
                this.data.set(rows);
                this.titulo.set(this.nombres[tipo]);
                if (rows.length > 0) {
                    this.headers.set(Object.keys(rows[0]));
                }
                this.loading.set(false);
                this.toast.info(`Reporte cargado: ${rows.length} registros.`);
            },
            error: () => this.loading.set(false)
        });
    }
}
