package com.pds.sportsmanager.patterns.strategy;

import com.pds.sportsmanager.model.dto.PartidoBusquedaResult;
import com.pds.sportsmanager.model.entity.Jugador;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Estrategia de emparejamiento por nivel de habilidad
 */
@Component("estrategiaPorNivel")
@Builder
@RequiredArgsConstructor
public class EmparejamientoPorNivel implements EstrategiaEmparejamiento {

    
    @Builder
    public record CriteriosPuntuacionNivel(
            PuntuacionCompatibilidadNivel compatibilidad,
            PuntuacionDistancia distancia,
            double deporteFavorito
    ) {
        public CriteriosPuntuacionNivel {
            if (compatibilidad == null) compatibilidad = PuntuacionCompatibilidadNivel.defaultConfig();
            if (distancia == null) distancia = PuntuacionDistancia.defaultConfig();
        }

        public static CriteriosPuntuacionNivel defaultConfig() {
            return CriteriosPuntuacionNivel.builder().build();
        }
    }

    
    @Builder
    public record PuntuacionCompatibilidadNivel(
            double nivelPerfecto,
            double penalizacionPorDistancia,
            double nivelAbierto,
            double soloMinimo,
            double soloMaximo
    ) {
        public PuntuacionCompatibilidadNivel {
            if (nivelPerfecto == 0.0) nivelPerfecto = 100.0;
            if (penalizacionPorDistancia == 0.0) penalizacionPorDistancia = 20.0;
            if (nivelAbierto == 0.0) nivelAbierto = 50.0;
            if (soloMinimo == 0.0) soloMinimo = 70.0;
            if (soloMaximo == 0.0) soloMaximo = 70.0;
        }

        public static PuntuacionCompatibilidadNivel defaultConfig() {
            return PuntuacionCompatibilidadNivel.builder().build();
        }

        public double calcularPuntuacion(Jugador jugador, PartidoBusquedaResult partido) {
            if (jugador.getNivelDeJuego() == null) return 0.0;

            return switch (evaluarTipoCompatibilidad(jugador, partido)) {
                case RANGO_COMPLETO -> calcularPuntuacionRangoCompleto(jugador, partido);
                case ABIERTO -> nivelAbierto();
                case SOLO_MINIMO -> soloMinimo();
                case SOLO_MAXIMO -> soloMaximo();
                case INCOMPATIBLE -> 0.0;
            };
        }

        private TipoCompatibilidad evaluarTipoCompatibilidad(Jugador jugador, PartidoBusquedaResult partido) {
            boolean tieneMinimo = partido.nivelMinimo() != null;
            boolean tieneMaximo = partido.nivelMaximo() != null;
            
            if (!tieneMinimo && !tieneMaximo) return TipoCompatibilidad.ABIERTO;
            
            if (tieneMinimo && tieneMaximo) {
                int nivelUser = jugador.getNivelDeJuego().ordinal();
                int nivelMin = partido.nivelMinimo().ordinal();
                int nivelMax = partido.nivelMaximo().ordinal();
                
                return (nivelUser >= nivelMin && nivelUser <= nivelMax) 
                    ? TipoCompatibilidad.RANGO_COMPLETO 
                    : TipoCompatibilidad.INCOMPATIBLE;
            }
            
            if (tieneMinimo) {
                return jugador.getNivelDeJuego().ordinal() >= partido.nivelMinimo().ordinal()
                    ? TipoCompatibilidad.SOLO_MINIMO
                    : TipoCompatibilidad.INCOMPATIBLE;
            }
            
            return jugador.getNivelDeJuego().ordinal() <= partido.nivelMaximo().ordinal()
                ? TipoCompatibilidad.SOLO_MAXIMO
                : TipoCompatibilidad.INCOMPATIBLE;
        }

        private double calcularPuntuacionRangoCompleto(Jugador jugador, PartidoBusquedaResult partido) {
            int nivelMin = partido.nivelMinimo().ordinal();
            int nivelMax = partido.nivelMaximo().ordinal();
            int nivelUser = jugador.getNivelDeJuego().ordinal();
            
            double centro = (nivelMin + nivelMax) / 2.0;
            double distancia = Math.abs(nivelUser - centro);
            
            return Math.max(0, nivelPerfecto() - (distancia * penalizacionPorDistancia()));
        }

        private enum TipoCompatibilidad {
            RANGO_COMPLETO, ABIERTO, SOLO_MINIMO, SOLO_MAXIMO, INCOMPATIBLE
        }
    }

    @Builder
    public record PuntuacionDistancia(
            double muyerca,
            double media,
            double lejos
    ) {
        public PuntuacionDistancia {
            if (muyerca == 0.0) muyerca = 5.0;
            if (media == 0.0) media = 0.0;
            if (lejos == 0.0) lejos = -10.0;
        }

        public static PuntuacionDistancia defaultConfig() {
            return PuntuacionDistancia.builder().build();
        }

        public double calcularPuntuacion(Double distanciaKm) {
            if (distanciaKm == null) return 0.0;
            
            return switch (getRangoDistancia(distanciaKm)) {
                case MUY_CERCA -> muyerca();
                case MEDIA -> media();
                case LEJOS -> lejos();
            };
        }

        private RangoDistancia getRangoDistancia(double distancia) {
            if (distancia <= 5.0) return RangoDistancia.MUY_CERCA;
            if (distancia <= 20.0) return RangoDistancia.MEDIA;
            return RangoDistancia.LEJOS;
        }

        private enum RangoDistancia {
            MUY_CERCA, MEDIA, LEJOS
        }
    }

    
    @Builder
    public record ResultadoCompatibilidadNivel(
            double puntuacionNivel,
            double puntuacionDistancia,
            double puntuacionDeporte,
            double puntuacionTotal
    ) {
        public static ResultadoCompatibilidadNivel calcular(Jugador jugador, PartidoBusquedaResult partido, CriteriosPuntuacionNivel criterios) {
            var builder = ResultadoCompatibilidadNivel.builder();

            double puntuacionNivel = criterios.compatibilidad().calcularPuntuacion(jugador, partido);
            builder.puntuacionNivel(puntuacionNivel);

            double puntuacionDistancia = criterios.distancia().calcularPuntuacion(partido.distanciaKm());
            builder.puntuacionDistancia(puntuacionDistancia);

            double puntuacionDeporte = esDeporteFavorito(jugador, partido) ? criterios.deporteFavorito() : 0.0;
            builder.puntuacionDeporte(puntuacionDeporte);

            double total = puntuacionNivel + puntuacionDistancia + puntuacionDeporte;
            builder.puntuacionTotal(Math.max(0, total));

            return builder.build();
        }

        private static boolean esDeporteFavorito(Jugador jugador, PartidoBusquedaResult partido) {
            return jugador.getDeporteFavorito() != null &&
                   jugador.getDeporteFavorito().equals(partido.deporte());

        }
    }

    private final CriteriosPuntuacionNivel criterios = CriteriosPuntuacionNivel.defaultConfig();

    @Override
    public String getNombre() {
        return "POR_NIVEL";
    }

    @Override
    public String getDescripcion() {
        return "Prioriza partidos con jugadores de nivel similar usando criterios configurables";
    }

    @Override
    public List<PartidoBusquedaResult> buscarPartidos(Jugador jugador, List<PartidoBusquedaResult> partidosDisponibles) {
        return partidosDisponibles.stream()
                .filter(partido -> partido.necesitaJugadores())
                .filter(partido -> partido.esNivelCompatible(jugador.getNivelDeJuego()))
                .sorted((p1, p2) -> Double.compare(
                        calcularCompatibilidad(jugador, p2), // Orden descendente
                        calcularCompatibilidad(jugador, p1)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public double calcularCompatibilidad(Jugador jugador, PartidoBusquedaResult partido) {
        return ResultadoCompatibilidadNivel.calcular(jugador, partido, criterios)
                .puntuacionTotal();
    }

    public static EmparejamientoPorNivel conCriterios(CriteriosPuntuacionNivel criteriosPersonalizados) {
        EmparejamientoPorNivel estrategia = EmparejamientoPorNivel.builder().build();
        return estrategia;
    }

    public static EmparejamientoPorNivel estricto() {
        PuntuacionCompatibilidadNivel compatibilidad = PuntuacionCompatibilidadNivel.builder()
                .nivelPerfecto(120.0)
                .penalizacionPorDistancia(30.0)
                .nivelAbierto(20.0)
                .soloMinimo(70.0)
                .soloMaximo(70.0)
                .build();
        PuntuacionDistancia distancia = PuntuacionDistancia.defaultConfig();
        CriteriosPuntuacionNivel criterios = CriteriosPuntuacionNivel.builder()
                .compatibilidad(compatibilidad)
                .distancia(distancia)
                .deporteFavorito(30.0)
                .build();
        EmparejamientoPorNivel estrategia = EmparejamientoPorNivel.builder().build();
        return estrategia;
    }

    public static EmparejamientoPorNivel flexible() {
        PuntuacionCompatibilidadNivel compatibilidad = PuntuacionCompatibilidadNivel.builder()
                .nivelPerfecto(80.0)
                .penalizacionPorDistancia(10.0)
                .nivelAbierto(70.0)
                .soloMinimo(70.0)
                .soloMaximo(70.0)
                .build();
        PuntuacionDistancia distancia = PuntuacionDistancia.defaultConfig();
        CriteriosPuntuacionNivel criterios = CriteriosPuntuacionNivel.builder()
                .compatibilidad(compatibilidad)
                .distancia(distancia)
                .deporteFavorito(15.0)
                .build();
        EmparejamientoPorNivel estrategia = EmparejamientoPorNivel.builder().build();
        return estrategia;
    }
} 