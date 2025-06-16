package com.pds.sportsmanager.service.impl;

import com.pds.sportsmanager.model.entity.Deporte;
import com.pds.sportsmanager.repository.DeporteRepository;
import com.pds.sportsmanager.service.DeporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeporteServiceImpl implements DeporteService {

    private final DeporteRepository deporteRepository;

    @Autowired
    public DeporteServiceImpl(DeporteRepository deporteRepository) {
        this.deporteRepository = deporteRepository;
    }

    @Override
    public Deporte crearDeporte(Deporte deporte) {
        return deporteRepository.save(deporte);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Deporte> obtenerDeportePorId(Long id) {
        return deporteRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Deporte> obtenerDeportePorNombre(String nombre) {
        return deporteRepository.findByNombre(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Deporte> listarTodosLosDeportes() {
        return deporteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Deporte> buscarDeportesPorCantidadJugadores(Integer minJugadores, Integer maxJugadores) {
        return deporteRepository.findByMinJugadoresPorEquipoLessThanEqualAndMaxJugadoresPorEquipoGreaterThanEqual(
                minJugadores, maxJugadores);
    }

    @Override
    public Deporte actualizarDeporte(Long id, Deporte deporteActualizado) {
        return deporteRepository.findById(id)
                .map(deporteExistente -> {
                    deporteExistente.setNombre(deporteActualizado.getNombre());
                    deporteExistente.setDescripcion(deporteActualizado.getDescripcion());
                    deporteExistente.setMinJugadoresPorEquipo(deporteActualizado.getMinJugadoresPorEquipo());
                    deporteExistente.setMaxJugadoresPorEquipo(deporteActualizado.getMaxJugadoresPorEquipo());
                    deporteExistente.setDuracionEstandarMinutos(deporteActualizado.getDuracionEstandarMinutos());
                    return deporteRepository.save(deporteExistente);
                })
                .orElseThrow(() -> new RuntimeException("Deporte no encontrado con id: " + id));
    }

    @Override
    public void eliminarDeporte(Long id) {
        deporteRepository.deleteById(id);
    }
} 