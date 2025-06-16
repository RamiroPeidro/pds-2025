package com.pds.sportsmanager.controller;

import com.pds.sportsmanager.model.entity.Deporte;
import com.pds.sportsmanager.service.DeporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deportes")
public class DeporteController {

    private final DeporteService deporteService;

    @Autowired
    public DeporteController(DeporteService deporteService) {
        this.deporteService = deporteService;
    }

    @PostMapping
    public ResponseEntity<Deporte> crearDeporte(@RequestBody Deporte deporte) {
        return ResponseEntity.ok(deporteService.crearDeporte(deporte));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Deporte> obtenerDeportePorId(@PathVariable Long id) {
        return deporteService.obtenerDeportePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Deporte> obtenerDeportePorNombre(@PathVariable String nombre) {
        return deporteService.obtenerDeportePorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Deporte>> listarTodosLosDeportes() {
        return ResponseEntity.ok(deporteService.listarTodosLosDeportes());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Deporte>> buscarDeportesPorCantidadJugadores(
            @RequestParam Integer minJugadores,
            @RequestParam Integer maxJugadores) {
        return ResponseEntity.ok(deporteService.buscarDeportesPorCantidadJugadores(minJugadores, maxJugadores));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deporte> actualizarDeporte(
            @PathVariable Long id,
            @RequestBody Deporte deporte) {
        try {
            return ResponseEntity.ok(deporteService.actualizarDeporte(id, deporte));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDeporte(@PathVariable Long id) {
        deporteService.eliminarDeporte(id);
        return ResponseEntity.noContent().build();
    }
} 