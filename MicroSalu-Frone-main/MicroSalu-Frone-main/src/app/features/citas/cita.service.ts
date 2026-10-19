import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cita } from './cita.model';

@Injectable({ providedIn: 'root' })
export class CitaService {
    private readonly url = `${environment.apiBaseUrl}/api/citas`;

    constructor(private http: HttpClient) { }

    getAll(): Observable<Cita[]> {
        return this.http.get<Cita[]>(this.url);
    }

    getById(id: number): Observable<Cita> {
        return this.http.get<Cita>(`${this.url}/${id}`);
    }

    getByPaciente(pacienteId: number): Observable<Cita[]> {
        return this.http.get<Cita[]>(`${this.url}/paciente/${pacienteId}`);
    }

    getByMedico(medicoId: number): Observable<Cita[]> {
        return this.http.get<Cita[]>(`${this.url}/medico/${medicoId}`);
    }

    create(c: Cita): Observable<Cita> {
        return this.http.post<Cita>(this.url, c);
    }

    update(id: number, c: Cita): Observable<Cita> {
        return this.http.put<Cita>(`${this.url}/${id}`, c);
    }

    cancelar(id: number): Observable<Cita> {
        return this.http.put<Cita>(`${this.url}/${id}/cancelar`, {});
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.url}/${id}`);
    }
}
