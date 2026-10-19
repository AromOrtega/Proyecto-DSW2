# 🏥 SaludVital — Guía de Inicio (Sin Docker)

## Prerequisitos locales
| Servicio | Puerto | Cómo instalar |
|---|---|---|
| MySQL 8 | 3306 | https://dev.mysql.com/downloads/installer/ |
| RabbitMQ | 5672 / 15672 | https://www.rabbitmq.com/docs/install-windows |
| Prometheus | 9090 | https://prometheus.io/download/ |
| Grafana | 3000 | https://grafana.com/grafana/download |

---

## 1. Preparar la BD MySQL
```sql
CREATE DATABASE salud CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 2. Configurar contraseña MySQL
Edita **todos** los `config-repo/*.properties` y el `auth-service-ws/application.properties`,
cambia `spring.datasource.password=root` a tu contraseña real.

## 3. Arrancar Prometheus
```powershell
cd C:\ruta\prometheus
.\prometheus.exe --config.file="C:\Users\Andrea PC\Desktop\proyect-agente\Micros-salud\monitoring\prometheus.yml"
```

## 4. Arrancar Grafana
```powershell
cd C:\ruta\grafana
.\bin\grafana-server.exe
# Abrir: http://localhost:3000   usuario: admin   password: admin
```
Agregar datasource manualmente: http://localhost:9090 (Prometheus)

## 5. Orden de arranque de microservicios
```
Desde cada carpeta: mvn spring-boot:run
```

| Orden | Servicio | Puerto | Carpeta |
|---|---|---|---|
| 1 | eureka-server | 8761 | eureka-server |
| 2 | config-server-ws | 8888 | config-server |
| 3 | auth-service-ws | 8083 | auth-service-ws |
| 4 | medico-service-ws | 8082 | medico-service-ws |
| 5 | paciente-service-ws | 8086 | paciente-service-ws |
| 6 | gestion-medicamentos-ws | 8081 | gestion-medicamentos-ws |
| 7 | notificaciones-service-ws | 8091 | notificaciones-service-ws |
| 8 | cita-service-ws | 8010 | cita-service-ws |
| 9 | reportes-service-ws | 8090 | reportes-service-ws |
| 10 | api-gateway | 8001 | api-gateway |

## 6. Verificar
```powershell
# Eureka dashboard
Start-Process "http://localhost:8761"

# Config server entrega config de medico
Invoke-RestMethod "http://localhost:8888/medico-service-ws/default"

# Login admin
Invoke-RestMethod -Method POST -Uri "http://localhost:8083/api/auth/login" `
  -ContentType "application/json" `
  -Body '{"username":"admin","password":"Admin1234!"}'

# RabbitMQ Management UI
Start-Process "http://localhost:15672"  # guest / guest

# Prometheus targets
Start-Process "http://localhost:9090/targets"

# Grafana
Start-Process "http://localhost:3000"
```

## 7. Credenciales Admin por defecto
| | |
|---|---|
| **username** | admin |
| **password** | Admin1234! |
| **email** | admin@saludvital.com |

> El `DataInitializer` en `auth-service-ws` crea el usuario automáticamente al iniciar.
