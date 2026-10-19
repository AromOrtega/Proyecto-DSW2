import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { roleGuard } from './core/auth/role.guard';
import { ShellComponent } from './layout/shell.component';

export const routes: Routes = [
    { path: 'login', loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent) },
    { path: 'register-paciente', loadComponent: () => import('./features/auth/register-paciente.component').then(m => m.RegisterPacienteComponent) },
    {
        path: '',
        component: ShellComponent,
        canActivate: [authGuard],
        children: [
            // ADMIN - Médicos
            { path: 'medicos', loadComponent: () => import('./features/medicos/medico-list.component').then(m => m.MedicoListComponent), canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] } },
            { path: 'medicos/nuevo', loadComponent: () => import('./features/medicos/medico-form.component').then(m => m.MedicoFormComponent), canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] } },
            { path: 'medicos/:id/editar', loadComponent: () => import('./features/medicos/medico-form.component').then(m => m.MedicoFormComponent), canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] } },

            // ADMIN - Pacientes
            { path: 'pacientes', loadComponent: () => import('./features/pacientes/paciente-list.component').then(m => m.PacienteListComponent), canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] } },
            { path: 'pacientes/nuevo', loadComponent: () => import('./features/pacientes/paciente-form.component').then(m => m.PacienteFormComponent), canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] } },
            { path: 'pacientes/:id/editar', loadComponent: () => import('./features/pacientes/paciente-form.component').then(m => m.PacienteFormComponent), canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] } },

            // ADMIN - Medicamentos
            { path: 'medicamentos', loadComponent: () => import('./features/medicamentos/medicamento-list.component').then(m => m.MedicamentoListComponent), canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] } },
            { path: 'medicamentos/nuevo', loadComponent: () => import('./features/medicamentos/medicamento-form.component').then(m => m.MedicamentoFormComponent), canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] } },
            { path: 'medicamentos/:id/editar', loadComponent: () => import('./features/medicamentos/medicamento-form.component').then(m => m.MedicamentoFormComponent), canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] } },

            // Citas - shared route (list adapts by role)
            { path: 'citas', loadComponent: () => import('./features/citas/cita-list.component').then(m => m.CitaListComponent), canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] } },
            { path: 'mis-citas', loadComponent: () => import('./features/citas/cita-list.component').then(m => m.CitaListComponent), canActivate: [roleGuard], data: { roles: ['ROLE_MEDICO', 'ROLE_PACIENTE'] } },
            { path: 'nueva-cita', loadComponent: () => import('./features/citas/cita-form.component').then(m => m.CitaFormComponent), canActivate: [roleGuard], data: { roles: ['ROLE_PACIENTE'] } },

            // Reportes - ADMIN only
            { path: 'reportes', loadComponent: () => import('./features/reportes/reportes.component').then(m => m.ReportesComponent), canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] } },

            // Notificaciones - todos
            { path: 'notificaciones', loadComponent: () => import('./features/notificaciones/notificacion-list.component').then(m => m.NotificacionListComponent), canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] } },
            { path: 'mis-notificaciones', loadComponent: () => import('./features/notificaciones/notificacion-list.component').then(m => m.NotificacionListComponent), canActivate: [roleGuard], data: { roles: ['ROLE_MEDICO', 'ROLE_PACIENTE'] } },

            // Default redirect
            { path: '', redirectTo: 'medicos', pathMatch: 'full' },
        ]
    },
    { path: '**', redirectTo: '/login' }
];
