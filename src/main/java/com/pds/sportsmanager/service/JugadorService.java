package com.pds.sportsmanager.service;

import com.pds.sportsmanager.model.entity.Deporte;
import com.pds.sportsmanager.model.entity.Jugador;
import com.pds.sportsmanager.model.entity.Partido;
import com.pds.sportsmanager.model.entity.JugadorDeporte;
import com.pds.sportsmanager.repository.JugadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pds.sportsmanager.repository.PartidoRepository;
import com.pds.sportsmanager.repository.DeporteRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class JugadorService {

    private static final Logger logger = LoggerFactory.getLogger(JugadorService.class);

    private final JugadorRepository jugadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final PartidoRepository partidoRepository;
    private final DeporteRepository deporteRepository;


    @Autowired
    public JugadorService(
            JugadorRepository jugadorRepository,
            PartidoRepository partidoRepository,
            DeporteRepository deporteRepository,
            PasswordEncoder passwordEncoder
    ) {
    private final JugadorDeporteService jugadorDeporteService;
    private final PreferenciaNotificacionService preferenciaNotificacionService;


    @Autowired
    public JugadorService(JugadorRepository jugadorRepository, PasswordEncoder passwordEncoder, JugadorDeporteService jugadorDeporteService, PreferenciaNotificacionService preferenciaNotificacionService) {
        this.jugadorRepository = jugadorRepository;
        this.partidoRepository = partidoRepository;
        this.deporteRepository = deporteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jugadorDeporteService = jugadorDeporteService;
        this.preferenciaNotificacionService = preferenciaNotificacionService;
    }


    /**
     * Registra un nuevo jugador
     */
    public Jugador registrarJugador(Jugador jugador) {
        logger.info("Registrando nuevo jugador: {}", jugador.getNombre());
        
        // Validaciones de negocio
        validarJugadorParaRegistro(jugador);
        
        // Encriptar contraseña
        jugador.setContrasenia(passwordEncoder.encode(jugador.getContrasenia()));

        // Guardar jugador
        Jugador jugadorGuardado = jugadorRepository.save(jugador);

        // Registrar deportes asociados

        
        logger.info("Jugador registrado exitosamente con ID: {}", jugadorGuardado.getId());
        return jugadorGuardado;
    }

    /**
     * Actualiza un jugador existente
     */
    public Jugador actualizarJugador(Long id, Jugador jugadorActualizado) {
        logger.info("Actualizando jugador con ID: {}", id);
        
        Jugador jugadorExistente = obtenerJugadorPorId(id);
        
        // Actualizar campos permitidos
        if (jugadorActualizado.getNombre() != null &&
            !jugadorActualizado.getNombre().equals(jugadorExistente.getNombre())) {
            
            if (jugadorRepository.existsByNombre(jugadorActualizado.getNombre())) {
                throw new IllegalArgumentException("El nombre de jugador ya está en uso");
            }
            jugadorExistente.setNombre(jugadorActualizado.getNombre());
        }
        
        if (jugadorActualizado.getEmail() != null &&
            !jugadorActualizado.getEmail().equals(jugadorExistente.getEmail())) {
            
            if (jugadorRepository.existsByEmail(jugadorActualizado.getEmail())) {
                throw new IllegalArgumentException("El email ya está en uso");
            }
            jugadorExistente.setEmail(jugadorActualizado.getEmail());
        }
        
        if (jugadorActualizado.getNivelDeJuego() != null) {
            jugadorExistente.setNivelDeJuego(jugadorActualizado.getNivelDeJuego());
        }
        
        if (jugadorActualizado.getDeporteFavorito() != null) {
            jugadorExistente.setDeporteFavorito(jugadorActualizado.getDeporteFavorito());
        }
        
        if (jugadorActualizado.getUbicacion() != null) {
            jugadorExistente.setUbicacion(jugadorActualizado.getUbicacion());
        }
        
        Jugador jugadorGuardado = jugadorRepository.save(jugadorExistente);
        logger.info("Jugador actualizado exitosamente");
        
        return jugadorGuardado;
    }

    /**
     * Actualiza la contraseña de un jugador
     */
    public void actualizarContrasenia(Long id, String nuevaContrasenia) {
        logger.info("Actualizando contraseña para jugador ID: {}", id);
        
        Jugador jugador = obtenerJugadorPorId(id);
        jugador.setContrasenia(passwordEncoder.encode(nuevaContrasenia));
        jugadorRepository.save(jugador);
        
        logger.info("Contraseña actualizada exitosamente");
    }

    /**
     * Busca un jugador por nombre con deportes favoritos cargados
     */
    @Transactional(readOnly = true)
    public Optional<Jugador> buscarPorNombre(String nombre) {
        return jugadorRepository.findByNombreWithDeportesFavs(nombre);
    }

    /**
     * Busca un jugador por email con deportes favoritos cargados
     */
    @Transactional(readOnly = true)
    public Optional<Jugador> buscarPorEmail(String email) {
        return jugadorRepository.findByEmailWithDeportesFavs(email);
    }

    /**
     * Obtiene un jugador por ID con sus deportes favoritos cargados
     */
    @Transactional(readOnly = true)
    public Jugador obtenerJugadorPorId(Long id) {
        return jugadorRepository.findByIdWithDeportesFavs(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + id));
    }

    /**
     * Obtiene todos los jugadores con sus deportes favoritos cargados
     */
    @Transactional(readOnly = true)
    public List<Jugador> obtenerTodosLosJugadores() {
        return jugadorRepository.findAllWithDeportesFavs();
    }

    /**
     * Busca jugadores cercanos a una ubicación
     */
    @Transactional(readOnly = true)
    public List<Jugador> buscarJugadoresCercanos(Double latitud, Double longitud, Double radioKm) {
        return jugadorRepository.findJugadoresCercanos(latitud, longitud, radioKm);
    }

    /**
     * Busca jugadores activos
     */
    @Transactional(readOnly = true)
    public List<Jugador> buscarJugadoresActivos() {
        return jugadorRepository.findJugadoresActivos();
    }

    /**
     * Elimina un jugador
     */
    public void eliminarJugador(Long id) {
        logger.info("Eliminando jugador con ID: {}", id);
        
        Jugador jugador = obtenerJugadorPorId(id);
        
        // Verificar que no tenga partidos activos
        long partidosActivos = jugadorRepository.findById(id)
                .map(j -> j.getPartidos().stream()
                        .filter(p -> !p.getEstadoNombre().equals("PARTIDO_FINALIZADO") &&
                                !p.getEstadoNombre().equals("PARTIDO_CANCELADO"))
                        .count())
                .orElse(0L);

        if (partidosActivos > 0) {
            throw new IllegalStateException("No se puede eliminar un jugador con partidos activos");
        }
        
        jugadorRepository.delete(jugador);
        logger.info("Jugador eliminado exitosamente");
    }

    /**
     * Valida la contraseña de un jugador
     */
    @Transactional(readOnly = true)
    public boolean validarContrasenia(String nombre, String contrasenia) {
        Optional<Jugador> jugador = buscarPorNombre(nombre);
        
        if (jugador.isPresent()) {
            return passwordEncoder.matches(contrasenia, jugador.get().getContrasenia());
        }
        
        return false;
    }

    /**
     * Verifica si un nombre de jugador está disponible
     */
    @Transactional(readOnly = true)
    public boolean esNombreJugadorDisponible(String nombre) {
        return !jugadorRepository.existsByNombre(nombre);
    }

    /**
     * Verifica si un email está disponible
     */
    @Transactional(readOnly = true)
    public boolean esEmailDisponible(String email) {
        return !jugadorRepository.existsByEmail(email);
    }

    /**
     * Validaciones de negocio para el registro de jugador
     */
    private void validarJugadorParaRegistro(Jugador jugador) {
        if (jugadorRepository.existsByNombre(jugador.getNombre())) {
            throw new IllegalArgumentException("El nombre de jugador ya está en uso");
        }
        
        if (jugadorRepository.existsByEmail(jugador.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        if (jugador.getContrasenia() == null || jugador.getContrasenia().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
    }
    public void crearPartido(Long jugadorId, Partido partido) {
        Jugador jugador = obtenerJugadorPorId(jugadorId);
        jugador.crearPartido(partido);
        partidoRepository.save(partido);
        jugadorRepository.save(jugador);
    }

    public void aceptarPartido(Long jugadorId, Long partidoId) {
        Jugador jugador = obtenerJugadorPorId(jugadorId);
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        jugador.aceptarPartido(partido);
        partidoRepository.save(partido);
        jugadorRepository.save(jugador);
    }

    public List<Partido> buscarPartidosDisponibles(Long jugadorId) {
        Jugador jugador = obtenerJugadorPorId(jugadorId);
        List<Partido> disponibles = partidoRepository.findAll();
        return jugador.buscarPartido(disponibles);
    }

    public void agregarDeporteFavorito(Long jugadorId, Long deporteId) {
        Jugador jugador = obtenerJugadorPorId(jugadorId);
        Deporte deporte = deporteRepository.findById(deporteId)
                .orElseThrow(() -> new RuntimeException("Deporte no encontrado"));
        jugador.agregarDeporte(deporte);
        jugadorRepository.save(jugador);
    }

    public void establecerDeporteFavorito(Long jugadorId, String nombreDeporte) {
        Jugador jugador = obtenerJugadorPorId(jugadorId);
        jugador.establecerDeporteFavorito(nombreDeporte);
        jugadorRepository.save(jugador);

    /**
     * Busca jugadores por deporte favorito (múltiples deportes + fallback)
     * Combina tabla intermedia + campo String para máxima cobertura
     */
    public List<Jugador> buscarJugadoresPorDeporte(Long deporteId) {
        logger.info("Buscando jugadores por deporte ID: {}", deporteId);
        
        // 1. Buscar en tabla intermedia (múltiples deportes)
        List<Jugador> jugadoresMultiples = jugadorRepository.findByDeporteFavorito(deporteId);
        
        // 2. Buscar en campo String (fallback para jugadores sin tabla intermedia)
        List<Jugador> jugadoresString = jugadorRepository.findByDeporteFavoritoString(deporteId);
        
        // 3. Combinar y eliminar duplicados usando Set
        Set<Jugador> jugadoresUnicos = new HashSet<>(jugadoresMultiples);
        jugadoresUnicos.addAll(jugadoresString);
        
        List<Jugador> resultado = new ArrayList<>(jugadoresUnicos);
        logger.info("Encontrados {} jugadores para deporte ID {}: {} con múltiples deportes, {} por String", 
                   resultado.size(), deporteId, jugadoresMultiples.size(), jugadoresString.size());
        
        return resultado;
    }
} 