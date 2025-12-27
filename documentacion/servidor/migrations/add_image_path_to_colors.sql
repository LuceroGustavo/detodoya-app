-- Migración: Agregar campo image_path a la tabla colors
-- Fecha: Enero 2025
-- Descripción: Permite almacenar imágenes de patrones para colores (ej: Animal Print, Nevado, Estampado)

-- Agregar columna image_path a la tabla colors
ALTER TABLE colors 
ADD COLUMN image_path VARCHAR(500) NULL 
AFTER hex_code;

-- Comentario en la columna
ALTER TABLE colors 
MODIFY COLUMN image_path VARCHAR(500) NULL 
COMMENT 'Ruta de la imagen del patrón (ej: colors/animal_print.webp) - Opcional, alternativa a hexCode';

-- Verificar que la columna se agregó correctamente
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    CHARACTER_MAXIMUM_LENGTH, 
    IS_NULLABLE, 
    COLUMN_COMMENT
FROM 
    INFORMATION_SCHEMA.COLUMNS
WHERE 
    TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'colors'
    AND COLUMN_NAME = 'image_path';

