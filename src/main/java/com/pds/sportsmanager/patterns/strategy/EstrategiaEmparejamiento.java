package com.pds.sportsmanager.patterns.strategy;

import com.pds.sportsmanager.model.dto.PartidoBusquedaResult;
import com.pds.sportsmanager.model.entity.Jugador;

import java.util.List;

public interface EstrategiaEmparejamiento {
    
    String getNombre();
    String getDescripcion();

    /**
     * @param jugador Usuario que busca partidos
     * @param partidosDisponibles Lista de partidos disponibles
     */
    List<PartidoBusquedaResult> buscarPartidos(Jugador jugador, List<PartidoBusquedaResult> partidosDisponibles);
    
   
    double calcularCompatibilidad(Jugador jugador, PartidoBusquedaResult partido);
} 