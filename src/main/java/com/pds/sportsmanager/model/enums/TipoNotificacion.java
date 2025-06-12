package com.pds.sportsmanager.model.enums;

public enum TipoNotificacion {
    PARTIDO_CREADO("Nuevo partido creado"),
    PARTIDO_ARMADO("Partido armado"),
    PARTIDO_CONFIRMADO("Partido confirmado"),
    PARTIDO_EN_JUEGO("Partido en juego"),
    PARTIDO_FINALIZADO("Partido finalizado"),
    PARTIDO_CANCELADO("Partido cancelado"),
    JUGADOR_UNIDO("Jugador se unió al partido"),
    JUGADOR_ABANDONO("Jugador abandonó el partido");

    private final String descripcion;

    TipoNotificacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
} 