package com.pds.sportsmanager.patterns.strategy;

import com.pds.sportsmanager.model.dto.PartidoBusquedaResult;
import com.pds.sportsmanager.model.entity.Usuario;

import java.util.List;

public interface EstrategiaEmparejamiento {
    
    String getNombre();
    String getDescripcion();

    /**
     * @param usuario Usuario que busca partidos
     * @param partidosDisponibles Lista de partidos disponibles
     */
    List<PartidoBusquedaResult> buscarPartidos(Usuario usuario, List<PartidoBusquedaResult> partidosDisponibles);
    
   
    double calcularCompatibilidad(Usuario usuario, PartidoBusquedaResult partido);
} 