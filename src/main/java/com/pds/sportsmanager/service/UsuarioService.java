package com.pds.sportsmanager.service;

import com.pds.sportsmanager.model.entity.PreferenciaNotificacion;
import com.pds.sportsmanager.model.entity.Usuario;
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
    public Usuario registrarUsuario(Usuario usuario) {
        logger.info("Registrando nuevo usuario: {}", usuario.getNombreUsuario());
        
        // Validaciones de negocio
        validarUsuarioParaRegistro(usuario);
        
        // Encriptar contraseña
        usuario.setContrasenia(passwordEncoder.encode(usuario.getContrasenia()));

        // Inicializar preferencias de notificación

        // Guardar usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        logger.info("Usuario registrado exitosamente con ID: {}", usuarioGuardado.getId());
        return usuarioGuardado;
    }

    /**
     * Actualiza un usuario existente
     */
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        logger.info("Actualizando usuario con ID: {}", id);
        
        Usuario usuarioExistente = obtenerUsuarioPorId(id);
        
        // Actualizar campos permitidos
        if (usuarioActualizado.getNombreUsuario() != null && 
            !usuarioActualizado.getNombreUsuario().equals(usuarioExistente.getNombreUsuario())) {
            
            if (usuarioRepository.existsByNombreUsuario(usuarioActualizado.getNombreUsuario())) {
                throw new IllegalArgumentException("El nombre de usuario ya está en uso");
            }
            usuarioExistente.setNombreUsuario(usuarioActualizado.getNombreUsuario());
        }
        
        if (usuarioActualizado.getEmail() != null && 
            !usuarioActualizado.getEmail().equals(usuarioExistente.getEmail())) {
            
            if (usuarioRepository.existsByEmail(usuarioActualizado.getEmail())) {
                throw new IllegalArgumentException("El email ya está en uso");
            }
            usuarioExistente.setEmail(usuarioActualizado.getEmail());
        }
        
        if (usuarioActualizado.getNivelDeJugador() != null) {
            usuarioExistente.setNivelDeJugador(usuarioActualizado.getNivelDeJugador());
        }
        
        if (usuarioActualizado.getDeporteFavorito() != null) {
            usuarioExistente.setDeporteFavorito(usuarioActualizado.getDeporteFavorito());
        }
        
        if (usuarioActualizado.getUbicacion() != null) {
            usuarioExistente.setUbicacion(usuarioActualizado.getUbicacion());
        }
        
        Usuario usuarioGuardado = usuarioRepository.save(usuarioExistente);
        logger.info("Usuario actualizado exitosamente");
        
        return usuarioGuardado;
    }

    /**
     * Actualiza la contraseña de un usuario
     */
    public void actualizarContrasenia(Long id, String nuevaContrasenia) {
        logger.info("Actualizando contraseña para usuario ID: {}", id);
        
        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setContrasenia(passwordEncoder.encode(nuevaContrasenia));
        usuarioRepository.save(usuario);
        
        logger.info("Contraseña actualizada exitosamente");
    }

    /**
     * Busca un usuario por nombre de usuario
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    /**
     * Busca un usuario por email
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Obtiene un usuario por ID
     */
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Obtiene todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca usuarios cercanos a una ubicación
     */
    @Transactional(readOnly = true)
    public List<Usuario> buscarUsuariosCercanos(Double latitud, Double longitud, Double radioKm) {
        return usuarioRepository.findUsuariosCercanos(latitud, longitud, radioKm);
    }

    /**
     * Busca usuarios activos
     */
    @Transactional(readOnly = true)
    public List<Usuario> buscarUsuariosActivos() {
        return usuarioRepository.findUsuariosActivos();
    }

    /**
     * Elimina un usuario
     */
    public void eliminarUsuario(Long id) {
        logger.info("Eliminando usuario con ID: {}", id);
        
        Usuario usuario = obtenerUsuarioPorId(id);
        
        // Verificar que no tenga partidos activos
        Long partidosActivos = usuarioRepository.findById(id)
                .map(u -> u.getPartidosParticipando().stream()
                        .filter(p -> !p.getEstadoNombre().equals("PARTIDO_FINALIZADO") && 
                                   !p.getEstadoNombre().equals("PARTIDO_CANCELADO"))
                        .count())
                .orElse(0L);
        
        if (partidosActivos > 0) {
            throw new IllegalStateException("No se puede eliminar un usuario con partidos activos");
        }
        
        usuarioRepository.delete(usuario);
        logger.info("Usuario eliminado exitosamente");
    }

    /**
     * Valida la contraseña de un usuario
     */
    @Transactional(readOnly = true)
    public boolean validarContrasenia(String nombreUsuario, String contrasenia) {
        Optional<Usuario> usuario = buscarPorNombreUsuario(nombreUsuario);
        
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
    private void validarUsuarioParaRegistro(Usuario usuario) {
        if (usuarioRepository.existsByNombreUsuario(usuario.getNombreUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }
        
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        if (usuario.getContrasenia() == null || usuario.getContrasenia().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
    }
} 