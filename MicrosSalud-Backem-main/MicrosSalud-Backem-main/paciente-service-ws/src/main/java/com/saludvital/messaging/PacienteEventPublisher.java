package com.saludvital.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publisher de eventos de paciente a RabbitMQ.
 * Solo PUBLICA mensajes al exchange, no consume.
 */
@Component
public class PacienteEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PacienteEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange:salud.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routingKey.paciente:paciente.evento}")
    private String routingKey;

    public PacienteEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarEvento(String accion, Long pacienteId) {
        EventoPaciente evento = new EventoPaciente(accion, pacienteId, System.currentTimeMillis());
        log.info("📤 Publicando evento RabbitMQ — exchange={} routingKey={} payload={}", exchange, routingKey, evento);
        rabbitTemplate.convertAndSend(exchange, routingKey, evento);
    }

    public record EventoPaciente(String accion, Long pacienteId, long timestamp) {
    }
}
