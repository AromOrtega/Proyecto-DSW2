package com.saludvital.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CitaEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(CitaEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange:salud.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routingKey.cita:cita.creada}")
    private String routingKey;

    public CitaEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarCitaCreada(Long citaId, Long pacienteId, Long medicoId) {
        EventoCita evento = new EventoCita("CITA_CREADA", citaId, pacienteId, medicoId, System.currentTimeMillis());
        log.info("📤 Publicando evento RabbitMQ — routingKey={} payload={}", routingKey, evento);
        rabbitTemplate.convertAndSend(exchange, routingKey, evento);
    }

    public record EventoCita(String accion, Long citaId, Long pacienteId, Long medicoId, long timestamp) {
    }
}
