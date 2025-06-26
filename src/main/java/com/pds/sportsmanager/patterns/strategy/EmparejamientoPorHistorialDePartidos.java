package com.pds.sportsmanager.patterns.strategy;

import com.pds.sportsmanager.model.dto.PartidoBusquedaResult;
import com.pds.sportsmanager.model.entity.Jugador;
import com.pds.sportsmanager.model.entity.Partido;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Estrategia de emparejamiento basada en el historial de partidos del jugador
 * Prioriza partidos con jugadores que tengan historiales similares y deportes conocidos
 */
@Component("estrategiaPorHistorial")
@Builder
@RequiredArgsConstructor
public class EmparejamientoPorHistorialDePartidos implements EstrategiaEmparejamiento {

    /**
     * Record para criterios de puntuación basados en historial
     */
    @Builder
    public record CriteriosHistorial(
            PuntuacionExperienciaDeporte experienciaDeporte,
            PuntuacionFrecuenciaJuego frecuenciaJuego,
            PuntuacionCompatibilidadJugadores compatibilidadJugadores,
            double deporteFavorito,
            double jugadorNuevo,
            double distanciaMaxima
    ) {
        public CriteriosHistorial {
            if (experienciaDeporte == null) experienciaDeporte = PuntuacionExperienciaDeporte.defaultConfig();
            if (frecuenciaJuego == null) frecuenciaJuego = PuntuacionFrecuenciaJuego.defaultConfig();
            if (compatibilidadJugadores == null) compatibilidadJugadores = PuntuacionCompatibilidadJugadores.defaultConfig();
        }

        public static CriteriosHistorial defaultConfig() {
            return CriteriosHistorial.builder()
                    .deporteFavorito(25.0)
                    .jugadorNuevo(15.0)
                    .distanciaMaxima(20.0) // km máxima considerada
                    .build();
        }
    }

    /**
     * Puntuación basada en experiencia en el deporte específico
     */
    @Builder
    public record PuntuacionExperienciaDeporte(
            double sinExperiencia,
            double pocaExperiencia,      // 1-3 partidos
            double experienciaMedia,     // 4-10 partidos
            double muchaExperiencia,     // 11+ partidos
            double deporteConocido       // ha jugado este deporte antes
    ) {
        public static PuntuacionExperienciaDeporte defaultConfig() {
            return PuntuacionExperienciaDeporte.builder()
                    .sinExperiencia(5.0)
                    .pocaExperiencia(15.0)
                    .experienciaMedia(30.0)
                    .muchaExperiencia(50.0)
                    .deporteConocido(20.0)
                    .build();
        }

        public double calcularPuntuacion(Jugador jugador, String deporte) {
            int partidosEnDeporte = contarPartidosEnDeporte(jugador, deporte);
            boolean conoceDeporte = jugador.getDeporteFavorito() != null && 
                                  jugador.getDeporteFavorito().equals(deporte);

            double puntuacionBase = switch (partidosEnDeporte) {
                case 0 -> sinExperiencia;
                case 1, 2, 3 -> pocaExperiencia;
                case 4, 5, 6, 7, 8, 9, 10 -> experienciaMedia;
                default -> muchaExperiencia;
            };

            return conoceDeporte ? puntuacionBase + deporteConocido : puntuacionBase;
        }

        private int contarPartidosEnDeporte(Jugador jugador, String deporte) {
            return (int) jugador.getPartidos().stream()
                    .filter(p -> p.getDeporte().getNombre().equals(deporte))
                    .filter(p -> "PARTIDO_FINALIZADO".equals(p.getEstadoNombre()))
                    .count();
        }
    }

    /**
     * Puntuación basada en frecuencia de juego del usuario
     */
    @Builder
    public record PuntuacionFrecuenciaJuego(
            double jugadorInactivo,      // no ha jugado en 60+ días
            double jugadorOcasional,     // último partido hace 30-60 días
            double jugadorRegular,       // último partido hace 7-30 días
            double jugadorActivo,        // último partido hace menos de 7 días
            double penalizacionInactivo
    ) {
        public static PuntuacionFrecuenciaJuego defaultConfig() {
            return PuntuacionFrecuenciaJuego.builder()
                    .jugadorInactivo(5.0)
                    .jugadorOcasional(15.0)
                    .jugadorRegular(30.0)
                    .jugadorActivo(40.0)
                    .penalizacionInactivo(-10.0)
                    .build();
        }

        public double calcularPuntuacion(Jugador jugador) {
            LocalDateTime ultimoPartido = obtenerFechaUltimoPartido(jugador);
            
            if (ultimoPartido == null) {
                return jugadorInactivo + penalizacionInactivo;
            }

            long diasDesdeUltimoPartido = LocalDateTime.now().toLocalDate()
                    .toEpochDay() - ultimoPartido.toLocalDate().toEpochDay();

            return switch ((int) diasDesdeUltimoPartido) {
                case 0, 1, 2, 3, 4, 5, 6 -> jugadorActivo;
                case 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 -> jugadorRegular;
                case 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60 -> jugadorOcasional;
                default -> jugadorInactivo;
            };
        }

        private LocalDateTime obtenerFechaUltimoPartido(Jugador jugador) {
            return jugador.getPartidos().stream()
                    .filter(p -> "PARTIDO_FINALIZADO".equals(p.getEstadoNombre()))
                    .map(Partido::getFechaHora)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
        }
    }

    /**
     * Puntuación basada en compatibilidad con otros jugadores del partido
     */
    @Builder
    public record PuntuacionCompatibilidadJugadores(
            double jugadoresConocidos,    // ha jugado antes con algunos
            double jugadoresDesconocidos, // nunca ha jugado con ninguno
            double mixtoConocidos,        // mezcla de conocidos y desconocidos
            double organizadorConocido    // conoce al organizador
    ) {
        public static PuntuacionCompatibilidadJugadores defaultConfig() {
            return PuntuacionCompatibilidadJugadores.builder()
                    .jugadoresConocidos(25.0)
                    .jugadoresDesconocidos(10.0)
                    .mixtoConocidos(20.0)
                    .organizadorConocido(15.0)
                    .build();
        }

        public double calcularPuntuacion(Jugador jugador, PartidoBusquedaResult partido) {
            // Por simplicidad, asumimos que no tenemos acceso directo a los jugadores del partido
            // En una implementación real, necesitaríamos acceso al repository para obtener esta info
            
            // Por ahora, damos puntuación base por jugadores desconocidos
            // TODO: Implementar lógica real cuando tengamos acceso a los jugadores del partido
            return jugadoresDesconocidos;
        }
    }

    /**
     * Resultado del cálculo de compatibilidad con desglose detallado
     */
    @Builder
    public record ResultadoCompatibilidadHistorial(
            double puntuacionExperiencia,
            double puntuacionFrecuencia,
            double puntuacionCompatibilidad,
            double puntuacionDeporte,
            double puntuacionDistancia,
            double puntuacionTotal
    ) {
        public static ResultadoCompatibilidadHistorial calcular(
                Jugador jugador, 
                PartidoBusquedaResult partido, 
                CriteriosHistorial criterios) {
            
            var builder = ResultadoCompatibilidadHistorial.builder();

            double puntuacionExperiencia = criterios.experienciaDeporte()
                    .calcularPuntuacion(jugador, partido.deporte());
            builder.puntuacionExperiencia(puntuacionExperiencia);

            double puntuacionFrecuencia = criterios.frecuenciaJuego()
                    .calcularPuntuacion(jugador);
            builder.puntuacionFrecuencia(puntuacionFrecuencia);

            double puntuacionCompatibilidad = criterios.compatibilidadJugadores()
                    .calcularPuntuacion(jugador, partido);
            builder.puntuacionCompatibilidad(puntuacionCompatibilidad);

            double puntuacionDeporte = esDeporteFavorito(jugador, partido) ? 
                    criterios.deporteFavorito() : 0.0;
            builder.puntuacionDeporte(puntuacionDeporte);

            double puntuacionDistancia = calcularPuntuacionDistancia(jugador, partido, criterios);
            builder.puntuacionDistancia(puntuacionDistancia);

            double total = puntuacionExperiencia + puntuacionFrecuencia + 
                          puntuacionCompatibilidad + puntuacionDeporte + puntuacionDistancia;
            builder.puntuacionTotal(Math.max(0, total));

            return builder.build();
        }

        private static boolean esDeporteFavorito(Jugador jugador, PartidoBusquedaResult partido) {
            return jugador.getDeporteFavorito() != null &&
                   jugador.getDeporteFavorito().equals(partido.deporte());
        }

        private static double calcularPuntuacionDistancia(
                Jugador jugador, 
                PartidoBusquedaResult partido, 
                CriteriosHistorial criterios) {
            
            if (jugador.getUbicacion() == null || partido.distanciaKm() == null) {
                return 0.0;
            }

            double distancia = partido.distanciaKm();
            double maxDistancia = criterios.distanciaMaxima();

            if (distancia > maxDistancia) {
                return -15.0; // Penalización por distancia excesiva
            }

            // Puntuación inversamente proporcional a la distancia
            return Math.max(0, 15.0 * (1 - (distancia / maxDistancia)));
        }
    }

    private final CriteriosHistorial criterios = CriteriosHistorial.defaultConfig();

    @Override
    public String getNombre() {
        return "POR_HISTORIAL";
    }

    @Override
    public String getDescripcion() {
        return "Prioriza partidos basándose en el historial de juego, experiencia en deportes y frecuencia de actividad del usuario";
    }

    @Override
    public List<PartidoBusquedaResult> buscarPartidos(Jugador jugador, List<PartidoBusquedaResult> partidosDisponibles) {
        return partidosDisponibles.stream()
                .filter(partido -> partido.necesitaJugadores())
                .filter(partido -> partido.esNivelCompatible(jugador.getNivelDeJuego()))
                .sorted((p1, p2) -> Double.compare(
                        calcularCompatibilidad(jugador, p2),
                        calcularCompatibilidad(jugador, p1)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public double calcularCompatibilidad(Jugador jugador, PartidoBusquedaResult partido) {
        return ResultadoCompatibilidadHistorial.calcular(jugador, partido, criterios)
                .puntuacionTotal();
    }

    /**
     * Factory method para crear estrategia con criterios personalizados
     */
    public static EmparejamientoPorHistorialDePartidos conCriterios(CriteriosHistorial criteriosPersonalizados) {
        return EmparejamientoPorHistorialDePartidos.builder().build();
    }

    /**
     * Factory method para jugadores experimentados (prioriza experiencia y frecuencia)
     */
    public static EmparejamientoPorHistorialDePartidos paraExperimentados() {
        var experiencia = PuntuacionExperienciaDeporte.builder()
                .sinExperiencia(-5.0)  // penaliza falta de experiencia
                .pocaExperiencia(10.0)
                .experienciaMedia(35.0)
                .muchaExperiencia(60.0)
                .deporteConocido(30.0)
                .build();

        var frecuencia = PuntuacionFrecuenciaJuego.builder()
                .jugadorInactivo(-10.0)
                .jugadorOcasional(5.0)
                .jugadorRegular(35.0)
                .jugadorActivo(50.0)
                .penalizacionInactivo(-20.0)
                .build();

        var criterios = CriteriosHistorial.builder()
                .experienciaDeporte(experiencia)
                .frecuenciaJuego(frecuencia)
                .deporteFavorito(35.0)
                .jugadorNuevo(5.0)
                .distanciaMaxima(15.0)
                .build();

        return EmparejamientoPorHistorialDePartidos.builder().build();
    }

    /**
     * Factory method para jugadores principiantes (más tolerante, enfocado en aprender)
     */
    public static EmparejamientoPorHistorialDePartidos paraPrincipiantes() {
        var experiencia = PuntuacionExperienciaDeporte.builder()
                .sinExperiencia(20.0)  // premia a otros principiantes
                .pocaExperiencia(25.0)
                .experienciaMedia(15.0)
                .muchaExperiencia(5.0) // penaliza expertos para equilibrar
                .deporteConocido(15.0)
                .build();

        var frecuencia = PuntuacionFrecuenciaJuego.builder()
                .jugadorInactivo(15.0)
                .jugadorOcasional(20.0)
                .jugadorRegular(25.0)
                .jugadorActivo(20.0)
                .penalizacionInactivo(0.0)
                .build();

        var criterios = CriteriosHistorial.builder()
                .experienciaDeporte(experiencia)
                .frecuenciaJuego(frecuencia)
                .deporteFavorito(20.0)
                .jugadorNuevo(25.0)
                .distanciaMaxima(25.0) // más tolerante con distancia
                .build();

        return EmparejamientoPorHistorialDePartidos.builder().build();
    }
} 