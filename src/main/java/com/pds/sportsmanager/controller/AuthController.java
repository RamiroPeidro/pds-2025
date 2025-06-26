package com.pds.sportsmanager.controller;

import com.pds.sportsmanager.dtos.auth.LoginDto;
import com.pds.sportsmanager.dtos.auth.RegistrarUsuarioDto;
import com.pds.sportsmanager.dtos.auth.RespuestaLoginDto;
import com.pds.sportsmanager.dtos.usuario.CrearUsuarioDto;
import com.pds.sportsmanager.dtos.usuario.UsuarioDto;
import com.pds.sportsmanager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDto> register(@Valid @RequestBody RegistrarUsuarioDto registrarUsuarioDto) {
        UsuarioDto created = authService.register(registrarUsuarioDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<RespuestaLoginDto> login(@Valid @RequestBody LoginDto loginDto) {
        RespuestaLoginDto resp = authService.login(loginDto);
        return ResponseEntity.ok(resp);
    }

    /*
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    */

}
