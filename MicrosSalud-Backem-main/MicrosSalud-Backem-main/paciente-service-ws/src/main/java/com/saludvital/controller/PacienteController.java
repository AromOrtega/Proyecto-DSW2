package com.saludvital.controller;

import com.saludvital.dto.PacienteDTO;
import com.saludvital.service.PacienteService;
import com.saludvital.Paciente;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PacienteDTO> getAll() {
        return pacienteService.findAll().stream().map(this::toDTO).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteDTO> getById(@PathVariable Long id) {
        return pacienteService.findById(id).map(this::toDTO).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PacienteDTO> create(@RequestBody PacienteDTO dto) {
        Paciente saved = pacienteService.save(fromDTO(dto, new Paciente()));
        return ResponseEntity.created(URI.create("/api/pacientes/" + saved.getId())).body(toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteDTO> update(@PathVariable Long id, @RequestBody PacienteDTO dto) {
        return pacienteService.findById(id).map(db -> {
            return ResponseEntity.ok(toDTO(pacienteService.save(fromDTO(dto, db))));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!pacienteService.findById(id).isPresent()) return ResponseEntity.notFound().build();
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private PacienteDTO toDTO(Paciente p) {
        PacienteDTO dto = new PacienteDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setApellido(p.getApellido());
        dto.setNumeroIdentificacion(p.getNumeroIdentificacion());
        dto.setFechaNacimiento(p.getFechaNacimiento());
        dto.setTelefono(p.getTelefono());
        dto.setDireccion(p.getDireccion());
        dto.setUsuarioId(p.getUsuarioId());
        dto.setAlergias(new ArrayList<>());
        dto.setAlergiaIds(new ArrayList<>());
        return dto;
    }

    private Paciente fromDTO(PacienteDTO dto, Paciente p) {
        p.setNombre(dto.getNombre());
        p.setApellido(dto.getApellido());
        p.setNumeroIdentificacion(dto.getNumeroIdentificacion());
        p.setFechaNacimiento(dto.getFechaNacimiento());
        p.setTelefono(dto.getTelefono());
        p.setDireccion(dto.getDireccion());
        p.setUsuarioId(dto.getUsuarioId());
        return p;
    }
}