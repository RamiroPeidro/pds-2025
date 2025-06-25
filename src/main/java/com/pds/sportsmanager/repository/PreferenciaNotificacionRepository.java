// src/main/java/com/pds/sportsmanager/repository/PreferenciaNotificacionRepository.java
package com.pds.sportsmanager.repository;

import com.pds.sportsmanager.model.entity.PreferenciaNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenciaNotificacionRepository extends JpaRepository<PreferenciaNotificacion, Long> {
}