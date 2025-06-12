package com.pds.sportsmanager.patterns.state;

/**
 * Factory para crear estados usando singleton instances
 * Evita la creación repetida de objetos stateless con 'new'
 */
public class EstadoPartidoFactory {
    
    private static final EstadoPartido NECESITAMOS_JUGADORES = new NecesitamosJugadores();
    private static final EstadoPartido PARTIDO_ARMADO = new PartidoArmado();
    private static final EstadoPartido PARTIDO_CONFIRMADO = new PartidoConfirmado();
    private static final EstadoPartido EN_JUEGO = new EnJuego();
    private static final EstadoPartido PARTIDO_FINALIZADO = new PartidoFinalizado();
    private static final EstadoPartido PARTIDO_CANCELADO = new PartidoCancelado();

    /**
     * Crea estado desde el nombre (usado por JPA)
     */
    public static EstadoPartido fromNombre(String nombre) {
        return switch (nombre) {
            case "NECESITAMOS_JUGADORES" -> necesitamosJugadores();
            case "PARTIDO_ARMADO" -> partidoArmado();
            case "PARTIDO_CONFIRMADO" -> partidoConfirmado();
            case "EN_JUEGO" -> enJuego();
            case "PARTIDO_FINALIZADO" -> partidoFinalizado();
            case "PARTIDO_CANCELADO" -> partidoCancelado();
            default -> necesitamosJugadores(); 
        };
    }

    public static EstadoPartido necesitamosJugadores() {
        return NECESITAMOS_JUGADORES;
    }

    public static EstadoPartido partidoArmado() {
        return PARTIDO_ARMADO;
    }

    public static EstadoPartido partidoConfirmado() {
        return PARTIDO_CONFIRMADO;
    }

    public static EstadoPartido enJuego() {
        return EN_JUEGO;
    }

    public static EstadoPartido partidoFinalizado() {
        return PARTIDO_FINALIZADO;
    }

    public static EstadoPartido partidoCancelado() {
        return PARTIDO_CANCELADO;
    }
} 