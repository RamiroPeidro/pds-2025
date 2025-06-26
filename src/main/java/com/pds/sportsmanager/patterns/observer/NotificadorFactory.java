// src/main/java/com/pds/sportsmanager/patterns/observer/NotificadorFactory.java
package com.pds.sportsmanager.patterns.observer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificadorFactory {

    private final NotificadorEmail notificadorEmail;
    private final NotificadorFirebase notificadorFirebase;

    public Notificador crearPorTipo(String tipo) {
        return switch (tipo.toUpperCase()) {
            case "EMAIL" -> notificadorEmail;
            case "FIREBASE_PUSH" -> notificadorFirebase;
            default -> null;
        };
    }
}