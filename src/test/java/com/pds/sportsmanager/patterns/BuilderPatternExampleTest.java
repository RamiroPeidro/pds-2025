package com.pds.sportsmanager.patterns;

import com.pds.sportsmanager.model.dto.PartidoCreacionDTO;
import com.pds.sportsmanager.model.dto.UsuarioDTO;
import com.pds.sportsmanager.model.entity.Ubicacion;
import com.pds.sportsmanager.model.enums.NivelDeJugador;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de ejemplo que demuestra las ventajas del patrón Builder con Lombok
 */
class BuilderPatternExampleTest {

    @Test
    @DisplayName("Builder Pattern - Creación legible y flexible")
    void testBuilderPattern_Legibilidad() {
        // ✅ Builder Pattern: Legible, orden flexible, auto-documentado
        PartidoCreacionDTO partido = PartidoCreacionDTO.builder()
            .titulo("Partido de Fútbol en Palermo")
            .descripcion("Buscamos jugadores para completar equipos")
            .ubicacion(new Ubicacion(-34.5755, -58.4007)) // Palermo, CABA
            .fechaHora(LocalDateTime.now().plusDays(2))
            .deporteId(1L)
            .nivelMinimo(NivelDeJugador.PRINCIPIANTE)
            .nivelMaximo(NivelDeJugador.INTERMEDIO)
            .maxJugadores(10)
            .esPrivado(false)
            .costo(500.0)
            .equipamientoRequerido("Botines y canilleras")
            .build();

        assertNotNull(partido);
        assertEquals("Partido de Fútbol en Palermo", partido.getTitulo());
        assertEquals(10, partido.getMaxJugadores());
        assertTrue(partido.getRequiereConfirmacion()); // valor por defecto
        assertFalse(partido.getEsPrivado());
    }

    @Test
    @DisplayName("Builder Pattern - Parámetros opcionales")
    void testBuilderPattern_ParametrosOpcionales() {
        // ✅ Solo campos obligatorios - otros usan valores por defecto
        PartidoCreacionDTO partidoMinimo = PartidoCreacionDTO.builder()
            .titulo("Partido Rápido")
            .ubicacion(new Ubicacion(-34.6118, -58.3960))
            .fechaHora(LocalDateTime.now().plusHours(2))
            .deporteId(1L)
            .nivelMinimo(NivelDeJugador.PRINCIPIANTE)
            .nivelMaximo(NivelDeJugador.AVANZADO)
            .maxJugadores(6)
            .build();

        // Verificamos valores por defecto
        assertFalse(partidoMinimo.getEsPrivado()); // @Builder.Default = false
        assertTrue(partidoMinimo.getRequiereConfirmacion()); // @Builder.Default = true
        assertNull(partidoMinimo.getDescripcion()); // opcional, no seteado
    }

    @Test
    @DisplayName("Builder Pattern - Builders especializados")
    void testBuilderPattern_BuildersEspecializados() {
        // ✅ Builders pre-configurados para casos comunes
        PartidoCreacionDTO partidoFutbol = PartidoCreacionDTO.futbolBuilder()
            .titulo("Fútbol 5 en Núñez")
            .ubicacion(new Ubicacion(-34.5450, -58.4634))
            .fechaHora(LocalDateTime.now().plusDays(1))
            .deporteId(1L)
            .build();

        assertEquals(10, partidoFutbol.getMaxJugadores()); // Pre-configurado para fútbol
        assertFalse(partidoFutbol.getEsPrivado());
        assertTrue(partidoFutbol.getRequiereConfirmacion());

        PartidoCreacionDTO partidoTenis = PartidoCreacionDTO.tenisBuilder()
            .titulo("Tenis Singles")
            .ubicacion(new Ubicacion(-34.5955, -58.4066))
            .fechaHora(LocalDateTime.now().plusHours(3))
            .deporteId(2L)
            .build();

        assertEquals(2, partidoTenis.getMaxJugadores()); // Pre-configurado para tenis
        assertTrue(partidoTenis.getEsPrivado());
        assertFalse(partidoTenis.getRequiereConfirmacion());
    }

    @Test
    @DisplayName("Builder Pattern - Inmutabilidad con @Value")
    void testBuilderPattern_Inmutabilidad() {
        UsuarioDTO usuario = UsuarioDTO.builder()
            .id(1L)
            .nombreUsuario("testUser")
            .email("test@example.com")
            .nivelDeJugador(NivelDeJugador.INTERMEDIO)
            .deporteFavorito("Fútbol")
            .ubicacion(new Ubicacion(-34.6118, -58.3960))
            .createdAt(LocalDateTime.now())
            .build();

        // ✅ @Value hace el objeto completamente inmutable
        assertNotNull(usuario);
        assertEquals("testUser", usuario.nombreUsuario());
        assertEquals("test@example.com", usuario.email());
        
        // No hay setters - objeto inmutable
        // usuario.setEmail("nuevo@email.com"); // ❌ No existe este método
    }

    @Test
    @DisplayName("Builder Pattern vs Constructor telescópico")
    void testBuilderPattern_VsConstructorTelescópico() {
        // ❌ Problema sin Builder: Constructor telescópico confuso
        // Imagina tener que recordar el orden de 10+ parámetros:
        // new PartidoCreacionDTO("titulo", "desc", ubicacion, fecha, 1L, 
        //                       PRINCIPIANTE, AVANZADO, 10, false, true, 
        //                       "notas", 500.0, "equipo")
        
        // ✅ Con Builder: claro, legible, menos propenso a errores
        PartidoCreacionDTO partidoClaro = PartidoCreacionDTO.builder()
            .titulo("Partido Claro")                          // Qué es cada campo
            .maxJugadores(8)                                  // Orden flexible
            .nivelMinimo(NivelDeJugador.INTERMEDIO)          // Tipo-safe
            .nivelMaximo(NivelDeJugador.AVANZADO)            // Auto-completado IDE
            .ubicacion(new Ubicacion(-34.6118, -58.3960))   // Sin confusión
            .fechaHora(LocalDateTime.now().plusDays(1))      // Parámetros con nombre
            .deporteId(1L)
            .costo(750.0)
            .build();

        assertEquals("Partido Claro", partidoClaro.getTitulo());
        assertEquals(8, partidoClaro.getMaxJugadores());
    }

    @Test
    @DisplayName("Builder Pattern - Validación en build()")
    void testBuilderPattern_ValidacionEnBuild() {
        // ✅ Builder permite validación compleja en el momento del build()
        // Aunque en este ejemplo usamos Bean Validation, se podría hacer validación custom
        
        assertDoesNotThrow(() -> {
            PartidoCreacionDTO partidoValido = PartidoCreacionDTO.builder()
                .titulo("Partido Válido")
                .ubicacion(new Ubicacion(-34.6118, -58.3960))
                .fechaHora(LocalDateTime.now().plusDays(1))
                .deporteId(1L)
                .nivelMinimo(NivelDeJugador.PRINCIPIANTE)
                .nivelMaximo(NivelDeJugador.AVANZADO)
                .maxJugadores(6)
                .build();
            
            assertNotNull(partidoValido);
        });
    }
} 