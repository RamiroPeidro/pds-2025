package com.pds.sportsmanager.patterns.state;

import com.pds.sportsmanager.model.entity.Partido;
import com.pds.sportsmanager.model.entity.Usuario;

/**
 * Estado cuando el partido ha sido cancelado
 */
public class PartidoCancelado implements EstadoPartido {

    @Override
    public String getNombre() {
        return "PARTIDO_CANCELADO";
    }

    @Override
    public String getDescripcion() {
        return "El partido ha sido cancelado";
    }

    @Override
    public void agregarJugador(Partido partido, Usuario jugador) {
        throw new IllegalStateException("No se pueden agregar jugadores a un partido cancelado");
    }

    @Override
    public void confirmarPartido(Partido partido) {
        throw new IllegalStateException("No se puede confirmar un partido cancelado");
    }

    @Override
    public void cancelarPartido(Partido partido) {
        throw new IllegalStateException("El partido ya está cancelado");
    }

    @Override
    public void finalizarPartido(Partido partido) {
        throw new IllegalStateException("No se puede finalizar un partido cancelado");
    }

    @Override
    public void iniciarPartido(Partido partido) {
        throw new IllegalStateException("No se puede iniciar un partido cancelado");
    }
} 