package com.pds.sportsmanager.repository;

import com.pds.sportsmanager.model.entity.JugadorDeporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JugadorDeporteRepository extends JpaRepository<JugadorDeporte, Long> {

    //buscar jugadores por deporte
    List<JugadorDeporte> findByDeporteId(Long deporteId);

}
