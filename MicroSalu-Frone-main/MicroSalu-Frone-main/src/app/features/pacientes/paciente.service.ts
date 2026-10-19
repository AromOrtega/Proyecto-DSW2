import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Paciente } from './paciente.model';

@Injectable({ providedIn: 'root' })
export class PacienteService {
    private readonly url = `${environment.apiBaseUrl}/api/pacientes`;

    constructor(private http: HttpClient) { }

    getAll(): Observable<Paciente[]> {
        return this.http.get<Paciente[]>(this.url);
    }

    getById(id: number): Observable<Paciente> {
        return this.http.get<Paciente>(`${this.url}/${id}`);
    }

    getByUserId(userId: number): Observable<Paciente> {
        return this.http.get<Paciente>(`${this.url}/user/${userId}`);
    }

    create(p: Paciente): Observable<Paciente> {
        return this.http.post<Paciente>(this.url, p);
    }

    update(id: number, p: Paciente): Observable<Paciente> {
        return this.http.put<Paciente>(`${this.url}/${id}`, p);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.url}/${id}`);
    }
}
