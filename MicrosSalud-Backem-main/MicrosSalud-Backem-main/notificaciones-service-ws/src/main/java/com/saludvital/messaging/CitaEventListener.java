package com.saludvital.messaging;

import com.saludvital.client.MedicoClient;
import com.saludvital.client.PacienteClient;
import com.saludvital.dto.EventoCita;
import com.saludvital.service.NotificacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Escucha las citas creadas y genera una notificacion para el paciente y el medico.
 */
@Component
public class CitaEventListener {

    private static final Logger log = LoggerFactory.getLogger(CitaEventListener.class);

    private final NotificacionService service;
    private final PacienteClient pacienteClient;
    private final MedicoClient medicoClient;

    public CitaEventListener(NotificacionService service, PacienteClient pacienteClient, MedicoClient medicoClient) {
        this.service = service;
        this.pacienteClient = pacienteClient;
        this.medicoClient = medicoClient;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.cita:cola-cita-eventos}")
    public void onCitaCreada(EventoCita evento) {
        log.info("📥 Evento recibido de RabbitMQ: {}", evento);

        // 1) Notificar al paciente
        try {
            Map<String, Object> paciente = pacienteClient.obtenerPaciente(evento.pacienteId());
            Long userId = toLong(paciente.get("usuarioId"));
            if (userId != null) {
                service.crear(userId, "Tu cita #" + evento.citaId() + " fue programada correctamente.", "CITA");
            }
        } catch (Exception e) {
            log.warn("No se pudo notificar al paciente id={}: {}", evento.pacienteId(), e.getMessage());
        }

        // 2) Notificar al medico
        try {
            Map<String, Object> medico = medicoClient.obtenerMedico(evento.medicoId());
            Long userId = toLong(medico.get("userId"));
            if (userId != null) {
                service.crear(userId, "Tienes una nueva cita asignada (#" + evento.citaId() + ").", "CITA");
            }
        } catch (Exception e) {
            log.warn("No se pudo notificar al medico id={}: {}", evento.medicoId(), e.getMessage());
        }
    }

    private Long toLong(Object valor) {
        return valor != null ? ((Number) valor).longValue() : null;
    }
}
