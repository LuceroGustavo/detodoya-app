# Documentación - Detodoya.com

**Proyecto:** Catálogo online de productos - Detodoya.com  
**Versión:** 2.0.0 (Migración desde ORIOLA Indumentaria)  
**Fecha de inicio:** Enero 2025  
**Estado:** 🔄 En desarrollo

---

## 📋 Índice de Documentación

### 🚀 Despliegue y Servidor
- [Configuración de Servidor](./servidor/) - Guías de configuración y despliegue
- Ver carpeta `servidor/` para toda la documentación relacionada con servidores

### 📝 Progreso del Proyecto
- [Estado del Proyecto](./ESTADO-PROYECTO.md) - Estado actual y próximos pasos
- [Plan de Modificación V2](./PLAN-MODIFICACION-ENTIDADES-V2.md) - Plan flexible que mantiene indumentaria y agrega otros productos
- [Casos de Uso](./CASOS-USO-DETODOYA.md) - Cómo los vendedores usarán Detodoya como showcase profesional
- [Changelog](../CHANGELOG.md) - Historial de cambios

---

## 🎯 Objetivo del Proyecto

**Detodoya.com** es un catálogo online estilo MercadoLibre (sin sistema de venta) que permite publicar cualquier tipo de producto, no solo indumentaria. El proyecto es una migración y adaptación del sistema ORIOLA Indumentaria.

### Características Principales:
- ✅ Catálogo de productos genéricos (no limitado a indumentaria)
- ✅ Sistema de categorías dinámicas
- ✅ Gestión de imágenes y videos
- ✅ Panel de administración completo
- ✅ Sistema de consultas/contacto
- ✅ Analytics de productos

---

## 📁 Estructura de Documentación

```
documentacion/
├── servidor/              # Documentación de servidores y despliegue
├── backup-oriola/         # Documentación del sistema anterior (backup)
└── README.md              # Este archivo
```

---

## 🔄 Migración desde ORIOLA

Este proyecto es una migración desde **ORIOLA Indumentaria** a **Detodoya.com**. Los cambios principales incluyen:

1. **Renombrado de paquetes:** `com.orioladenim` → `com.detodoya`
2. **Nueva base de datos:** `detodoya` (en lugar de `oriola_indumentaria`)
3. **Adaptación de entidades:** Eliminación de campos específicos de indumentaria
4. **Nuevo branding:** Detodoya en lugar de ORIOLA

---

**Última actualización:** Enero 2025

