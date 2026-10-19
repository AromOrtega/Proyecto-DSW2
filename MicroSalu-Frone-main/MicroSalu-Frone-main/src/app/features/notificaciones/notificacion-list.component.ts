import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificacionService } from './notificacion.service';
import { Notificacion } from './notificacion.model';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/ui/toast.service';

@Component({
    selector: 'app-notificacion-list',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="page-container">
      <div class="page-header">
        <h2>🔔 Notificaciones</h2>
        @if (unread() > 0) {
          <span class="badge badge-error">{{ unread() }} sin leer</span>
        }
      </div>

      @if (loading()) {
        <div class="loading">Cargando notificaciones...</div>
      } @else if (notificaciones().length === 0) {
        <div class="empty-state">No hay notificaciones.</div>
      } @else {
        <div class="notif-list">
          @for (n of notificaciones(); track n.id) {
            <div class="notif-card" [class.notif-card--unread]="!n.leida">
              <div class="notif-icon">🔔</div>
              <div class="notif-body">
                <p class="notif-msg">{{ n.mensaje }}</p>
                <span class="notif-date">{{ n.fechaCreacion | date:'dd/MM/yyyy HH:mm' }}</span>
                @if (n.tipo) { <span class="badge badge-info">{{ n.tipo }}</span> }
              </div>
              @if (!n.leida) { <span class="notif-dot"></span> }
            </div>
          }
        </div>
      }
    </div>
  `
})
export class NotificacionListComponent implements OnInit {
    notificaciones = signal<Notificacion[]>([]);
    loading = signal(true);
    unread = computed(() => this.notificaciones().filter(n => !n.leida).length);

    constructor(
        private service: NotificacionService,
        public auth: AuthService,
        private toast: ToastService
    ) { }

    ngOnInit(): void { this.load(); }

    load(): void {
        this.loading.set(true);
        if (this.auth.isAdmin()) {
            this.service.getAll().subscribe({
                next: (d) => { this.notificaciones.set(d); this.loading.set(false); },
                error: () => this.loading.set(false)
            });
        } else {
            const uid = this.auth.userId();
            if (!uid) {
                this.toast.warning('No se encontró tu ID de usuario.');
                this.loading.set(false);
                return;
            }
            this.service.getByUser(uid).subscribe({
                next: (d) => { this.notificaciones.set(d); this.loading.set(false); },
                error: () => this.loading.set(false)
            });
        }
    }
}
