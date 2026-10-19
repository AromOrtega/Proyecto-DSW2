package com.saludvital.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saludvital.dto.CitaDTO;
import com.saludvital.dto.CitaRequest;
import com.saludvital.service.CitaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public List<CitaDTO> getAll() {
        return citaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.findById(id));
    }    

    @PostMapping
    public ResponseEntity<CitaDTO> create(@Valid @RequestBody CitaRequest request) {
        CitaDTO dto = citaService.create(request);
        return ResponseEntity
                .created(URI.create("/api/citas/" + dto.id()))
                .body(dto);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CitaDTO> update(@PathVariable Long id,
                                          @Valid @RequestBody CitaRequest request) {
        return ResponseEntity.ok(citaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        citaService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}
