package com.pds.sportsmanager.controller;

import com.pds.sportsmanager.model.entity.Ubicacion;
import com.pds.sportsmanager.service.UbicacionService;
import lombok.Builder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ubicaciones")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    public UbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @Builder
    public static record UbicacionInputDTO(
        String direccion,
        Double latitud,
        Double longitud
    ) {}

    @Builder
    public static record UbicacionOutputDTO(
        Long id,
        String direccion,
        Double latitud,
        Double longitud
    ) {
        public UbicacionOutputDTO(Ubicacion ubicacion) {
            this(
                ubicacion.getId(),
                ubicacion.getDireccion(),
                ubicacion.getLatitud(),
                ubicacion.getLongitud()
            );
        }
    }

    @PostMapping
    public ResponseEntity<UbicacionOutputDTO> crearUbicacion(@RequestBody UbicacionInputDTO ubicacionDTO) {
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setDireccion(ubicacionDTO.direccion());
        ubicacion.setLatitud(ubicacionDTO.latitud());
        ubicacion.setLongitud(ubicacionDTO.longitud());
        Ubicacion nuevaUbicacion = ubicacionService.crearUbicacion(ubicacion);
        return ResponseEntity.ok(new UbicacionOutputDTO(nuevaUbicacion));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UbicacionOutputDTO> obtenerUbicacionPorId(@PathVariable Long id) {
        return ubicacionService.obtenerUbicacionPorId(id)
                .map(ubicacion -> ResponseEntity.ok(new UbicacionOutputDTO(ubicacion)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UbicacionOutputDTO>> listarTodasLasUbicaciones() {
        List<UbicacionOutputDTO> dtoList = ubicacionService.listarTodasLasUbicaciones().stream()
                .map(UbicacionOutputDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UbicacionOutputDTO> actualizarUbicacion(
            @PathVariable Long id,
            @RequestBody UbicacionInputDTO ubicacionDTO) {
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setDireccion(ubicacionDTO.direccion());
        ubicacion.setLatitud(ubicacionDTO.latitud());
        ubicacion.setLongitud(ubicacionDTO.longitud());

        return ubicacionService.actualizarUbicacion(id, ubicacion)
                .map(ubicacionActualizada -> ResponseEntity.ok(new UbicacionOutputDTO(ubicacionActualizada)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUbicacion(@PathVariable Long id) {
        ubicacionService.eliminarUbicacion(id);
        return ResponseEntity.noContent().build();
    }
} 