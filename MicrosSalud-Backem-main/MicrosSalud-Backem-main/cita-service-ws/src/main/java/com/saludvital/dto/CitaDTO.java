package com.saludvital.dto;

import com.saludvital.enums.EstadoCita;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO de respuesta para Cita.
 * Eliminado import no usado de Especialidad (no existe en la entidad Cita).
 */
public record CitaDTO(

                Long id,
                LocalDate fecha,
                LocalTime hora,
                Long pacienteId,
                Long medicoId,
                String nombreMedico,
                String motivo,
                EstadoCita estado

) {
}
