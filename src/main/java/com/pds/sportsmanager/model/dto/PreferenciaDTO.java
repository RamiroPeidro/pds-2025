package com.pds.sportsmanager.model.dto;

public record PreferenciaDTO (
        Boolean emailNotificaciones,
        Boolean firebaseNotificaciones
) {
}
