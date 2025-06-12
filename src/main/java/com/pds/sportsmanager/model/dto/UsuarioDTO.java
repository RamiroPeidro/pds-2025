package com.pds.sportsmanager.model.dto;

import com.pds.sportsmanager.model.entity.Ubicacion;
import com.pds.sportsmanager.model.enums.NivelDeJugador;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UsuarioDTO(
    Long id,
    String nombreUsuario,
    String email,
    NivelDeJugador nivelDeJugador,
    String deporteFavorito,
    Ubicacion ubicacion,
    LocalDateTime createdAt
) {
} 