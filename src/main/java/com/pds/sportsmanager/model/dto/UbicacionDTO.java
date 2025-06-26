// src/main/java/com/pds/sportsmanager/model/dto/UbicacionDTO.java
package com.pds.sportsmanager.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UbicacionDTO {
    private Long id;
    private String direccion;
    private Double latitud;
    private Double longitud;
}