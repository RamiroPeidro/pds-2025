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

    @Column(name = "juego_preferido")
    private String DeporteFavorito; // Nuevo campo

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "jugador_deporte_favorito",
            joinColumns = @JoinColumn(name = "jugador_id"),
            inverseJoinColumns = @JoinColumn(name = "deporte_id")
    )
    @ToString.Exclude
    private List<Deporte> deportesFavs = new ArrayList<>(); // Cambiado de deporteFavorito a lista

    @Embedded
    private Ubicacion ubicacion;

    @OneToMany(mappedBy = "organizador", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Partido> partidosOrganizados = new ArrayList<>();

    @ManyToMany(mappedBy = "participantes", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Partido> partidos = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Agrega un nuevo partido organizado por este jugador
    public void crearPartido(Partido partido) {
        partidosOrganizados.add(partido);
        partido.setOrganizador(this);
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
            partido.getParticipantes().add(this);
        }
    }

    // Cancela su participación en un partido si ya estaba anotado
    public void cancelarPartido(Partido partido) {
        if (partidos.contains(partido)) {
            partidos.remove(partido);
            partido.getParticipantes().remove(this);
        }
    }

    // Agrega un deporte favorito si no está en la lista
    public void agregarDeporte(Deporte deporte) {
        if (!deportesFavs.contains(deporte)) {
            deportesFavs.add(deporte);
        }
    }

}