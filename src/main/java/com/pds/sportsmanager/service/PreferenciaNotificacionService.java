package com.pds.sportsmanager.service;

import com.pds.sportsmanager.model.dto.PreferenciaDTO;
import com.pds.sportsmanager.model.entity.PreferenciaNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferenciaNotificacionService {
    Optional<PreferenciaNotificacion> obtenerPorUsuarioId(Long usuarioId);

    Optional<PreferenciaNotificacion> obtenerPorEmail(String email);

    void guardar(PreferenciaDTO preferenciaNotificacion, Long usuarioId);
}