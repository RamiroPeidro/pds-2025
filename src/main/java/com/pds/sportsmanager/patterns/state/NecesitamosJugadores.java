package com.pds.sportsmanager.patterns.state;

import com.pds.sportsmanager.model.entity.Partido;
import com.pds.sportsmanager.model.entity.Usuario;

import static com.pds.sportsmanager.patterns.state.EstadoPartidoFactory.*;

/**
 * Estado inicial cuando el partido necesita más jugadores
 */
public class NecesitamosJugadores implements EstadoPartido {

    @Override
    public String getNombre() {
        return "NECESITAMOS_JUGADORES";
    }

    @Override
    public String getDescripcion() {
        return "El partido necesita más jugadores para completarse";
    }

    @Override
    public void agregarJugador(Partido partido, Usuario jugador) {  
        partido.getJugadores().add(jugador);
        
        if (partido.getJugadores().size() >= partido.getMaxJugadores()) {
            partido.cambiarEstado(partidoArmado()); 
        }
    }

    @Override
    public void confirmarPartido(Partido partido) {
        throw new IllegalStateException("No se puede confirmar un partido que necesita más jugadores");
    }

    @Override
    public void cancelarPartido(Partido partido) {
        partido.cambiarEstado(partidoCancelado()); 
    }

    @Override
    public void finalizarPartido(Partido partido) {
        throw new IllegalStateException("No se puede finalizar un partido que necesita más jugadores");
    }

    @Override
    public void iniciarPartido(Partido partido) {
        throw new IllegalStateException("No se puede iniciar un partido que necesita más jugadores");
    }
} 