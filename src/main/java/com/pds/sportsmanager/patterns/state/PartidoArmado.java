package com.pds.sportsmanager.patterns.state;

import com.pds.sportsmanager.model.entity.Partido;
import com.pds.sportsmanager.model.entity.Jugador;

/**
 * Estado cuando el partido tiene suficientes jugadores pero aún no está confirmado
 */
public class PartidoArmado implements EstadoPartido {

    @Override
    public String getNombre() {
        return "PARTIDO_ARMADO";
    }

    @Override
    public String getDescripcion() {
        return "El partido tiene suficientes jugadores y está esperando confirmación";
    }

    @Override
    public void agregarJugador(Partido partido, Jugador jugador) {
        throw new IllegalStateException("El partido ya tiene suficientes jugadores");
    }

    @Override
    public void confirmarPartido(Partido partido) {
        partido.cambiarEstado(new PartidoConfirmado());
    }

    @Override
    public void cancelarPartido(Partido partido) {
        partido.cambiarEstado(new PartidoCancelado());
    }

    @Override
    public void finalizarPartido(Partido partido) {
        throw new IllegalStateException("No se puede finalizar un partido que no ha sido confirmado");
    }

    @Override
    public void enJuego(Partido partido) {
        throw new IllegalStateException("No se puede iniciar un partido que no ha sido confirmado");
    }
} 