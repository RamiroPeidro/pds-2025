package com.pds.sportsmanager.model.dto;

import com.pds.sportsmanager.model.entity.Ubicacion;
import com.pds.sportsmanager.model.enums.NivelDeJugador;

import java.time.LocalDateTime;

public record PartidoBusquedaResult(
    Long id,
    String deporte,
    String titulo,
    String descripcion,
    Integer cantidadJugadoresRequeridos,
    Integer cantidadJugadoresActuales,
    LocalDateTime fechaHora,
    Integer duracionMinutos,
    Ubicacion ubicacion,
    String organizador,
    NivelDeJugador nivelMinimo,
    NivelDeJugador nivelMaximo,
    String estado,
    Double distanciaKm 
) {
    
    /**
     * Verifica si el partido aún necesita jugadores
     */
    public boolean necesitaJugadores() {
        return cantidadJugadoresActuales < cantidadJugadoresRequeridos;
    }
    
    /**
     * Calcula cuántos jugadores faltan
     */
    public int jugadoresFaltantes() {
        return Math.max(0, cantidadJugadoresRequeridos - cantidadJugadoresActuales);
    }
    
    /**
     * Verifica si un nivel de jugador es compatible con este partido
     */
    public boolean esNivelCompatible(NivelDeJugador nivel) {
        if (nivelMinimo == null && nivelMaximo == null) {
            return true;
        }
        
        if (nivelMinimo != null && nivel.ordinal() < nivelMinimo.ordinal()) {
            return false;
        }
        
        if (nivelMaximo != null && nivel.ordinal() > nivelMaximo.ordinal()) {
            return false;
        }
        
        return true;
    }
} 