package com.saludvital.service;

import com.saludvital.messaging.NotificacionPublisher;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);
    private final NotificacionPublisher publisher;
    private final Counter notificacionesEnviadas;

    public NotificacionService(NotificacionPublisher publisher, MeterRegistry meterRegistry) {
        this.publisher = publisher;
        this.notificacionesEnviadas = Counter.builder("notificaciones_enviadas_total")
                .description("Total de notificaciones procesadas")
                .tag("service", "notificaciones").register(meterRegistry);
    }

    public void procesarNotificacion(String destinatario, String mensaje, String tipo) {
        MDC.put("operation", "procesar-notificacion");
        MDC.put("correlationId", UUID.randomUUID().toString());

        log.info("[PROCESO] Iniciando proceso de notificacion para: {}", destinatario);
        String mensajeFormateado = String.format("[%s] %s", tipo.toUpperCase(), mensaje);

        publisher.enviarNotificacion(destinatario, mensajeFormateado, tipo);

        notificacionesEnviadas.increment();
        log.info("[PROCESO] Notificacion procesada y encolada correctamente");

        MDC.clear();
    }
}