-- Script de Optimización de Base de Datos - ORIOLA Indumentaria
-- Fecha: 14 de enero de 2025
-- Objetivo: Crear índices para mejorar el rendimiento de consultas

-- =====================================================
-- ÍNDICES PARA TABLA PRODUCT
-- =====================================================

-- Índice para búsquedas por nombre (más común)
CREATE INDEX idx_product_name ON product(name);

-- Índice para búsquedas por precio
CREATE INDEX idx_product_price ON product(price);

-- Índice para filtros de activo
CREATE INDEX idx_product_activo ON product(activo);

-- Índice para productos destacados
CREATE INDEX idx_product_destacado ON product(es_destacado, activo);

-- Índice para productos nuevos
CREATE INDEX idx_product_nuevo ON product(es_nuevo, activo);

-- Índice para búsquedas por color
CREATE INDEX idx_product_color ON product(color);

-- Índice para ordenamiento por fecha
CREATE INDEX idx_product_fecha_creacion ON product(fecha_creacion);

-- Índice compuesto para consultas frecuentes
CREATE INDEX idx_product_activo_precio ON product(activo, price);

-- =====================================================
-- ÍNDICES PARA TABLA PRODUCT_IMAGE
-- =====================================================

-- Índice para búsquedas por producto
CREATE INDEX idx_product_image_product_id ON product_image(product_id);

-- Índice para imagen principal
CREATE INDEX idx_product_image_primary ON product_image(product_id, is_primary);

-- Índice para orden de visualización
CREATE INDEX idx_product_image_display_order ON product_image(product_id, display_order);

-- =====================================================
-- ÍNDICES PARA TABLA PRODUCT_CATEGORIES
-- =====================================================

-- Índice compuesto para relación producto-categoría
CREATE INDEX idx_product_categories_product_category ON product_categories(product_id, category_id);

-- Índice para búsquedas por categoría
CREATE INDEX idx_product_categories_category ON product_categories(category_id);

-- =====================================================
-- ÍNDICES PARA TABLA PRODUCT_COLORS
-- =====================================================

-- Índice compuesto para relación producto-color
CREATE INDEX idx_product_colors_product_color ON product_colors(product_id, color_id);

-- Índice para búsquedas por color
CREATE INDEX idx_product_colors_color ON product_colors(color_id);

-- =====================================================
-- ÍNDICES PARA TABLA PRODUCT_TALLES
-- =====================================================

-- Índice compuesto para relación producto-talle
CREATE INDEX idx_product_talles_product_talle ON product_talles(product_id, talle);

-- Índice para búsquedas por talle
CREATE INDEX idx_product_talles_talle ON product_talles(talle);

-- =====================================================
-- ÍNDICES PARA TABLA CATEGORIES
-- =====================================================

-- Índice para búsquedas por nombre de categoría
CREATE INDEX idx_categories_name ON categories(name);

-- Índice para categorías activas
CREATE INDEX idx_categories_active ON categories(is_active);

-- Índice para orden de visualización
CREATE INDEX idx_categories_display_order ON categories(display_order);

-- =====================================================
-- ÍNDICES PARA TABLA COLORS
-- =====================================================

-- Índice para búsquedas por nombre de color
CREATE INDEX idx_colors_name ON colors(name);

-- Índice para colores activos
CREATE INDEX idx_colors_active ON colors(is_active);

-- Índice para orden de visualización
CREATE INDEX idx_colors_display_order ON colors(display_order);

-- =====================================================
-- ÍNDICES PARA TABLA USERS
-- =====================================================

-- Índice para búsquedas por username
CREATE INDEX idx_users_username ON users(username);

-- Índice para búsquedas por email
CREATE INDEX idx_users_email ON users(email);

-- Índice para usuarios activos
CREATE INDEX idx_users_active ON users(is_active);

-- =====================================================
-- ÍNDICES PARA TABLA CONTACT
-- =====================================================

-- Índice para búsquedas por email
CREATE INDEX idx_contact_email ON contact(email);

-- Índice para filtros por estado
CREATE INDEX idx_contact_estado ON contact(estado);

-- Índice para ordenamiento por fecha
CREATE INDEX idx_contact_fecha_creacion ON contact(fecha_creacion);

-- =====================================================
-- OPTIMIZACIONES ADICIONALES
-- =====================================================

-- Configurar charset y collation para mejor rendimiento
ALTER TABLE product CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE product_image CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE categories CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE colors CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE users CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE contact CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Optimizar tablas después de crear índices
OPTIMIZE TABLE product;
OPTIMIZE TABLE product_image;
OPTIMIZE TABLE product_categories;
OPTIMIZE TABLE product_colors;
OPTIMIZE TABLE product_talles;
OPTIMIZE TABLE categories;
OPTIMIZE TABLE colors;
OPTIMIZE TABLE users;
OPTIMIZE TABLE contact;

-- =====================================================
-- CONFIGURACIÓN DE MYSQL PARA RENDIMIENTO
-- =====================================================

-- Configuraciones recomendadas para my.cnf o my.ini
-- (Estas configuraciones deben aplicarse en el archivo de configuración de MySQL)

/*
[mysqld]
# Configuración de memoria
innodb_buffer_pool_size = 256M
innodb_log_file_size = 64M
innodb_log_buffer_size = 16M
innodb_flush_log_at_trx_commit = 2

# Configuración de consultas
query_cache_size = 32M
query_cache_type = 1
query_cache_limit = 2M

# Configuración de conexiones
max_connections = 100
max_connect_errors = 1000

# Configuración de tablas
table_open_cache = 2000
table_definition_cache = 1400

# Configuración de archivos temporales
tmp_table_size = 32M
max_heap_table_size = 32M

# Configuración de logging (desactivar para producción)
general_log = 0
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2
*/

-- =====================================================
-- VERIFICACIÓN DE ÍNDICES CREADOS
-- =====================================================

-- Verificar índices de la tabla product
SHOW INDEX FROM product;

-- Verificar índices de la tabla product_image
SHOW INDEX FROM product_image;

-- Verificar índices de la tabla product_categories
SHOW INDEX FROM product_categories;

-- Verificar índices de la tabla product_colors
SHOW INDEX FROM product_colors;

-- Verificar índices de la tabla product_talles
SHOW INDEX FROM product_talles;

-- Verificar índices de la tabla categories
SHOW INDEX FROM categories;

-- Verificar índices de la tabla colors
SHOW INDEX FROM colors;

-- Verificar índices de la tabla users
SHOW INDEX FROM users;

-- Verificar índices de la tabla contact
SHOW INDEX FROM contact;

-- =====================================================
-- ANÁLISIS DE RENDIMIENTO
-- =====================================================

-- Habilitar profiling para análisis de consultas
SET profiling = 1;

-- Ejecutar consultas de prueba
SELECT * FROM product WHERE activo = true LIMIT 10;
SELECT * FROM product p JOIN product_categories pc ON p.p_id = pc.product_id WHERE pc.category_id = 1;
SELECT * FROM product_image WHERE product_id = 1 ORDER BY display_order;

-- Ver perfil de consultas
SHOW PROFILES;

-- Ver detalles de una consulta específica
-- SHOW PROFILE FOR QUERY 1;

-- Deshabilitar profiling
SET profiling = 0;

