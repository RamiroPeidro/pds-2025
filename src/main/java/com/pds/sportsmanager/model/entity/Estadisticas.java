package com.pds.sportsmanager.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

/**
 * Entidad JPA optimizada con Lombok
 * Representa las estadísticas de un jugador en un partido específico
 */
@Entity
@Table(name = "estadisticas")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Estadisticas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partido_id", nullable = false)
    @ToString.Exclude
    private Partido partido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jugador_id", nullable = false)
    @ToString.Exclude
    private Usuario jugador;

    @PositiveOrZero(message = "Los puntos deben ser cero o positivos")
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer puntos = 0;

    @PositiveOrZero(message = "Las faltas deben ser cero o positivas")
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer faltas = 0;

    @PositiveOrZero(message = "Las tarjetas amarillas deben ser cero o positivas")
    @Column(name = "tarjetas_amarillas", nullable = false, columnDefinition = "integer default 0")
    private Integer tarjetasAmarillas = 0;

    @PositiveOrZero(message = "Las tarjetas rojas deben ser cero o positivas")
    @Column(name = "tarjetas_rojas", nullable = false, columnDefinition = "integer default 0")
    private Integer tarjetasRojas = 0;

    public Estadisticas(Partido partido, Usuario jugador) {
        this.partido = partido;
        this.jugador = jugador;
        this.puntos = 0;
        this.faltas = 0;
        this.tarjetasAmarillas = 0;
        this.tarjetasRojas = 0;
    }
} 