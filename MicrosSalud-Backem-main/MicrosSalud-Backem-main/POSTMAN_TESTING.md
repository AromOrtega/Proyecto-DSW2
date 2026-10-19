# 🧪 Guía de Pruebas con Postman – SaludVital Microservicios

> **Archivo de colección:** `SaludVital_API.postman_collection.json`  
> Importa ese archivo directamente en Postman para tener todos los requests listos.

---

## 📋 Tabla de Contenidos

1. [Requisitos Previos](#-requisitos-previos)
2. [Arquitectura y Puertos](#-arquitectura-y-puertos)
3. [Infraestructura de Soporte](#-infraestructura-de-soporte)
4. [Importar la Colección en Postman](#-importar-la-colección-en-postman)
5. [Variables de la Colección](#-variables-de-la-colección)
6. [Orden de Arranque de Servicios](#-orden-de-arranque-de-servicios)
7. [Flujo de Prueba Recomendado](#-flujo-de-prueba-recomendado)
8. [Documentación de Endpoints](#-documentación-de-endpoints)
   - [Auth Service](#-auth-service--port-8083)
   - [Médicos Service](#-médicos-service--port-8082)
   - [Pacientes Service](#-pacientes-service--port-8086)
   - [Medicamentos Service](#-medicamentos-service--port-8081)
   - [Citas Service](#-citas-service--port-8010)
   - [Notificaciones Service](#-notificaciones-service--port-8091)
9. [Enumeraciones válidas](#-enumeraciones-válidas)
10. [Verificación de Salud](#-verificación-de-salud-actuator)
11. [Solución de Problemas](#-solución-de-problemas-comunes)

---

## ✅ Requisitos Previos

| Requisito | Versión / Detalle |
|---|---|
| **Java** | 17 o superior |
| **Maven** | 3.8+ |
| **MySQL** | 8.0+ corriendo en `localhost:3306` |
| **RabbitMQ** | Corriendo en `localhost:5672` (usuario: `guest` / contraseña: `guest`) |
| **Postman** | Cualquier versión reciente |

### Credenciales MySQL por defecto
```
Host:     localhost
Puerto:   3306
Usuario:  root
Password: root
Base de datos: salud (se crea automáticamente si no existe)
```

> ⚠️ Si tu MySQL tiene contraseña diferente, cámbiala en:  
> - `auth-service-ws/src/main/resources/application.properties`  
> - `config-server/config-repo/*.properties` (todos los servicios)

---

## 🗺️ Arquitectura y Puertos

Todos los requests de Postman deben apuntar al **API Gateway** en el puerto `8080`.  
El gateway enruta automáticamente a cada microservicio interno.

```
                         ┌─────────────────────────────────────────────────────┐
                         │              POSTMAN / CLIENTE                      │
                         └────────────────────────┬────────────────────────────┘
                                                  │
                                          :8080 (API Gateway)
                                                  │
                         ┌────────────────────────▼────────────────────────────┐
                         │              API GATEWAY                             │
                         │         Spring Cloud Gateway                         │
                         └──┬─────┬──────┬──────┬──────┬──────┬──────┬────────┘
                            │     │      │      │      │      │      │
                        /api/auth  /api/medicos  /api/pacientes  /api/medicamentos
                            │     │      │      │      │      │      │
                       :8083 │  :8082   :8086  :8081  :8010  :8091  :8090
                             │     │      │      │      │      │      │
                           Auth  Médicos Pacs  Meds  Citas Notif  Reportes
```

### Tabla de puertos completa

| Servicio | Nombre interno (Eureka) | Puerto | Ruta Gateway |
|---|---|---|---|
| **Config Server** | `config-server-ws` | **8888** | *(no expuesto en gateway)* |
| **Eureka Server** | `eureka-server` | **8761** | *(no expuesto en gateway)* |
| **API Gateway** | `api-gateway` | **8080** | ← **Siempre usar este** |
| **Auth Service** | `auth-service-ws` | **8083** | `/api/auth/**` |
| **Médicos Service** | `medico-service-ws` | **8082** | `/api/medicos/**` |
| **Pacientes Service** | `paciente-service-ws` | **8086** | `/api/pacientes/**` |
| **Medicamentos Service** | `gestion-medicamentos-ws` | **8081** | `/api/medicamentos/**` |
| **Citas Service** | `cita-service-ws` | **8010** | `/api/citas/**` |
| **Notificaciones Service** | `notificaciones-service-ws` | **8091** | `/api/notificaciones/**` |
| **Reportes Service** | `reportes-service-ws` | **8090** | `/api/reportes/**` |

---

## 🔧 Infraestructura de Soporte

| Servicio | Puerto | URL de acceso | Descripción |
|---|---|---|---|
| **MySQL** | 3306 | `localhost:3306/salud` | Base de datos principal |
| **RabbitMQ Management** | 15672 | http://localhost:15672 | Consola web de RabbitMQ (guest/guest) |
| **RabbitMQ AMQP** | 5672 | `localhost:5672` | Protocolo de mensajería |
| **Eureka Dashboard** | 8761 | http://localhost:8761 | Registro de servicios |
| **Config Server** | 8888 | http://localhost:8888 | Configuración centralizada |
| **Prometheus** | 9090 | http://localhost:9090 | Métricas |
| **Grafana** | 3000 | http://localhost:3000 | Dashboards de métricas |

### Exchanges y Routing Keys de RabbitMQ

| Exchange | Routing Key | Productor | Consumidor |
|---|---|---|---|
| `salud.exchange` | `medico.evento` | `medico-service-ws` | `notificaciones-service-ws` |
| `salud.exchange` | `paciente.evento` | `paciente-service-ws` | `notificaciones-service-ws` |
| `salud.exchange` | `cita.creada` | `cita-service-ws` | `notificaciones-service-ws` |
| `salud.exchange` | `medicamento.evento` | `gestion-medicamentos-ws` | `notificaciones-service-ws` |

---

## 📦 Importar la Colección en Postman

1. Abre **Postman**
2. Haz clic en **Import** (esquina superior izquierda)
3. Selecciona el archivo **`SaludVital_API.postman_collection.json`** (en la raíz del proyecto)
4. La colección aparecerá en el panel izquierdo con 6 carpetas

### Variables de la Colección

La colección usa **variables de nivel colección** (no necesitas un entorno separado):

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `base_url` | `http://localhost:8080` | URL base — API Gateway |
| `token` | *(vacío)* | JWT — se llena automáticamente al hacer Login |

> 💡 El request **Login** tiene un **script de Test** que extrae el JWT y lo guarda en `{{token}}` automáticamente. No necesitas copiarlo a mano.

---

## 🚦 Orden de Arranque de Servicios

**Respeta este orden. Si arrancas en otro orden, los servicios no encontrarán sus configuraciones.**

```
1️⃣  Config Server     (puerto 8888) — debe ser el primero
2️⃣  Eureka Server     (puerto 8761) — debe ser el segundo
3️⃣  API Gateway       (puerto 8080)
4️⃣  Auth Service      (puerto 8083)
5️⃣  Médicos Service   (puerto 8082)
6️⃣  Pacientes Service (puerto 8086)
7️⃣  Medicamentos      (puerto 8081)
8️⃣  Citas Service     (puerto 8010)
9️⃣  Notificaciones    (puerto 8091)
🔟  Reportes Service  (puerto 8090)
```

Espera ~10 segundos entre el inicio de Eureka y el resto de los servicios.  
Verifica que todos estén registrados en: http://localhost:8761

---

## 🔄 Flujo de Prueba Recomendado

### Paso 1 – Registrar un Paciente

```
POST http://localhost:8080/api/auth/register
```
Registra un usuario con rol `paciente`. Esto crea tanto el `User` (credenciales) como el `Paciente` (perfil) automáticamente.

### Paso 2 – Registrar un Médico

```
POST http://localhost:8080/api/auth/register
```
Usa `"role": ["medico"]`. Crea el `User` + el perfil `Medico` con estado `ACTIVO`.

### Paso 3 – Login y obtener JWT

```
POST http://localhost:8080/api/auth/login
```
El script de Postman captura el `token` del response y lo guarda en `{{token}}`. Desde este punto, **todos los requests usan el header `Authorization: Bearer {{token}}`** automáticamente.

### Paso 4 – Consultar datos creados

```
GET http://localhost:8080/api/pacientes       → Copiar el ID del paciente creado
GET http://localhost:8080/api/medicos         → Copiar el ID del médico creado
GET http://localhost:8080/api/medicamentos    → Ver medicamentos disponibles
```

### Paso 5 – Crear una Cita

```
POST http://localhost:8080/api/citas
```
Usa los IDs del paciente y médico obtenidos en el paso anterior.

### Paso 6 – Verificar Notificación (RabbitMQ)

Al crear la cita, el `cita-service` publica un evento al exchange `salud.exchange` con routing key `cita.creada`. El `notificaciones-service` lo consume y procesa. Verifica en la consola de RabbitMQ: http://localhost:15672

---

## 📚 Documentación de Endpoints

### 🔐 Auth Service – Port 8083

**Base path en gateway:** `http://localhost:8080/api/auth`

---

#### `POST /api/auth/register` — Registrar usuario

**Body (Paciente):**
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

**Body (Médico):**
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

**Body (Admin):**
```json
{
  "username": "administrador",
  "email": "admin@saludvital.com",
  "password": "Admin1234!",
  "role": ["admin"]
}
```

> ℹ️ Si no se envía el campo `role`, se asigna `ROLE_PACIENTE` por defecto.

**Respuesta exitosa (200):**
```json
{ "message": "¡Usuario registrado exitosamente!" }
```

**Respuestas de error:**
| Código | Causa |
|---|---|
| 400 | Username ya en uso |
| 400 | Email ya registrado |

---

#### `POST /api/auth/login` — Iniciar sesión

**Body:**
```json
{
  "username": "juan.perez",
  "password": "password123"
}
```

**Respuesta exitosa (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "id": 1,
  "username": "juan.perez",
  "email": "juan.perez@example.com",
  "roles": ["ROLE_PACIENTE"]
}
```

> ✅ El script de Postman guarda `token` en `{{token}}` automáticamente.

**Usuario admin pre-cargado:**
```
username: admin
password: Admin1234!
```
*(Creado por el DataInitializer al arrancar el Auth Service)*

---

#### `POST /api/auth/refresh` — Renovar token

**Header:**
```
Authorization: Bearer {{token}}
```
*(No requiere body)*

**Respuesta exitosa (200):** Devuelve un nuevo `JwtResponse` con un JWT renovado.  
**Respuesta de error (400):** `Token inválido, expirado o no proporcionado`

---

### 👨‍⚕️ Médicos Service – Port 8082

**Base path en gateway:** `http://localhost:8080/api/medicos`

> 🔒 **Control de acceso:**
> | Método | Requiere |
> |---|---|
> | `GET` | Público (sin token) |
> | `POST` | 🔴 Solo `ROLE_ADMIN` |
> | `PUT` | 🔴 Solo `ROLE_ADMIN` |
> | `DELETE` | 🔴 Solo `ROLE_ADMIN` |

---

#### `GET /api/medicos` — Listar todos los médicos

**Sin header de autorización requerido.**

**Respuesta (200):** Array de objetos `Medico`.

```json
[
  {
    "id": 1,
    "nombre": "Carlos",
    "apellido": "Ramírez",
    "numeroLicencia": "MED-001",
    "telefono": "987654321",
    "email": "dr.carlos@saludvital.com",
    "especialidad": "CARDIOLOGIA",
    "estado": "ACTIVO",
    "disponible": true,
    "tarifaConsulta": 150.00,
    "userId": 3,
    "horarios": []
  }
]
```

---

#### `GET /api/medicos/{id}` — Obtener médico por ID

**Ejemplo:** `GET /api/medicos/1`  
**Respuesta (200):** Objeto `Medico` completo.  
**Respuesta (404):** Not Found.

---

#### `GET /api/medicos/exists-licencia?numero=MED-001` — Verificar licencia

| Query Param | Tipo | Requerido | Descripción |
|---|---|---|---|
| `numero` | String | ✅ | Número de licencia a verificar |
| `id` | Long | ❌ | Si se envía, excluye este médico (útil al editar) |

**Respuesta:** `true` si ya existe, `false` si está libre.

---

#### `POST /api/medicos` — Registrar médico ⭐ NUEVO

> 🔴 **Requiere rol ADMIN**  
> Header: `Authorization: Bearer {{token_admin}}`

Esta operación crea **en una sola transacción**:
1. Un registro en la tabla `users` (con password encriptado BCrypt y rol `ROLE_MEDICO`)
2. Un registro en la tabla `medicos` (con `user_id` apuntando al usuario creado)
3. Publica evento `"CREADO"` en RabbitMQ

**Body:**
```json
{
  "nombre": "Carlos",
  "apellido": "Ramírez",
  "numeroLicencia": "MED-001",
  "telefono": "987654321",
  "email": "dr.carlos@saludvital.com",
  "especialidad": "CARDIOLOGIA",
  "estado": "ACTIVO",
  "disponible": true,
  "tarifaConsulta": 150.00,
  "usuario": {
    "username": "dr.carlos",
    "password": "123456"
  }
}
```

| Campo | Tipo | Obligatorio | Descripción |
|---|---|---|---|
| `nombre` | String | ✅ | Nombre del médico |
| `apellido` | String | ✅ | Apellido del médico |
| `numeroLicencia` | String | ✅ | Debe ser **único** en la BD |
| `telefono` | String | ❌ | Teléfono de contacto |
| `email` | String | ✅ | Debe ser **único** en la BD |
| `especialidad` | Enum | ✅ | Ver valores en [Enumeraciones](#-enumeraciones-válidas) |
| `estado` | Enum | ✅ | `ACTIVO`, `INACTIVO`, `VACACIONES`, `LICENCIA` |
| `disponible` | Boolean | ❌ | `true` o `false` |
| `tarifaConsulta` | Decimal | ❌ | Valor en moneda local |
| `usuario.username` | String | ✅ | Nombre de usuario único (máx. 20 chars) |
| `usuario.password` | String | ✅ | Contraseña (se guarda encriptada con BCrypt) |

**Respuesta exitosa (200):** Objeto `Medico` creado con su `id` y `userId`.

```json
{
  "id": 1,
  "nombre": "Carlos",
  "apellido": "Ramírez",
  "numeroLicencia": "MED-001",
  "telefono": "987654321",
  "email": "dr.carlos@saludvital.com",
  "especialidad": "CARDIOLOGIA",
  "estado": "ACTIVO",
  "disponible": true,
  "tarifaConsulta": 150.00,
  "userId": 3,
  "horarios": []
}
```

**Errores posibles:**

| Código | Causa | Mensaje |
|---|---|---|
| `401` | Sin token o token inválido | `Unauthorized` |
| `403` | Token de PACIENTE o MEDICO | `Forbidden` |
| `400` | Username ya en uso | `"El nombre de usuario 'X' ya está en uso"` |
| `400` | Email ya registrado | `"El email 'X' ya está registrado"` |
| `400` | Licencia ya registrada | `"El número de licencia 'X' ya está registrado"` |
| `400` | Campos obligatorios vacíos | `"El username y la contraseña del usuario son obligatorios"` |

**Flujo de prueba completo:**
```
1. POST /api/auth/login  (con usuario admin)       → Obtener token
2. POST /api/medicos     (Header: Bearer {{token}}) → Crear médico + usuario
3. GET  /api/medicos     (sin token)                → Ver médico creado
4. POST /api/auth/login  (con dr.carlos / 123456)  → Verificar que el user funciona
```

---

#### `PUT /api/medicos/{id}` — Actualizar médico

> 🔴 **Requiere rol ADMIN**  
> Header: `Authorization: Bearer {{token_admin}}`

Misma estructura que POST (campos opcionales, solo se actualiza lo que se envía).

```json
{
  "telefono": "999888777",
  "estado": "VACACIONES",
  "disponible": false,
  "tarifaConsulta": 200.00
}
```

**Respuesta (200):** Objeto `Medico` actualizado.  
**Respuesta (404):** Not Found.

---

#### `DELETE /api/medicos/{id}` — Eliminar médico

> 🔴 **Requiere rol ADMIN**  
> Header: `Authorization: Bearer {{token_admin}}`

**Respuesta (200):** OK.  
**Respuesta (404):** Not Found.

---



### 🧑 Pacientes Service – Port 8086

**Base path en gateway:** `http://localhost:8080/api/pacientes`  
**Header requerido en todos:** `Authorization: Bearer {{token}}`

---

#### `GET /api/pacientes` — Listar todos los pacientes

**Respuesta:** Array de `PacienteDTO`.

---

#### `GET /api/pacientes/{id}` — Obtener paciente por ID

**Respuesta (200):** Objeto `PacienteDTO`.  
**Respuesta (404):** Not Found.

---

#### `POST /api/pacientes` — Crear paciente

```json
{
  "nombre": "Laura",
  "apellido": "Martínez",
  "numeroIdentificacion": "5555666677",
  "fechaNacimiento": "1995-08-20",
  "telefono": "3204445566",
  "direccion": "Carrera 50 # 12-34, Cali",
  "usuarioId": 1
}
```

> ℹ️ Normalmente el paciente se crea automáticamente al registrar el usuario. Usa este endpoint para casos manuales o de prueba.

---

#### `PUT /api/pacientes/{id}` — Actualizar paciente

Misma estructura que POST.

---

#### `DELETE /api/pacientes/{id}` — Eliminar paciente

**Respuesta (204):** No Content.  
**Respuesta (404):** Not Found.

---

### 💊 Medicamentos Service – Port 8081

**Base path en gateway:** `http://localhost:8080/api/medicamentos`  
**Header requerido en todos:** `Authorization: Bearer {{token}}`

---

#### `GET /api/medicamentos` — Listar todos los medicamentos

**Respuesta:** Array de `Medicamento` (con `id`, `nombre`, `descripcion`).

---

#### `GET /api/medicamentos/options` — Lista para combos/dropdowns

**Respuesta:** Array simplificado `[{"id": 1, "label": "Ibuprofeno 400mg"}, ...]`

---

#### `GET /api/medicamentos/{id}` — Obtener medicamento por ID

---

#### `POST /api/medicamentos` — Crear medicamento

```json
{
  "nombre": "Ibuprofeno 400mg",
  "descripcion": "Analgésico y antiinflamatorio no esteroideo. Dosis: 1 tableta cada 8 horas."
}
```

**Respuesta (201):** `Medicamento` creado con `Location: /api/medicamentos/{id}`.

---

#### `PUT /api/medicamentos/{id}` — Actualizar medicamento

```json
{
  "nombre": "Ibuprofeno 600mg",
  "descripcion": "Dosis revisada: 1 tableta cada 12 horas con alimentos."
}
```

---

#### `DELETE /api/medicamentos/{id}` — Eliminar medicamento

**Respuesta (204):** No Content.

---

### 📅 Citas Service – Port 8010

**Base path en gateway:** `http://localhost:8080/api/citas`  
**Header requerido en todos:** `Authorization: Bearer {{token}}`

> ⚡ Al crear o actualizar una cita, se publica un evento en RabbitMQ:  
> Exchange `salud.exchange` → Routing Key `cita.creada`

---

#### `GET /api/citas` — Listar todas las citas

**Respuesta:** Array de `CitaDTO`.

---

#### `GET /api/citas/{id}` — Obtener cita por ID

---

#### `POST /api/citas` — Crear cita

```json
{
  "fecha": "2026-03-15",
  "hora": "09:00:00",
  "pacienteId": 1,
  "medicoId": 1,
  "motivo": "Consulta general por dolor de cabeza persistente"
}
```

| Campo | Tipo | Obligatorio | Formato |
|---|---|---|---|
| `fecha` | LocalDate | ✅ | `YYYY-MM-DD` |
| `hora` | LocalTime | ✅ | `HH:MM:SS` |
| `pacienteId` | Long | ✅ | ID existente en BD |
| `medicoId` | Long | ✅ | ID existente en BD |
| `motivo` | String | ✅ | Texto no vacío |

**Respuesta (201):** `CitaDTO` creado con `Location: /api/citas/{id}`.

---

#### `PUT /api/citas/{id}` — Actualizar cita

Misma estructura que POST (todos los campos requeridos también en actualización).

---

#### `DELETE /api/citas/{id}` — Eliminar cita

**Respuesta (204):** No Content.

---

### 🔔 Notificaciones Service – Port 8091

**Base path en gateway:** `http://localhost:8080/api/notificaciones`  
**Header requerido:** `Authorization: Bearer {{token}}`

> ℹ️ Este servicio recibe eventos de RabbitMQ automáticamente. Usa este endpoint para disparar notificaciones manuales (pruebas).

---

#### `POST /api/notificaciones/enviar` — Enviar notificación manual

```json
{
  "destinatario": "juan.perez@example.com",
  "mensaje": "Su cita médica ha sido confirmada para el 15 de marzo a las 9:00 AM.",
  "tipo": "EMAIL"
}
```

| Campo | Tipo | Descripción |
|---|---|---|
| `destinatario` | String | Email o número del destinatario |
| `mensaje` | String | Contenido del mensaje |
| `tipo` | String | Tipo de notificación |

**Respuesta (200):** `"Proceso de notificacion iniciado correctamente"`

---

## 🔢 Enumeraciones Válidas

### `Especialidad` (campo en Médico)

```
MEDICINA_GENERAL
CARDIOLOGIA
DERMATOLOGIA
ENDOCRINOLOGIA
GASTROENTEROLOGIA
GINECOLOGIA
NEUROLOGIA
OFTALMOLOGIA
ORTOPEDIA
PEDIATRIA
PSIQUIATRIA
RADIOLOGIA
UROLOGIA
MEDICINA_INTERNA
```

### `EstadoDoctor` (campo `estado` en Médico)

```
ACTIVO
INACTIVO
VACACIONES
LICENCIA
```

### `EstadoCita` (campo `estado` en Cita — solo en respuestas)

```
PENDIENTE
CONFIRMADA
CANCELADA
COMPLETADA
```

### `ERole` (campo `role` en registro)

```
paciente   → ROLE_PACIENTE
medico     → ROLE_MEDICO
admin      → ROLE_ADMIN
```

---

## 💓 Verificación de Salud (Actuator)

Todos los microservicios exponen endpoints de Actuator. Úsalos para verificar que un servicio está vivo:

```
GET http://localhost:{puerto}/actuator/health
GET http://localhost:{puerto}/actuator/info
GET http://localhost:{puerto}/actuator/prometheus   ← métricas para Grafana
```

### Ejemplos rápidos

```
GET http://localhost:8080/actuator/health    ← API Gateway
GET http://localhost:8083/actuator/health    ← Auth Service (directo, sin gateway)
GET http://localhost:8082/actuator/health    ← Médicos Service (directo)
GET http://localhost:8086/actuator/health    ← Pacientes Service (directo)
GET http://localhost:8081/actuator/health    ← Medicamentos (directo)
GET http://localhost:8010/actuator/health    ← Citas Service (directo)
GET http://localhost:8091/actuator/health    ← Notificaciones (directo)
GET http://localhost:8090/actuator/health    ← Reportes (directo)
```

---

## ❌ Solución de Problemas Comunes

### El request devuelve `401 Unauthorized`

- Ejecuta el request **Login** primero para obtener el token.
- Verifica que `{{token}}` no esté vacío en las **Collection Variables**.
- El token JWT tiene 24 horas de expiración (`jwt.expiration=86400000` ms).

### El request devuelve `503 Service Unavailable` desde el Gateway

- El microservicio de destino no está corriendo.
- El microservicio no se registró en Eureka todavía (espera ~15 seg).
- Verifica en http://localhost:8761 que el servicio aparezca como `UP`.

### El registro devuelve `500 Internal Server Error`

- MySQL no está corriendo o las credenciales son incorrectas.
- Verifica que la base de datos `salud` sea accesible.

### No llegan mensajes a Notificaciones tras crear una Cita

- RabbitMQ no está corriendo.
- Verifica en http://localhost:15672 que el exchange `salud.exchange` exista.
- Revisa los logs del `notificaciones-service-ws` para mensajes `ConditionalOnMissingBean` o errores de conexión.

### Error `Field 'usuario_id' doesn't have a default value`

- Asegúrate de enviar `usuarioId` en el body al crear paciente/médico manualmente.
- Al registrar via `/api/auth/register`, este campo se llena automáticamente.

### Config Server no carga las propiedades

- Verifica que el **Config Server** (puerto 8888) esté arrancado **antes** que los microservicios.
- Los archivos de configuración están en: `config-server/config-repo/`
