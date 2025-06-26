
package com.pds.sportsmanager.service;

import com.pds.sportsmanager.model.dto.PreferenciaDTO;
import com.pds.sportsmanager.model.entity.Jugador;
import com.pds.sportsmanager.model.entity.PreferenciaNotificacion;
import com.pds.sportsmanager.repository.PreferenciaNotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PreferenciaNotificacionServiceImpl implements PreferenciaNotificacionService {

    private final PreferenciaNotificacionRepository repository;
//    private final JugadorService jugadorService;

    @Override
    public Optional<PreferenciaNotificacion> obtenerPorUsuarioId(Long usuarioId) {
        return repository.findByJugador_Id(usuarioId);
    }

    @Override
    public Optional<PreferenciaNotificacion> obtenerPorEmail(String email) {
        return repository.findByUsuarioEmail(email);
    }

    @Override
    public void guardar(PreferenciaDTO preferenciaNotificacion, Jugador jugador) {
        PreferenciaNotificacion preferencia = new PreferenciaNotificacion();
        preferencia.setJugador(jugador);
        preferencia.setPush(preferenciaNotificacion.firebaseNotificaciones());
        preferencia.setEmail(preferenciaNotificacion.emailNotificaciones());

        repository.save(preferencia);
    }





}