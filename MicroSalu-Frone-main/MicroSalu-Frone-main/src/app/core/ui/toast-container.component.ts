import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from './toast.service';

@Component({
    selector: 'app-toast-container',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="toast-container">
      @for (msg of toast.messages(); track msg.id) {
        <div class="toast toast--{{ msg.type }}" (click)="toast.remove(msg.id)">
          <span class="toast-icon">{{ icons[msg.type] }}</span>
          <span class="toast-text">{{ msg.text }}</span>
          <button class="toast-close" (click)="toast.remove(msg.id)">×</button>
        </div>
      }
    </div>
  `
})
export class ToastContainerComponent {
    readonly icons = { success: '✅', error: '❌', info: 'ℹ️', warning: '⚠️' };
    constructor(public toast: ToastService) { }
}
