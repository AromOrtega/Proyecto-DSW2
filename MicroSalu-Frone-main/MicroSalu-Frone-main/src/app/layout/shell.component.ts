import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './navbar.component';
import { ToastContainerComponent } from '../core/ui/toast-container.component';
import { ConfirmDialogComponent } from '../core/ui/confirm-dialog.component';

@Component({
    selector: 'app-shell',
    standalone: true,
    imports: [RouterOutlet, NavbarComponent, ToastContainerComponent, ConfirmDialogComponent],
    template: `
    <div class="app-shell">
      <app-navbar />
      <main class="main-content">
        <router-outlet />
      </main>
      <app-toast-container />
      <app-confirm-dialog />
    </div>
  `
})
export class ShellComponent { }
