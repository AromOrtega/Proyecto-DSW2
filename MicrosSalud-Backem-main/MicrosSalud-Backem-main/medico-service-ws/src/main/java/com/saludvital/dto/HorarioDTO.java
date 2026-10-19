package com.saludvital.dto;


import java.time.LocalTime;

import com.saludvital.enums.DiaSemana;

public record HorarioDTO(
    Long id,
    DiaSemana dia,
    LocalTime horaInicio,
    LocalTime horaFin
) {}