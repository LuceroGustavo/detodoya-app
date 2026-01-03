# Changelog

Todos los cambios notables de este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
