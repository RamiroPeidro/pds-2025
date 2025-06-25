package com.pds.sportsmanager.controller;

import com.pds.sportsmanager.patterns.observer.NotificacionEvent;
import com.pds.sportsmanager.patterns.observer.Notificador;
import com.pds.sportsmanager.service.NotificacionService;
import com.pds.sportsmanager.model.enums.TipoNotificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificadores")
@RequiredArgsConstructor
public class NotificadorController {

    private final NotificacionService notificacionService;

    @GetMapping
    public List<String> listarNotificadores() {
        return notificacionService.getNotificadores()
                .stream()
                .map(Notificador::getTipo)
                .toList();
    }

    @PostMapping("/enviar")
    public String enviarNotificacion(
            @RequestParam TipoNotificacion tipo,
            @RequestParam String mensaje,
            @RequestParam List<String> destinatarios,
            @RequestParam(required = false) Long partidoId
    ) {
        NotificacionEvent evento = NotificacionEvent.of(tipo, mensaje, destinatarios, partidoId);
        notificacionService.enviarNotificacionPersonalizada(tipo, mensaje, destinatarios, partidoId);
        return "Notificación enviada";
    }

    @PostMapping("/registrar")
    public String registrarNotificador(@RequestBody Notificador notificador) {
        notificacionService.agregarNotificador(notificador);
        return "Notificador registrado exitosamente";
    }
}