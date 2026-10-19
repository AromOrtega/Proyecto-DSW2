import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { decodeToken, isTokenExpired, JwtPayload } from './token.util';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  id: number;
  username: string;
  email: string;
  roles: string[];
}

const TOKEN_KEY = 'sv_token';
const USER_ID_KEY = 'sv_userId';
const PACIENTE_ID_KEY = 'sv_pacienteId';
const MEDICO_ID_KEY = 'sv_medicoId';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly base = environment.apiBaseUrl;

  private _token = signal<string | null>(localStorage.getItem(TOKEN_KEY));
  readonly token = this._token.asReadonly();

  readonly payload = computed<JwtPayload | null>(() => {
    const t = this._token();
    return t ? decodeToken(t) : null;
  });

  readonly username = computed(() => this.payload()?.sub ?? null);
  readonly roles = computed(() => this.payload()?.roles ?? []);
  readonly userId = computed<number | null>(() => {
    const stored = localStorage.getItem(USER_ID_KEY);
    return stored ? +stored : (this.payload()?.userId ?? null);
  });

  readonly isLoggedIn = computed(() => {
    const t = this._token();
    return !!t && !isTokenExpired(t);
  });

  isAdmin = computed(() => this.roles().includes('ROLE_ADMIN'));
  isMedico = computed(() => this.roles().includes('ROLE_MEDICO'));
  isPaciente = computed(() => this.roles().includes('ROLE_PACIENTE'));

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.base}/api/auth/login`, credentials).pipe(
      tap(res => {
        // 🔥 GUARDAR TOKEN CORRECTO
        localStorage.setItem(TOKEN_KEY, res.accessToken);
        this._token.set(res.accessToken);

        // 🔥 Guardar userId también
        localStorage.setItem(USER_ID_KEY, String(res.id));
      }),
      catchError(err => throwError(() => err))
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_ID_KEY);
    localStorage.removeItem(PACIENTE_ID_KEY);
    localStorage.removeItem(MEDICO_ID_KEY);
    this._token.set(null);
    this.router.navigate(['/login']);
  }

  setUserId(id: number): void {
    localStorage.setItem(USER_ID_KEY, String(id));
  }

  setPacienteId(id: number): void {
    localStorage.setItem(PACIENTE_ID_KEY, String(id));
  }

  setMedicoId(id: number): void {
    localStorage.setItem(MEDICO_ID_KEY, String(id));
  }

  getPacienteId(): number | null {
    const v = localStorage.getItem(PACIENTE_ID_KEY);
    return v ? +v : null;
  }

  getMedicoId(): number | null {
    const v = localStorage.getItem(MEDICO_ID_KEY);
    return v ? +v : null;
  }

  hasRole(role: string): boolean {
    return this.roles().includes(role);
  }

  checkSessionExpiry(): void {
    const t = this._token();
    if (t && isTokenExpired(t)) {
      this.logout();
    }
  }
}
