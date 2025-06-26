package com.pds.sportsmanager.controller;

import com.pds.sportsmanager.model.dto.JugadorDTO;
import com.pds.sportsmanager.model.dto.JugadorRequestDTO;
import com.pds.sportsmanager.model.entity.Jugador;
import com.pds.sportsmanager.service.JugadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jugadores")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Jugadores", description = "Gestión de jugadores del sistema")
public class JugadorController {

    private final JugadorService jugadorService;

    @PostMapping
    @Operation(summary = "Registrar jugador", description = "Registra un nuevo jugador en el sistema")
    public ResponseEntity<JugadorDTO> registrarJugador(@Valid @RequestBody JugadorRequestDTO jugadorRequest) {
        log.info("REST: Registrando nuevo jugador: {}", jugadorRequest.nombre());
        
        try {
            Jugador jugador = fromRequestDTO(jugadorRequest);
            Jugador jugadorGuardado = jugadorService.registrarJugador(jugador);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(jugadorGuardado));
        } catch (IllegalArgumentException e) {
            log.warn("Error validando jugador: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener jugador por ID", description = "Retorna un jugador específico")
    public ResponseEntity<JugadorDTO> obtenerJugador(@PathVariable Long id) {
        log.info("REST: Obteniendo jugador {}", id);
        
        try {
            Jugador jugador = jugadorService.obtenerJugadorPorId(id);
            return ResponseEntity.ok(toDTO(jugador));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos los jugadores", description = "Retorna todos los jugadores")
    public ResponseEntity<List<JugadorDTO>> listarJugadores() {
        log.info("REST: Listando todos los jugadores");
        
        List<Jugador> jugadores = jugadorService.obtenerTodosLosJugadores();
        List<JugadorDTO> jugadoresDTO = jugadores.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(jugadoresDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar jugador", description = "Actualiza un jugador existente")
    public ResponseEntity<JugadorDTO> actualizarJugador(
            @PathVariable Long id, 
            @Valid @RequestBody JugadorRequestDTO jugadorRequest) {
        log.info("REST: Actualizando jugador {}", id);
        
        try {
            Jugador jugadorActualizado = fromRequestDTO(jugadorRequest);
            Jugador jugadorGuardado = jugadorService.actualizarJugador(id, jugadorActualizado);
            return ResponseEntity.ok(toDTO(jugadorGuardado));
        } catch (IllegalArgumentException e) {
            log.warn("Error validando jugador: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.warn("Error actualizando jugador: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar jugador", description = "Elimina un jugador del sistema")
    public ResponseEntity<Void> eliminarJugador(@PathVariable Long id) {
        log.info("REST: Eliminando jugador {}", id);
        
        try {
            jugadorService.eliminarJugador(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            log.warn("No se puede eliminar jugador: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            log.warn("Error eliminando jugador: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar/email")
    @Operation(summary = "Buscar por email", description = "Busca un jugador por email")
    public ResponseEntity<JugadorDTO> buscarPorEmail(@RequestParam String email) {
        log.info("REST: Buscando jugador por email: {}", email);
        
        Optional<Jugador> jugador = jugadorService.buscarPorEmail(email);
        return jugador.map(j -> ResponseEntity.ok(toDTO(j)))
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar/nombre")
    @Operation(summary = "Buscar por nombre", description = "Busca un jugador por nombre")
    public ResponseEntity<JugadorDTO> buscarPorNombre(@RequestParam String nombre) {
        log.info("REST: Buscando jugador por nombre: {}", nombre);
        
        Optional<Jugador> jugador = jugadorService.buscarPorNombre(nombre);
        return jugador.map(j -> ResponseEntity.ok(toDTO(j)))
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/activos")
    @Operation(summary = "Jugadores activos", description = "Retorna jugadores que han organizado partidos")
    public ResponseEntity<List<JugadorDTO>> jugadoresActivos() {
        log.info("REST: Obteniendo jugadores activos");
        
        List<Jugador> jugadores = jugadorService.buscarJugadoresActivos();
        List<JugadorDTO> jugadoresDTO = jugadores.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(jugadoresDTO);
    }

    @GetMapping("/cercanos")
    @Operation(summary = "Jugadores cercanos", description = "Busca jugadores cercanos a una ubicación")
    public ResponseEntity<List<JugadorDTO>> jugadoresCercanos(
            @RequestParam Double latitud,
            @RequestParam Double longitud,
            @RequestParam(defaultValue = "10.0") Double radioKm) {
        log.info("REST: Buscando jugadores cercanos a {}, {} en {}km", latitud, longitud, radioKm);
        
        List<Jugador> jugadores = jugadorService.buscarJugadoresCercanos(latitud, longitud, radioKm);
        List<JugadorDTO> jugadoresDTO = jugadores.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(jugadoresDTO);
    }

    /**
     * Convierte JugadorRequestDTO a Jugador
     */
    private Jugador fromRequestDTO(JugadorRequestDTO dto) {
        Jugador jugador = new Jugador();
        jugador.setNombre(dto.nombre());
        jugador.setEmail(dto.email());
        jugador.setContrasenia(dto.contrasenia());
        jugador.setNivelDeJuego(dto.nivelDeJuego());
        jugador.setDeporteFavorito(dto.deporteFavorito());
        jugador.setUbicacion(dto.ubicacion());
        return jugador;
    }

    /**
     * Convierte Jugador a JugadorDTO
     */
    private JugadorDTO toDTO(Jugador jugador) {
        return JugadorDTO.builder()
                .id(jugador.getId())
                .nombre(jugador.getNombre())
                .email(jugador.getEmail())
                .nivelDeJuego(jugador.getNivelDeJuego())
                .deporteFavorito(jugador.getDeporteFavorito())
                .ubicacion(jugador.getUbicacion())
                .createdAt(jugador.getCreatedAt())
                .build();
    }
} 