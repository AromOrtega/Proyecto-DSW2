import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Medico } from './medico.model';

@Injectable({ providedIn: 'root' })
export class MedicoService {
    private readonly url = `${environment.apiBaseUrl}/api/medicos`;

    constructor(private http: HttpClient) { }

    getAll(): Observable<Medico[]> {
        return this.http.get<Medico[]>(this.url);
    }

    getById(id: number): Observable<Medico> {
        return this.http.get<Medico>(`${this.url}/${id}`);
    }

    create(m: Medico): Observable<Medico> {
        return this.http.post<Medico>(this.url, m);
    }

    update(id: number, m: Medico): Observable<Medico> {
        return this.http.put<Medico>(`${this.url}/${id}`, m);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.url}/${id}`);
    }
}
