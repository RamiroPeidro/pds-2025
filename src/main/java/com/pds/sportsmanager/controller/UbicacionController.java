package com.pds.sportsmanager.controller;

import com.pds.sportsmanager.model.entity.Ubicacion;
import com.pds.sportsmanager.service.UbicacionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ubicaciones")
@AllArgsConstructor
public class UbicacionController {

    private final UbicacionService ubicacionService;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UbicacionInputDTO {
        private String direccion;
        private Double latitud;
        private Double longitud;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UbicacionOutputDTO {
        private Long id;
        private String direccion;
        private Double latitud;
        private Double longitud;

        public UbicacionOutputDTO(Ubicacion ubicacion) {
            this.id = ubicacion.getId();
            this.direccion = ubicacion.getDireccion();
            this.latitud = ubicacion.getLatitud();
            this.longitud = ubicacion.getLongitud();
        }
    }

    @PostMapping
    public ResponseEntity<UbicacionOutputDTO> crearUbicacion(@RequestBody UbicacionInputDTO ubicacionDTO) {
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setDireccion(ubicacionDTO.getDireccion());
        ubicacion.setLatitud(ubicacionDTO.getLatitud());
        ubicacion.setLongitud(ubicacionDTO.getLongitud());
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
        ubicacion.setDireccion(ubicacionDTO.getDireccion());
        ubicacion.setLatitud(ubicacionDTO.getLatitud());
        ubicacion.setLongitud(ubicacionDTO.getLongitud());

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