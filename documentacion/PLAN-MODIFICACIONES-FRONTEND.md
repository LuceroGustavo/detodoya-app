# Plan de Modificaciones Frontend - Detodoya.com

**Fecha:** Enero 2025  
**Proyecto:** Detodoya.com  
**Estado:** 🔄 En progreso

---

## 📋 Resumen

Este documento detalla las modificaciones a realizar en el frontend para completar la migración de ORIOLA a Detodoya.com.

---

## ✅ ÁREAS QUE NO SE TOCARÁN

- ❌ **Sistema de usuarios y roles** - Se mantiene como está
- ❌ **Seguridad (Spring Security)** - Sin cambios
- ❌ **Estructura de admin** - Se mantiene igual
- ❌ **Usuario admin y desarrollo** - Sin cambios

---

## 🎯 MODIFICACIONES A REALIZAR

### **Fase 1: Cambio de Branding (Prioridad Alta)**

#### **1.1 Templates HTML Públicos**
- [ ] `index.html` - Cambiar "Oriola" → "Detodoya"
- [ ] `catalog.html` - Cambiar branding
- [ ] `product-detail.html` - Cambiar branding
- [ ] `contact.html` - Cambiar branding
- [ ] `about.html` - Cambiar contenido (de indumentaria a catálogo genérico)
- [ ] `fragments/footer-black.html` - Cambiar branding y descripción

#### **1.2 Templates HTML Admin**
- [ ] `admin/layout.html` - Cambiar "Oriola - Panel de Admin" → "Detodoya - Panel de Admin"
- [ ] `admin/dashboard.html` - Cambiar títulos
- [ ] `admin/login.html` - Cambiar branding
- [ ] Todos los templates admin - Cambiar referencias a Oriola

#### **1.3 Archivos Estáticos**
- [ ] `static/css/style.css` - Cambiar comentarios y clases CSS (`.navbar-oriola` → `.navbar-detodoya`)
- [ ] `static/js/oriola-messages.js` - Renombrar o actualizar
- [ ] `static/css/oriola-messages.css` - Renombrar o actualizar

#### **1.4 Textos y Descripciones**
- [ ] Cambiar "Indumentaria" → "Catálogo de Productos" o similar
- [ ] Cambiar "orioladenim.com" → "detodoya.com"
- [ ] Actualizar descripciones del footer
- [ ] Actualizar página "Sobre Nosotros"

---

### **Fase 2: Formulario de Productos (Prioridad Alta)**

#### **2.1 Agregar Campos Nuevos**
- [ ] Selector de "Tipo de Producto" (enum TipoProducto)
- [ ] Campos de integración marketplace:
  - [ ] Código de Producto / SKU
  - [ ] Link de Venta
  - [ ] Contacto del Vendedor
  - [ ] Ubicación
- [ ] Campos genéricos:
  - [ ] Especificaciones (nuevo campo)
  - [ ] Marca
  - [ ] Modelo
  - [ ] Garantía

#### **2.2 Campos Condicionales (JavaScript)**
- [ ] Mostrar campos de INDUMENTARIA solo si tipo = INDUMENTARIA
- [ ] Mostrar campos de ELECTRÓNICA solo si tipo = ELECTRONICA
- [ ] Ocultar campos no relevantes según tipo

#### **2.3 Actualizar Validaciones**
- [ ] Hacer `medidas` opcional (ya es nullable)
- [ ] Validar campos según tipo de producto

---

### **Fase 3: Vista de Detalle de Producto**

#### **3.1 Mostrar Campos Según Tipo**
- [ ] Mostrar talles, géneros, temporadas solo si es INDUMENTARIA
- [ ] Mostrar marca, modelo, garantía solo si es ELECTRONICA
- [ ] Mostrar campos de marketplace siempre (linkVenta, contacto, etc.)

#### **3.2 Actualizar Diseño**
- [ ] Organizar información por secciones
- [ ] Destacar link de venta y contacto
- [ ] Mejorar presentación de especificaciones

---

### **Fase 4: Catálogo y Búsqueda**

#### **4.1 Filtros**
- [ ] Agregar filtro por tipo de producto (opcional)
- [ ] Mantener filtros existentes (categoría, color, etc.)

#### **4.2 Tarjetas de Producto**
- [ ] Mostrar tipo de producto (badge)
- [ ] Mostrar link de venta si existe
- [ ] Actualizar diseño si es necesario

---

## 📝 Archivos a Modificar (Resumen)

### **Templates HTML (29 archivos):**
- Públicos: `index.html`, `catalog.html`, `product-detail.html`, `contact.html`, `about.html`
- Admin: `admin/layout.html`, `admin/dashboard.html`, `admin/login.html`, `admin/product-form.html`, etc.
- Fragments: `fragments/footer-black.html`

### **Archivos Estáticos (9 archivos):**
- CSS: `style.css`, `oriola-messages.css`, `lovely-style.css`
- JS: `oriola-messages.js`, `whatsapp.js`

---

## 🎯 Orden de Implementación Sugerido

1. **Cambio de Branding** (Fase 1) - Más visible, impacto inmediato
2. **Formulario de Productos** (Fase 2) - Funcionalidad nueva
3. **Vista de Detalle** (Fase 3) - Mostrar información correcta
4. **Catálogo** (Fase 4) - Mejoras opcionales

---

## ⚠️ Consideraciones

- **No cambiar estructura de seguridad** - Mantener como está
- **Mantener funcionalidad existente** - Solo actualizar textos y agregar campos
- **Probar después de cada fase** - Verificar que todo funciona

---

**¿Por dónde empezamos?**

