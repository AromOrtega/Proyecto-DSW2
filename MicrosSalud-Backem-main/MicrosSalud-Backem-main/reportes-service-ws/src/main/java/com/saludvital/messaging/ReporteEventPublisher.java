package com.saludvital.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReporteEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ReporteEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange:salud.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routingKey.reporte:reporte.generado}")
    private String routingKey;

    public ReporteEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarReporteGenerado(String tipoReporte) {
        EventoReporte evento = new EventoReporte("REPORTE_GENERADO", tipoReporte, System.currentTimeMillis());
        log.info("📤 Publicando evento RabbitMQ — routingKey={} payload={}", routingKey, evento);
        rabbitTemplate.convertAndSend(exchange, routingKey, evento);
    }

    public record EventoReporte(String accion, String tipoReporte, long timestamp) {
    }
}
