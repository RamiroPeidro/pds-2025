package com.pds.sportsmanager.patterns.observer;

public interface EmailAdapter {
    void enviarNotificacion(String destinatario, String asunto, String cuerpoHtml);
    
   }
