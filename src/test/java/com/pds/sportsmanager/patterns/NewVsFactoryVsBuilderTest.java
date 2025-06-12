package com.pds.sportsmanager.patterns;

import com.pds.sportsmanager.model.dto.PartidoCreacionDTO;
import com.pds.sportsmanager.model.dto.UsuarioDTO;
import com.pds.sportsmanager.model.entity.Ubicacion;
import com.pds.sportsmanager.model.enums.NivelDeJugador;
import com.pds.sportsmanager.patterns.state.EstadoPartido;
import com.pds.sportsmanager.patterns.state.EstadoPartidoFactory;
import com.pds.sportsmanager.patterns.strategy.EmparejamientoPorNivel;
import com.pds.sportsmanager.patterns.strategy.EstrategiaEmparejamiento;
import com.pds.sportsmanager.service.EstrategiaEmparejamientoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test que demuestra cuándo usar new, Factory Pattern o Builder Pattern
 */
@SpringBootTest
class NewVsFactoryVsBuilderTest {

    @Autowired
    private EstrategiaEmparejamientoFactory estrategiaFactory;

    @Test
    @DisplayName("❌ Problemático: usar 'new' para objetos complejos")
    void problematicoUsarNewParaObjetosComplejos() {
        // ❌ MAL: Constructor con muchos parámetros, confuso
        // No podemos hacer esto porque no tenemos constructor de 10+ parámetros,
        // pero imagina si tuviéramos:
        // new Partido("título", "desc", ubicacion, fecha, deporte, nivel1, nivel2, 
        //            maxJugadores, esPrivado, requiereConfirmacion, costo, notas)
        
        // En su lugar, usar Builder es mucho mejor
        assertNotNull("Builder es la mejor opción para objetos complejos");
    }

    @Test
    @DisplayName("✅ CORRECTO: Builder Pattern para objetos complejos con muchos campos")
    void correctoBuilderParaObjetosComplejos() {
        // ✅ BUILDER: Para objetos complejos con muchos campos opcionales
        PartidoCreacionDTO partido = PartidoCreacionDTO.builder()
            .titulo("Partido de Fútbol")                          // Legible
            .descripcion("Buscamos jugadores")                     // Auto-documentado
            .ubicacion(new Ubicacion(-34.6118, -58.3960))        // Orden flexible
            .fechaHora(LocalDateTime.now().plusDays(1))
            .deporteId(1L)
            .nivelMinimo(NivelDeJugador.PRINCIPIANTE)
            .nivelMaximo(NivelDeJugador.INTERMEDIO)
            .maxJugadores(10)
            .esPrivado(false)                                      // Valores opcionales
            .costo(500.0)
            .equipamientoRequerido("Botines")
            .build();

        assertEquals("Partido de Fútbol", partido.getTitulo());
        assertEquals(10, partido.getMaxJugadores());
        assertFalse(partido.getEsPrivado());
        
        // ✅ Builder con Records también funciona
        UsuarioDTO usuario = UsuarioDTO.builder()
            .nombreUsuario("testUser")
            .email("test@example.com")
            .nivelDeJugador(NivelDeJugador.INTERMEDIO)
            .build();
            
        assertEquals("testUser", usuario.nombreUsuario());
    }

    @Test
    @DisplayName("✅ CORRECTO: Factory Pattern para objetos stateless/reutilizables")
    void correctoFactoryParaObjetosStateless() {
        // ✅ FACTORY: Para estados (stateless, reutilizables)
        EstadoPartido estado1 = EstadoPartidoFactory.necesitamosJugadores();
        EstadoPartido estado2 = EstadoPartidoFactory.necesitamosJugadores();
        
        // Son la misma instancia (singleton)
        assertSame(estado1, estado2, "Factory devuelve misma instancia para estados");
        assertEquals("NECESITAMOS_JUGADORES", estado1.getNombre());
        
        // ✅ SPRING DI FACTORY: Para strategies (también stateless)
        EstrategiaEmparejamiento estrategia1 = estrategiaFactory.porNivel();
        EstrategiaEmparejamiento estrategia2 = estrategiaFactory.porNivel();
        
        // Son la misma instancia (Spring singleton)
        assertSame(estrategia1, estrategia2, "Spring devuelve misma instancia");
        assertEquals("POR_NIVEL", estrategia1.getNombre());
    }

    @Test
    @DisplayName("✅ OCASIONALMENTE OK: 'new' para objetos simples sin estado")
    void ocasionalmenteOkNewParaObjetosSimples() {
        // ✅ OK: Para Value Objects simples
        Ubicacion ubicacion = new Ubicacion(-34.6118, -58.3960);
        assertNotNull(ubicacion);
        assertEquals(-34.6118, ubicacion.latitud());
        
        // ✅ OK: Para objetos muy simples sin configuración
        LocalDateTime fecha = LocalDateTime.now();
        assertNotNull(fecha);
        
        // ❌ EVITAR: Para strategies/states que se reutilizan
        // EmparejamientoPorNivel estrategia = new EmparejamientoPorNivel(); // MAL
        // PartidoCancelado estado = new PartidoCancelado(); // MAL
    }

    @Test
    @DisplayName("📋 RESUMEN: Cuándo usar cada patrón")
    void resumenCuandoUsarCadaPatron() {
        // 🏗️ BUILDER PATTERN - Usar cuando:
        // ✅ Objetos complejos con muchos campos
        // ✅ Muchos parámetros opcionales
        // ✅ Necesitas inmutabilidad
        // ✅ DTOs, Configuration objects, Test data
        
        PartidoCreacionDTO ejemploBuilder = PartidoCreacionDTO.builder()
            .titulo("Ejemplo Builder")
            .ubicacion(new Ubicacion(-34.6118, -58.3960))
            .fechaHora(LocalDateTime.now().plusHours(2))
            .deporteId(1L)
            .nivelMinimo(NivelDeJugador.PRINCIPIANTE)
            .nivelMaximo(NivelDeJugador.AVANZADO)
            .maxJugadores(6)
            .build();
        
        // 🏭 FACTORY PATTERN - Usar cuando:
        // ✅ Objetos stateless que se reutilizan
        // ✅ States, Strategies, Commands
        // ✅ Evitar crear múltiples instancias idénticas
        // ✅ Control centralizado de creación
        
        EstadoPartido ejemploFactory = EstadoPartidoFactory.partidoConfirmado();
        
        // 🆕 NEW OPERATOR - Usar cuando:
        // ✅ Value Objects simples
        // ✅ Objetos con pocos parámetros
        // ✅ No hay configuración compleja
        // ❌ EVITAR para objetos complejos o reutilizables
        
        Ubicacion ejemploNew = new Ubicacion(-34.6118, -58.3960);
        
        // Verificaciones
        assertNotNull(ejemploBuilder);
        assertNotNull(ejemploFactory);
        assertNotNull(ejemploNew);
        
        assertTrue(ejemploBuilder.getTitulo().equals("Ejemplo Builder"));
        assertTrue(ejemploFactory.getNombre().equals("PARTIDO_CONFIRMADO"));
        assertTrue(ejemploNew.latitud() == -34.6118);
    }
} 