package com.saludvital.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MedicamentoEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(MedicamentoEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange:salud.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routingKey.medicamento:medicamento.evento}")
    private String routingKey;

    public MedicamentoEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarEvento(String accion, Long medicamentoId) {
        EventoMedicamento evento = new EventoMedicamento(accion, medicamentoId, System.currentTimeMillis());
        log.info("📤 Publicando evento RabbitMQ — routingKey={} payload={}", routingKey, evento);
        rabbitTemplate.convertAndSend(exchange, routingKey, evento);
    }

    public record EventoMedicamento(String accion, Long medicamentoId, long timestamp) {
    }
}
