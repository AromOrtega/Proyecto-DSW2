package com.saludvital.controller;

import com.saludvital.Medico;
import com.saludvital.dto.MedicoDTO;
import com.saludvital.service.MedicoService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    // --- LISTAR TODOS (público) ---
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Medico> getAllMedicos() {
        return medicoService.findAll();
    }

    // --- OBTENER UNO (público) ---
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Medico> getMedicoById(@PathVariable Long id) {
        return medicoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ============================================================
    // REGISTRO DE MÉDICO — Solo ADMIN
    // Crea User + Medico en una sola transacción
    // ============================================================
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createMedico(@RequestBody MedicoDTO dto) {
        try {
            Medico nuevo = medicoService.registrarMedico(dto);
            return ResponseEntity.ok(nuevo);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("Error interno al crear médico: " + ex.getMessage());
        }
    }

    // ============================================================
    // ACTUALIZAR MÉDICO — Solo ADMIN
    // Ahora usa el nuevo método del Service para evitar bugs con los horarios
    // ============================================================
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateMedico(@PathVariable Long id, @RequestBody MedicoDTO dto) {
        // 1. Verificamos si existe antes de mandar al servicio
        if (!medicoService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            // 2. Le pasamos toda la responsabilidad al servicio que creamos
            Medico actualizado = medicoService.actualizarMedico(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("Error interno al actualizar médico: " + ex.getMessage());
        }
    }

    // --- ELIMINAR (solo ADMIN) ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMedico(@PathVariable Long id) {
        if (!medicoService.existsById(id))
            return ResponseEntity.notFound().build();
        medicoService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // --- VERIFICAR LICENCIA ---
    @GetMapping(value = "/exists-licencia", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> existsLicencia(
            @RequestParam("numero") String numero,
            @RequestParam(value = "id", required = false) Long id) {

        if (numero == null) return ResponseEntity.ok(false);

        boolean exists = (id == null)
                ? medicoService.existsByNumeroLicencia(numero)
                : medicoService.existsByNumeroLicenciaAndIdNot(numero, id);

        return ResponseEntity.ok(exists);
    }
}