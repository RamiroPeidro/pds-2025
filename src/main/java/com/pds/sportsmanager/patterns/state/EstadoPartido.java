package com.pds.sportsmanager.patterns.state;

import com.pds.sportsmanager.model.entity.Partido;
import com.pds.sportsmanager.model.entity.Jugador;

/**
 * Patrón State - Interfaz para los diferentes estados de un partido
 */
public interface EstadoPartido {
    
    /**
     * Nombre del estado para persistencia
     */
    String getNombre();
    
    /**
     * Descripción del estado
     */
    String getDescripcion();
    
    /**
     * Agrega un jugador al partido
     */
    void agregarJugador(Partido partido, Jugador jugador);
    
    /**
     * Confirma el partido
     */
    void confirmarPartido(Partido partido);
    
    /**
     * Cancela el partido
     */
    void cancelarPartido(Partido partido);
    
    /**
     * Finaliza el partido
     */
    void finalizarPartido(Partido partido);
    
    /**
     * Inicia el partido automáticamente cuando llega la hora
     */
    void enJuego(Partido partido);
    
    /**
     * Factory method para crear estados desde el nombre
     */
    static EstadoPartido fromNombre(String nombre) {
        return EstadoPartidoFactory.fromNombre(nombre);
    }
} 