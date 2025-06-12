package com.pds.sportsmanager.repository;

import com.pds.sportsmanager.model.entity.Deporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeporteRepository extends JpaRepository<Deporte, Long> {
    
    /**
     * Busca un deporte por nombre
     */
    Optional<Deporte> findByNombre(String nombre);
    
    /**
     * Busca un deporte por nombre ignorando mayúsculas/minúsculas
     */
    Optional<Deporte> findByNombreIgnoreCase(String nombre);
    
    /**
     * Verifica si existe un deporte con el nombre dado
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Busca deportes ordenados por popularidad (cantidad de partidos)
     */
    @Query("""
        SELECT d FROM Deporte d 
        LEFT JOIN d.partidos p 
        GROUP BY d.id 
        ORDER BY COUNT(p) DESC, d.nombre ASC
        """)
    List<Deporte> findDeportesOrdenadosPorPopularidad();
    
    /**
     * Busca deportes que tienen partidos activos
     */
    @Query("""
        SELECT DISTINCT d FROM Deporte d 
        JOIN d.partidos p 
        WHERE p.estadoNombre IN ('NECESITAMOS_JUGADORES', 'PARTIDO_ARMADO', 'PARTIDO_CONFIRMADO')
        ORDER BY d.nombre ASC
        """)
    List<Deporte> findDeportesConPartidosActivos();
} 