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
    private Jugador jugador;

    @PositiveOrZero(message = "Los puntos deben ser cero o positivos")
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer puntos = 0;

    @PositiveOrZero(message = "Las faltas deben ser cero o positivas")
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer faltas = 0;

    @PositiveOrZero(message = "Las tarjetas deben ser cero o positivas")
    @Column(name = "tarjetas_amarillas", nullable = false, columnDefinition = "integer default 0")
    private Integer tarjetas = 0;

    @Column(name = "comentarios", columnDefinition = "TEXT")
    private String comentarios;


    public Estadisticas(Partido partido, Jugador jugador) {
        this.partido = partido;
        this.jugador = jugador;
    }
} 