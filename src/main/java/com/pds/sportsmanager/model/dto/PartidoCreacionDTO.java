package com.pds.sportsmanager.model.dto;

import com.pds.sportsmanager.model.entity.Ubicacion;
import com.pds.sportsmanager.model.enums.NivelDeJuego;
import lombok.Builder;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * DTO para la creación de partidos usando el patrón Builder con Records.
 */
@Builder
public record PartidoCreacionDTO(
    
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no puede exceder 100 caracteres")
    String titulo,
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    String descripcion,
    
    @NotNull(message = "La ubicación es obligatoria")
    Ubicacion ubicacion,
    
    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La fecha debe ser futura")
    LocalDateTime fechaHora,
    
    @NotNull(message = "El deporte es obligatorio")
    Long deporteId,
    
    @NotNull(message = "El nivel mínimo es obligatorio")
    NivelDeJuego nivelMinimo,
    
    @NotNull(message = "El nivel máximo es obligatorio")
    NivelDeJuego nivelMaximo,
    
    @Min(value = 2, message = "Mínimo 2 jugadores")
    @Max(value = 22, message = "Máximo 22 jugadores")
    Integer maxJugadores,
    
    Boolean esPrivado,
    
    Boolean requiereConfirmacion,
    
    @Size(max = 200, message = "Las notas no pueden exceder 200 caracteres")
    String notas,
    
    @DecimalMin(value = "0.0", message = "El costo no puede ser negativo")
    @DecimalMax(value = "10000.0", message = "El costo no puede exceder 10000")
    Double costo,
    
    String equipamientoRequerido
    
) {
    
    public static PartidoCreacionDTOBuilder futbolBuilder() {
        return PartidoCreacionDTO.builder()
            .nivelMinimo(NivelDeJuego.PRINCIPIANTE)
            .nivelMaximo(NivelDeJuego.AVANZADO)
            .maxJugadores(10)
            .requiereConfirmacion(true)  
            .esPrivado(false);           
    }
    
    /**
     * Factory method para partidos de tenis
     */
    public static PartidoCreacionDTOBuilder tenisBuilder() {
        return PartidoCreacionDTO.builder()
            .nivelMinimo(NivelDeJuego.PRINCIPIANTE)
            .nivelMaximo(NivelDeJuego.AVANZADO)
            .maxJugadores(2)
            .requiereConfirmacion(false)
            .esPrivado(true);
    }
    
    /**
     * Builder con valores por defecto más comunes
     */
    public static PartidoCreacionDTOBuilder defaultBuilder() {
        return PartidoCreacionDTO.builder()
            .esPrivado(false)
            .requiereConfirmacion(true);
    }
} 