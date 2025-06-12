package com.pds.sportsmanager.controller;

import com.pds.sportsmanager.model.dto.PartidoBusquedaResult;
import com.pds.sportsmanager.model.entity.Partido;
import com.pds.sportsmanager.patterns.strategy.EstrategiaEmparejamiento;
import com.pds.sportsmanager.service.EstrategiaEmparejamientoFactory;
import com.pds.sportsmanager.service.PartidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partidos")
@Tag(name = "Partidos", description = "Gestión de encuentros deportivos")
@RequiredArgsConstructor
@Slf4j
public class PartidoController {

    private final PartidoService partidoService;
    private final EstrategiaEmparejamientoFactory estrategiaFactory;

    @PostMapping
    @Operation(summary = "Crear un nuevo partido", description = "Crea un partido en estado 'Necesitamos Jugadores'")
    public ResponseEntity<Partido> crearPartido(@Valid @RequestBody Partido partido) {
        log.info("REST: Creando nuevo partido");
        
        try {
            Partido partidoCreado = partidoService.crearPartido(partido);
            return ResponseEntity.status(HttpStatus.CREATED).body(partidoCreado);
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al crear partido: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{partidoId}/jugadores/{jugadorId}")
    @Operation(summary = "Unirse a un partido", description = "Agrega un jugador a un partido usando el patrón State")
    public ResponseEntity<String> unirseAlPartido(
            @Parameter(description = "ID del partido") @PathVariable Long partidoId,
            @Parameter(description = "ID del jugador") @PathVariable Long jugadorId) {
        
        log.info("REST: Jugador {} uniéndose al partido {}", jugadorId, partidoId);
        
        try {
            partidoService.agregarJugadorAlPartido(partidoId, jugadorId);
            return ResponseEntity.ok("Jugador agregado exitosamente");
        } catch (IllegalStateException e) {
            log.warn("Error de estado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            log.error("Error al agregar jugador: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{partidoId}/confirmar")
    @Operation(summary = "Confirmar partido", description = "Cambia el estado del partido a 'Confirmado'")
    public ResponseEntity<String> confirmarPartido(@PathVariable Long partidoId) {
        log.info("REST: Confirmando partido {}", partidoId);
        
        try {
            partidoService.confirmarPartido(partidoId);
            return ResponseEntity.ok("Partido confirmado exitosamente");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{partidoId}/cancelar")
    @Operation(summary = "Cancelar partido", description = "Cambia el estado del partido a 'Cancelado'")
    public ResponseEntity<String> cancelarPartido(@PathVariable Long partidoId) {
        log.info("REST: Cancelando partido {}", partidoId);
        
        try {
            partidoService.cancelarPartido(partidoId);
            return ResponseEntity.ok("Partido cancelado exitosamente");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{partidoId}/finalizar")
    @Operation(summary = "Finalizar partido", description = "Cambia el estado del partido a 'Finalizado'")
    public ResponseEntity<String> finalizarPartido(@PathVariable Long partidoId) {
        log.info("REST: Finalizando partido {}", partidoId);
        
        try {
            partidoService.finalizarPartido(partidoId);
            return ResponseEntity.ok("Partido finalizado exitosamente");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar partidos", description = "Busca partidos usando diferentes estrategias de emparejamiento")
    public ResponseEntity<List<PartidoBusquedaResult>> buscarPartidos(
            @Parameter(description = "ID del usuario") @RequestParam Long usuarioId,
            @Parameter(description = "Estrategia: 'nivel' o 'cercania'") @RequestParam(defaultValue = "nivel") String estrategia) {
        
        log.info("REST: Buscando partidos para usuario {} con estrategia {}", usuarioId, estrategia);
        
        try {
            EstrategiaEmparejamiento estrategiaObj = estrategiaFactory.obtenerEstrategia(estrategia);
            
            List<PartidoBusquedaResult> partidos = partidoService.buscarPartidos(usuarioId, estrategiaObj);
            return ResponseEntity.ok(partidos);
        } catch (RuntimeException e) {
            log.error("Error al buscar partidos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/cercanos")
    @Operation(summary = "Buscar partidos cercanos", description = "Busca partidos en un radio específico")
    public ResponseEntity<List<Partido>> buscarPartidosCercanos(
            @Parameter(description = "Latitud") @RequestParam Double latitud,
            @Parameter(description = "Longitud") @RequestParam Double longitud,
            @Parameter(description = "Radio en kilómetros") @RequestParam(defaultValue = "10.0") Double radioKm) {
        
        log.info("REST: Buscando partidos cercanos a lat:{}, lng:{}, radio:{}km", latitud, longitud, radioKm);
        
        List<Partido> partidos = partidoService.obtenerPartidosCercanos(latitud, longitud, radioKm);
        return ResponseEntity.ok(partidos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener partido por ID", description = "Retorna un partido específico")
    public ResponseEntity<Partido> obtenerPartido(@PathVariable Long id) {
        log.info("REST: Obteniendo partido {}", id);
        
        try {
            Partido partido = partidoService.obtenerPartidoPorId(id);
            return ResponseEntity.ok(partido);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos los partidos", description = "Retorna todos los partidos")
    public ResponseEntity<List<Partido>> listarPartidos() {
        log.info("REST: Listando todos los partidos");
        
        List<Partido> partidos = partidoService.obtenerTodosLosPartidos();
        return ResponseEntity.ok(partidos);
    }

    @GetMapping("/usuario/{usuarioId}/creados")
    @Operation(summary = "Partidos creados por usuario", description = "Retorna partidos creados por un usuario específico")
    public ResponseEntity<List<Partido>> partidosCreados(@PathVariable Long usuarioId) {
        log.info("REST: Obteniendo partidos creados por usuario {}", usuarioId);
        
        try {
            List<Partido> partidos = partidoService.obtenerPartidosDeUsuario(usuarioId);
            return ResponseEntity.ok(partidos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuario/{usuarioId}/participando")
    @Operation(summary = "Partidos donde participa usuario", description = "Retorna partidos donde participa un usuario específico")
    public ResponseEntity<List<Partido>> partidosParticipando(@PathVariable Long usuarioId) {
        log.info("REST: Obteniendo partidos donde participa usuario {}", usuarioId);
        
        List<Partido> partidos = partidoService.obtenerPartidosParticipando(usuarioId);
        return ResponseEntity.ok(partidos);
    }
} 