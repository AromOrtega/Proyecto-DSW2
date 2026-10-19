import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ReporteItem {
    [key: string]: unknown;
}

@Injectable({ providedIn: 'root' })
export class ReportesService {
    private readonly url = `${environment.apiBaseUrl}/api/reportes`;

    constructor(private http: HttpClient) { }

    getCitas(): Observable<ReporteItem[]> {
        return this.http.get<ReporteItem[]>(`${this.url}/citas`);
    }

    getMedicos(): Observable<ReporteItem[]> {
        return this.http.get<ReporteItem[]>(`${this.url}/medicos`);
    }

    getPacientes(): Observable<ReporteItem[]> {
        return this.http.get<ReporteItem[]>(`${this.url}/pacientes`);
    }

    getMedicamentos(): Observable<ReporteItem[]> {
        return this.http.get<ReporteItem[]>(`${this.url}/medicamentos`);
    }
}
