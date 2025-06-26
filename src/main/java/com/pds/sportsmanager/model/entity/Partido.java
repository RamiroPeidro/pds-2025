package com.pds.sportsmanager.model.entity;

import com.pds.sportsmanager.model.enums.NivelDeJugador;
import com.pds.sportsmanager.patterns.state.EstadoPartido;
import com.pds.sportsmanager.patterns.state.NecesitamosJugadores;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA optimizada con Lombok
 * Partido con State Pattern integrado
 */
@Entity
@Table(name = "partidos")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include  
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La fecha debe ser futura")
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Positive(message = "La duración debe ser positiva")
    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    @Positive(message = "La cantidad de jugadores debe ser positiva")
    @Column(name = "cantidad_jugadores_requeridos", nullable = false)
    private Integer cantidadJugadoresRequeridos;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ubicacion_id", nullable = false)
    @NotNull(message = "La ubicación es obligatoria")
    private Ubicacion ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_minimo")
    private NivelDeJugador nivelMinimo;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_maximo")
    private NivelDeJugador nivelMaximo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deporte_id", nullable = false)
    @ToString.Exclude  
    private Deporte deporte;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude  
    private Usuario owner;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "partido_jugadores",
        joinColumns = @JoinColumn(name = "partido_id"),
        inverseJoinColumns = @JoinColumn(name = "jugador_id")
    )
    @ToString.Exclude  
    private List<Usuario> jugadores = new ArrayList<>();

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude  
    private List<Estadisticas> estadisticas = new ArrayList<>();

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude  
    private List<Comentarios> comentarios = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient // No se mapea en ninguna columna
    private EstadoPartido estado;

    @Column(name = "estado_nombre")
    private String estadoNombre;

    public Partido(String titulo, LocalDateTime fechaHora, Integer duracionMinutos, 
                   Integer cantidadJugadoresRequeridos, Ubicacion ubicacion, 
                   Deporte deporte, Usuario owner) {
        this.estado = new NecesitamosJugadores();
        this.estadoNombre = this.estado.getNombre();
        this.titulo = titulo;
        this.fechaHora = fechaHora;
        this.duracionMinutos = duracionMinutos;
        this.cantidadJugadoresRequeridos = cantidadJugadoresRequeridos;
        this.ubicacion = ubicacion;
        this.deporte = deporte;
        this.owner = owner;
        this.jugadores.add(owner);
    }

    @PostLoad
    private void initializeEstado() {
        this.estado = EstadoPartido.fromNombre(this.estadoNombre);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = new NecesitamosJugadores();
        }
        this.estadoNombre = this.estado.getNombre();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.estado != null) {
            this.estadoNombre = this.estado.getNombre();
        }
    }

    public void cambiarEstado(EstadoPartido nuevoEstado) {
        this.estado = nuevoEstado;
        this.estadoNombre = nuevoEstado.getNombre();
    }

    public void agregarJugador(Usuario jugador) {
        this.estado.agregarJugador(this, jugador);
    }

    public void confirmarPartido() {
        this.estado.confirmarPartido(this);
    }
    public void enJuego() {
        this.estado.enJuego(this);
    }

    public void cancelarPartido() {
        this.estado.cancelarPartido(this);
    }

    public void finalizarPartido() {
        this.estado.finalizarPartido(this);
    }

    public void iniciarPartido() {
        this.estado.enJuego(this);
    }

    public boolean necesitaJugadores() {
        return jugadores.size() < cantidadJugadoresRequeridos;
    }

    public int getMaxJugadores() {
        return cantidadJugadoresRequeridos;
    }

    public int jugadoresFaltantes() {
        return Math.max(0, cantidadJugadoresRequeridos - jugadores.size());
    }

    public boolean esNivelCompatible(NivelDeJugador nivel) {
        if (nivelMinimo == null && nivelMaximo == null) {
            return true;
        }

        if (nivelMinimo != null && nivel.ordinal() < nivelMinimo.ordinal()) {
            return false;
        }

        return nivelMaximo == null || nivel.ordinal() <= nivelMaximo.ordinal();
    }

}