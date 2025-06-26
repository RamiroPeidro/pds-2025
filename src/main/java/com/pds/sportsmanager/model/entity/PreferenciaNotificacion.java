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
    @JoinColumn(name = "jugador_id", referencedColumnName = "id", unique = true)
    private Jugador jugador;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean email = true;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean push = false;


    public static PreferenciaNotificacion porDefecto() {
        return new PreferenciaNotificacion();
    }

    public boolean estaHabilitadaEmail() {
        return email;
    }
    public boolean estaHabilitadaPush() {
        return push;
    }

}