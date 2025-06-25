package com.pds.sportsmanager.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "preferencias_notificacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreferenciaNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", unique = true)
    private Usuario usuario;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean email = true;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean push = false;
}