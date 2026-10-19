# 🏥 SaludVital – Guía de Pruebas con Postman

> Guía completa y paso a paso para probar todos los endpoints de la API REST del sistema **SaludVital Microservicios** usando Postman.

---

## 📋 Tabla de Contenidos

1. [Requisitos Previos](#1-requisitos-previos)
2. [Orden de Arranque de Servicios](#2-orden-de-arranque-de-servicios)
3. [Importar la Colección en Postman](#3-importar-la-colección-en-postman)
4. [Configurar Variables de la Colección](#4-configurar-variables-de-la-colección)
5. [Flujo Completo de Prueba (Paso a Paso)](#5-flujo-completo-de-prueba-paso-a-paso)
   - [Paso 1 – Registrar Admin](#paso-1--registrar-admin-o-verificar-que-existe)
   - [Paso 2 – Registrar un Médico](#paso-2--registrar-un-médico)
   - [Paso 3 – Registrar un Paciente](#paso-3--registrar-un-paciente)
   - [Paso 4 – Hacer Login y obtener el JWT](#paso-4--hacer-login-y-obtener-el-jwt)
   - [Paso 5 – Gestionar Medicamentos (Admin)](#paso-5--gestionar-medicamentos-solo-admin)
   - [Paso 6 – Crear una Cita (Paciente)](#paso-6--crear-una-cita-rol-paciente)
   - [Paso 7 – Ver Citas del Médico](#paso-7--ver-citas-asignadas-rol-médico)
   - [Paso 8 – Verificar Notificaciones en RabbitMQ](#paso-8--verificar-notificación-en-rabbitmq)
6. [Documentación de Endpoints](#6-documentación-completa-de-endpoints)
7. [Valores Válidos (Enums)](#7-valores-válidos-enums)
8. [Verificación de Salud de los Servicios](#8-verificación-de-salud-actuator)
9. [Solución de Problemas Comunes](#9-solución-de-problemas-comunes)

---

## 1. Requisitos Previos

Antes de empezar, asegúrate de tener todo lo siguiente corriendo:

| Herramienta | Versión / Detalle |
|---|---|
| **Java** | 17 o superior |
| **Maven** | 3.8+ |
| **MySQL** | 8.0 — corriendo en `localhost:3306` |
| **RabbitMQ** | Corriendo en `localhost:5672` (usuario: `guest` / password: `guest`) |
| **Postman** | Cualquier versión reciente (desktop recomendado) |

### Credenciales MySQL por defecto

```
Host:     localhost
Puerto:   3306
Usuario:  root
Password: root
Base de datos: salud  ← se crea automáticamente al arrancar
```

> ⚠️ Si tu MySQL usa contraseña diferente, cámbiala en:
> `auth-service-ws/src/main/resources/application.properties`
> y en todos los archivos de `config-server/config-repo/*.properties`

### Arquitectura – Todos los requests van por el puerto 8080

```
  POSTMAN
     │
     ▼
 :8080  API Gateway  ← ¡Siempre apunta aquí!
     │
     ├─ /api/auth/**          → Auth Service       :8083
     ├─ /api/medicos/**       → Médico Service     :8082
     ├─ /api/pacientes/**     → Paciente Service   :8086
     ├─ /api/medicamentos/**  → Medicamentos       :8081
     ├─ /api/citas/**         → Cita Service       :8010
     ├─ /api/notificaciones/**→ Notificaciones     :8091
     └─ /api/reportes/**      → Reportes           :8090
```

---

## 2. Orden de Arranque de Servicios

> ⚠️ **MUY IMPORTANTE:** Respetar este orden. Si arrancas en otro orden, los servicios no encontrarán su configuración.

```
1️⃣  Config Server      (puerto 8888)  ← PRIMERO siempre
2️⃣  Eureka Server      (puerto 8761)  ← SEGUNDO siempre
3️⃣  API Gateway        (puerto 8080)
4️⃣  Auth Service       (puerto 8083)
5️⃣  Médicos Service    (puerto 8082)
6️⃣  Pacientes Service  (puerto 8086)
7️⃣  Medicamentos       (puerto 8081)
8️⃣  Citas Service      (puerto 8010)
9️⃣  Notificaciones     (puerto 8091)
🔟  Reportes Service   (puerto 8090)
```

**Cómo arrancar cada servicio** (desde la carpeta raíz del proyecto):

```bash
# Ejemplo: arrancar Auth Service
cd auth-service-ws
mvn spring-boot:run
```

Espera ~15 segundos tras iniciar Eureka y luego verifica que todos los servicios aparezcan como `UP` en:
**http://localhost:8761**

---

## 3. Importar la Colección en Postman

**Sigue estos pasos exactamente:**

1. Abre **Postman**
2. En la barra lateral izquierda, haz clic en el botón **Import** (ícono `+` o botón "Import" en la parte superior)
3. Selecciona la pestaña **File**
4. Haz clic en **Upload Files** y navega hasta la raíz del proyecto
5. Selecciona el archivo: **`SaludVital_API.postman_collection.json`**
6. Haz clic en **Import**

✅ Verás la colección **"SaludVital – Microservicios"** aparecer en tu panel izquierdo con **6 carpetas**:
- 🔐 Auth Service
- 👨‍⚕️ Médicos Service
- 🧑 Pacientes Service
- 💊 Medicamentos Service
- 📅 Citas Service
- 🔔 Notificaciones Service

---

## 4. Configurar Variables de la Colección

La colección usa **2 variables propias** (no necesitas crear un entorno separado):

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `base_url` | `http://localhost:8080` | URL base del API Gateway |
| `token` | *(vacío)* | JWT — se llena **automáticamente** al hacer Login |

### Cómo verificar / editar las variables

1. En el panel izquierdo, haz clic derecho sobre **"SaludVital – Microservicios"**
2. Selecciona **"Edit"**
3. Ve a la pestaña **"Variables"**
4. Confirma que `base_url` = `http://localhost:8080`

> 💡 **El token se guarda solo.** El request **Login** tiene un script automático que extrae el JWT de la respuesta y lo guarda en `{{token}}`. No tienes que copiarlo manualmente.

---

## 5. Flujo Completo de Prueba (Paso a Paso)

### Paso 1 – Registrar Admin (o verificar que existe)

> Al arrancar el `auth-service-ws`, se crea un usuario admin por defecto. Puedes **saltar este paso** y usar directamente:
> - **Username:** `admin`
> - **Password:** `Admin1234!`

Si quieres crear un admin adicional, usa la carpeta **🔐 Auth Service → Registrar Paciente** y cambia el body:

**Carpeta:** `🔐 Auth Service`  
**Request:** `Registrar Paciente` (modifica el body)  
**Método:** `POST`  
**URL:** `{{base_url}}/api/auth/register`  
**Body (raw JSON):**

```json
{
  "username": "administrador",
  "email": "admin@saludvital.com",
  "password": "Admin1234!",
  "role": ["admin"]
}
```

**Respuesta esperada (200 OK):**
```json
{ "message": "¡Usuario registrado exitosamente!" }
```

---

### Paso 2 – Registrar un Médico

**Carpeta:** `🔐 Auth Service`  
**Request:** `Registrar Médico`  
**Método:** `POST`  
**URL:** `{{base_url}}/api/auth/register`  
**Body (raw JSON):**

```json
{
  "username": "dra.garcia",
  "email": "garcia@saludvital.com",
  "password": "medico123",
  "role": ["medico"],
  "nombre": "María",
  "apellido": "García",
  "numeroIdentificacion": "9876543210",
  "telefono": "3109876543",
  "direccion": "Av. Salud # 10-20, Medellín"
}
```

**Respuesta esperada (200 OK):**
```json
{ "message": "¡Usuario registrado exitosamente!" }
```

> ✅ Esto crea automáticamente el `User` (credenciales) **y** el perfil `Medico` en la base de datos.

---

### Paso 3 – Registrar un Paciente

**Carpeta:** `🔐 Auth Service`  
**Request:** `Registrar Paciente`  
**Método:** `POST`  
**URL:** `{{base_url}}/api/auth/register`  
**Body (raw JSON):**

```json
{
  "username": "juan.perez",
  "email": "juan.perez@example.com",
  "password": "password123",
  "role": ["paciente"],
  "nombre": "Juan",
  "apellido": "Pérez",
  "numeroIdentificacion": "1234567890",
  "fechaNacimiento": "1990-05-15",
  "telefono": "3001234567",
  "direccion": "Calle 123 # 45-67, Bogotá"
}
```

**Respuesta esperada (200 OK):**
```json
{ "message": "¡Usuario registrado exitosamente!" }
```

---

### Paso 4 – Hacer Login y obtener el JWT

> Este paso es obligatorio. Sin el JWT, **todos los demás endpoints devuelven 401 Unauthorized.**

**Carpeta:** `🔐 Auth Service`  
**Request:** `Login`  
**Método:** `POST`  
**URL:** `{{base_url}}/api/auth/login`  
**Body (raw JSON):**

```json
{
  "username": "juan.perez",
  "password": "password123"
}
```

**Respuesta esperada (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWF....",
  "id": 1,
  "username": "juan.perez",
  "email": "juan.perez@example.com",
  "roles": ["ROLE_PACIENTE"]
}
```

> 🎉 **¡El token se guarda automáticamente!** El script de la pestaña "Tests" del request extrae `token` del JSON y lo almacena en `{{token}}`. Todos los siguientes requests lo usan en el header `Authorization: Bearer {{token}}`.

**Para hacer login como Admin:**
```json
{
  "username": "admin",
  "password": "Admin1234!"
}
```

**Para hacer login como Médico:**
```json
{
  "username": "dra.garcia",
  "password": "medico123"
}
```

---

### Paso 5 – Gestionar Medicamentos (solo Admin)

> ⚠️ Estos endpoints requieren token de un usuario con rol **ADMIN**. Haz login como `admin` primero.

#### 5.1 – Crear un Medicamento

**Carpeta:** `💊 Medicamentos Service`  
**Request:** `Crear medicamento`  
**Método:** `POST`  
**URL:** `{{base_url}}/api/medicamentos`  
**Headers:** `Authorization: Bearer {{token}}` (automático), `Content-Type: application/json`  
**Body (raw JSON):**

```json
{
  "nombre": "Ibuprofeno 400mg",
  "descripcion": "Analgésico y antiinflamatorio no esteroideo. Dosis: 1 tableta cada 8 horas."
}
```

**Respuesta esperada (201 Created):** Medicamento creado con su `id`.

#### 5.2 – Listar todos los Medicamentos

**Request:** `Listar todos los medicamentos`  
**Método:** `GET`  
**URL:** `{{base_url}}/api/medicamentos`  

> 📌 **Anota el `id`** del medicamento creado, lo usarás en las citas.

---

### Paso 6 – Crear una Cita (rol Paciente)

> Antes de crear la cita, necesitas los IDs del **paciente** y del **médico**. Haz login como paciente o admin para obtenerlos.

#### 6.1 – Obtener ID del Paciente

**Carpeta:** `🧑 Pacientes Service`  
**Request:** `Listar todos los pacientes`  
**Método:** `GET`  
**URL:** `{{base_url}}/api/pacientes`  

Busca el paciente `juan.perez` en la respuesta y anota su `id` (normalmente `1`).

#### 6.2 – Obtener ID del Médico

**Carpeta:** `👨‍⚕️ Médicos Service`  
**Request:** `Listar todos los médicos`  
**Método:** `GET`  
**URL:** `{{base_url}}/api/medicos`  

Anota el `id` de la médica `García`.

#### 6.3 – Crear la Cita

> Haz login como **paciente** (juan.perez) antes de este paso.

**Carpeta:** `📅 Citas Service`  
**Request:** `Crear cita`  
**Método:** `POST`  
**URL:** `{{base_url}}/api/citas`  
**Body (raw JSON):**

```json
{
  "fecha": "2026-03-15",
  "hora": "09:00:00",
  "pacienteId": 1,
  "medicoId": 1,
  "motivo": "Consulta general por dolor de cabeza persistente"
}
```

| Campo | Formato | Ejemplo |
|---|---|---|
| `fecha` | `YYYY-MM-DD` | `"2026-03-15"` |
| `hora` | `HH:MM:SS` | `"09:00:00"` |
| `pacienteId` | número entero | `1` |
| `medicoId` | número entero | `1` |
| `motivo` | texto libre | `"Consulta general"` |

**Respuesta esperada (201 Created):**
```json
{
  "id": 1,
  "fecha": "2026-03-15",
  "hora": "09:00:00",
  "pacienteId": 1,
  "medicoId": 1,
  "motivo": "Consulta general por dolor de cabeza persistente",
  "estado": "PENDIENTE"
}
```

> ⚡ Al crear la cita, el servicio publica automáticamente un evento en RabbitMQ (`salud.exchange` → routing key `cita.creada`). El servicio de Notificaciones lo consume.

---

### Paso 7 – Ver Citas asignadas (rol Médico)

> Haz login como **médico** (`dra.garcia`) para este paso.

**Carpeta:** `📅 Citas Service`  
**Request:** `Listar todas las citas`  
**Método:** `GET`  
**URL:** `{{base_url}}/api/citas`  

Verás todas las citas. El médico solo debería ver las suyas (según las reglas de negocio configuradas).

Para ver una cita específica:

**Request:** `Obtener cita por ID`  
**Método:** `GET`  
**URL:** `{{base_url}}/api/citas/1`  

---

### Paso 8 – Verificar Notificación en RabbitMQ

1. Abre en el navegador: **http://localhost:15672**
2. Credenciales: `guest` / `guest`
3. Ve a la pestaña **"Queues"**
4. Busca la cola vinculada a `salud.exchange` con routing key `cita.creada`
5. Verifica que el mensaje fue consumido (la cola debe estar vacía si Notificaciones está corriendo)

#### Disparar notificación manual (opcional)

**Carpeta:** `🔔 Notificaciones Service`  
**Request:** `Enviar notificación`  
**Método:** `POST`  
**URL:** `{{base_url}}/api/notificaciones/enviar`  
**Body (raw JSON):**

```json
{
  "destinatario": "juan.perez@example.com",
  "mensaje": "Su cita médica ha sido confirmada para el 15 de marzo a las 9:00 AM.",
  "tipo": "EMAIL"
}
```

**Respuesta esperada (200 OK):**
```
"Proceso de notificacion iniciado correctamente"
```

---

## 6. Documentación Completa de Endpoints

### 🔐 Auth Service — `http://localhost:8080/api/auth`

| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| `POST` | `/api/auth/register` | No | Registrar nuevo usuario |
| `POST` | `/api/auth/login` | No | Iniciar sesión, obtener JWT |
| `POST` | `/api/auth/refresh` | JWT | Renovar token JWT |

---

### 👨‍⚕️ Médicos Service — `http://localhost:8080/api/medicos`

> Requiere rol **ADMIN** para POST, PUT, DELETE. GET disponible para todos los roles autenticados.

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/medicos` | Listar todos los médicos |
| `GET` | `/api/medicos/{id}` | Obtener médico por ID |
| `GET` | `/api/medicos/exists-licencia?numero=MED-001` | Verificar si número de licencia ya existe |
| `POST` | `/api/medicos` | Crear médico (Admin) |
| `PUT` | `/api/medicos/{id}` | Actualizar médico (Admin) |
| `DELETE` | `/api/medicos/{id}` | Eliminar médico (Admin) |

**Body para crear/actualizar médico:**
```json
{
  "nombre": "Carlos",
  "apellido": "Rodríguez",
  "numeroLicencia": "MED-001",
  "telefono": "3001112233",
  "email": "carlos.rodriguez@saludvital.com",
  "especialidad": "CARDIOLOGIA",
  "estado": "ACTIVO",
  "disponible": true,
  "tarifaConsulta": 80000.00
}
```

---

### 🧑 Pacientes Service — `http://localhost:8080/api/pacientes`

> Requiere rol **ADMIN** para POST, PUT, DELETE. GET disponible para todos los roles autenticados.

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/pacientes` | Listar todos los pacientes |
| `GET` | `/api/pacientes/{id}` | Obtener paciente por ID |
| `POST` | `/api/pacientes` | Crear paciente (Admin) |
| `PUT` | `/api/pacientes/{id}` | Actualizar paciente (Admin) |
| `DELETE` | `/api/pacientes/{id}` | Eliminar paciente (Admin) |

---

### 💊 Medicamentos Service — `http://localhost:8080/api/medicamentos`

> Requiere rol **ADMIN** para POST, PUT, DELETE.

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/medicamentos` | Listar todos los medicamentos |
| `GET` | `/api/medicamentos/options` | Lista simplificada para combos/dropdowns |
| `GET` | `/api/medicamentos/{id}` | Obtener medicamento por ID |
| `POST` | `/api/medicamentos` | Crear medicamento (Admin) |
| `PUT` | `/api/medicamentos/{id}` | Actualizar medicamento (Admin) |
| `DELETE` | `/api/medicamentos/{id}` | Eliminar medicamento (Admin) |

---

### 📅 Citas Service — `http://localhost:8080/api/citas`

> Pacientes pueden crear citas. Médicos pueden ver sus citas. Admin tiene acceso total.

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/citas` | Listar todas las citas |
| `GET` | `/api/citas/{id}` | Obtener cita por ID |
| `POST` | `/api/citas` | Crear cita |
| `PUT` | `/api/citas/{id}` | Actualizar cita |
| `DELETE` | `/api/citas/{id}` | Eliminar cita |

---

### 🔔 Notificaciones Service — `http://localhost:8080/api/notificaciones`

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/notificaciones/enviar` | Disparar notificación manual |

---

## 7. Valores Válidos (Enums)

### `especialidad` (campo del Médico)

```
MEDICINA_GENERAL    CARDIOLOGIA      DERMATOLOGIA
ENDOCRINOLOGIA      GASTROENTEROLOGIA GINECOLOGIA
NEUROLOGIA          OFTALMOLOGIA     ORTOPEDIA
PEDIATRIA           PSIQUIATRIA      RADIOLOGIA
UROLOGIA            MEDICINA_INTERNA
```

### `estado` del Médico

```
ACTIVO    INACTIVO    VACACIONES    LICENCIA
```

### `estado` de la Cita (solo en respuestas — lo asigna el sistema)

```
PENDIENTE    CONFIRMADA    CANCELADA    COMPLETADA
```

### `role` al registrar usuario

```
"admin"     → ROLE_ADMIN
"medico"    → ROLE_MEDICO
"paciente"  → ROLE_PACIENTE  (valor por defecto si no se envía role)
```

---

## 8. Verificación de Salud (Actuator)

Puedes verificar que un servicio esté vivo con peticiones GET directas (sin token):

```
GET http://localhost:8080/actuator/health    ← API Gateway
GET http://localhost:8083/actuator/health    ← Auth Service
GET http://localhost:8082/actuator/health    ← Médicos Service
GET http://localhost:8086/actuator/health    ← Pacientes Service
GET http://localhost:8081/actuator/health    ← Medicamentos
GET http://localhost:8010/actuator/health    ← Citas Service
GET http://localhost:8091/actuator/health    ← Notificaciones
GET http://localhost:8090/actuator/health    ← Reportes
GET http://localhost:8761                    ← Panel Eureka (navegador)
GET http://localhost:15672                   ← Panel RabbitMQ (navegador)
```

**Respuesta esperada:**
```json
{ "status": "UP" }
```

---

## 9. Solución de Problemas Comunes

### ❌ `401 Unauthorized` en cualquier endpoint

- Ejecuta primero el request **Login** para obtener el token.
- Verifica que `{{token}}` no esté vacío: clic derecho en la colección → Edit → Variables.
- El token expira en **24 horas**. Si expiró, haz Login de nuevo.

### ❌ `403 Forbidden`

- Estás usando un token de un rol que no tiene acceso a ese endpoint.
- Ejemplo: intentar `POST /api/medicos` con token de paciente. Necesitas token de **admin**.
- Haz login con el usuario correcto y repite la operación.

### ❌ `503 Service Unavailable` desde el Gateway

- El microservicio de destino no está corriendo o no se registró en Eureka.
- Verifica en **http://localhost:8761** que el servicio aparezca como `UP`.
- Espera ~15 segundos después de arrancar el servicio y vuelve a intentarlo.

### ❌ `500 Internal Server Error` al registrar

- MySQL no está corriendo o las credenciales son incorrectas.
- Verifica la conexión: `mysql -u root -p` desde la terminal.

### ❌ No llega notificación al crear una Cita

- RabbitMQ no está corriendo.
- Verifica en **http://localhost:15672** que el exchange `salud.exchange` exista.
- Asegúrate de que `notificaciones-service-ws` esté corriendo.

### ❌ Error `Field 'usuario_id' doesn't have a default value`

- Al crear paciente/médico via `/api/auth/register`, el campo `usuarioId` se llena automáticamente.
- Si usas el endpoint directo `POST /api/pacientes`, debes enviar `"usuarioId": <id_del_usuario>` en el body.

### ❌ Config Server no carga propiedades

- Confirma que el **Config Server** (puerto 8888) fue arrancado **antes** que todos los demás servicios.
- Sus archivos de configuración están en: `config-server/config-repo/`

---

## 💡 Usuario Admin Pre-creado

Al arrancar el `auth-service-ws` por primera vez, se crea automáticamente un usuario admin:

```
username:  admin
password:  Admin1234!
role:      ROLE_ADMIN
```

Úsalo directamente sin necesidad de registrarlo primero.

---

> 📄 Para documentación más detallada de la API consulta también: [`POSTMAN_TESTING.md`](./POSTMAN_TESTING.md)  
> 🗂️ Archivo de colección Postman: [`SaludVital_API.postman_collection.json`](./SaludVital_API.postman_collection.json)
