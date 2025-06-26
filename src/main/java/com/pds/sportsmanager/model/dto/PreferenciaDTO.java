package com.pds.sportsmanager.model.dto;

import lombok.Builder;

@Builder
public record PreferenciaDTO (
        Boolean emailNotificaciones,
        Boolean firebaseNotificaciones
) {
}
