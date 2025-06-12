package com.pds.sportsmanager.patterns.observer;

import com.pds.sportsmanager.model.enums.TipoNotificacion;

import java.time.LocalDateTime;
import java.util.List;

public record NotificacionEvent(
    TipoNotificacion tipo,
    String mensaje,
    List<String> destinatarios,
    Long partidoId,
    LocalDateTime timestamp
) {
    
    public NotificacionEvent {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
    
    public static NotificacionEvent of(TipoNotificacion tipo, String mensaje, List<String> destinatarios, Long partidoId) {
        return new NotificacionEvent(tipo, mensaje, destinatarios, partidoId, LocalDateTime.now());
    }
} 