package com.pds.sportsmanager.service;

import com.pds.sportsmanager.model.entity.PreferenciaNotificacion;
import java.util.Optional;

public interface PreferenciaNotificacionService {
    Optional<PreferenciaNotificacion> obtenerPorUsuarioId(Long usuarioId);
}