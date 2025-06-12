package com.pds.sportsmanager.service;

import com.pds.sportsmanager.patterns.strategy.EstrategiaEmparejamiento;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Factory para crear estrategias de emparejamiento usando Spring DI
 * Elimina la necesidad de usar 'new' en el controller
 */
@Service
@RequiredArgsConstructor
public class EstrategiaEmparejamientoFactory {

    @Qualifier("estrategiaPorNivel")
    private final EstrategiaEmparejamiento estrategiaPorNivel;
    
    @Qualifier("estrategiaPorCercania")
    private final EstrategiaEmparejamiento estrategiaPorCercania;

    /**
     * Obtiene la estrategia por nombre
     * @param nombre "nivel" o "cercania"
     * @return la estrategia correspondiente
     */
    public EstrategiaEmparejamiento obtenerEstrategia(String nombre) {
        return switch (nombre.toLowerCase()) {
            case "cercania" -> estrategiaPorCercania;
            case "nivel" -> estrategiaPorNivel;
            default -> estrategiaPorNivel; // Por defecto
        };
    }

    /**
     * Retorna la estrategia por nivel (por defecto)
     */
    public EstrategiaEmparejamiento porNivel() {
        return estrategiaPorNivel;
    }

    /**
     * Retorna la estrategia por cercanía
     */
    public EstrategiaEmparejamiento porCercania() {
        return estrategiaPorCercania;
    }
} 