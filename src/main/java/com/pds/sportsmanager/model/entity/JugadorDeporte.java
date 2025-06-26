package com.pds.sportsmanager.model.entity;

import com.pds.sportsmanager.model.enums.NivelDeJuego;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la relación entre Jugador y Deporte.
 * Contiene información adicional como el nivel del jugador en ese deporte
 * y si es uno de sus deportes favoritos.
 */
@Entity
@Table(name = "jugador_deporte")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class JugadorDeporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deporte_id", nullable = false)
    private Deporte deporte;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel")
    private NivelDeJuego nivel; // Nivel del jugador en este deporte específico

    @Column(name = "es_favorito")
    private Boolean esFavorito = false; // Si este deporte es favorito para el jugador

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 