package com.pds.sportsmanager.service.impl;

import com.pds.sportsmanager.model.entity.Ubicacion;
import com.pds.sportsmanager.repository.UbicacionRepository;
import com.pds.sportsmanager.service.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UbicacionServiceImpl implements UbicacionService {

    private final UbicacionRepository ubicacionRepository;

    @Autowired
    public UbicacionServiceImpl(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    @Override
    public Ubicacion crearUbicacion(Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ubicacion> obtenerUbicacionPorId(Long id) {
        return ubicacionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ubicacion> listarTodasLasUbicaciones() {
        return ubicacionRepository.findAll();
    }

    @Override
    public Ubicacion actualizarUbicacion(Long id, Ubicacion ubicacionActualizada) {
        return ubicacionRepository.findById(id)
                .map(ubicacionExistente -> {
                    Ubicacion nuevaUbicacion = new Ubicacion(
                        ubicacionActualizada.direccion(),
                        ubicacionActualizada.latitud(),
                        ubicacionActualizada.longitud()
                    );
                    return ubicacionRepository.save(nuevaUbicacion);
                })
                .orElseThrow(() -> new RuntimeException("Ubicación no encontrada con id: " + id));
    }

    @Override
    public void eliminarUbicacion(Long id) {
        ubicacionRepository.deleteById(id);
    }
} 