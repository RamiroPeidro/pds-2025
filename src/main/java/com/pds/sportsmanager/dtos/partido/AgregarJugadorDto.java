package com.pds.sportsmanager.dtos.partido;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgregarJugadorDto {
    private Long jugadorId;
    private Long partidoId;
}