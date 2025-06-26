package com.pds.sportsmanager.model.dto;


import lombok.Getter;
import lombok.Setter;


public record JugadorDeporteDTO(
    Long jugadorId,
    Long deporteId,
    String nivelDeJuego,
    String ubicacionCiudad,
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
    public String getUbicacionCiudad() {
        return ubicacionCiudad;
    }
    public boolean esFavorito() {
        return esFavorito;
    }

}
