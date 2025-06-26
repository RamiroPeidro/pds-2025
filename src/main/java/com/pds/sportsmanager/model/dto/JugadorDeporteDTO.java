package com.pds.sportsmanager.model.dto;


public record JugadorDeporteDTO(
    Long jugadorId,
    Long deporteId,
    String nivelDeJuego,
    boolean esFavorito
) {
    public long getJugadorId() {
        return jugadorId;
    }
    public long getDeporteId() {
        return deporteId;
    }
    public String getNivelDeJuego() {
        return nivelDeJuego;
    }

    public boolean esFavorito() {
        return esFavorito;
    }

}
