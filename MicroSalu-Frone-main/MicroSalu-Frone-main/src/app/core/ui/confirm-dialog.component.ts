import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfirmService } from './confirm.service';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (confirm.visible()) {
      <div class="modal-overlay" (click)="confirm.cancel()">
        <div class="modal-dialog" (click)="$event.stopPropagation()">
          <div class="modal-body">
            <span class="modal-icon">⚠️</span>
            <p>{{ confirm.message() }}</p>
          </div>
          <div class="modal-actions">
            <button class="btn btn-outline" (click)="confirm.cancel()">Cancelar</button>
            <button class="btn btn-danger" (click)="confirm.accept()">Confirmar</button>
          </div>
        </div>
      </div>
    }
  `
})
export class ConfirmDialogComponent {
  constructor(public confirm: ConfirmService) { }
}
