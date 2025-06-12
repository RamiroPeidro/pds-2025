package com.pds.sportsmanager.model.entity;

import com.pds.sportsmanager.model.enums.NivelDeJugador;
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

/**
 * Entidad JPA optimizada con Lombok
 * - @Getter/@Setter: eliminan boilerplate
 * - @EqualsAndHashCode
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include  
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Column(unique = true, nullable = false, length = 50)
    private String nombreUsuario;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String contrasenia;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_de_jugador")
    private NivelDeJugador nivelDeJugador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deporte_favorito_id")
    @ToString.Exclude  
    private Deporte deporteFavorito;

    @Embedded
    private Ubicacion ubicacion;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude  
    private List<Partido> partidosCreados = new ArrayList<>();

    @ManyToMany(mappedBy = "jugadores", fetch = FetchType.LAZY)
    @ToString.Exclude  
    private List<Partido> partidosParticipando = new ArrayList<>();

    @OneToMany(mappedBy = "jugador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude  
    private List<Estadisticas> estadisticas = new ArrayList<>();

    @OneToMany(mappedBy = "jugador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude  
    private List<Comentarios> comentarios = new ArrayList<>();

    public Usuario(String nombreUsuario, String email, String contrasenia) {
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.contrasenia = contrasenia;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

} 