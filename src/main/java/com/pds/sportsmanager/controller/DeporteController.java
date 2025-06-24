package com.pds.sportsmanager.controller;

import com.pds.sportsmanager.model.entity.Deporte;
import com.pds.sportsmanager.service.DeporteService;
import lombok.Builder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/deportes")
public class DeporteController {

    private final DeporteService deporteService;

    public DeporteController(DeporteService deporteService) {
        this.deporteService = deporteService;
    }

    @Builder
    public static record DeporteInputDTO(
        String nombre,
        String descripcion,
        Integer minJugadoresPorEquipo,
        Integer maxJugadoresPorEquipo,
        Integer duracionEstandarMinutos
    ) {}

    @Builder
    public static record DeporteOutputDTO(
        Long id,
        String nombre,
        String descripcion,
        Integer minJugadoresPorEquipo,
        Integer maxJugadoresPorEquipo,
        Integer duracionEstandarMinutos
    ) {
        public DeporteOutputDTO(Deporte deporte) {
            this(
                deporte.getId(),
                deporte.getNombre(),
                deporte.getDescripcion(),
                deporte.getMinJugadoresPorEquipo(),
                deporte.getMaxJugadoresPorEquipo(),
                deporte.getDuracionEstandarMinutos()
            );
        }
    }

    @PostMapping
    public ResponseEntity<DeporteOutputDTO> crearDeporte(@RequestBody DeporteInputDTO deporteDTO) {
        Deporte deporte = new Deporte(deporteDTO.nombre(), deporteDTO.descripcion(),
                deporteDTO.minJugadoresPorEquipo(), deporteDTO.maxJugadoresPorEquipo(),
                deporteDTO.duracionEstandarMinutos());
        Deporte nuevoDeporte = deporteService.crearDeporte(deporte);
        return ResponseEntity.ok(new DeporteOutputDTO(nuevoDeporte));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeporteOutputDTO> obtenerDeportePorId(@PathVariable Long id) {
        return deporteService.obtenerDeportePorId(id)
                .map(deporte -> ResponseEntity.ok(new DeporteOutputDTO(deporte)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<DeporteOutputDTO> obtenerDeportePorNombre(@PathVariable String nombre) {
        return deporteService.obtenerDeportePorNombre(nombre)
                .map(deporte -> ResponseEntity.ok(new DeporteOutputDTO(deporte)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<DeporteOutputDTO>> listarTodosLosDeportes() {
        List<DeporteOutputDTO> dtoList = deporteService.listarTodosLosDeportes().stream()
                .map(DeporteOutputDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<DeporteOutputDTO>> buscarDeportesPorCantidadJugadores(
            @RequestParam Integer minJugadores,
            @RequestParam Integer maxJugadores) {
        List<DeporteOutputDTO> dtoList = deporteService.buscarDeportesPorCantidadJugadores(minJugadores, maxJugadores).stream()
                .map(DeporteOutputDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeporteOutputDTO> actualizarDeporte(
            @PathVariable Long id,
            @RequestBody DeporteInputDTO deporteDTO) {
        Deporte deporte = new Deporte(deporteDTO.nombre(), deporteDTO.descripcion(),
                deporteDTO.minJugadoresPorEquipo(), deporteDTO.maxJugadoresPorEquipo(),
                deporteDTO.duracionEstandarMinutos());
        return deporteService.actualizarDeporte(id, deporte)
                .map(deporteActualizado -> ResponseEntity.ok(new DeporteOutputDTO(deporteActualizado)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDeporte(@PathVariable Long id) {
        deporteService.eliminarDeporte(id);
        return ResponseEntity.noContent().build();
    }
} 