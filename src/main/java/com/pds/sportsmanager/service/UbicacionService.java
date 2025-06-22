package com.pds.sportsmanager.service;

import com.pds.sportsmanager.model.entity.Ubicacion;
import java.util.List;
import java.util.Optional;

public interface UbicacionService {
    Ubicacion crearUbicacion(Ubicacion ubicacion);
    Optional<Ubicacion> obtenerUbicacionPorId(Long id);
    List<Ubicacion> listarTodasLasUbicaciones();
    Optional<Ubicacion> actualizarUbicacion(Long id, Ubicacion ubicacion);
    void eliminarUbicacion(Long id);
} 