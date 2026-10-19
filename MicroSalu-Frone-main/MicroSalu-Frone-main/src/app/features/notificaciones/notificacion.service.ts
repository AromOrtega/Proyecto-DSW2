import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Notificacion } from './notificacion.model';

@Injectable({ providedIn: 'root' })
export class NotificacionService {
    private readonly url = `${environment.apiBaseUrl}/api/notificaciones`;

    constructor(private http: HttpClient) { }

    getAll(): Observable<Notificacion[]> {
        return this.http.get<Notificacion[]>(this.url);
    }

    getById(id: number): Observable<Notificacion> {
        return this.http.get<Notificacion>(`${this.url}/${id}`);
    }

    getByUser(userId: number): Observable<Notificacion[]> {
        return this.http.get<Notificacion[]>(`${this.url}/user/${userId}`);
    }
}
