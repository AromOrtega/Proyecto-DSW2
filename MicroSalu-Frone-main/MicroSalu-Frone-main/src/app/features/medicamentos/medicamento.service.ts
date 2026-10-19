import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Medicamento } from './medicamento.model';

@Injectable({ providedIn: 'root' })
export class MedicamentoService {
    private readonly url = `${environment.apiBaseUrl}/api/medicamentos`;

    constructor(private http: HttpClient) { }

    getAll(): Observable<Medicamento[]> {
        return this.http.get<Medicamento[]>(this.url);
    }

    getById(id: number): Observable<Medicamento> {
        return this.http.get<Medicamento>(`${this.url}/${id}`);
    }

    search(nombre: string): Observable<Medicamento[]> {
        const params = new HttpParams().set('nombre', nombre);
        return this.http.get<Medicamento[]>(`${this.url}/search`, { params });
    }

    create(m: Medicamento): Observable<Medicamento> {
        return this.http.post<Medicamento>(this.url, m);
    }

    update(id: number, m: Medicamento): Observable<Medicamento> {
        return this.http.put<Medicamento>(`${this.url}/${id}`, m);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.url}/${id}`);
    }
}
