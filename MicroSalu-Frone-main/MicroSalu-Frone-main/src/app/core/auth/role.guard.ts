import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const requiredRoles: string[] = route.data['roles'] ?? [];

    if (!auth.isLoggedIn()) return router.createUrlTree(['/login']);

    if (requiredRoles.length === 0) return true;

    const hasRole = requiredRoles.some(r => auth.hasRole(r));
    if (!hasRole) {
        // redirect to unauthorized page or home
        return router.createUrlTree(['/']);
    }
    return true;
};
