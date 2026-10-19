package com.saludvital.controller;

import com.saludvital.Medicamento;
import com.saludvital.dto.MedicamentoDTO;
import com.saludvital.dto.OptionDTO;
import com.saludvital.service.MedicamentoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize; // <---  Descomentar para configurar el security
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api/medicamentos")
public class MedicamentoController {

    private final MedicamentoService service;

    public MedicamentoController(MedicamentoService service) {
        this.service = service;
    }

    /** Lista completa */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize("hasAnyRole('ADMIN','MEDICO')") 
    public List<Medicamento> getAll() {
        return service.findAll();
    }

    /** Options para combos: /api/medicamentos/options */
    @GetMapping(value = "/options", produces = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public List<OptionDTO> options() {
        return service.findAll().stream()
                .map(m -> new OptionDTO(m.getId(), m.getNombre()))
                .toList();
    }

    /** Obtener por id */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Medicamento> getById(@PathVariable Long id) {
        Medicamento m = service.findById(id); 
        return ResponseEntity.ok(m);
    }

    /** Crear */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Medicamento> create(@RequestBody MedicamentoDTO dto) {
        Medicamento saved = service.create(dto);
        return ResponseEntity
                .created(URI.create("/api/medicamentos/" + saved.getId()))
                .body(saved);
    }

    /** Actualizar */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Medicamento> update(@PathVariable Long id, @RequestBody MedicamentoDTO dto) {
        Medicamento updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /** Eliminar */
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}