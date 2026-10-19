package com.saludvital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request para crear/actualizar una cita.
 * Se eliminó el campo 'especialidad' que no existe en la entidad Cita.
 */
public record CitaRequest(

                @NotNull LocalDate fecha,

                @NotNull LocalTime hora,

                @NotNull Long pacienteId,

                @NotNull Long medicoId,

                @NotBlank String motivo

) {
}
