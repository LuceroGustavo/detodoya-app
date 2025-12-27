-- Script para arreglar la columna qty en la tabla product
-- Ejecutar este script en MySQL para permitir valores NULL en la columna qty

USE oriola_indumentaria;

-- Modificar la columna qty para permitir valores NULL
ALTER TABLE product MODIFY COLUMN qty INT NULL;

-- Verificar que el cambio se aplicó correctamente
DESCRIBE product;

