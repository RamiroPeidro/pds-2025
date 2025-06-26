package com.pds.sportsmanager.service;

import com.pds.sportsmanager.controller.PartidoController.PartidoInputDTO;
import com.pds.sportsmanager.model.dto.PartidoBusquedaResult;
import com.pds.sportsmanager.model.entity.Deporte;
import com.pds.sportsmanager.model.entity.Jugador;
import com.pds.sportsmanager.model.entity.Partido;
import com.pds.sportsmanager.model.entity.Ubicacion;
import com.pds.sportsmanager.patterns.state.NecesitamosJugadores;
import com.pds.sportsmanager.patterns.strategy.EstrategiaEmparejamiento;
import com.pds.sportsmanager.repository.PartidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PartidoService {

    private static final Logger logger = LoggerFactory.getLogger(PartidoService.class);

    private final PartidoRepository partidoRepository;
    private final JugadorService jugadorService;
    private final NotificacionService notificacionService;
    private final DeporteService deporteService;
    private final UbicacionService ubicacionService;
    private final EstrategiaEmparejamientoFactory estrategiaFactory;

    @Autowired
    public PartidoService(PartidoRepository partidoRepository, 
                         JugadorService jugadorService,
                         NotificacionService notificacionService,
                         DeporteService deporteService,
                         UbicacionService ubicacionService,
                         EstrategiaEmparejamientoFactory estrategiaFactory) {
        this.partidoRepository = partidoRepository;
        this.jugadorService = jugadorService;
        this.notificacionService = notificacionService;
        this.deporteService = deporteService;
        this.ubicacionService = ubicacionService;
        this.estrategiaFactory = estrategiaFactory;
    }

    /**
     * Crea un nuevo partido desde DTO
     * Retorna Optional.empty() si alguna entidad relacionada no existe
     */
    public Optional<Partido> crearPartidoFromDTO(PartidoInputDTO partidoDTO) {
        logger.info("Creando nuevo partido desde DTO: {}", partidoDTO.titulo());
        
        // Obtener entidades relacionadas - si alguna no existe, retornar empty
        Optional<Ubicacion> ubicacionOpt = ubicacionService.obtenerUbicacionPorId(partidoDTO.ubicacionId());
        if (ubicacionOpt.isEmpty()) {
            logger.warn("Ubicación no encontrada con ID: {}", partidoDTO.ubicacionId());
            return Optional.empty();
        }
        
        Optional<Deporte> deporteOpt = deporteService.obtenerDeportePorId(partidoDTO.deporteId());
        if (deporteOpt.isEmpty()) {
            logger.warn("Deporte no encontrado con ID: {}", partidoDTO.deporteId());
            return Optional.empty();
        }
        
        Jugador owner;
        try {
            owner = jugadorService.obtenerJugadorPorId(partidoDTO.ownerId());
            if (owner == null) {
                logger.warn("Jugador no encontrado con ID: {}", partidoDTO.ownerId());
                return Optional.empty();
            }
        } catch (RuntimeException e) {
            logger.warn("Jugador no encontrado con ID: {}", partidoDTO.ownerId());
            return Optional.empty();
        }
        
        Ubicacion ubicacion = ubicacionOpt.get();
        Deporte deporte = deporteOpt.get();
        
        // Crear lista de jugadores con el owner
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(owner);
        
        // Crear el partido usando builder
        Partido partido = Partido.builder()
            .titulo(partidoDTO.titulo())
            .descripcion(partidoDTO.descripcion())
            .fechaHora(partidoDTO.fechaHora())
            .duracionMinutos(partidoDTO.duracionMinutos())
            .cantidadJugadoresRequeridos(partidoDTO.cantidadJugadoresRequeridos())
            .nivelMinimo(partidoDTO.nivelMinimo())
            .nivelMaximo(partidoDTO.nivelMaximo())
            .ubicacion(ubicacion)
            .deporte(deporte)
            .owner(owner)
            .jugadores(jugadores)
            .estado(new NecesitamosJugadores())
            .estadoNombre("NECESITAMOS_JUGADORES")
            .estadisticas(new ArrayList<>())
            .comentarios(new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        return Optional.of(crearPartido(partido));
    }

    /**
     * Crea un nuevo partido
     */
    public Partido crearPartido(Partido partido) {
        logger.info("Creando nuevo partido: {}", partido.getTitulo());
        
        // Validaciones de negocio
        validarPartido(partido);
        
        // Guardar el partido
        Partido partidoGuardado = partidoRepository.save(partido);
        
        // Notificar creación del partido
        notificacionService.notificarPartidoCreado(partidoGuardado);
        
        logger.info("Partido creado exitosamente con ID: {}", partidoGuardado.getId());
        return partidoGuardado;
    }

    /**
     * Agrega un jugador a un partido
     */
    public void agregarJugadorAlPartido(Long partidoId, Long jugadorId) {
        logger.info("Agregando jugador {} al partido {}", jugadorId, partidoId);
        
        Partido partido = obtenerPartidoPorIdInterno(partidoId);
        Jugador jugador = jugadorService.obtenerJugadorPorId(jugadorId);
        
        // Usar el patrón State para agregar el jugador
        partido.agregarJugador(jugador);
        
        // Guardar los cambios
        partidoRepository.save(partido);
        
        // Notificar si el partido cambió de estado
        if ("PARTIDO_ARMADO".equals(partido.getEstadoNombre())) {
            notificacionService.notificarPartidoArmado(partido);
        }
        
        logger.info("Jugador agregado exitosamente. Estado actual: {}", partido.getEstadoNombre());
    }

    /**
     * Confirma un partido
     */
    public void confirmarPartido(Long partidoId) {
        logger.info("Confirmando partido {}", partidoId);
        
        Partido partido = obtenerPartidoPorIdInterno(partidoId);
        partido.confirmarPartido();
        
        partidoRepository.save(partido);
        notificacionService.notificarPartidoConfirmado(partido);
        
        logger.info("Partido confirmado exitosamente");
    }

    /**
     * Cancela un partido
     */
    public void cancelarPartido(Long partidoId) {
        logger.info("Cancelando partido {}", partidoId);
        
        Partido partido = obtenerPartidoPorIdInterno(partidoId);
        partido.cancelarPartido();
        
        partidoRepository.save(partido);
        notificacionService.notificarPartidoCancelado(partido);
        
        logger.info("Partido cancelado exitosamente");
    }

    /**
     * Finaliza un partido
     */
    public void finalizarPartido(Long partidoId) {
        logger.info("Finalizando partido {}", partidoId);
        
        Partido partido = obtenerPartidoPorIdInterno(partidoId);
        partido.finalizarPartido();
        
        partidoRepository.save(partido);
        notificacionService.notificarPartidoFinalizado(partido);
        
        logger.info("Partido finalizado exitosamente");
    }

    /**
     * Busca partidos usando una estrategia por nombre (para Controller)
     */
    @Transactional(readOnly = true)
    public List<PartidoBusquedaResult> buscarPartidos(Long usuarioId, String estrategia) {
        logger.info("Buscando partidos para usuario {} con estrategia {}", usuarioId, estrategia);
        
        // Obtener la estrategia usando la Factory
        EstrategiaEmparejamiento estrategiaObj = estrategiaFactory.obtenerEstrategia(estrategia);
        
        // Delegar al método que recibe la estrategia
        return buscarPartidos(usuarioId, estrategiaObj);
    }

    /**
     * Busca partidos usando una estrategia específica (para uso interno)
     */
    @Transactional(readOnly = true)
    public List<PartidoBusquedaResult> buscarPartidos(Long usuarioId, EstrategiaEmparejamiento estrategia) {
        logger.info("Buscando partidos para usuario {} con estrategia {}", usuarioId, estrategia.getNombre());
        
        Jugador jugador = jugadorService.obtenerJugadorPorId(usuarioId);
        List<Partido> partidosDisponibles = partidoRepository.findPartidosNecesitanJugadores(LocalDateTime.now());
        
        // Convertir a DTO con información de distancia
        List<PartidoBusquedaResult> partidosDTO = partidosDisponibles.stream()
                .map(partido -> convertirAPartidoBusquedaResult(partido, jugador))
                .collect(Collectors.toList());
        
        // Aplicar la estrategia de búsqueda
        List<PartidoBusquedaResult> resultados = estrategia.buscarPartidos(jugador, partidosDTO);
        
        logger.info("Encontrados {} partidos compatibles", resultados.size());
        return resultados;
    }

    /**
     * Obtiene partidos cercanos a una ubicación
     */
    @Transactional(readOnly = true)
    public List<Partido> obtenerPartidosCercanos(Double latitud, Double longitud, Double radioKm) {
        return partidoRepository.findPartidosCercanosWithRelations(latitud, longitud, radioKm, LocalDateTime.now());
    }

    /**
     * Obtiene partidos de un usuario
     */
    @Transactional(readOnly = true)
    public List<Partido> obtenerPartidosDeUsuario(Long usuarioId) {
        Jugador jugador = jugadorService.obtenerJugadorPorId(usuarioId);
        return partidoRepository.findByOwnerWithRelations(jugador);
    }

    /**
     * Obtiene partidos donde participa un usuario
     */
    @Transactional(readOnly = true)
    public List<Partido> obtenerPartidosParticipando(Long usuarioId) {
        return partidoRepository.findPartidosUsuarioParticipaWithRelations(usuarioId);
    }

    /**
     * Inicia partidos que están listos para comenzar
     */
    public void iniciarPartidosProgamados() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime ventana = ahora.plusMinutes(5); // 5 minutos de ventana
        
        List<Partido> partidosParaIniciar = partidoRepository.findPartidosParaIniciar(ahora, ventana);
        
        for (Partido partido : partidosParaIniciar) {
            try {
                partido.enJuego();
                partidoRepository.save(partido);
                notificacionService.notificarPartidoEnJuego(partido);
                logger.info("Partido {} iniciado automáticamente", partido.getId());
            } catch (Exception e) {
                logger.error("Error al iniciar partido {}: {}", partido.getId(), e.getMessage());
            }
        }
    }

    /**
     * Obtiene un partido por ID
     */
    @Transactional(readOnly = true)
    public Optional<Partido> obtenerPartidoPorId(Long id) {
        Partido partido = partidoRepository.findByIdWithRelations(id);
        return Optional.ofNullable(partido);
    }
    
    /**
     * Obtiene un partido por ID (para uso interno donde sabemos que existe)
     */
    @Transactional(readOnly = true)
    public Partido obtenerPartidoPorIdInterno(Long id) {
        Partido partido = partidoRepository.findByIdWithRelations(id);
        if (partido == null) {
            throw new IllegalStateException("Partido no encontrado con ID: " + id);
        }
        return partido;
    }

    /**
     * Obtiene todos los partidos
     */
    @Transactional(readOnly = true)
    public List<Partido> obtenerTodosLosPartidos() {
        return partidoRepository.findAllWithRelations();
    }

    /**
     * Validaciones de negocio para un partido
     */
    private void validarPartido(Partido partido) {
        if (partido.getFechaHora().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new IllegalArgumentException("La fecha del partido debe ser al menos 1 hora en el futuro");
        }
        
        if (partido.getCantidadJugadoresRequeridos() < 2) {
            throw new IllegalArgumentException("Se requieren al menos 2 jugadores para un partido");
        }
        
        if (partido.getDuracionMinutos() < 15 || partido.getDuracionMinutos() > 300) {
            throw new IllegalArgumentException("La duración debe estar entre 15 y 300 minutos");
        }
    }

    /**
     * Convierte un Partido a PartidoBusquedaResult calculando la distancia
     */
    private PartidoBusquedaResult convertirAPartidoBusquedaResult(Partido partido, Jugador jugador) {
        Double distancia = null;
        
        if (jugador.getUbicacion() != null && partido.getUbicacion() != null) {
            distancia = jugador.getUbicacion().calcularDistanciaKm(partido.getUbicacion());
        }
        
        return new PartidoBusquedaResult(
                partido.getId(),
                partido.getDeporte().getNombre(),
                partido.getTitulo(),
                partido.getDescripcion(),
                partido.getCantidadJugadoresRequeridos(),
                partido.getJugadores().size(),
                partido.getFechaHora(),
                partido.getDuracionMinutos(),
                partido.getUbicacion(),
                partido.getOwner().getNombre(),
                partido.getNivelMinimo(),
                partido.getNivelMaximo(),
                partido.getEstadoNombre(),
                distancia
        );
    }
} 