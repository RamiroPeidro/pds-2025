-- Datos iniciales para el sistema de Sports Manager
-- Estructura actualizada para usar entidad Jugador

-- Insertar ubicaciones de ejemplo
INSERT INTO ubicaciones (direccion, latitud, longitud) VALUES
('Av. Corrientes 1234, CABA', -34.603722, -58.381592),
('Av. Santa Fe 2468, CABA', -34.593919, -58.383079),
('Av. Rivadavia 5000, CABA', -34.617778, -58.438611),
('Palermo, CABA', -34.588333, -58.420278),
('Belgrano, CABA', -34.563056, -58.457778);

-- Insertar deportes populares  
INSERT INTO deportes (nombre, descripcion, min_jugadores_por_equipo, max_jugadores_por_equipo, duracion_estandar_minutos) VALUES
('Fútbol', 'El deporte más popular del mundo', 8, 11, 90),
('Básquet', 'Baloncesto de 5 vs 5', 4, 5, 48),
('Vóley', 'Voleibol en cancha', 6, 6, 60),
('Tenis', 'Tenis individual o dobles', 1, 2, 120),
('Paddle', 'Paddle en parejas', 2, 2, 90);

-- Insertar jugadores de prueba
INSERT INTO jugadores (nombre, email, contrasenia, nivel_de_juego, deporte_favorito, ubicacion_id) VALUES
('Juan Pérez', 'juan@example.com', '$2a$10$dXJ3SW6G7P6iJR7HjrwkIe8M6mX6sXJb3tQ2xJRJQ8XJRJQXJRJQXq', 'INTERMEDIO', 'Fútbol', 1),
('María González', 'maria@example.com', '$2a$10$dXJ3SW6G7P6iJR7HjrwkIe8M6mX6sXJb3tQ2xJRJQ8XJRJQXJRJQXq', 'AVANZADO', 'Básquet', 2),
('Carlos López', 'carlos@example.com', '$2a$10$dXJ3SW6G7P6iJR7HjrwkIe8M6mX6sXJb3tQ2xJRJQ8XJRJQXJRJQXq', 'PRINCIPIANTE', 'Vóley', 3),
('Ana Martínez', 'ana@example.com', '$2a$10$dXJ3SW6G7P6iJR7HjrwkIe8M6mX6sXJb3tQ2xJRJQ8XJRJQXJRJQXq', 'INTERMEDIO', 'Paddle', 4),
('Luis García', 'luis@example.com', '$2a$10$dXJ3SW6G7P6iJR7HjrwkIe8M6mX6sXJb3tQ2xJRJQ8XJRJQXJRJQXq', 'AVANZADO', 'Tenis', 5);

-- Nota: Las contraseñas encriptadas corresponden a "123456"
-- En producción se deberían usar contraseñas más seguras
-- Los IDs se asignan automáticamente por la secuencia de la base de datos 