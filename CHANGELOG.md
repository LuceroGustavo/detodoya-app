# Changelog

Todos los cambios notables de este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed
- **Migración del proyecto de ORIOLA Indumentaria a Detodoya.com**
  - Renombrado de paquetes: `com.orioladenim` → `com.detodoya`
  - Nueva clase principal: `DetodoyaApplication`
  - Actualización de `pom.xml` con nuevo groupId y artifactId
  - Actualización de nombres de base de datos: `oriola_indumentaria` → `detodoya`
  - Actualización de usuarios de base de datos: `oriola_user` → `detodoya_user`
  - Reorganización completa de documentación

### Added
- Sistema de gestión de categorías dinámicas
- Sistema de gestión de colores flexibles
- Usuario desarrollador con privilegios SUPER_ADMIN
- Funcionalidad de cambio de contraseña para administradores
- Reset de contraseñas para SUPER_ADMIN
- Templates de formularios para categorías y colores
- Mejoras en el catálogo de productos con diseño moderno
- **Menú desplegable de categorías inspirado en Lovely Denim**
- **Navegación por hover en el menú de categorías**
- **Diseño de 4 columnas para organización de categorías**
- **Configuración .gitignore para archivos específicos de entorno**
- **Interfaz mejorada de cambio de contraseña** - Navbar y sidebar consistentes con el resto del panel de admin
- **Validación avanzada de contraseñas** - Validación en tiempo real en cliente y servidor con indicadores de fortaleza
- **Funcionalidad de recuperación de contraseña del admin** - El desarrollador puede restablecer la contraseña del admin si se olvidó

### Changed
- Refactorización del sistema de usuarios
- Mejora en la estructura de la base de datos
- Actualización del formulario de productos con ejemplos de colores flexibles
- **Rediseño completo del navbar con menú desplegable integrado**
- **Aplicación del diseño Lovely Denim a todos los HTML públicos**
- **Configuración de sincronización Git para entornos múltiples**

### Fixed
- Errores de sintaxis en templates Thymeleaf
- Problemas de compilación en controladores
- Errores de TemplateInputException en páginas de administración
- **Problemas de centrado del menú desplegable**
- **Activación del menú por click vs hover**
- **Bug de bloqueo de login** - Corregido problema donde `mustChangePassword = true` impedía el login después de cambiar contraseña del admin por desarrollador
- **Error "Data truncated for column 'talle'"** - Corregido problema donde la columna `talle` en la tabla `product_talles` era demasiado pequeña para almacenar nombres de enum como "T32", "T34", "XXXL". Solución: Script SQL `fix-talle-column.sql` para modificar la columna a VARCHAR(10)

## [2.0.0] - 2025-01-XX (En desarrollo)

### Changed
- **Migración completa de ORIOLA Indumentaria a Detodoya.com**
  - Cambio de enfoque: de catálogo de indumentaria a catálogo genérico de productos
  - Renombrado completo del proyecto
  - Actualización de todas las configuraciones
  - **Actualización de Java 17 a Java 21** - Compatibilidad con servidor Ubuntu
  - **Limpieza de código legacy** - Eliminación completa de carpeta `com.orioladenim` (62 archivos legacy)

### Added
- Nueva estructura de documentación organizada
- Documentación de estado del proyecto
- **Sistema flexible de productos con TipoProducto**
  - Nuevo enum `TipoProducto` (INDUMENTARIA, ELECTRONICA, HOGAR, DEPORTES, JUGUETES, LIBROS, BELLEZA, AUTOMOTOR, OTROS)
  - Campo `tipoProducto` en entidad `Product`
  - Campo `tipoProductoDefault` en entidad `Category`
  - Renderizado condicional de campos según tipo de producto (preparado para frontend)
- **Sistema de subcategorías**
  - Nueva entidad `Subcategoria` con relación Many-to-One con `Category`
  - Relación Many-to-Many entre `Product` y `Subcategoria`
  - `SubcategoriaRepository`, `SubcategoriaService` y `SubcategoriaController`
  - Integración en formulario de productos con filtrado dinámico por categoría
- **Campos adicionales en Product para integración con marketplaces**
  - `especificaciones` (TEXT) - Especificaciones técnicas del producto
  - `marca` (VARCHAR 100) - Marca del producto
  - `modelo` (VARCHAR 100) - Modelo del producto
  - `garantia` (VARCHAR 100) - Información de garantía
  - `codigoProducto` (VARCHAR 100) - Código SKU o identificador único
  - `linkVenta` (VARCHAR 500) - Enlace a marketplace o sitio de venta
  - `contactoVendedor` (VARCHAR 200) - Información de contacto del vendedor
  - `ubicacion` (VARCHAR 200) - Ubicación del vendedor/producto
- **Categorías principales por defecto**
  - Tecnología, Indumentaria y Calzado, Hogar y Muebles, Electrodomésticos, Bebés y Niños, Deportes y Fitness, Librería Arte y Educación, Automotor, Otros
  - Subcategorías predefinidas para Tecnología e Indumentaria

### Fixed
- **Problemas de reconocimiento de Lombok en IDE** - Agregados getters/setters manuales para nuevos campos en `Product` y `Category` para compatibilidad con IDE
- **Compilación Maven** - Verificada compilación exitosa después de limpieza de código legacy

---

## [1.0.0] - 2025-09-23

### Added
- Sistema completo de e-commerce para ORIOLA Indumentaria
- Catálogo de productos con imágenes WebP
- Sistema de administración con Spring Boot
- Integración con WhatsApp para consultas
- Geolocalización de usuarios
- Sistema de formularios de contacto
- Gestión de usuarios con roles (ADMIN, SUPER_ADMIN)
- Base de datos MySQL con JPA/Hibernate
- Interfaz responsive con Bootstrap
- Sistema de carga de imágenes con thumbnails

### Technical Details
- **Framework**: Spring Boot 3.x
- **Database**: MySQL 8.0
- **Frontend**: Thymeleaf + Bootstrap 5
- **Security**: Spring Security
- **Build Tool**: Maven
- **Java Version**: 17+

---

## Estructura de Versiones

- **MAJOR**: Cambios incompatibles en la API
- **MINOR**: Nueva funcionalidad compatible hacia atrás
- **PATCH**: Corrección de bugs compatible hacia atrás

## Formato de Entradas

- **Added**: Nueva funcionalidad
- **Changed**: Cambios en funcionalidad existente
- **Deprecated**: Funcionalidad que será removida
- **Removed**: Funcionalidad removida
- **Fixed**: Corrección de bugs
- **Security**: Mejoras de seguridad
