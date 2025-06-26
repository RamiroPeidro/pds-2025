package com.pds.sportsmanager.service;

import com.pds.sportsmanager.model.entity.Jugador;
import com.pds.sportsmanager.model.entity.Ubicacion;
import com.pds.sportsmanager.repository.JugadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JugadorService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(JugadorService.class);
    private final UbicacionService ubicacionService;

    private final JugadorRepository jugadorRepository;
    private final PasswordEncoder passwordEncoder;

    public JugadorService(JugadorRepository jugadorRepository, PasswordEncoder passwordEncoder, UbicacionService ubicacionService) {
        this.jugadorRepository = jugadorRepository;
        this.passwordEncoder = passwordEncoder;
        this.ubicacionService = ubicacionService;
    }

    /**
     * Para Spring Security: carga usuario por email (username)
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Jugador j = jugadorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Jugador no encontrado: " + email));
        return new User(j.getEmail(), j.getContrasenia(), Collections.emptyList());
    }

    /**
     * Registra un nuevo jugador: valida, encripta y guarda
     */
    public Jugador registrarJugador(Jugador jugador) {
        logger.info("Registrando nuevo jugador: {}", jugador.getNombre());

        // validaciones de negocio
        validarJugadorParaRegistro(jugador);

        // persistir primero la ubicación
        Ubicacion ubicacionInicial = jugador.getUbicacion();
        Ubicacion ubicacionGuardada = ubicacionService.crearUbicacion(ubicacionInicial);
        jugador.setUbicacion(ubicacionGuardada);

        // encriptar contraseña
        jugador.setContrasenia(passwordEncoder.encode(jugador.getContrasenia()));

        // guardar jugador con la ubicación ya persistida
        Jugador guardado = jugadorRepository.save(jugador);
        logger.info("Jugador registrado exitosamente con ID: {}", guardado.getId());
        return guardado;
    }

    /**
     * Actualiza un jugador existente
     */
    public Jugador actualizarJugador(Long id, Jugador jugadorActualizado) {
        logger.info("Actualizando jugador con ID: {}", id);
        Jugador existente = obtenerJugadorPorId(id);

        if (jugadorActualizado.getNombre() != null && !jugadorActualizado.getNombre().equals(existente.getNombre())) {
            if (jugadorRepository.existsByNombre(jugadorActualizado.getNombre())) {
                throw new IllegalArgumentException("El nombre de jugador ya está en uso");
            }
            existente.setNombre(jugadorActualizado.getNombre());
        }
        if (jugadorActualizado.getEmail() != null && !jugadorActualizado.getEmail().equals(existente.getEmail())) {
            if (jugadorRepository.existsByEmail(jugadorActualizado.getEmail())) {
                throw new IllegalArgumentException("El email ya está en uso");
            }
            existente.setEmail(jugadorActualizado.getEmail());
        }
        if (jugadorActualizado.getNivelDeJuego() != null) {
            existente.setNivelDeJuego(jugadorActualizado.getNivelDeJuego());
        }
        if (jugadorActualizado.getDeporteFavorito() != null) {
            existente.setDeporteFavorito(jugadorActualizado.getDeporteFavorito());
        }
        if (jugadorActualizado.getUbicacion() != null) {
            existente.setUbicacion(jugadorActualizado.getUbicacion());
        }

        Jugador actualizado = jugadorRepository.save(existente);
        logger.info("Jugador actualizado exitosamente");
        return actualizado;
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
     * Busca un jugador por nombre
     */
    @Transactional(readOnly = true)
    public Optional<Jugador> buscarPorNombre(String nombre) {
        return jugadorRepository.findByNombre(nombre);
    }

    /**
     * Busca un jugador por email
     */
    @Transactional(readOnly = true)
    public Optional<Jugador> buscarPorEmail(String email) {
        return jugadorRepository.findByEmail(email);
    }

    /**
     * Obtiene un jugador por ID
     */
    @Transactional(readOnly = true)
    public Jugador obtenerJugadorPorId(Long id) {
        return jugadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + id));
    }

    /**
     * Obtiene todos los jugadores
     */
    @Transactional(readOnly = true)
    public List<Jugador> obtenerTodosLosJugadores() {
        return jugadorRepository.findAll();
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
     * Elimina un jugador verificando que no tenga partidos activos
     */
    public void eliminarJugador(Long id) {
        logger.info("Eliminando jugador con ID: {}", id);
        Jugador j = obtenerJugadorPorId(id);
        long activos = j.getPartidos().stream()
                .filter(p -> !p.getEstadoNombre().equals("PARTIDO_FINALIZADO")
                        && !p.getEstadoNombre().equals("PARTIDO_CANCELADO"))
                .count();
        if (activos > 0) {
            throw new IllegalStateException("No se puede eliminar un jugador con partidos activos");
        }
        jugadorRepository.delete(j);
        logger.info("Jugador eliminado exitosamente");
    }

    /**
     * Valida la contraseña de un jugador
     */
    @Transactional(readOnly = true)
    public boolean validarContrasenia(String nombre, String contrasenia) {
        return buscarPorNombre(nombre)
                .map(j -> passwordEncoder.matches(contrasenia, j.getContrasenia()))
                .orElse(false);
    }

    /**
     * Verifica si un nombre está disponible
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
     * Validaciones de negocio para registro
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
}