import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { ToastService } from '../ui/toast.service';

export const apiErrorInterceptor: HttpInterceptorFn = (req, next) => {
    const auth = inject(AuthService);
    const toast = inject(ToastService);

    return next(req).pipe(
        catchError(error => {
            const status = error?.status;

            if (status === 401) {
                toast.error('Sesión expirada. Por favor inicia sesión nuevamente.');
                auth.logout();
            } else if (status === 403) {
                toast.error('No tienes permisos para realizar esta acción.');
            } else if (status === 404) {
                toast.error('Recurso no encontrado.');
            } else if (status === 503 || status === 0) {
                toast.error('Servicio no disponible. Revisa que el API Gateway esté corriendo en http://localhost:8001');
            } else if (status >= 500) {
                toast.error('Error interno del servidor. Intenta de nuevo.');
            } else if (status >= 400) {
                const msg = error?.error?.message ?? error?.error ?? 'Error en la solicitud.';
                toast.error(typeof msg === 'string' ? msg : JSON.stringify(msg));
            }

            return throwError(() => error);
        })
    );
};
