package com.pds.sportsmanager.dtos.partido;

import com.pds.sportsmanager.model.entity.Deporte;
import com.pds.sportsmanager.model.entity.Ubicacion;
import com.pds.sportsmanager.model.entity.Usuario;
import com.pds.sportsmanager.patterns.strategy.EstrategiaEmparejamiento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartidoDto {
    private String titulo; // NO aparece dentro del diagrama
    private Deporte deporte;
    private Ubicacion ubicacion;
    private LocalDateTime fechaHora;
    private Integer duracionMinutos;
    private Integer cantidadJugadoresRequeridos;
    private EstrategiaEmparejamiento estrategiaEmparejamiento;
    private Long ownerId;
}

