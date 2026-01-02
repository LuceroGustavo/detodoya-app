-- ===========================================
-- Script para crear la base de datos Detodoya
-- Ejecutar en MySQL Workbench o desde línea de comandos
-- ===========================================

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS detodoya 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Verificar que se creó correctamente
SHOW DATABASES LIKE 'detodoya';

-- Mensaje de confirmación
SELECT 'Base de datos detodoya creada exitosamente' AS Mensaje;




