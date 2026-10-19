# SaludVital — Frontend Angular

> Plataforma de Gestión Médica (Microservicios)  
> Angular 17+ · Standalone Components · SCSS · JWT+Roles

---

## 🚀 Inicio rápido

### Prerrequisitos
- Node.js 18+ y npm
- API Gateway corriendo en **http://localhost:8001**

### Instalación y ejecución

```bash
cd saludvital-web
npm install
npm start          # o: ng serve
```

Abre tu navegador en: **http://localhost:4200**

---

## 🔐 Cómo iniciar sesión

| Usuario       | Contraseña     | Rol      |
|---------------|----------------|----------|
| `admin`       | `Admin1234!`   | ADMIN    |
| *(tu médico)* | *(su pass)*    | MÉDICO   |
| *(paciente)*  | *(su pass)*    | PACIENTE |

> El frontend envía `POST http://localhost:8001/api/auth/login`
> y guarda el JWT en **localStorage** con clave `sv_token`.

---

## 🎭 Cómo probar cada rol desde la UI

### ADMIN
1. Login con `admin / Admin1234!`
2. Menú disponible: **Médicos · Pacientes · Medicamentos · Citas · Reportes · Notificaciones**
3. CRUD completo en todas las secciones
4. En **Reportes**: haz clic en cualquier botón para ver el reporte en tabla

### MÉDICO
1. Login con las credenciales de un médico registrado
2. Menú disponible: **Mis Citas · Notificaciones**
3. Ve las citas asignadas a tu ID de médico
4. Puedes cancelar citas

### PACIENTE
1. Login con credenciales de paciente (o regístrate en `/register-paciente`)
2. Menú disponible: **Mis Citas · Nueva Cita · Notificaciones**
3. Agenda nuevas citas seleccionando médico + fecha/hora + motivo
4. Cancela tus propias citas

---

## 🌐 URL base del API Gateway

**Todas** las peticiones HTTP van exclusivamente a:

```
http://localhost:8001
```

Configurado en `src/environments/environment.ts`:

```ts
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8001'
};
```

---

## 📁 Estructura del proyecto

```
src/
  environments/environment.ts       ← BASE_URL del Gateway
  app/
    core/
      auth/                         ← auth.service, token.util, interceptor, guards
      http/                         ← api-error.handler (interceptor global de errores)
      ui/                           ← toast.service, confirm.service + componentes
    layout/                         ← shell.component, navbar.component
    features/
      auth/                         ← login, register-paciente
      medicos/                      ← model, service, list, form
      pacientes/                    ← model, service, list, form
      medicamentos/                 ← model, service, list, form
      citas/                        ← model, service, list, form
      notificaciones/               ← model, service, list
      reportes/                     ← service, component
    app.routes.ts                   ← Rutas con lazy loading + guards
    app.config.ts                   ← provideRouter + provideHttpClient + interceptors
    app.component.ts                ← Raíz
```

---

## 🔒 Seguridad

- **AuthInterceptor**: adjunta `Authorization: Bearer <token>` a todas las rutas excepto `/api/auth/login`, `/api/auth/register`, `/api/auth/register-paciente`
- **AuthGuard**: redirige a `/login` si no hay token válido
- **RoleGuard**: bloquea rutas según el campo `roles` en `data` de la ruta:
  - `ROLE_ADMIN`, `ROLE_MEDICO`, `ROLE_PACIENTE`

---

## ⚠️ Manejo de errores HTTP

| Código | Mensaje mostrado |
|--------|-----------------|
| 401 | Sesión expirada → logout automático |
| 403 | No tienes permisos |
| 503 / 0 | Servicio no disponible. Revisa Gateway |
| 5xx | Error interno del servidor |

---

## 📋 Endpoints consumidos

| Módulo | Método | Ruta Gateway |
|--------|--------|-------------|
| Auth | POST | `/api/auth/login` |
| Auth | POST | `/api/auth/register-paciente` |
| Médicos | GET/POST/PUT/DELETE | `/api/medicos[/{id}]` |
| Pacientes | GET/POST/PUT/DELETE | `/api/pacientes[/{id}]` |
| Medicamentos | GET/POST/PUT/DELETE | `/api/medicamentos[/{id}]` |
| Citas | GET/POST/PUT/DELETE | `/api/citas[/{id}]` |
| Notificaciones | GET | `/api/notificaciones[/user/{userId}]` |
| Reportes | GET | `/api/reportes/{citas\|medicos\|pacientes\|medicamentos}` |

---

## 🛠️ Resolución automática de IDs

Al iniciar sesión, el frontend automáticamente:

1. Decodifica el JWT para obtener `username` y `roles`
2. Si el token incluye `userId`, lo guarda en localStorage
3. Para **PACIENTE**: llama a `GET /api/pacientes/user/{userId}` y guarda el `pacienteId`
4. Para **MÉDICO**: usa `medicoId` guardado en localStorage (`sv_medicoId`)  
   *(si no está disponible, el administrador debe configurarlo)*

---

*Construido con Angular 17 · TypeScript strict · SCSS · Signals*
