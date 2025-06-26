// src/main/java/com/pds/sportsmanager/repository/PreferenciaNotificacionRepository.java
package com.pds.sportsmanager.repository;

import com.pds.sportsmanager.model.entity.PreferenciaNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PreferenciaNotificacionRepository extends JpaRepository<PreferenciaNotificacion, Long> {

    /**
     * Busca las preferencias de notificación por el ID del usuario.
     *
     * @param usuarioId el ID del usuario
     * @return las preferencias de notificación del usuario, si existen
     */

    Optional<PreferenciaNotificacion> findByJugador_Id(Long usuarioId);

    /**
     * Busca las preferencias de notificación por el email del jugador.
     *
     * @param email el email del Jugador
     * @return las preferencias de notificación del jugador, si existen
     */
    @Query("SELECT p FROM PreferenciaNotificacion p JOIN p.jugador u WHERE u.email = :email")
    Optional<PreferenciaNotificacion> findByUsuarioEmail(String email);
}