package com.pds.sportsmanager.patterns.observer;

import com.pds.sportsmanager.model.enums.TipoNotificacion;

import java.time.LocalDateTime;
import java.util.List;

public record EventSingle(
        TipoNotificacion tipo,
        String mensaje,
        String destinatario,
        Long partidoId,
        LocalDateTime timestamp
) {
    public EventSingle {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public static EventSingle of(TipoNotificacion tipo, String mensaje, String destinatario, Long partidoId) {
        return new EventSingle(tipo, mensaje, destinatario, partidoId, LocalDateTime.now());
    }

    public String getDestinatario() {
        return destinatario;
    }
}