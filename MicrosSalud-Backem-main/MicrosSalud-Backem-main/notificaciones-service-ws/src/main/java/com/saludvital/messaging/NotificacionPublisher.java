package com.saludvital.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class NotificacionPublisher {

    private static final Logger log = LoggerFactory.getLogger(NotificacionPublisher.class);
    private final RabbitTemplate rabbitTemplate;
    public static final String EXCHANGE = "salud.exchange";
    public static final String ROUTING_KEY = "notificacion.enviada";

    public NotificacionPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarNotificacion(String destinatario, String mensaje, String tipo) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("eventId", UUID.randomUUID().toString());
        evento.put("eventType", "ENVIO_NOTIFICACION");
        evento.put("timestamp", LocalDateTime.now().toString());
        evento.put("service", "notificaciones-service");
        evento.put("destinatario", destinatario);
        evento.put("mensaje", mensaje);
        evento.put("tipo", tipo);
        evento.put("correlationId", MDC.get("correlationId"));

        try {
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, evento);
            log.info("[RabbitMQ] Notificacion enviada a cola: {}", destinatario);
        } catch (Exception e) {
            log.error("[RabbitMQ] Error enviando notificacion: {}", e.getMessage());
        }
    }
}