package com.pds.sportsmanager.dtos.auth;

import com.pds.sportsmanager.model.enums.NivelDeJugador;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarUsuarioDto {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String nombreUsuario;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasenia;

    @NotNull(message = "El nivel de jugador es obligatorio")
    private NivelDeJugador nivelDeJugador;

    @NotNull(message = "El ID de deporte favorito es obligatorio")
    private Long deporteFavoritoId;

    @NotNull(message = "El ID de ubicación es obligatorio")
    private Long ubicacionId;
}