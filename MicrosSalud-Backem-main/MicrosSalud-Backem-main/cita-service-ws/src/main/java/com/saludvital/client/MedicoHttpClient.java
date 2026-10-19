package com.saludvital.client;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Map;

/**
 * HttpExchange declarativo para consumir medico-service-ws via Eureka.
 * Spring crea el proxy en tiempo de ejecución usando RestClient + LoadBalancer.
 */
@HttpExchange
public interface MedicoHttpClient {

    @GetExchange("/api/medicos/{id}")
    Map<String, Object> obtenerMedicoPorId(@org.springframework.web.bind.annotation.PathVariable("id") Long id);
}
