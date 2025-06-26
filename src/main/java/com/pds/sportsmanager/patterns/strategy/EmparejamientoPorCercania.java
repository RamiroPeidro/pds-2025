package com.pds.sportsmanager.patterns.strategy;

import com.pds.sportsmanager.model.dto.PartidoBusquedaResult;
import com.pds.sportsmanager.model.entity.Jugador;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Estrategia de emparejamiento por cercanía geográfica
 */
@Component("estrategiaPorCercania")
@Builder
@RequiredArgsConstructor
public class EmparejamientoPorCercania implements EstrategiaEmparejamiento {

    /**
     * Record para criterios de puntuación con Lombok Builder
     */
    @Builder
    public record CriteriosPuntuacion(
            PuntuacionDistancia distancia,
            PuntuacionNivel nivel,
            PuntuacionJugadores jugadores,
            double deporteFavorito,
            double sinUbicacion
    ) {
        public CriteriosPuntuacion {
            if (distancia == null) distancia = PuntuacionDistancia.defaultConfig();
            if (nivel == null) nivel = PuntuacionNivel.defaultConfig();
            if (jugadores == null) jugadores = PuntuacionJugadores.defaultConfig();
        }

        public static CriteriosPuntuacion defaultConfig() {
            return CriteriosPuntuacion.builder().build();
        }
    }


    /**
     * Record para puntuación por distancia con Lombok Builder
     */
    @Builder
    public record PuntuacionDistancia(
            double muycerca,
            double cerca,
            double media,
            double lejos,
            double muyLejos,
            double fueraRango
    ) {
        public PuntuacionDistancia {
            if (muycerca == 0.0) muycerca = 100.0;
            if (cerca == 0.0) cerca = 90.0;
            if (media == 0.0) media = 70.0;
            if (lejos == 0.0) lejos = 50.0;
            if (muyLejos == 0.0) muyLejos = 20.0;
            if (fueraRango == 0.0) fueraRango = 5.0;
        }

        public static PuntuacionDistancia defaultConfig() {
            return PuntuacionDistancia.builder().build();
        }

        public double calcularPuntuacion(double distanciaKm) {
            return switch (getRangoDistancia(distanciaKm)) {
                case MUY_CERCA -> muycerca;
                case CERCA -> cerca;
                case MEDIA -> media;
                case LEJOS -> lejos;
                case MUY_LEJOS -> muyLejos;
                case FUERA_RANGO -> fueraRango;
            };
        }

        private RangoDistancia getRangoDistancia(double distancia) {
            if (distancia <= 1.0) return RangoDistancia.MUY_CERCA;
            if (distancia <= 5.0) return RangoDistancia.CERCA;
            if (distancia <= 10.0) return RangoDistancia.MEDIA;
            if (distancia <= 20.0) return RangoDistancia.LEJOS;
            if (distancia <= 50.0) return RangoDistancia.MUY_LEJOS;
            return RangoDistancia.FUERA_RANGO;
        }

        private enum RangoDistancia {
            MUY_CERCA, CERCA, MEDIA, LEJOS, MUY_LEJOS, FUERA_RANGO
        }
    }


    /**
     * Record para puntuación por nivel con Lombok Builder
     */
    @Builder
    public record PuntuacionNivel(
            double compatible,
            double abierto
    ) { 
        public PuntuacionNivel {
            if (compatible == 0.0) compatible = 15.0;
            if (abierto == 0.0) abierto = 10.0;
        }

        public static PuntuacionNivel defaultConfig() {
            return PuntuacionNivel.builder().build();
        }
    }


    /**
     * Record para puntuación por jugadores faltantes con Lombok Builder
     */
    @Builder
    public record PuntuacionJugadores(
            double casiCompleto,
            double parcialmenteCompleto,
            double pocosJugadores
    ) {
        // Constructor compact para defaults
        public PuntuacionJugadores {
            if (casiCompleto == 0.0) casiCompleto = 15.0;
            if (parcialmenteCompleto == 0.0) parcialmenteCompleto = 10.0;
            if (pocosJugadores == 0.0) pocosJugadores = 5.0;
        }

        public static PuntuacionJugadores defaultConfig() {
            return PuntuacionJugadores.builder().build();
        }

        public double calcularPuntuacion(int jugadoresFaltantes) {
            return switch (jugadoresFaltantes) {
                case 1 -> casiCompleto;
                case 2 -> parcialmenteCompleto;
                case 3, 4 -> pocosJugadores;
                default -> 0.0;
            };
        }
    }



    @Builder
    public record ResultadoCompatibilidad(
            double puntuacionDistancia,
            double puntuacionNivel,
            double puntuacionJugadores,
            double puntuacionDeporte,
            double puntuacionTotal
    ) {

        public static ResultadoCompatibilidad calcular(Jugador jugador, PartidoBusquedaResult partido, CriteriosPuntuacion criterios) {
            var builder = ResultadoCompatibilidad.builder();

            double puntuacionDistancia = calcularPuntuacionDistancia(jugador, partido, criterios);
            builder.puntuacionDistancia(puntuacionDistancia);

            double puntuacionNivel = calcularPuntuacionNivel(jugador, partido, criterios);
            builder.puntuacionNivel(puntuacionNivel);

            double puntuacionJugadores = criterios.jugadores().calcularPuntuacion(partido.jugadoresFaltantes());
            builder.puntuacionJugadores(puntuacionJugadores);

            double puntuacionDeporte = esDeporteFavorito(jugador, partido) ? criterios.deporteFavorito() : 0.0;
            builder.puntuacionDeporte(puntuacionDeporte);

            double total = puntuacionDistancia + puntuacionNivel + puntuacionJugadores + puntuacionDeporte;
            builder.puntuacionTotal(Math.max(0, total));

            return builder.build();
        }

        private static double calcularPuntuacionDistancia(Jugador jugador, PartidoBusquedaResult partido, CriteriosPuntuacion criterios) {
            if (jugador.getUbicacion() == null || partido.distanciaKm() == null) {
                return criterios.sinUbicacion();
            }
            return criterios.distancia().calcularPuntuacion(partido.distanciaKm());
        }

        private static double calcularPuntuacionNivel(Jugador jugador, PartidoBusquedaResult partido, CriteriosPuntuacion criterios) {
            if (jugador.getNivelDeJuego() == null) return 0.0;

            return switch (evaluarCompatibilidadNivel(jugador, partido)) {
                case COMPATIBLE -> criterios.nivel().compatible();
                case ABIERTO -> criterios.nivel().abierto();
                case INCOMPATIBLE -> 0.0;
            };
        }

        private static CompatibilidadNivel evaluarCompatibilidadNivel(Jugador jugador, PartidoBusquedaResult partido) {
            if (partido.nivelMinimo() == null && partido.nivelMaximo() == null) {
                return CompatibilidadNivel.ABIERTO;
            }

            if (partido.nivelMinimo() != null && partido.nivelMaximo() != null) {
                int nivelUser = jugador.getNivelDeJuego().ordinal();
                int nivelMin = partido.nivelMinimo().ordinal();
                int nivelMax = partido.nivelMaximo().ordinal();
                
                return (nivelUser >= nivelMin && nivelUser <= nivelMax) 
                    ? CompatibilidadNivel.COMPATIBLE 
                    : CompatibilidadNivel.INCOMPATIBLE;
            }

            return CompatibilidadNivel.INCOMPATIBLE;
        }

        private static boolean esDeporteFavorito(Jugador jugador, PartidoBusquedaResult partido) {
            return jugador.getDeporteFavorito() != null &&
                    jugador.getDeporteFavorito().equals(partido.deporte());
        }

        private enum CompatibilidadNivel {
            COMPATIBLE, ABIERTO, INCOMPATIBLE
        }
    }

    private final CriteriosPuntuacion criterios = CriteriosPuntuacion.defaultConfig();

    @Override
    public String getNombre() {
        return "POR_CERCANIA";
    }

    @Override
    public String getDescripcion() {
        return "Prioriza partidos más cercanos geográficamente usando criterios configurables";
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
        return ResultadoCompatibilidad.calcular(jugador, partido, criterios)
                .puntuacionTotal();
    }


    public static EmparejamientoPorCercania conCriterios(CriteriosPuntuacion criteriosPersonalizados) {
        EmparejamientoPorCercania estrategia = EmparejamientoPorCercania.builder().build();
        return estrategia;
    }


    public static EmparejamientoPorCercania conDistanciaPersonalizada() {
        PuntuacionDistancia distancia = PuntuacionDistancia.builder()
                .muycerca(120.0)
                .cerca(100.0)
                .media(80.0)
                .lejos(60.0)
                .muyLejos(30.0)
                .fueraRango(10.0)
                .build();
        PuntuacionNivel nivel = PuntuacionNivel.defaultConfig();
        PuntuacionJugadores jugadores = PuntuacionJugadores.defaultConfig();
        CriteriosPuntuacion criteriosCustom = CriteriosPuntuacion.builder()
                .distancia(distancia)
                .nivel(nivel)
                .jugadores(jugadores)
                .deporteFavorito(10.0)
                .sinUbicacion(5.0)
                .build();

        EmparejamientoPorCercania estrategia = EmparejamientoPorCercania.builder().build();
        return estrategia;
    }

    public static EmparejamientoPorCercania paraTorneos() {
        PuntuacionDistancia distancia = PuntuacionDistancia.builder()
                .muycerca(150.0)
                .cerca(130.0)
                .media(70.0)
                .lejos(50.0)
                .muyLejos(20.0)
                .fueraRango(5.0)
                .build();
        PuntuacionNivel nivel = PuntuacionNivel.defaultConfig();
        PuntuacionJugadores jugadores = PuntuacionJugadores.defaultConfig();
        CriteriosPuntuacion criterios = CriteriosPuntuacion.builder()
                .distancia(distancia)
                .nivel(nivel)
                .jugadores(jugadores)
                .deporteFavorito(25.0)
                .sinUbicacion(5.0)
                .build();
        EmparejamientoPorCercania estrategia = EmparejamientoPorCercania.builder().build();
        return estrategia;
    }
} 