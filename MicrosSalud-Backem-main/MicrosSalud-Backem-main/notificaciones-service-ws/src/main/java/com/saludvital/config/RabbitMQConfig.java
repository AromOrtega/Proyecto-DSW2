package com.saludvital.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "salud.exchange";

    @Value("${app.rabbitmq.queue.medico:cola-medico-eventos}")
    private String queueMedico;
    @Value("${app.rabbitmq.queue.paciente:cola-paciente-eventos}")
    private String queuePaciente;
    @Value("${app.rabbitmq.queue.cita:cola-cita-eventos}")
    private String queueCita;
    @Value("${app.rabbitmq.queue.medicamento:cola-medicamento-eventos}")
    private String queueMedicamento;
    @Value("${app.rabbitmq.queue.reporte:cola-reporte-eventos}")
    private String queueReporte;
    @Value("${app.rabbitmq.queue.notificacion:cola-notificacion-enviada}")
    private String queueNotificacion;

    @Bean
    public DirectExchange saludExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue queueMedico() {
        return new Queue(queueMedico, true);
    }

    @Bean
    public Queue queuePaciente() {
        return new Queue(queuePaciente, true);
    }

    @Bean
    public Queue queueCita() {
        return new Queue(queueCita, true);
    }

    @Bean
    public Queue queueMedicamento() {
        return new Queue(queueMedicamento, true);
    }

    @Bean
    public Queue queueReporte() {
        return new Queue(queueReporte, true);
    }

    @Bean
    public Queue queueNotificacion() {
        return new Queue(queueNotificacion, true);
    }

    @Bean
    public Binding bindingMedico(DirectExchange e) {
        return BindingBuilder.bind(queueMedico()).to(e).with("medico.evento");
    }

    @Bean
    public Binding bindingPaciente(DirectExchange e) {
        return BindingBuilder.bind(queuePaciente()).to(e).with("paciente.evento");
    }

    @Bean
    public Binding bindingCita(DirectExchange e) {
        return BindingBuilder.bind(queueCita()).to(e).with("cita.creada");
    }

    @Bean
    public Binding bindingMedicamento(DirectExchange e) {
        return BindingBuilder.bind(queueMedicamento()).to(e).with("medicamento.evento");
    }

    @Bean
    public Binding bindingReporte(DirectExchange e) {
        return BindingBuilder.bind(queueReporte()).to(e).with("reporte.generado");
    }

    @Bean
    public Binding bindingNotificacion(DirectExchange e) {
        return BindingBuilder.bind(queueNotificacion()).to(e).with("notificacion.enviada");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
