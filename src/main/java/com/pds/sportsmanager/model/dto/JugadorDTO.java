package com.pds.sportsmanager.model.dto;

import com.pds.sportsmanager.model.entity.Ubicacion;
import com.pds.sportsmanager.model.enums.NivelDeJuego;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record JugadorDTO(
    Long id,
    String nombre,
    String email,
    NivelDeJuego nivelDeJuego,
    String deporteFavorito,            // Deporte principal (String)
    List<String> deportesFavoritos,    // Lista de todos los deportes (tabla intermedia)
    Ubicacion ubicacion,
    LocalDateTime createdAt
) {
} 