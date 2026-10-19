package com.saludvital.controller;

import com.saludvital.dto.CitaDTO;
import com.saludvital.dto.MedicamentoDTO;
import com.saludvital.dto.MedicoDTO;
import com.saludvital.dto.PacienteDTO;
import com.saludvital.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    /** GET /api/reportes/citas */
    @GetMapping("/citas")
    public ResponseEntity<List<CitaDTO>> citas() {
        return ResponseEntity.ok(service.reporteCitas());
    }

    /** GET /api/reportes/medicos */
    @GetMapping("/medicos")
    public ResponseEntity<List<MedicoDTO>> medicos() {
        return ResponseEntity.ok(service.reporteMedicos());
    }

    /** GET /api/reportes/pacientes */
    @GetMapping("/pacientes")
    public ResponseEntity<List<PacienteDTO>> pacientes() {
        return ResponseEntity.ok(service.reportePacientes());
    }

    /** GET /api/reportes/medicamentos */
    @GetMapping("/medicamentos")
    public ResponseEntity<List<MedicamentoDTO>> medicamentos() {
        return ResponseEntity.ok(service.reporteMedicamentos());
    }
}
