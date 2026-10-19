import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `<router-outlet />`
})
export class AppComponent implements OnInit {
  constructor(private auth: AuthService) { }

  ngOnInit(): void {
    // Check session expiry on app start
    this.auth.checkSessionExpiry();
  }
}
