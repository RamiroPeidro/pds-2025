package com.pds.sportsmanager.repository;

import com.pds.sportsmanager.model.entity.Jugador;
import com.pds.sportsmanager.model.enums.NivelDeJuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {
    
    /**
     * Busca un jugador por nombre
     */
    Optional<Jugador> findByNombre(String nombre);
    
    /**
     * Busca un jugador por email
     */
    Optional<Jugador> findByEmail(String email);
    
    /**
     * Verifica si existe un jugador con el nombre dado
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Verifica si existe un jugador con el email dado
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca jugadores por nivel de juego
     */
    List<Jugador> findByNivelDeJuego(NivelDeJuego nivel);
    
    /**
     * Busca jugadores por deportes favoritos (usando String deporteFavorito)
     */
    @Query("SELECT j FROM Jugador j JOIN Deporte d ON j.deporteFavorito = d.nombre WHERE d.id = :deporteId")
    List<Jugador> findByDeporteFavorito(@Param("deporteId") Long deporteId);
    
    /**
     * Busca jugadores cercanos a una ubicación específica
     */
    @Query("""
        SELECT j FROM Jugador j 
        WHERE j.ubicacion IS NOT NULL 
        AND (6371 * acos(cos(radians(:latitud)) * cos(radians(j.ubicacion.latitud)) * 
             cos(radians(j.ubicacion.longitud) - radians(:longitud)) + 
             sin(radians(:latitud)) * sin(radians(j.ubicacion.latitud)))) <= :radioKm
        """)
    List<Jugador> findJugadoresCercanos(
        @Param("latitud") Double latitud, 
        @Param("longitud") Double longitud, 
        @Param("radioKm") Double radioKm
    );
    
    /**
     * Busca jugadores activos (que han creado al menos un partido)
     */
    @Query("SELECT DISTINCT j FROM Jugador j WHERE SIZE(j.partidosOrganizados) > 0")
    List<Jugador> findJugadoresActivos();
} 