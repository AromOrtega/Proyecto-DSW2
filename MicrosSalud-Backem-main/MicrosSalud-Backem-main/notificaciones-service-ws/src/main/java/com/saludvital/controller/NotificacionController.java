package com.saludvital.controller;

import com.saludvital.dto.NotificacionRequest;
import com.saludvital.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    @PostMapping("/enviar")
    public ResponseEntity<String> enviar(@RequestBody NotificacionRequest request) {
        service.procesarNotificacion(request.getDestinatario(), request.getMensaje(), request.getTipo());
        return ResponseEntity.ok("Proceso de notificacion iniciado correctamente");
    }
}