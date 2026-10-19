package com.saludvital.client;

import com.saludvital.dto.MedicoDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

/**
 * HttpExchange declarativo para medico-service-ws (reemplaza @FeignClient).
 */
@HttpExchange
public interface MedicoClient {

    @GetExchange("/api/medicos")
    List<MedicoDTO> listarMedicos();

    @GetExchange("/api/medicos/{id}")
    MedicoDTO obtenerMedico(@PathVariable("id") Long id);
}
