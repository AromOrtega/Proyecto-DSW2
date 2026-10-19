package com.saludvital.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Map;

public interface PacienteClient {

    @GetExchange("/api/pacientes/{id}")
    Map<String, Object> obtenerPaciente(@PathVariable("id") Long id);
}
