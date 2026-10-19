package com.saludvital.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publisher de eventos de médico a RabbitMQ.
 * Solo PUBLICA mensajes al exchange, no consume.
 */
@Component
public class MedicoEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(MedicoEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange:salud.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routingKey.medico:medico.evento}")
    private String routingKey;

    public MedicoEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarEvento(String accion, Long medicoId) {
        EventoMedico evento = new EventoMedico(accion, medicoId, System.currentTimeMillis());
        log.info("📤 Publicando evento RabbitMQ — exchange={} routingKey={} payload={}", exchange, routingKey, evento);
        rabbitTemplate.convertAndSend(exchange, routingKey, evento);
    }

    public record EventoMedico(String accion, Long medicoId, long timestamp) {
    }
}
