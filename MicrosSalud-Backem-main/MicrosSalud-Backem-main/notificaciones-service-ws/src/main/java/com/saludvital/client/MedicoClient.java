package com.saludvital.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Map;

public interface MedicoClient {

    @GetExchange("/api/medicos/{id}")
    Map<String, Object> obtenerMedico(@PathVariable("id") Long id);
}
