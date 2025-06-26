
package com.pds.sportsmanager.service;

import com.pds.sportsmanager.model.entity.PreferenciaNotificacion;
import com.pds.sportsmanager.repository.PreferenciaNotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PreferenciaNotificacionServiceImpl implements PreferenciaNotificacionService {

    private final PreferenciaNotificacionRepository repository;

    @Override
    public Optional<PreferenciaNotificacion> obtenerPorUsuarioId(Long usuarioId) {
        return repository.findByJugador_Id(usuarioId);
    }

    @Override
    public Optional<PreferenciaNotificacion> obtenerPorEmail(String email) {
        return repository.findByUsuarioEmail(email);
    }



}