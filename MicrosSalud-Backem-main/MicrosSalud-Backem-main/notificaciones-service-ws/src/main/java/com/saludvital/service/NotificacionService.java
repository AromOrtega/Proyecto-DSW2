package com.saludvital.service;

import com.saludvital.messaging.NotificacionPublisher;
import com.saludvital.model.Notificacion;
import com.saludvital.repository.NotificacionRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);
    private final NotificacionRepository repo;
    private final NotificacionPublisher publisher;
    private final Counter notificacionesEnviadas;

    public NotificacionService(NotificacionRepository repo, NotificacionPublisher publisher, MeterRegistry meterRegistry) {
        this.repo = repo;
        this.publisher = publisher;
        this.notificacionesEnviadas = Counter.builder("notificaciones_enviadas_total")
                .description("Total de notificaciones procesadas")
                .tag("service", "notificaciones").register(meterRegistry);
    }

    public List<Notificacion> listar() {
        return repo.findAllByOrderByFechaCreacionDesc();
    }

    public Notificacion buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificacion no encontrada: " + id));
    }

    public List<Notificacion> listarPorUsuario(Long userId) {
        return repo.findByUserIdOrderByFechaCreacionDesc(userId);
    }

    @Transactional
    public Notificacion crear(Long userId, String mensaje, String tipo) {
        Notificacion saved = repo.save(new Notificacion(userId, mensaje, tipo));
        notificacionesEnviadas.increment();
        log.info("[PROCESO] Notificacion guardada para userId={}", userId);
        return saved;
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
