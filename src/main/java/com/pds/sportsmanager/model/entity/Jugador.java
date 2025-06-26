package com.pds.sportsmanager.model.entity;

import com.pds.sportsmanager.model.enums.NivelDeJuego;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Entidad JPA que representa un Jugador en el sistema.
 * Contiene información personal, preferencias y relaciones con partidos.
 */
@Entity
@Table(name = "jugadores") // Cambiado de "usuarios" a "jugadores"
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String contrasenia;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_de_juego")
    private NivelDeJuego nivelDeJuego; // Cambiado de nivelDeJugador

    @Column(name = "deporte_favorito")
    private String deporteFavorito; // Campo texto simple según DB
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "jugador_deporte",
            joinColumns = @JoinColumn(name = "jugador_id"),
            inverseJoinColumns = @JoinColumn(name = "deporte_id")
    )
    @ToString.Exclude
    private List<Deporte> deportesFavs = new ArrayList<>(); // Lista de deportes según diagrama

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id")
    private Ubicacion ubicacion;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Partido> partidosOrganizados = new ArrayList<>();

    @ManyToMany(mappedBy = "jugadores", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Partido> partidos = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "jugador", cascade = CascadeType.ALL, orphanRemoval = true)
    private PreferenciaNotificacion preferenciaNotificacion;

    // Agrega un nuevo partido organizado por este jugador
    public void crearPartido(Partido partido) {
        partidosOrganizados.add(partido);
        partido.setOwner(this);
    }

    // Busca partidos disponibles a los que no esté unido y que no haya organizado
    public List<Partido> buscarPartido(List<Partido> disponibles) {
        return disponibles.stream()
                .filter(p -> !partidos.contains(p) && !partidosOrganizados.contains(p))
                .toList();
    }

    // Acepta un partido y se añade a la lista de participantes
    public void aceptarPartido(Partido partido) {
        if (!partidos.contains(partido)) {
            partidos.add(partido);
            partido.getJugadores().add(this);
        }
    }

    // Cancela su participación en un partido si ya estaba anotado
    public void cancelarPartido(Partido partido) {
        if (partidos.contains(partido)) {
            partidos.remove(partido);
            partido.getJugadores().remove(this);
        }
    }

    // Establece el deporte favorito (solo uno como texto)
    public void establecerDeporteFavorito(String deporte) {
        this.deporteFavorito = deporte;
    }

    // Agrega un deporte favorito si no está en la lista (según diagrama UML)
    public void agregarDeporte(Deporte favorito) {
        if (!deportesFavs.contains(favorito)) {
            deportesFavs.add(favorito);
        }
    }

}