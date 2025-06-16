package com.pds.sportsmanager.patterns.state;

import com.pds.sportsmanager.model.entity.Partido;
import com.pds.sportsmanager.model.entity.Usuario;

import static com.pds.sportsmanager.patterns.state.EstadoPartidoFactory.*;

/**
 * Estado cuando el partido está en progreso
 */
public class EnJuego implements EstadoPartido {

    @Override
    public String getNombre() {
        return "EN_JUEGO";
    }

    @Override
    public String getDescripcion() {
        return "El partido está en progreso";
    }

    @Override
    public void agregarJugador(Partido partido, Usuario jugador) {
        throw new IllegalStateException("No se pueden agregar jugadores a un partido en juego");
    }

    @Override
    public void confirmarPartido(Partido partido) {
        throw new IllegalStateException("El partido ya está en juego");
    }

    @Override
    public void cancelarPartido(Partido partido) {
        partido.cambiarEstado(partidoCancelado()); 
    }

    @Override
    public void finalizarPartido(Partido partido) {
        partido.cambiarEstado(partidoFinalizado()); 
    }

    @Override
    public void enJuego(Partido partido) {
        throw new IllegalStateException("El partido ya está en juego");
    }
} 