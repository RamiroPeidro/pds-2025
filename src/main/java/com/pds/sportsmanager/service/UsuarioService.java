package com.pds.sportsmanager.service;

import com.pds.sportsmanager.model.entity.Jugador;
import com.pds.sportsmanager.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo usuario
     */
    public Jugador registrarUsuario(Jugador jugador) {
        logger.info("Registrando nuevo jugador: {}", jugador.getNombre());
        
        // Validaciones de negocio
        validarUsuarioParaRegistro(jugador);
        
        // Encriptar contraseña
        jugador.setContrasenia(passwordEncoder.encode(jugador.getContrasenia()));
        
        // Guardar usuario
        Jugador jugadorGuardado = usuarioRepository.save(jugador);
        
        logger.info("Usuario registrado exitosamente con ID: {}", jugadorGuardado.getId());
        return jugadorGuardado;
    }

    /**
     * Actualiza un usuario existente
     */
    public Jugador actualizarUsuario(Long id, Jugador jugadorActualizado) {
        logger.info("Actualizando usuario con ID: {}", id);
        
        Jugador jugadorExistente = obtenerUsuarioPorId(id);
        
        // Actualizar campos permitidos
        if (jugadorActualizado.getNombre() != null &&
            !jugadorActualizado.getNombre().equals(jugadorExistente.getNombre())) {
            
            if (usuarioRepository.existsByNombreUsuario(jugadorActualizado.getNombre())) {
                throw new IllegalArgumentException("El nombre de usuario ya está en uso");
            }
            jugadorExistente.setNombre(jugadorActualizado.getNombre());
        }
        
        if (jugadorActualizado.getEmail() != null &&
            !jugadorActualizado.getEmail().equals(jugadorExistente.getEmail())) {
            
            if (usuarioRepository.existsByEmail(jugadorActualizado.getEmail())) {
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
        
        Jugador jugadorGuardado = usuarioRepository.save(jugadorExistente);
        logger.info("Usuario actualizado exitosamente");
        
        return jugadorGuardado;
    }

    /**
     * Actualiza la contraseña de un usuario
     */
    public void actualizarContrasenia(Long id, String nuevaContrasenia) {
        logger.info("Actualizando contraseña para usuario ID: {}", id);
        
        Jugador jugador = obtenerUsuarioPorId(id);
        jugador.setContrasenia(passwordEncoder.encode(nuevaContrasenia));
        usuarioRepository.save(jugador);
        
        logger.info("Contraseña actualizada exitosamente");
    }

    /**
     * Busca un usuario por nombre de usuario
     */
    @Transactional(readOnly = true)
    public Optional<Jugador> buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    /**
     * Busca un usuario por email
     */
    @Transactional(readOnly = true)
    public Optional<Jugador> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Obtiene un usuario por ID
     */
    @Transactional(readOnly = true)
    public Jugador obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Obtiene todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<Jugador> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca usuarios cercanos a una ubicación
     */
    @Transactional(readOnly = true)
    public List<Jugador> buscarUsuariosCercanos(Double latitud, Double longitud, Double radioKm) {
        return usuarioRepository.findUsuariosCercanos(latitud, longitud, radioKm);
    }

    /**
     * Busca usuarios activos
     */
    @Transactional(readOnly = true)
    public List<Jugador> buscarUsuariosActivos() {
        return usuarioRepository.findUsuariosActivos();
    }

    /**
     * Elimina un usuario
     */
    public void eliminarUsuario(Long id) {
        logger.info("Eliminando usuario con ID: {}", id);
        
        Jugador jugador = obtenerUsuarioPorId(id);
        
        // Verificar que no tenga partidos activos
        long partidosActivos = usuarioRepository.findById(id)
                .map(u -> u.getPartidos().stream()
                        .filter(p -> !p.getEstado().equals("PARTIDO_FINALIZADO") &&
                                !p.getEstado().equals("PARTIDO_CANCELADO"))
                        .count())
                .orElse(0L);

        if (partidosActivos > 0) {
            throw new IllegalStateException("No se puede eliminar un usuario con partidos activos");
        }
        
        usuarioRepository.delete(jugador);
        logger.info("Usuario eliminado exitosamente");
    }

    /**
     * Valida la contraseña de un usuario
     */
    @Transactional(readOnly = true)
    public boolean validarContrasenia(String nombreUsuario, String contrasenia) {
        Optional<Jugador> usuario = buscarPorNombreUsuario(nombreUsuario);
        
        if (usuario.isPresent()) {
            return passwordEncoder.matches(contrasenia, usuario.get().getContrasenia());
        }
        
        return false;
    }

    /**
     * Verifica si un nombre de usuario está disponible
     */
    @Transactional(readOnly = true)
    public boolean esNombreUsuarioDisponible(String nombreUsuario) {
        return !usuarioRepository.existsByNombreUsuario(nombreUsuario);
    }

    /**
     * Verifica si un email está disponible
     */
    @Transactional(readOnly = true)
    public boolean esEmailDisponible(String email) {
        return !usuarioRepository.existsByEmail(email);
    }

    /**
     * Validaciones de negocio para el registro de usuario
     */
    private void validarUsuarioParaRegistro(Jugador jugador) {
        if (usuarioRepository.existsByNombreUsuario(jugador.getNombre())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }
        
        if (usuarioRepository.existsByEmail(jugador.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        if (jugador.getContrasenia() == null || jugador.getContrasenia().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
    }
} 