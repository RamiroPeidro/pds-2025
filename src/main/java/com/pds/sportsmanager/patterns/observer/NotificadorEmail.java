package com.pds.sportsmanager.patterns.observer;

import com.pds.sportsmanager.model.entity.PreferenciaNotificacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

/**
 * Notificador concreto para envío de emails
 * Implementa el patrón Observer usando Java 21 + Lombok
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "notificaciones.email.habilitado", havingValue = "true", matchIfMissing = true)
public class NotificadorEmail implements Notificador {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final EmailAdapter emailAdapter;

    @Override
    public void notificar(EventSingle evento) {

        log.info("Enviando notificación por email: {} a {} destinatario",
                evento.tipo(), evento.destinatario());

        CompletableFuture.runAsync(() -> {
            try {
                enviarEmailsAsincrono(evento);
            } catch (Exception e) {
                log.error("Error enviando emails para evento {}: {}", evento.tipo(), e.getMessage());
            }
        });
    }

    @Override
    public String getTipo() {
        return "EMAIL";
    }

    @Override
    public boolean estaHabilitado(PreferenciaNotificacion preferencia) {
        if (emailAdapter == null) {
            log.warn("EmailAdapter no configurado, NotificadorEmail deshabilitado");
            return false;
        }
        return preferencia.estaHabilitadaEmail();
    }

    /**
     * Envía emails usando switch expressions
     */
    private void enviarEmailsAsincrono(EventSingle evento) {
        String asunto = generarAsunto(evento);
        String cuerpoHtml = generarCuerpoHtml(evento);

            try {
                enviarEmailIndividual(evento.destinatario(), asunto, cuerpoHtml);
            } catch (Exception e) {
                log.error("Error enviando email a {}: {}", evento.destinatario(), e.getMessage());
            }
    }

    /**
     * Genera el asunto usando switch expressions
     */
    private String generarAsunto(EventSingle evento) {
        return switch (evento.tipo()) {
            case PARTIDO_CREADO -> "🏆 Nuevo partido disponible";
            case PARTIDO_ARMADO -> "✅ Partido armado - Esperando confirmación";
            case PARTIDO_CONFIRMADO -> "🎯 Partido confirmado - ¡Nos vemos en la cancha!";
            case PARTIDO_EN_JUEGO -> "🏃‍♂️ El partido ha comenzado";
            case PARTIDO_FINALIZADO -> "🏁 Partido finalizado - ¡Gracias por participar!";
            case PARTIDO_CANCELADO -> "❌ Partido cancelado";
            case JUGADOR_UNIDO -> "👥 Nuevo jugador se unió al partido";
            case JUGADOR_ABANDONO -> "👋 Un jugador abandonó el partido";
        };
    }

    /**
     * Genera el cuerpo HTML usando text blocks
     */
    private String generarCuerpoHtml(EventSingle evento) {
        String timestamp = evento.timestamp().format(FORMATTER);
        
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
                        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
                        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; }
                        .content { padding: 30px; }
                        .footer { background: #f8f9fa; padding: 15px; text-align: center; font-size: 12px; color: #666; }
                        .btn { display: inline-block; background: #28a745; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; margin: 10px 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🏆 Sports Manager</h1>
                        </div>
                        <div class="content">
                            <h2>%s</h2>
                            <p>%s</p>
                            %s
                        </div>
                        <div class="footer">
                            <p>Enviado el %s | Sports Manager System</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                    generarAsunto(evento),
                    evento.mensaje(),
                    evento.partidoId() != null ? 
                        "<a href=\"#\" class=\"btn\">Ver Partido #" + evento.partidoId() + "</a>" : "",
                    timestamp
                );
    }

    /**
     * Simula el envío de email individual
     */
    private void enviarEmailIndividual(String destinatario, String asunto, String cuerpoHtml) {
        try {
            emailAdapter.enviarNotificacion(destinatario, asunto, cuerpoHtml);
            log.info("📧 EMAIL ENVIADO - Para: {} | Asunto: {}", destinatario, asunto);
        } catch (Exception e) {
            log.error("Error al enviar email: {}", e.getMessage());
        }
    }
} 