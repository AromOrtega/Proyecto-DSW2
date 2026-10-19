package com.saludvital.client;

import com.saludvital.dto.PacienteDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

/**
 * HttpExchange declarativo para paciente-service-ws (reemplaza @FeignClient).
 */
@HttpExchange
public interface PacienteClient {

    @GetExchange("/api/pacientes")
    List<PacienteDTO> listarPacientes();

    @GetExchange("/api/pacientes/{id}")
    PacienteDTO obtenerPaciente(@PathVariable("id") Long id);
}
