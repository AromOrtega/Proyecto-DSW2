package com.saludvital.dto;

/**
 * Evento que publica cita-service-ws en la cola cola-cita-eventos.
 */
public record EventoCita(String accion, Long citaId, Long pacienteId, Long medicoId, long timestamp) {
}
