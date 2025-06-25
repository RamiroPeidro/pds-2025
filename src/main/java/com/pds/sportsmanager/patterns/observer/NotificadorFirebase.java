package com.pds.sportsmanager.patterns.observer;

import com.pds.sportsmanager.model.entity.PreferenciaNotificacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

/**
 * Notificador concreto para notificaciones push via Firebase
 * Implementa el patrón Observer usando Java 21 + Lombok
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "notificaciones.firebase.habilitado", havingValue = "true", matchIfMissing = true)
public class NotificadorFirebase implements Notificador {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
   // Inyectar FirebaseMessaging

    @Override
    public void notificar(NotificacionEvent evento, PreferenciaNotificacion preferencia) {

        log.info("Enviando notificación push: {} a {} dispositivos", 
                evento.tipo(), evento.destinatarios().size());

        CompletableFuture.runAsync(() -> {
            try {
                enviarNotificacionesAsincrono(evento);
            } catch (Exception e) {
                log.error("Error enviando notificaciones push para evento {}: {}", evento.tipo(), e.getMessage());
            }
        });
    }

    @Override
    public String getTipo() {
        return "FIREBASE_PUSH";
    }

    @Override
    public boolean estaHabilitado(PreferenciaNotificacion preferencia) {
        //Verificar configuración Firebase, tokens, etc.
        if (preferencia == null || !preferencia.estaHabilitadaPush()) {
            log.debug("Preferencias de notificación deshabilitadas o no encontradas");
            return false;
        }
        return true;
    }

    /**
     * Enviar notificaciones push usando Firebase Admin SDK
     */
    private void enviarNotificacionesAsincrono(NotificacionEvent evento) {
        var notificacionData = crearNotificacionData(evento);

        for (String token : evento.destinatarios()) {
            try {
                enviarNotificacionIndividual(token, notificacionData);
                log.debug("Push notification enviada exitosamente a token: {}***", 
                         token.substring(0, Math.min(10, token.length())));
            } catch (Exception e) {
                log.error("Error enviando push notification a token {}: {}", 
                         token.substring(0, Math.min(10, token.length())), e.getMessage());
            }
        }
    }

    /**
     * Crea los datos de la notificación usando records
     */
    private NotificacionData crearNotificacionData(NotificacionEvent evento) {
        String titulo = generarTitulo(evento);
        String cuerpo = evento.mensaje();
        String icono = generarIcono(evento);
        String sonido = generarSonido(evento);
        
        return new NotificacionData(titulo, cuerpo, icono, sonido, evento.partidoId());
    }

    /**
     * Record para encapsular datos de notificación push
     */
    private record NotificacionData(
            String titulo,
            String cuerpo,
            String icono,
            String sonido,
            Long partidoId
    ) {}

    /**
     * Genera el título usando switch expressions
     */
    private String generarTitulo(NotificacionEvent evento) {
        return switch (evento.tipo()) {
            case PARTIDO_CREADO -> "🏆 Nuevo Partido Disponible";
            case PARTIDO_ARMADO -> "✅ Partido Armado";
            case PARTIDO_CONFIRMADO -> "🎯 Partido Confirmado";
            case PARTIDO_EN_JUEGO -> "🏃‍♂️ ¡Partido en Marcha!";
            case PARTIDO_FINALIZADO -> "🏁 Partido Finalizado";
            case PARTIDO_CANCELADO -> "❌ Partido Cancelado";
            case JUGADOR_UNIDO -> "👥 Nuevo Jugador";
            case JUGADOR_ABANDONO -> "👋 Jugador se fue";
        };
    }

    /**
     * Genera el icono de la notificación
     */
    private String generarIcono(NotificacionEvent evento) {
        return switch (evento.tipo()) {
            case PARTIDO_CREADO, JUGADOR_UNIDO -> "ic_partido_nuevo";
            case PARTIDO_ARMADO, PARTIDO_CONFIRMADO -> "ic_partido_confirmado";
            case PARTIDO_EN_JUEGO -> "ic_partido_jugando";
            case PARTIDO_FINALIZADO -> "ic_partido_finalizado";
            case PARTIDO_CANCELADO, JUGADOR_ABANDONO -> "ic_partido_cancelado";
        };
    }

    /**
     * Genera el sonido de la notificación
     */
    private String generarSonido(NotificacionEvent evento) {
        return switch (evento.tipo()) {
            case PARTIDO_CREADO, JUGADOR_UNIDO -> "notification_success";
            case PARTIDO_ARMADO, PARTIDO_CONFIRMADO -> "notification_important";
            case PARTIDO_EN_JUEGO -> "notification_game_start";
            case PARTIDO_FINALIZADO -> "notification_complete";
            case PARTIDO_CANCELADO, JUGADOR_ABANDONO -> "notification_alert";
        };
    }

    /**
     * Simula el envío de notificación push individual
     * En un proyecto real usarías Firebase Admin SDK
     */
    private void enviarNotificacionIndividual(String token, NotificacionData data) {
        // Simulación del envío
        log.info("🔔 PUSH ENVIADO - Token: {}*** | Título: {} | Partido: {}", 
                token.substring(0, Math.min(8, token.length())), 
                data.titulo(), 
                data.partidoId());
        
        //TODO: Enviar notificaciones push usando Firebase Admin SDK
    }

    /**
     * Método de utilidad para validar tokens FCM
     */
    private boolean esTokenValido(String token) {
        // Validación básica de formato de token FCM
        return token != null && 
               token.length() > 100 &&  
               token.matches("^[a-zA-Z0-9_:-]+$");
    }
} 