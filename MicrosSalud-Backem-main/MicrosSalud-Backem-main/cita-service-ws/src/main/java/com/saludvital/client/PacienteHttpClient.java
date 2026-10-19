package com.saludvital.client;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Map;

/**
 * HttpExchange declarativo para consumir paciente-service-ws via Eureka.
 */
@HttpExchange
public interface PacienteHttpClient {

    @GetExchange("/api/pacientes/{id}")
    Map<String, Object> obtenerPacientePorId(@org.springframework.web.bind.annotation.PathVariable("id") Long id);
}
