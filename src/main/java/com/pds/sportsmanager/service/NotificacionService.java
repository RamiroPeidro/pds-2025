package com.pds.sportsmanager.service;

import com.pds.sportsmanager.model.entity.Jugador;
import com.pds.sportsmanager.model.entity.Partido;
import com.pds.sportsmanager.model.entity.PreferenciaNotificacion;
import com.pds.sportsmanager.model.enums.TipoNotificacion;
import com.pds.sportsmanager.patterns.observer.NotificacionEvent;
import com.pds.sportsmanager.patterns.observer.Notificador;
import com.pds.sportsmanager.patterns.observer.NotificadorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de notificaciones que implementa el patrón Observer
 * Modernizado con Lombok para Java 21
 */
@Service
@Slf4j
public class NotificacionService {

    private final List<Notificador> notificadores = new ArrayList<>();
    private final NotificadorFactory notificadorFactory;
    /**
     * Constructor por defecto
     */
    private final PreferenciaNotificacionServiceImpl preferenciasService;

    public NotificacionService(PreferenciaNotificacionServiceImpl preferenciasService, NotificadorFactory notificadorFactory) {
        this.preferenciasService = preferenciasService;
        this.notificadorFactory = notificadorFactory;
    }


    /**
     * Agrega un notificador (Observer)
     */
    public void agregarNotificador(Notificador notificador) {
        notificadores.add(notificador);
        log.info("Notificador agregado: {}", notificador.getClass().getSimpleName());
    }

    /**
     * Remueve un notificador
     */
    public void removerNotificador(Notificador notificador) {
        notificadores.remove(notificador);
        log.info("Notificador removido: {}", notificador.getClass().getSimpleName());
    }

    /**
     * Notifica la creación de un partido
     */
    public void notificarPartidoCreado(Partido partido) {
        log.info("Notificando creación del partido: {}", partido.getId());
        
        List<String> destinatarios = obtenerDestinatariosParaDeporte(partido);
        String mensaje = String.format(
            "¡Nuevo partido de %s! '%s' - %s. Faltan %d jugadores.",
            partido.getDeporte().getNombre(),
            partido.getTitulo(),
            partido.getFechaHora().toString(),
            partido.jugadoresFaltantes()
        );
        
        NotificacionEvent evento = NotificacionEvent.of(
            TipoNotificacion.PARTIDO_CREADO,
            mensaje,
            destinatarios,
            partido.getId()
        );
        
        notificarTodos(evento);
    }

    /**
     * Notifica que un partido está armado (completo)
     */
    public void notificarPartidoArmado(Partido partido) {
        log.info("Notificando partido armado: {}", partido.getId());
        
        List<String> destinatarios = obtenerEmailsJugadores(partido);
        String mensaje = String.format(
            "¡Partido armado! '%s' tiene suficientes jugadores. Esperando confirmación.",
            partido.getTitulo()
        );
        
        NotificacionEvent evento = NotificacionEvent.of(
            TipoNotificacion.PARTIDO_ARMADO,
            mensaje,
            destinatarios,
            partido.getId()
        );
        
        notificarTodos(evento);
    }

    /**
     * Notifica que un partido está confirmado
     */
    public void notificarPartidoConfirmado(Partido partido) {
        log.info("Notificando partido confirmado: {}", partido.getId());
        
        List<String> destinatarios = obtenerEmailsJugadores(partido);
        String mensaje = String.format(
            "¡Partido confirmado! '%s' el %s. ¡Nos vemos en la cancha!",
            partido.getTitulo(),
            partido.getFechaHora().toString()
        );
        
        NotificacionEvent evento = NotificacionEvent.of(
            TipoNotificacion.PARTIDO_CONFIRMADO,
            mensaje,
            destinatarios,
            partido.getId()
        );
        
        notificarTodos(evento);
    }

    /**
     * Notifica que un partido está en juego
     */
    public void notificarPartidoEnJuego(Partido partido) {
        log.info("Notificando partido en juego: {}", partido.getId());
        
        List<String> destinatarios = obtenerEmailsJugadores(partido);
        String mensaje = String.format(
            "¡El partido '%s' ha comenzado! ¡A jugar!",
            partido.getTitulo()
        );
        
        NotificacionEvent evento = NotificacionEvent.of(
            TipoNotificacion.PARTIDO_EN_JUEGO,
            mensaje,
            destinatarios,
            partido.getId()
        );
        
        notificarTodos(evento);
    }

    /**
     * Notifica que un partido ha finalizado
     */
    public void notificarPartidoFinalizado(Partido partido) {
        log.info("Notificando partido finalizado: {}", partido.getId());
        
        List<String> destinatarios = obtenerEmailsJugadores(partido);
        String mensaje = String.format(
            "El partido '%s' ha finalizado. ¡Gracias por participar! Puedes dejar comentarios y ver estadísticas.",
            partido.getTitulo()
        );
        
        NotificacionEvent evento = NotificacionEvent.of(
            TipoNotificacion.PARTIDO_FINALIZADO,
            mensaje,
            destinatarios,
            partido.getId()
        );
        
        notificarTodos(evento);
    }

    /**
     * Notifica que un partido ha sido cancelado
     */
    public void notificarPartidoCancelado(Partido partido) {
        log.info("Notificando partido cancelado: {}", partido.getId());
        
        List<String> destinatarios = obtenerEmailsJugadores(partido);
        String mensaje = String.format(
            "El partido '%s' programado para %s ha sido cancelado.",
            partido.getTitulo(),
            partido.getFechaHora().toString()
        );
        
        NotificacionEvent evento = NotificacionEvent.of(
            TipoNotificacion.PARTIDO_CANCELADO,
            mensaje,
            destinatarios,
            partido.getId()
        );
        
        notificarTodos(evento);
    }

    /**
     * Notifica que un jugador se unió al partido
     */
    public void notificarJugadorUnido(Partido partido, String nombreJugador) {
        log.info("Notificando jugador unido al partido: {}", partido.getId());
        
        List<String> destinatarios = obtenerEmailsJugadores(partido);
        String mensaje = String.format(
            "%s se unió al partido '%s'. Faltan %d jugadores.",
            nombreJugador,
            partido.getTitulo(),
            partido.jugadoresFaltantes()
        );
        
        NotificacionEvent evento = NotificacionEvent.of(
            TipoNotificacion.JUGADOR_UNIDO,
            mensaje,
            destinatarios,
            partido.getId()
        );
        
        notificarTodos(evento);
    }

    /**
     * Envía una notificación personalizada
     */
    public void enviarNotificacionPersonalizada(TipoNotificacion tipo, String mensaje, 
                                               List<String> destinatarios, Long partidoId) {
        log.info("Enviando notificación personalizada: {}", tipo);
        
        NotificacionEvent evento = NotificacionEvent.of(tipo, mensaje, destinatarios, partidoId);
        notificarTodos(evento);
    }

    /**
     * Notifica a todos los observadores registrados
     */
    private void notificarTodos(NotificacionEvent evento) {
        for (String email : evento.getDestinatarios()) {
            log.info("Notificando a: {}", email);

            PreferenciaNotificacion pref = preferenciasService.obtenerPorEmail(email)
                    .orElse(PreferenciaNotificacion.porDefecto());

            for (Notificador notificador : notificadores) {
                if (notificador.estaHabilitado(pref)) {
                    try {
                        notificador.notificar(evento);
                    } catch (Exception e) {
                        log.error("Error al notificar a {} por {}: {}", email, notificador.getClass().getSimpleName(), e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Obtiene los emails de los jugadores de un partido
     */
    private List<String> obtenerEmailsJugadores(Partido partido) {
        return partido.getJugadores().stream()
                .map(Jugador::getEmail)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene destinatarios para notificaciones de nuevo partido basado en el deporte
     * Esto podría conectarse con un servicio de preferencias de usuario
     */
    private List<String> obtenerDestinatariosParaDeporte(Partido partido) {
        // Por ahora, retorna lista vacía
        // En una implementación real, buscaría usuarios interesados en el deporte
        return new ArrayList<>();
    }

    /**
     * Obtiene la lista de notificadores registrados
     */
    public List<Notificador> getNotificadores() {
        return new ArrayList<>(notificadores);
    }

    /**
     * Crea un notificador basado en el tipo
     */
    public void crearNotificadorPorTipo(String tipo) {
        Notificador notificador = notificadorFactory.crearPorTipo(tipo);
        if (notificador != null) {
            agregarNotificador(notificador);
        }
    }

}