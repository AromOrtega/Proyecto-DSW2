package com.saludvital.client;

import com.saludvital.dto.CitaDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

/**
 * HttpExchange declarativo para cita-service-ws (reemplaza @FeignClient).
 */
@HttpExchange
public interface CitaClient {

    @GetExchange("/api/citas")
    List<CitaDTO> listarCitas();

    @GetExchange("/api/citas/{id}")
    CitaDTO obtenerCita(@PathVariable("id") Long id);
}
