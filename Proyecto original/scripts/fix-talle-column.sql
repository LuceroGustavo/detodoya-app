-- Script para arreglar la columna talle en la tabla product_talles
-- Ejecutar este script en MySQL para permitir valores más largos en la columna talle
-- El problema: La columna talle fue creada con VARCHAR(1) o VARCHAR(2) pero ahora
-- se necesitan valores más largos como "T32", "T34", "XXXL", etc.

USE oriola_indumentaria;

-- Modificar la columna talle para permitir valores más largos (VARCHAR(10) es suficiente)
-- Esto permitirá almacenar nombres de enum como "T32", "T34", "XXXL", etc.
ALTER TABLE product_talles MODIFY COLUMN talle VARCHAR(10) NOT NULL;

-- Verificar que el cambio se aplicó correctamente
DESCRIBE product_talles;

-- Verificar los valores actuales en la tabla (opcional, para debugging)
-- SELECT DISTINCT talle FROM product_talles ORDER BY talle;

