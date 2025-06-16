package com.pds.sportsmanager.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA optimizada con Lombok
 * Representa un deporte con sus características básicas
 */
@Entity
@Table(name = "deportes")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Deporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "El nombre del deporte es obligatorio")
    @Column(unique = true, nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Positive(message = "La cantidad mínima de jugadores debe ser positiva")
    @Column(name = "min_jugadores_por_equipo", nullable = false)
    private Integer minJugadoresPorEquipo;

    @Positive(message = "La cantidad máxima de jugadores debe ser positiva")
    @Column(name = "max_jugadores_por_equipo", nullable = false)
    private Integer maxJugadoresPorEquipo;

    @Column(name = "duracion_estandar_minutos")
    private Integer duracionEstandarMinutos;

    @OneToMany(mappedBy = "deporte", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Partido> partidos = new ArrayList<>();

    public Deporte(String nombre, Integer minJugadoresPorEquipo, Integer maxJugadoresPorEquipo) {
        this.nombre = nombre;
        this.minJugadoresPorEquipo = minJugadoresPorEquipo;
        this.maxJugadoresPorEquipo = maxJugadoresPorEquipo;
    }

    public Deporte(String nombre, String descripcion, Integer minJugadoresPorEquipo, Integer maxJugadoresPorEquipo, Integer duracionEstandarMinutos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.minJugadoresPorEquipo = minJugadoresPorEquipo;
        this.maxJugadoresPorEquipo = maxJugadoresPorEquipo;
        this.duracionEstandarMinutos = duracionEstandarMinutos;
    }
} 