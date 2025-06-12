package com.pds.sportsmanager.model.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Embeddable
public record Ubicacion(
    @NotBlank(message = "La dirección es obligatoria")
    String direccion,
    
    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    Double latitud,
    
    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    Double longitud
) {
    
    public Ubicacion {
        if (direccion != null) {
            direccion = direccion.trim();
        }
    }
    
    /**
     * Calcula la distancia en kilómetros entre esta ubicación y otra usando la fórmula de Haversine
     */
    public double calcularDistanciaKm(Ubicacion otra) {
        if (otra == null) {
            throw new IllegalArgumentException("La otra ubicación no puede ser null");
        }
        
        final double R = 6371; // Radio de la Tierra en km
        
        double latDistance = Math.toRadians(otra.latitud - this.latitud);
        double lonDistance = Math.toRadians(otra.longitud - this.longitud);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitud)) * Math.cos(Math.toRadians(otra.latitud))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * Verifica si esta ubicación está dentro del radio especificado de otra ubicación
     */
    public boolean estaDentroDelRadio(Ubicacion otra, double radioKm) {
        return calcularDistanciaKm(otra) <= radioKm;
    }
} 