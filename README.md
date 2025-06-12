# 🏆 Sports Manager - Sistema de Gestión de Encuentros Deportivos

Un sistema completo para la gestión de encuentros deportivos donde los usuarios pueden encontrar jugadores para completar equipos de diferentes deportes.

## 🎯 Características Principales

- **Gestión de Usuarios**: Registro, autenticación y perfiles de jugadores
- **Creación de Partidos**: Los usuarios pueden crear partidos especificando deporte, ubicación, nivel requerido, etc.
- **Sistema de Estados**: Los partidos transicionan automáticamente entre estados usando el patrón State
- **Búsqueda Inteligente**: Diferentes estrategias de emparejamiento (por nivel, cercanía, historial)
- **Notificaciones**: Sistema de notificaciones push y email usando el patrón Observer
- **Geolocalización**: Búsqueda de partidos cercanos usando cálculos de distancia

## 🏗️ Arquitectura y Patrones de Diseño

### Patrones Implementados

#### 1. **Patrón State** 🔄
**Ubicación**: `src/main/java/com/pds/sportsmanager/patterns/state/`

Los partidos transicionan automáticamente entre estados:
```
NecesitamosJugadores → PartidoArmado → PartidoConfirmado → EnJuego → PartidoFinalizado
                                  ↘
                                   PartidoCancelado
```

#### 2. **Patrón Strategy** 🎯
**Ubicación**: `src/main/java/com/pds/sportsmanager/patterns/strategy/`

Diferentes algoritmos de emparejamiento:
- `EmparejamientoPorNivel`: Prioriza compatibilidad de habilidades
- `EmparejamientoPorCercania`: Prioriza proximidad geográfica
- `EmparejamientoPorHistorial`: Basado en partidos anteriores (futuro)

#### 3. **Patrón Observer** 📢
**Ubicación**: `src/main/java/com/pds/sportsmanager/patterns/observer/`

Sistema de notificaciones con múltiples canales:
- `NotificadorEmail`: Notificaciones por correo
- `NotificadorFirebase`: Notificaciones push
- Extensible para otros canales (SMS, WhatsApp, etc.)

#### 4. **Patrón Adapter** 🔌
**Ubicación**: `src/main/java/com/pds/sportsmanager/patterns/adapter/`

Abstrae diferentes librerías de email:
- Actualmente: JavaMail
- Preparado para: SendGrid, AWS SES, etc.

## 🏛️ Arquitectura MVC

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controllers   │───▶│    Services     │───▶│  Repositories   │
│   (REST APIs)   │    │ (Lógica Negocio)│    │ (Acceso Datos)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│      DTOs       │    │    Patterns     │    │   Entities      │
│   (Records)     │    │   (Strategy,    │    │     (JPA)       │
│                 │    │ State, Observer)│    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 📁 Estructura del Proyecto

```
src/main/java/com/pds/sportsmanager/
├── 📱 SportsManagerApplication.java
├── 🎮 controller/
│   ├── PartidoController.java      # REST APIs para partidos
│   └── UsuarioController.java      # REST APIs para usuarios
├── 🧠 service/
│   ├── PartidoService.java         # Lógica de negocio de partidos
│   ├── UsuarioService.java         # Lógica de negocio de usuarios
│   └── NotificacionService.java    # Patrón Observer
├── 🗄️ repository/
│   ├── PartidoRepository.java      # Queries complejas de partidos
│   ├── UsuarioRepository.java      # Queries de usuarios
│   └── DeporteRepository.java      # Catálogo de deportes
├── 🏗️ patterns/
│   ├── state/                      # Patrón State
│   │   ├── EstadoPartido.java
│   │   ├── NecesitamosJugadores.java
│   │   ├── PartidoArmado.java
│   │   ├── PartidoConfirmado.java
│   │   ├── EnJuego.java
│   │   ├── PartidoFinalizado.java
│   │   └── PartidoCancelado.java
│   ├── strategy/                   # Patrón Strategy
│   │   ├── EstrategiaEmparejamiento.java
│   │   ├── EmparejamientoPorNivel.java
│   │   └── EmparejamientoPorCercania.java
│   ├── observer/                   # Patrón Observer
│   │   ├── Notificador.java
│   │   ├── NotificadorEmail.java
│   │   ├── NotificadorFirebase.java
│   │   └── NotificacionEvent.java
│   └── adapter/                    # Patrón Adapter
│       ├── EmailAdapter.java
│       └── EmailService.java
├── 📊 model/
│   ├── entity/                     # Entidades JPA
│   │   ├── Usuario.java
│   │   ├── Partido.java
│   │   ├── Deporte.java
│   │   ├── Ubicacion.java         # Record embebido
│   │   ├── Estadisticas.java
│   │   └── Comentarios.java
│   ├── dto/                       # Records para APIs
│   │   ├── UsuarioDTO.java
│   │   ├── UsuarioRequestDTO.java
│   │   └── PartidoBusquedaResult.java
│   └── enums/
│       ├── NivelDeJugador.java
│       └── TipoNotificacion.java
└── ⚙️ config/
    ├── SecurityConfig.java         # Configuración de seguridad
    ├── FirebaseConfig.java         # Configuración Firebase
    └── EmailConfig.java            # Configuración email
```

## 🚀 Tecnologías Utilizadas

- **Java 21** con Records y Pattern Matching
- **Spring Boot 3.5.0** (Framework principal)
- **Spring Data JPA** (Persistencia)
- **Spring Security** (Autenticación y autorización)
- **H2 Database** (Desarrollo) / **MySQL** (Producción)
- **Maven** (Gestión de dependencias)
- **OpenAPI/Swagger** (Documentación API)
- **MapStruct** (Mapeo entre objetos)
- **Firebase Admin SDK** (Notificaciones push)
- **JavaMail** (Envío de emails)

## 🏃‍♂️ Cómo Ejecutar

### Prerrequisitos
- Java 21+
- Maven 3.6+

### Pasos

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd sports-manager
```

2. **Ejecutar la aplicación**
```bash
./mvnw spring-boot:run
```

3. **Acceder a la aplicación**
- API REST: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:sportsmanager`
  - Usuario: `sa`
  - Contraseña: (vacía)

## 📡 Ejemplos de Uso de la API

### Crear un partido
```bash
POST /api/partidos
Content-Type: application/json

{
    "titulo": "Partido de fútbol 5",
    "descripcion": "Buscamos jugadores para completar el equipo",
    "fechaHora": "2024-02-15T19:00:00",
    "duracionMinutos": 90,
    "cantidadJugadoresRequeridos": 10,
    "ubicacion": {
        "direccion": "Cancha Centenario, Palermo",
        "latitud": -34.588333,
        "longitud": -58.420278
    },
    "nivelMinimo": "PRINCIPIANTE",
    "nivelMaximo": "INTERMEDIO",
    "deporte": { "id": 1 },
    "owner": { "id": 1 }
}
```

### Buscar partidos con estrategia
```bash
GET /api/partidos/buscar?usuarioId=1&estrategia=nivel
GET /api/partidos/buscar?usuarioId=1&estrategia=cercania
```

### Unirse a un partido
```bash
POST /api/partidos/1/jugadores/2
```

## 🧪 Datos de Prueba

La aplicación incluye datos iniciales:

**Deportes disponibles:**
- Fútbol (11 jugadores)
- Básquet (5 jugadores)
- Vóley (6 jugadores)
- Tenis (1 jugador)
- Paddle (2 jugadores)

**Usuarios de prueba:**
- `juan_futbolero` / `maria_basquet` / `carlos_voley` / etc.
- Contraseña: `123456`

## 🔐 Seguridad

- Autenticación HTTP Basic (desarrollo)
- Contraseñas encriptadas con BCrypt
- Endpoints protegidos por Spring Security
- Validación de datos con Bean Validation

## 📈 Características Avanzadas

### Geolocalización
- Cálculo de distancias usando fórmula de Haversine
- Búsqueda de partidos en radio específico
- Integración con mapas (preparado)

### Sistema de Estados Inteligente
- Transiciones automáticas basadas en eventos
- Validaciones de estado específicas
- Persistencia de estado en base de datos

### Notificaciones Múltiples
- Email automático para cambios de estado
- Push notifications vía Firebase
- Sistema extensible para nuevos canales

## 🛠️ Desarrollo

### Ejecutar tests
```bash
./mvnw test
```

### Generar documentación
```bash
./mvnw spring-boot:run
# Visitar http://localhost:8080/swagger-ui.html
```

### Perfiles de ambiente
- `dev`: Desarrollo (H2, logs detallados)
- `prod`: Producción (MySQL, logs optimizados)

## 🤝 Contribución

1. Fork el proyecto
2. Crear rama de feature: `git checkout -b feature/nueva-funcionalidad`
3. Commit cambios: `git commit -m 'Agregar nueva funcionalidad'`
4. Push a la rama: `git push origin feature/nueva-funcionalidad`
5. Abrir Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver archivo `LICENSE` para más detalles.

---

**Desarrollado con ❤️ usando Spring Boot y patrones de diseño** 