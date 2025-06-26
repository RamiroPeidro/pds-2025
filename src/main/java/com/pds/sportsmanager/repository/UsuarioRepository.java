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
public interface UsuarioRepository extends JpaRepository<Jugador, Long> {
    
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
     * Busca usuarios por nivel de juego
     */
    List<Jugador> findByNivelDeJuego(NivelDeJuego nivel);
    
    /**
     * Busca usuarios por deportes favoritos
     */
    @Query("SELECT u FROM Jugador u JOIN u.deportesFavs d WHERE d.id = :deporteId")
    List<Jugador> findByDeporteFavorito(@Param("deporteId") Long deporteId);
    
    /**
     * Busca usuarios cercanos a una ubicación específica
     */
    @Query("""
        SELECT u FROM Jugador u 
        WHERE u.ubicacion IS NOT NULL 
        AND (6371 * acos(cos(radians(:latitud)) * cos(radians(u.ubicacion.latitud)) * 
             cos(radians(u.ubicacion.longitud) - radians(:longitud)) + 
             sin(radians(:latitud)) * sin(radians(u.ubicacion.latitud)))) <= :radioKm
        """)
    List<Jugador> findUsuariosCercanos(
        @Param("latitud") Double latitud, 
        @Param("longitud") Double longitud, 
        @Param("radioKm") Double radioKm
    );
    
    /**
     * Busca usuarios activos (que han creado al menos un partido)
     */
    @Query("SELECT DISTINCT u FROM Jugador u WHERE SIZE(u.partidosOrganizados) > 0")
    List<Jugador> findUsuariosActivos();
} 