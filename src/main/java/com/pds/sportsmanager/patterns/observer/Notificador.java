package com.pds.sportsmanager.patterns.observer;

/**
 * Patrón Observer - Interfaz para notificadores (Observers)
 */
public interface Notificador {
    
    /**
     * Recibe y procesa un evento de notificación
     */
    void notificar(NotificacionEvent evento);
    
    /**
     * Tipo de notificador
     */
    String getTipo();
    
    /**
     * Verifica si el notificador está habilitado
     */
    boolean estaHabilitado();
} 