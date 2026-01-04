# Tareas Pendientes - Detodoya.com

**Fecha de creación:** Enero 2025  
**Estado:** 📋 Pendiente de implementación

---

## 🎥 **RESUELTO: Manejo de Videos en Publicaciones (2 de Enero 2025)**

### **Problema Resuelto:**
✅ El problema del fondo gris que aparecía cuando un producto tenía un video como medio principal ha sido resuelto.

### **Solución Implementada:**
- ✅ Videos se muestran directamente en la galería sin activar modales automáticamente
- ✅ Uso de `th:with` para detectar dinámicamente si es video o imagen
- ✅ Clases condicionales con `th:class` para mostrar/ocultar elementos correctamente
- ✅ Modal solo se activa cuando el usuario hace clic explícitamente
- ✅ Videos funcionan correctamente en thumbnails, galería principal y modal

### **Archivos Modificados:**
- `src/main/resources/templates/product-detail.html` (migrado completamente)

### **Estado:** ✅ RESUELTO - Videos funcionan correctamente sin fondo gris

---

## 🎨 **PRIORIDAD MEDIA: Retoques Finales en HTML de Detalle de Producto**

### **Estado Actual:**
- ✅ Migración completa del `product-detail.html` al nuevo diseño Tailwind CSS realizada (2 de Enero 2025)
- ✅ Soporte completo para videos funcionando correctamente (sin fondo gris)
- ✅ Modal funcional con navegación por teclado y flechas
- ✅ Tabs funcionales (Descripción, Especificaciones, Cuidados)
- ✅ Todo conectado dinámicamente con el backend mediante Thymeleaf
- ⚠️ Pendiente: Algunos retoques menores según feedback del usuario

### **Tareas Pendientes:**
- [ ] Aplicar retoques menores según feedback del usuario
- [ ] Verificar y ajustar estilos si es necesario
- [ ] Optimizar experiencia en móviles si es requerido
- [ ] Ajustar detalles visuales según sea necesario

### **Archivos Modificados:**
- `src/main/resources/templates/product-detail.html` (migrado completamente)

### **Notas:**
- La migración se completó exitosamente el 2 de Enero 2025
- Los videos funcionan correctamente sin el problema del fondo gris
- El modal y toda la funcionalidad están operativos
- Pendiente aplicar retoques menores según feedback

---

## 🎨 **PRIORIDAD MEDIA: Selección Opcional de Colores en Productos**

### **Objetivo:**
Permitir que el administrador elija si un producto debe mostrar colores o no, independientemente de su categoría o tipo.

### **Funcionalidad Requerida:**

#### **Backend:**
1. **Modificar Entidad `Product`:**
   - Agregar campo booleano `mostrarColores` (default: `false`)
   - Agregar campo booleano `mostrarTalles` (default: `false`)
   - Agregar campo booleano `mostrarGeneros` (default: `false`)
   - Agregar campo booleano `mostrarTemporadas` (default: `false`)

2. **Modificar `ProductController`:**
   - Actualizar `addProduct()` y `updateProduct()` para manejar nuevos campos
   - Agregar validación lógica

3. **Modificar `ProductService`:**
   - Actualizar métodos de guardado para incluir nuevos campos

#### **Frontend - Panel Admin:**
1. **Modificar `product-form.html`:**
   - Agregar checkboxes para cada opción:
     - ☐ Mostrar colores en la publicación
     - ☐ Mostrar talles en la publicación
     - ☐ Mostrar géneros en la publicación
     - ☐ Mostrar temporadas en la publicación
   - Los checkboxes solo deben aparecer si el tipo de producto es INDUMENTARIA
   - Si están desmarcados, no se deben guardar relaciones con colores/talles/géneros/temporadas

#### **Frontend - Vistas Públicas:**
1. **Modificar `product-detail.html`:**
   - Solo mostrar sección de colores si `product.mostrarColores == true`
   - Solo mostrar sección de talles si `product.mostrarTalles == true`
   - Solo mostrar sección de géneros si `product.mostrarGeneros == true`
   - Solo mostrar sección de temporadas si `product.mostrarTemporadas == true`

2. **Modificar `catalog.html`:**
   - Solo mostrar colores disponibles si `product.mostrarColores == true`
   - Ajustar layout de cards si no hay colores

### **Archivos a Modificar:**
- `src/main/java/com/detodoya/entity/Product.java`
- `src/main/java/com/detodoya/controller/ProductController.java`
- `src/main/java/com/detodoya/service/ProductService.java`
- `src/main/resources/templates/admin/product-form.html`
- `src/main/resources/templates/product-detail.html`
- `src/main/resources/templates/catalog.html`

### **Tareas Específicas:**
- [ ] Agregar campos booleanos a entidad `Product`
- [ ] Actualizar base de datos (migración)
- [ ] Modificar formulario de producto para incluir checkboxes
- [ ] Actualizar lógica de guardado para respetar checkboxes
- [ ] Modificar `product-detail.html` para mostrar/ocultar secciones
- [ ] Modificar `catalog.html` para mostrar/ocultar colores
- [ ] Probar con productos existentes
- [ ] Documentar cambios

---

## 🖼️ **PRIORIDAD MEDIA: Mejoras en Vistas de Imágenes**

### **Objetivos:**
1. Mejorar la visualización de imágenes en las cards de productos
2. Optimizar el lightbox para mejor experiencia de usuario
3. Mejorar la galería de thumbnails en `product-detail.html`

### **Mejoras Propuestas:**

#### **1. Cards de Productos (`catalog.html`):**
- [ ] Agregar efecto hover en imágenes
- [ ] Mejorar transición al cambiar de imagen
- [ ] Agregar indicador de múltiples imágenes (badge con número)
- [ ] Optimizar carga lazy de imágenes
- [ ] Agregar placeholder mientras carga la imagen

#### **2. Lightbox (`product-detail.html`):**
- [ ] Mejorar animación de apertura/cierre
- [ ] Agregar zoom en imágenes (doble tap en móvil)
- [ ] Mejorar navegación con teclado
- [ ] Agregar contador de imágenes (ej: "Imagen 3 de 8")
- [ ] Optimizar para pantallas grandes (4K)
- [ ] Agregar opción de descargar imagen

#### **3. Thumbnails (`product-detail.html`):**
- [ ] Mejorar indicador de imagen activa
- [ ] Agregar scroll horizontal suave
- [ ] Mejorar tamaño en móviles
- [ ] Agregar efecto hover más visible
- [ ] Optimizar carga de thumbnails

#### **4. Galería Principal:**
- [ ] Mejorar transición entre imágenes/videos
- [ ] Agregar preload de siguiente imagen
- [ ] Optimizar para videos largos
- [ ] Agregar controles de video más visibles
- [ ] Mejorar responsive en tablets

### **Archivos a Modificar:**
- `src/main/resources/templates/catalog.html`
- `src/main/resources/templates/product-detail.html`
- `src/main/resources/static/css/` (posible nuevo archivo CSS)

### **Tareas Específicas:**
- [ ] Investigar mejores prácticas de galerías de imágenes
- [ ] Implementar lazy loading
- [ ] Agregar animaciones suaves
- [ ] Optimizar para móviles
- [ ] Probar en diferentes navegadores
- [ ] Documentar mejoras

---

## 🔍 **PRIORIDAD BAJA: Mejoras Adicionales**

### **1. Optimización de Performance:**
- [ ] Implementar lazy loading para imágenes
- [ ] Optimizar carga de CSS/JS
- [ ] Implementar cache de imágenes
- [ ] Minificar archivos estáticos

### **2. Accesibilidad:**
- [ ] Agregar atributos `alt` descriptivos a todas las imágenes
- [ ] Mejorar contraste de colores
- [ ] Agregar soporte para lectores de pantalla
- [ ] Mejorar navegación con teclado

### **3. SEO:**
- [ ] Agregar meta tags descriptivos
- [ ] Implementar schema.org markup
- [ ] Optimizar títulos y descripciones
- [ ] Agregar sitemap.xml

### **4. Testing:**
- [ ] Probar en diferentes navegadores
- [ ] Probar en diferentes dispositivos
- [ ] Probar con diferentes tamaños de imágenes
- [ ] Probar con diferentes formatos de video

---

## 📊 **RESUMEN DE PRIORIDADES**

### **🔴 Prioridad Alta (Urgente):**
1. ~~Manejo de videos en publicaciones (pantalla gris)~~ ✅ RESUELTO (2 de Enero 2025)
2. ~~Completar vistas y estilo del HTML de detalle de producto~~ ✅ COMPLETADO (2 de Enero 2025) - Pendiente retoques menores

### **🟡 Prioridad Media (Importante):**
1. Selección opcional de colores en productos
2. Mejoras en vistas de imágenes

### **🟢 Prioridad Baja (Mejoras):**
1. Optimización de performance
2. Accesibilidad
3. SEO
4. Testing

---

## 📝 **NOTAS ADICIONALES**

### **Consideraciones Técnicas:**
- Mantener compatibilidad con productos existentes
- Asegurar que los cambios no rompan funcionalidades actuales
- Probar en servidor de producción antes de desplegar
- Documentar todos los cambios

### **Dependencias:**
- Algunas mejoras pueden requerir actualización de dependencias
- Verificar compatibilidad con versiones actuales de librerías
- Considerar impacto en performance

### **Recursos Necesarios:**
- Tiempo estimado para videos: 4-6 horas
- Tiempo estimado para colores opcionales: 6-8 horas
- Tiempo estimado para mejoras de imágenes: 4-6 horas

---

**Última actualización:** 2 de Enero 2025  
**Próxima revisión:** Después de aplicar retoques menores en product-detail.html

### **Nota del 2 de Enero 2025:**
- ✅ Migración completa de `product-detail.html` al nuevo diseño Tailwind CSS realizada
- ✅ Problema del fondo gris con videos RESUELTO
- ✅ Modal funcional con navegación completa
- ✅ Tabs funcionales (Descripción, Especificaciones, Cuidados)
- ✅ Todo conectado dinámicamente con el backend
- ⚠️ Pendiente: Aplicar retoques menores según feedback del usuario



