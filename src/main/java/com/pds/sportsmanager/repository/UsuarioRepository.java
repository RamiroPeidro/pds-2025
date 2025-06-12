package com.pds.sportsmanager.repository;

import com.pds.sportsmanager.model.entity.Usuario;
import com.pds.sportsmanager.model.enums.NivelDeJugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    /**
     * Busca un usuario por nombre de usuario
     */
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    
    /**
     * Busca un usuario por email
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Verifica si existe un usuario con el nombre de usuario dado
     */
    boolean existsByNombreUsuario(String nombreUsuario);
    
    /**
     * Verifica si existe un usuario con el email dado
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca usuarios por nivel de jugador
     */
    List<Usuario> findByNivelDeJugador(NivelDeJugador nivel);
    
    /**
     * Busca usuarios por deporte favorito
     */
    @Query("SELECT u FROM Usuario u WHERE u.deporteFavorito.id = :deporteId")
    List<Usuario> findByDeporteFavorito(@Param("deporteId") Long deporteId);
    
    /**
     * Busca usuarios cercanos a una ubicación específica
     */
    @Query("""
        SELECT u FROM Usuario u 
        WHERE u.ubicacion IS NOT NULL 
        AND (6371 * acos(cos(radians(:latitud)) * cos(radians(u.ubicacion.latitud)) * 
             cos(radians(u.ubicacion.longitud) - radians(:longitud)) + 
             sin(radians(:latitud)) * sin(radians(u.ubicacion.latitud)))) <= :radioKm
        """)
    List<Usuario> findUsuariosCercanos(
        @Param("latitud") Double latitud, 
        @Param("longitud") Double longitud, 
        @Param("radioKm") Double radioKm
    );
    
    /**
     * Busca usuarios activos (que han creado al menos un partido)
     */
    @Query("SELECT DISTINCT u FROM Usuario u WHERE SIZE(u.partidosCreados) > 0")
    List<Usuario> findUsuariosActivos();
} 