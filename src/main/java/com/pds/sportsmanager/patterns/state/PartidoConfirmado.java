package com.pds.sportsmanager.patterns.state;

import com.pds.sportsmanager.model.entity.Partido;
import com.pds.sportsmanager.model.entity.Jugador;

import java.time.LocalDateTime;

/**
 * Estado cuando el partido está confirmado y listo para comenzar
 */
public class PartidoConfirmado implements EstadoPartido {

    @Override
    public String getNombre() {
        return "PARTIDO_CONFIRMADO";
    }

    @Override
    public String getDescripcion() {
        return "El partido está confirmado y listo para comenzar";
    }

    @Override
    public void agregarJugador(Partido partido, Jugador jugador) {
        throw new IllegalStateException("No se pueden agregar jugadores a un partido confirmado");
    }

    @Override
    public void confirmarPartido(Partido partido) {
        throw new IllegalStateException("El partido ya está confirmado");
    }

    @Override
    public void cancelarPartido(Partido partido) {
        partido.cambiarEstado(new PartidoCancelado());
    }

    @Override
    public void finalizarPartido(Partido partido) {
        throw new IllegalStateException("El partido debe estar en juego antes de finalizar");
    }

    @Override
    public void enJuego(Partido partido) {
        LocalDateTime ahora = LocalDateTime.now();
        if (ahora.isBefore(partido.getFechaHora())) {
            throw new IllegalStateException("Aún no es la hora del partido");
        }
        
        partido.cambiarEstado(new EnJuego());
    }
} 