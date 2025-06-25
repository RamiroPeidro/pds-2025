package com.pds.sportsmanager.patterns.observer;

import com.pds.sportsmanager.model.entity.PreferenciaNotificacion;

/**
 * Patrón Observer - Interfaz para notificadores (Observers)
 */
public interface Notificador {
    
    /**
     * Recibe y procesa un evento de notificación
     */
    void notificar(NotificacionEvent evento, PreferenciaNotificacion preferencia);
    
    /**
     * Tipo de notificador
     */
    String getTipo();
    
    /**
     * Verifica si el notificador está habilitado
     */
    boolean estaHabilitado(PreferenciaNotificacion preferencia);
} 