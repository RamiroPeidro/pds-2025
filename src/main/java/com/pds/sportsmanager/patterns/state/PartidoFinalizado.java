package com.pds.sportsmanager.patterns.state;

import com.pds.sportsmanager.model.entity.Jugador;
import com.pds.sportsmanager.model.entity.Partido;

/**
 * Estado final cuando el partido ha terminado
 */
public class PartidoFinalizado implements EstadoPartido {

    @Override
    public String getNombre() {
        return "PARTIDO_FINALIZADO";
    }

    @Override
    public String getDescripcion() {
        return "El partido ha finalizado";
    }

    @Override
    public void agregarJugador(Partido partido, Jugador jugador) {
        throw new IllegalStateException("No se pueden agregar jugadores a un partido finalizado");
    }

    @Override
    public void confirmarPartido(Partido partido) {
        throw new IllegalStateException("No se puede confirmar un partido finalizado");
    }

    @Override
    public void cancelarPartido(Partido partido) {
        throw new IllegalStateException("No se puede cancelar un partido finalizado");
    }

    @Override
    public void finalizarPartido(Partido partido) {
        throw new IllegalStateException("El partido ya está finalizado");
    }

    @Override
    public void enJuego(Partido partido) {
        throw new IllegalStateException("No se puede iniciar un partido finalizado");
    }
} 