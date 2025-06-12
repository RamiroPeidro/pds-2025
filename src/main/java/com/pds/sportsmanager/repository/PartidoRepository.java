package com.pds.sportsmanager.repository;

import com.pds.sportsmanager.model.entity.Partido;
import com.pds.sportsmanager.model.entity.Usuario;
import com.pds.sportsmanager.model.enums.NivelDeJugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {
    
    /**
     * Busca partidos por estado
     */
    List<Partido> findByEstadoNombre(String estadoNombre);
    
    /**
     * Busca partidos que necesitan jugadores
     */
    @Query("""
        SELECT p FROM Partido p 
        WHERE p.estadoNombre = 'NECESITAMOS_JUGADORES' 
        AND SIZE(p.jugadores) < p.cantidadJugadoresRequeridos
        AND p.fechaHora > :ahora
        """)
    List<Partido> findPartidosNecesitanJugadores(@Param("ahora") LocalDateTime ahora);
    
    /**
     * Busca partidos por deporte
     */
    @Query("SELECT p FROM Partido p WHERE p.deporte.id = :deporteId")
    List<Partido> findByDeporteId(@Param("deporteId") Long deporteId);
    
    /**
     * Busca partidos creados por un usuario
     */
    List<Partido> findByOwner(Usuario owner);
    
    /**
     * Busca partidos donde participa un usuario
     */
    @Query("SELECT p FROM Partido p JOIN p.jugadores j WHERE j.id = :usuarioId")
    List<Partido> findPartidosUsuarioParticipa(@Param("usuarioId") Long usuarioId);
    
    /**
     * Busca partidos cercanos a una ubicación
     */
    @Query("""
        SELECT p FROM Partido p 
        WHERE p.estadoNombre = 'NECESITAMOS_JUGADORES' 
        AND SIZE(p.jugadores) < p.cantidadJugadoresRequeridos
        AND p.fechaHora > :ahora
        AND p.ubicacion IS NOT NULL 
        AND (6371 * acos(cos(radians(:latitud)) * cos(radians(p.ubicacion.latitud)) * 
             cos(radians(p.ubicacion.longitud) - radians(:longitud)) + 
             sin(radians(:latitud)) * sin(radians(p.ubicacion.latitud)))) <= :radioKm
        """)
    List<Partido> findPartidosCercanos(
        @Param("latitud") Double latitud, 
        @Param("longitud") Double longitud, 
        @Param("radioKm") Double radioKm,
        @Param("ahora") LocalDateTime ahora
    );
    
    /**
     * Busca partidos compatibles con el nivel de un usuario
     */
    @Query("""
        SELECT p FROM Partido p 
        WHERE p.estadoNombre = 'NECESITAMOS_JUGADORES' 
        AND SIZE(p.jugadores) < p.cantidadJugadoresRequeridos
        AND p.fechaHora > :ahora
        AND (
            (p.nivelMinimo IS NULL AND p.nivelMaximo IS NULL) OR
            (p.nivelMinimo IS NULL AND p.nivelMaximo >= :nivel) OR
            (p.nivelMaximo IS NULL AND p.nivelMinimo <= :nivel) OR
            (p.nivelMinimo <= :nivel AND p.nivelMaximo >= :nivel)
        )
        """)
    List<Partido> findPartidosCompatiblesConNivel(
        @Param("nivel") NivelDeJugador nivel,
        @Param("ahora") LocalDateTime ahora
    );
    
    /**
     * Busca partidos para notificar (próximos a iniciar)
     */
    @Query("""
        SELECT p FROM Partido p 
        WHERE p.estadoNombre = 'PARTIDO_CONFIRMADO' 
        AND p.fechaHora BETWEEN :inicio AND :fin
        """)
    List<Partido> findPartidosParaIniciar(
        @Param("inicio") LocalDateTime inicio, 
        @Param("fin") LocalDateTime fin
    );
    
    /**
     * Busca partidos por rango de fechas
     */
    @Query("""
        SELECT p FROM Partido p 
        WHERE p.fechaHora BETWEEN :inicio AND :fin
        ORDER BY p.fechaHora ASC
        """)
    List<Partido> findByFechaHoraBetween(
        @Param("inicio") LocalDateTime inicio, 
        @Param("fin") LocalDateTime fin
    );
    
    /**
     * Cuenta partidos activos de un usuario
     */
    @Query("""
        SELECT COUNT(p) FROM Partido p JOIN p.jugadores j 
        WHERE j.id = :usuarioId 
        AND p.estadoNombre IN ('NECESITAMOS_JUGADORES', 'PARTIDO_ARMADO', 'PARTIDO_CONFIRMADO', 'EN_JUEGO')
        """)
    Long countPartidosActivosUsuario(@Param("usuarioId") Long usuarioId);
} 