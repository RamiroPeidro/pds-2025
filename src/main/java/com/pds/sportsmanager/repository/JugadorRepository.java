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
     * Busca un jugador por nombre con jugadorDeportes y deportes cargados
     */
    @Query("SELECT j FROM Jugador j LEFT JOIN FETCH j.jugadorDeportes jd LEFT JOIN FETCH jd.deporte WHERE j.nombre = :nombre")
    Optional<Jugador> findByNombreWithDeportesFavs(@Param("nombre") String nombre);
    
    /**
     * Busca un jugador por email con jugadorDeportes y deportes cargados
     */
    @Query("SELECT j FROM Jugador j LEFT JOIN FETCH j.jugadorDeportes jd LEFT JOIN FETCH jd.deporte WHERE j.email = :email")
    Optional<Jugador> findByEmailWithDeportesFavs(@Param("email") String email);
    
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
     * Busca jugadores por deportes favoritos (usando tabla intermedia)
     * Solo jugadores que tienen ese deporte marcado como favorito
     */
    @Query("SELECT DISTINCT j FROM Jugador j JOIN j.jugadorDeportes jd WHERE jd.deporte.id = :deporteId AND jd.esFavorito = true")
    List<Jugador> findByDeporteFavorito(@Param("deporteId") Long deporteId);
    
    /**
     * Busca jugadores por deporte favorito principal (String - fallback)
     */
    @Query("SELECT j FROM Jugador j JOIN Deporte d ON j.deporteFavorito = d.nombre WHERE d.id = :deporteId")
    List<Jugador> findByDeporteFavoritoString(@Param("deporteId") Long deporteId);
    
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
    
    /**
     * Obtiene todos los jugadores con jugadorDeportes y deportes cargados (fetch join para evitar LazyInitializationException)
     */
    @Query("SELECT DISTINCT j FROM Jugador j LEFT JOIN FETCH j.jugadorDeportes jd LEFT JOIN FETCH jd.deporte")
    List<Jugador> findAllWithDeportesFavs();
    
    /**
     * Busca jugador por ID con jugadorDeportes y deportes cargados
     */
    @Query("SELECT j FROM Jugador j LEFT JOIN FETCH j.jugadorDeportes jd LEFT JOIN FETCH jd.deporte WHERE j.id = :id")
    Optional<Jugador> findByIdWithDeportesFavs(@Param("id") Long id);
} 