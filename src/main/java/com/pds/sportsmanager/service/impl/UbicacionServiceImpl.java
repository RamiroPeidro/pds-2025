package com.pds.sportsmanager.service.impl;

import com.pds.sportsmanager.model.entity.Ubicacion;
import com.pds.sportsmanager.repository.UbicacionRepository;
import com.pds.sportsmanager.service.UbicacionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class UbicacionServiceImpl implements UbicacionService {

    private final UbicacionRepository ubicacionRepository;

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
    public Optional<Ubicacion> actualizarUbicacion(Long id, Ubicacion ubicacionActualizada) {
        return ubicacionRepository.findById(id)
                .map(ubicacionExistente -> {
                    ubicacionExistente.setDireccion(ubicacionActualizada.getDireccion());
                    ubicacionExistente.setLatitud(ubicacionActualizada.getLatitud());
                    ubicacionExistente.setLongitud(ubicacionActualizada.getLongitud());
                    return ubicacionRepository.save(ubicacionExistente);
                });
    }

    @Override
    public void eliminarUbicacion(Long id) {
        ubicacionRepository.deleteById(id);
    }
} 