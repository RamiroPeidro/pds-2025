package com.pds.sportsmanager.service;

import com.pds.sportsmanager.model.entity.Deporte;
import java.util.List;
import java.util.Optional;

public interface DeporteService {
    Deporte crearDeporte(Deporte deporte);
    Optional<Deporte> obtenerDeportePorId(Long id);
    Optional<Deporte> obtenerDeportePorNombre(String nombre);
    List<Deporte> listarTodosLosDeportes();
    List<Deporte> buscarDeportesPorCantidadJugadores(Integer minJugadores, Integer maxJugadores);
    Optional<Deporte> actualizarDeporte(Long id, Deporte deporte);
    void eliminarDeporte(Long id);
} 