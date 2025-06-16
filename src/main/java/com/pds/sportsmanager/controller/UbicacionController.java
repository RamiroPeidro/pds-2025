package com.pds.sportsmanager.controller;

import com.pds.sportsmanager.model.entity.Ubicacion;
import com.pds.sportsmanager.service.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubicaciones")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    @Autowired
    public UbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @PostMapping
    public ResponseEntity<Ubicacion> crearUbicacion(@RequestBody Ubicacion ubicacion) {
        return ResponseEntity.ok(ubicacionService.crearUbicacion(ubicacion));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ubicacion> obtenerUbicacionPorId(@PathVariable Long id) {
        return ubicacionService.obtenerUbicacionPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Ubicacion>> listarTodasLasUbicaciones() {
        return ResponseEntity.ok(ubicacionService.listarTodasLasUbicaciones());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ubicacion> actualizarUbicacion(
            @PathVariable Long id,
            @RequestBody Ubicacion ubicacion) {
        try {
            return ResponseEntity.ok(ubicacionService.actualizarUbicacion(id, ubicacion));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUbicacion(@PathVariable Long id) {
        ubicacionService.eliminarUbicacion(id);
        return ResponseEntity.noContent().build();
    }
} 