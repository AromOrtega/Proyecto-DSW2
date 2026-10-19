package com.saludvital.client;

import com.saludvital.dto.MedicamentoDTO;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

/**
 * HttpExchange declarativo para gestion-medicamentos-ws.
 */
@HttpExchange
public interface MedicamentoClient {

    @GetExchange("/api/medicamentos")
    List<MedicamentoDTO> listarMedicamentos();
}
