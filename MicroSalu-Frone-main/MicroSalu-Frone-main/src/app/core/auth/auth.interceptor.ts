import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

const PUBLIC_URLS = [
    '/api/auth/login',
    '/api/auth/register',
    '/api/auth/register-paciente'
];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const auth = inject(AuthService);
    const isPublic = PUBLIC_URLS.some(url => req.url.includes(url));

    if (!isPublic) {
        const token = auth.token();
        if (token) {
            req = req.clone({
                setHeaders: { Authorization: `Bearer ${token}` }
            });
        }
    }

    return next(req);
};
