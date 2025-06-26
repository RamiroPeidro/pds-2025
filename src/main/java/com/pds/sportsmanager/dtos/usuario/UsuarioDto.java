package com.pds.sportsmanager.dtos.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto { // Dto para devolver una respuesta http
    private Long id;
    private String nombreUsuario;
    private String email;
}
