package com.saludvital.controller;

import com.saludvital.dto.NotificacionRequest;
import com.saludvital.model.Notificacion;
import com.saludvital.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    /** Lista todas (vista del ADMIN) */
    @GetMapping
    public ResponseEntity<List<Notificacion>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    /** Obtener una por id */
    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /** Notificaciones de un usuario (vista del MEDICO o PACIENTE) */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notificacion>> listarPorUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(service.listarPorUsuario(userId));
    }

    @PostMapping("/enviar")
    public ResponseEntity<String> enviar(@RequestBody NotificacionRequest request) {
        service.procesarNotificacion(request.getDestinatario(), request.getMensaje(), request.getTipo());
        return ResponseEntity.ok("Proceso de notificacion iniciado correctamente");
    }
}
