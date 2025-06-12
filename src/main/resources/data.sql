-- Insertar deportes populares
INSERT INTO deportes (nombre, descripcion, jugadores_por_equipo, duracion_estandar_minutos) VALUES
('Fútbol', 'El deporte más popular del mundo', 11, 90),
('Básquet', 'Baloncesto de 5 vs 5', 5, 48),
('Vóley', 'Voleibol en cancha', 6, 60),
('Tenis', 'Tenis individual', 1, 120),
('Paddle', 'Paddle en parejas', 2, 90),
('Fútbol 5', 'Fútbol reducido', 5, 50),
('Hockey', 'Hockey sobre césped', 11, 70);

-- Insertar usuarios de prueba
INSERT INTO usuarios (nombre_usuario, email, contrasenia, nivel_de_jugador, deporte_favorito_id, created_at, updated_at, direccion, latitud, longitud) VALUES
('juan_futbolero', 'juan@example.com', '$2a$10$dXJ3SW6G7P6iJR7HjrwkIe8M6mX6sXJb3tQ2xJRJQ8XJRJQXJRJQXq', 'INTERMEDIO', 1, NOW(), NOW(), 'Av. Corrientes 1234, CABA', -34.603722, -58.381592),
('maria_basquet', 'maria@example.com', '$2a$10$dXJ3SW6G7P6iJR7HjrwkIe8M6mX6sXJb3tQ2xJRJQ8XJRJQXJRJQXq', 'AVANZADO', 2, NOW(), NOW(), 'Av. Santa Fe 2468, CABA', -34.593919, -58.383079),
('carlos_voley', 'carlos@example.com', '$2a$10$dXJ3SW6G7P6iJR7HjrwkIe8M6mX6sXJb3tQ2xJRJQ8XJRJQXJRJQXq', 'PRINCIPIANTE', 3, NOW(), NOW(), 'Av. Rivadavia 5000, CABA', -34.617778, -58.438611),
('ana_paddle', 'ana@example.com', '$2a$10$dXJ3SW6G7P6iJR7HjrwkIe8M6mX6sXJb3tQ2xJRJQ8XJRJQXJRJQXq', 'INTERMEDIO', 5, NOW(), NOW(), 'Palermo, CABA', -34.588333, -58.420278),
('luis_tennis', 'luis@example.com', '$2a$10$dXJ3SW6G7P6iJR7HjrwkIe8M6mX6sXJb3tQ2xJRJQ8XJRJQXJRJQXq', 'AVANZADO', 4, NOW(), NOW(), 'Belgrano, CABA', -34.563056, -58.457778);

-- Nota: Las contraseñas encriptadas corresponden a "123456"
-- En producción se deberían usar contraseñas más seguras 