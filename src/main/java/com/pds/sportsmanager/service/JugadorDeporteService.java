// src/main/java/com/pds/sportsmanager/service/JugadorDeporteService.java
package com.pds.sportsmanager.service;

import com.pds.sportsmanager.model.dto.JugadorDeporteDTO;
import com.pds.sportsmanager.model.entity.Jugador;
import com.pds.sportsmanager.model.entity.Deporte;
import com.pds.sportsmanager.model.entity.JugadorDeporte;
import com.pds.sportsmanager.model.enums.NivelDeJuego;
import com.pds.sportsmanager.repository.JugadorDeporteRepository;
import com.pds.sportsmanager.repository.JugadorRepository;
import com.pds.sportsmanager.repository.DeporteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JugadorDeporteService {

    private final JugadorDeporteRepository jugadorDeporteRepository;
    private final JugadorRepository jugadorRepository;
    private final DeporteRepository deporteRepository;

    @Autowired
    public JugadorDeporteService(
            JugadorDeporteRepository jugadorDeporteRepository,
            JugadorRepository jugadorRepository,
            DeporteRepository deporteRepository) {
        this.jugadorDeporteRepository = jugadorDeporteRepository;
        this.jugadorRepository = jugadorRepository;
        this.deporteRepository = deporteRepository;
    }



    public JugadorDeporte agregarJugadorDeporte(JugadorDeporteDTO dto) {
        Jugador jugador = jugadorRepository.findById(dto.jugadorId())
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));
        Deporte deporte = deporteRepository.findById(dto.getDeporteId())
                .orElseThrow(() -> new RuntimeException("Deporte no encontrado"));

        JugadorDeporte jugadorDeporte = new JugadorDeporte();
        jugadorDeporte.setJugador(jugador);
        jugadorDeporte.setDeporte(deporte);
        jugadorDeporte.setNivel(NivelDeJuego.valueOf(dto.getNivelDeJuego().toUpperCase()));
        jugadorDeporte.setEsFavorito(dto.esFavorito());
        return jugadorDeporteRepository.save(jugadorDeporte);
    }
}