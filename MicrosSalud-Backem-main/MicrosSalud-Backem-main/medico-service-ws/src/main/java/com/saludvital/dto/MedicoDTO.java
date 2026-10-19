package com.saludvital.dto;

import java.math.BigDecimal;
import java.util.List;

import com.saludvital.enums.Especialidad;
import com.saludvital.enums.EstadoDoctor;

public record MedicoDTO(
        Long id,
        String nombre,
        String apellido,
        String numeroLicencia,
        String telefono,
        String email,
        Especialidad especialidad,
        EstadoDoctor estado,
        Boolean disponible,
        BigDecimal tarifaConsulta,
        List<HorarioDTO> horarios,
        UsuarioDTO usuario) {
    public record UsuarioDTO(
            Long id,
            String username,
            String password) {
    }
}