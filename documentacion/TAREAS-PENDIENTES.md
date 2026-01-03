# Tareas Pendientes - Detodoya.com

**Fecha de creación:** Enero 2025  
**Estado:** 📋 Pendiente de implementación

---

## 🎥 **PRIORIDAD ALTA: Manejo de Videos en Publicaciones**

### **Problema Identificado:**
Cuando un producto tiene un video como medio principal, se muestra una pantalla gris que cubre toda la pantalla y bloquea la interacción con otros elementos de la página.

### **Síntomas:**
- Fondo gris cubre toda la pantalla cuando hay video
- No se puede interactuar con otros elementos (talles, botones, etc.)
- El video se reproduce automáticamente pero bloquea la UI
- Los talles se ven cortados por el fondo gris
- El problema solo aparece cuando se sube un video

### **Archivos Afectados:**
- `src/main/resources/templates/product-detail.html`

### **Análisis Necesario:**
1. Verificar si el modal/lightbox se está activando incorrectamente
2. Revisar `z-index` de elementos cuando hay video
3. Verificar que el video no esté dentro de un contenedor modal
4. Revisar CSS que pueda estar aplicando `backdrop` o `overlay` incorrectamente

### **Solución Propuesta:**
- Asegurar que el modal solo se active cuando se hace clic en una imagen
- Verificar que el video no active el modal automáticamente
- Ajustar `z-index` para que el video no interfiera con otros elementos
- Revisar y corregir cualquier CSS que aplique fondo gris cuando hay video

### **Tareas Específicas:**
- [ ] Investigar por qué aparece el fondo gris solo con videos
- [ ] Verificar que el lightbox no se active automáticamente con videos
- [ ] Ajustar CSS para que videos no bloqueen la UI
- [ ] Probar con diferentes tipos de videos (MP4, WebM, etc.)
- [ ] Asegurar que los controles del video funcionen correctamente
- [ ] Verificar que los talles y otros elementos sean accesibles cuando hay video

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
1. Manejo de videos en publicaciones (pantalla gris)

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

**Última actualización:** Enero 2025  
**Próxima revisión:** Después de implementar tareas de prioridad alta



