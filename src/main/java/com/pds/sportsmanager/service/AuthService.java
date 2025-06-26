package com.pds.sportsmanager.service;

import com.pds.sportsmanager.dtos.auth.LoginDto;
import com.pds.sportsmanager.dtos.auth.RegistrarUsuarioDto;
import com.pds.sportsmanager.dtos.auth.RespuestaLoginDto;
import com.pds.sportsmanager.dtos.usuario.CrearUsuarioDto;
import com.pds.sportsmanager.dtos.usuario.UsuarioDto;
import com.pds.sportsmanager.model.entity.Usuario;
import com.pds.sportsmanager.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class AuthService {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioService usuarioService,
                       AuthenticationManager authManager,
                       JwtUtil jwtUtil,
                       PasswordEncoder passwordEncoder) {
        this.usuarioService   = usuarioService;
        this.authManager      = authManager;
        this.jwtUtil          = jwtUtil;
        this.passwordEncoder  = passwordEncoder;
    }

    public UsuarioDto register(RegistrarUsuarioDto dto) {
        // 1) Validar que no exista otro usuario con el mismo email
        if (usuarioService.esEmailDisponible(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        // 2) Crear entidad Usuario a partir del DTO
        Usuario u = new Usuario();
        u.setNombreUsuario(dto.getNombreUsuario());
        u.setEmail(dto.getEmail());
        u.setContrasenia(passwordEncoder.encode(dto.getContrasenia()));

        // 3) Persistir y mapear a DTO de respuesta
        Usuario saved = usuarioService.registrarUsuario(u);
        return new UsuarioDto(
                saved.getId(),
                saved.getNombreUsuario(),
                saved.getEmail()
        );
    }

    public RespuestaLoginDto login(LoginDto loginDto) {
        try {
            // Autenticar usando el email en lugar de nombreUsuario
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getContrasenia()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        // Generar el token usando el email como sujeto
        String token = jwtUtil.generateToken(loginDto.getEmail());
        return new RespuestaLoginDto(token);
    }

    /*
    public void deleteUser(Long userId) {
        if (!usuarioService.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }
        usuarioService.deleteById(userId);
    }
    */

}
