# Aplicación de Video a Productos - Documentación

**Fecha:** 26 de octubre de 2025  
**Última actualización:** 27 de enero de 2025  
**Estado:** ✅ RESUELTO - Completamente funcional  
**Objetivo:** Permitir que los productos puedan tener videos además de imágenes, mostrando videos en index, catálogo y detalle de producto.

---

## 📋 Resumen Ejecutivo

Se implementó un sistema unificado de gestión de medios (imágenes y videos) para productos. El sistema permite cargar videos o imágenes desde un único formulario de administración, distinguiéndolos mediante un flag `isVideo` en la entidad `ProductImage`.

### ✅ Problemas Resueltos (27 de enero de 2025)

1. **✅ Error de parsing en `index.html`**: **RESUELTO** - Usando `th:with` para pre-calcular valores
2. **✅ Error de parsing en `product-detail.html`**: **RESUELTO** - Usando `product.getImagenPrincipal()` en lugar de `images[0]`
3. **✅ Carrusel de categorías no funciona**: **RESUELTO** - El error de parsing impedía que se renderizara correctamente
4. **✅ Selección de thumbnails no funciona**: **RESUELTO** - Ambos elementos (video e imagen) ahora están siempre en el DOM
5. **✅ Imágenes principales no se mostraban**: **RESUELTO** - Corregido acceso a la imagen principal
6. **✅ Videos y thumbnails no intercambiaban**: **RESUELTO** - JavaScript ahora funciona correctamente

---

## 🔧 Cambios Implementados

### 1. Backend - Entidad ProductImage

**Archivo:** `src/main/java/com/orioladenim/entity/ProductImage.java`

Se agregó el campo `isVideo` para distinguir entre imágenes y videos:

```java
@Column(name = "is_video")
private Boolean isVideo = false;

public Boolean getIsVideo() { return isVideo; }
public void setIsVideo(Boolean isVideo) { this.isVideo = isVideo; }
```

### 2. Backend - Entidad Product

**Archivo:** `src/main/java/com/orioladenim/entity/Product.java`

Se agregó el método `getImagenPrincipalIsVideo()` para saber si la imagen principal es un video sin acceder directamente a `images[0]`:

```java
// Método para verificar si la imagen principal es un video
public Boolean getImagenPrincipalIsVideo() {
    ProductImage imagenPrincipal = getImagenPrincipal();
    return imagenPrincipal != null ? imagenPrincipal.getIsVideo() : false;
}
```

### 3. Backend - ProductController

**Archivo:** `src/main/java/com/orioladenim/controller/ProductController.java`

Se modificó el método `uploadImages` para:

1. **Detectar automáticamente si un archivo es video o imagen**:
   ```java
   boolean isVideo = file.getContentType() != null && 
                   (file.getContentType().startsWith("video/"));
   ```

2. **Procesar videos** usando `VideoProcessingService`:
   ```java
   if (isVideo) {
       String videoPath = videoProcessingService.procesarVideoProducto(file, pId);
       // ... configuración del ProductImage como video
       productImage.setIsVideo(true);
   }
   ```

3. **Asegurar que solo haya una imagen principal**:
   - Al subir nuevas imágenes, desmarca todas las principales existentes
   - Marca la primera imagen nueva como principal

4. **Metodología para manejar videos**:
   ```java
   // Desactivar todas las imágenes principales existentes
   List<ProductImage> existingImages = productImageRepository.findByProductIdOrderByDisplayOrderAsc(pId);
   for (ProductImage existingImage : existingImages) {
       if (existingImage.getIsPrimary()) {
           existingImage.setIsPrimary(false);
           productImageRepository.save(existingImage);
       }
   }
   ```

### 4. Frontend - Admin Interface (product-images.html)

**Archivo:** `src/main/resources/templates/admin/product-images.html`

#### 4.1. Especificaciones Técnicas Unificadas

Se combinaron las especificaciones de imágenes y videos en un solo panel:

```html
<!-- Especificaciones de Imágenes y Videos -->
<div class="specifications-section mb-4">
    <h6 class="mb-3">
        <i class="bi bi-rulers me-2"></i>Especificaciones de Imágenes y Videos
    </h6>
    <div class="row">
        <div class="col-md-6">
            <div class="specification-card">
                <!-- Especificaciones de imágenes -->
            </div>
        </div>
        <div class="col-md-6">
            <div class="specification-card">
                <!-- Especificaciones de videos -->
            </div>
        </div>
    </div>
</div>
```

#### 4.2. Upload Unificado

El input de archivos acepta tanto imágenes como videos:

```html
<input type="file" id="fileInput" multiple accept="image/*,video/*" style="display: none;" max="5">
```

#### 4.3. Validación de Archivos

JavaScript actualizado para validar tanto imágenes como videos:

```javascript
function validateFiles(files) {
    const maxFiles = 5;
    const maxSizeImage = 5 * 1024 * 1024; // 5MB para imágenes
    const maxSizeVideo = 50 * 1024 * 1024; // 50MB para videos
    const allowedTypes = [
        'image/jpeg', 'image/jpg', 'image/png', 'image/webp', // Imágenes
        'video/mp4', 'video/webm', 'video/quicktime', 'video/x-msvideo' // Videos
    ];
    // ... validación
}
```

#### 4.4. Visualización de Videos en Miniatura

Se corrigió la función `displaySelectedFiles` para mostrar videos:

```javascript
// Determinar si es video
const isVideo = file.type && file.type.startsWith('video/');

if (isVideo) {
    // Para videos, crear un elemento video
    const videoUrl = URL.createObjectURL(file);
    // ... generar HTML con <video> tag
} else {
    // Para imágenes, usar FileReader
    // ... generar HTML con <img> tag
}
```

#### 4.5. Visualización en Galería de Administración

Se modificó el HTML para mostrar videos en la galería de administración:

```html
<!-- Mostrar video si es video -->
<video th:if="${image.isVideo}" th:src="${image.getImageUrl()}" muted loop style="max-width: 100%; max-height: 100%; object-fit: contain;"></video>
<!-- Mostrar imagen si no es video -->
<img th:unless="${image.isVideo}" th:src="${image.getImageUrl()}" class="img-fluid" style="max-width: 100%; max-height: 100%; object-fit: contain;">
```

### 5. Frontend - Index Page

**Archivo:** `src/main/resources/templates/index.html`

Se intentó agregar soporte para videos en las tarjetas de productos:

```html
<!-- Mostrar video si es video -->
<video th:if="${product.images != null and !product.images.empty and product.getImagenPrincipalIsVideo()}" 
     th:src="${product.getImagenPrincipalUrl()}" 
     th:title="${product.name}" 
     class="product-image"
     muted loop autoplay
     style="width: 100%; height: 100%; object-fit: cover;"></video>
<!-- Mostrar imagen si no es video -->
<img th:if="${product.images != null and !product.images.empty and !product.getImagenPrincipalIsVideo()}" 
     th:src="${product.getImagenPrincipalUrl()}" 
     th:alt="${product.name}" 
     class="product-image">
```

**⚠️ PROBLEMA ACTUAL:** El template de index.html tiene un error de parsing de Thymeleaf que impide que el carrusel y los productos se muestren correctamente. Las imágenes de categorías se muestran agrandadas y sin formato.

### 6. Frontend - Product Detail Page

**Archivo:** `src/main/resources/templates/product-detail.html`

#### 6.1. Visualización de Video/Imagen Principal

```html
<video th:if="${product.images != null and !product.images.isEmpty() and product.getImagenPrincipalIsVideo()}" 
     th:src="${product.getImagenPrincipalUrl()}" 
     id="mainVideo"
     autoplay muted loop
     style="width: 100%; height: 100%; ...">
</video>
<img th:if="${product.images != null and !product.images.isEmpty() and !product.getImagenPrincipalIsVideo()}" 
     th:src="${product.getImagenPrincipalUrl()}" 
     id="mainImage" 
     class="img-fluid" 
     style="...">
```

#### 6.2. Atributo data-is-video en Thumbnails

Se agregó el atributo `data-is-video` a los thumbnails para el JavaScript:

```html
<div th:each="image, iterStat : ${product.images}" 
     th:data-image-url="${image.getImageUrl()}"
     th:data-image-id="${image.id}"
     th:data-is-video="${image.isVideo}"
     class="thumbnail-vertical">
```

#### 6.3. JavaScript para Cambio de Thumbnails

El JavaScript fue actualizado para manejar tanto videos como imágenes:

```javascript
document.querySelectorAll('.thumbnail-vertical').forEach(thumb => {
    thumb.addEventListener('click', function() {
        const imageUrl = this.getAttribute('data-image-url');
        const isVideo = this.getAttribute('data-is-video') === 'true';
        
        const mainImage = document.getElementById('mainImage');
        const mainVideo = document.getElementById('mainVideo');
        const modalImage = document.getElementById('modalImage');
        
        if (isVideo) {
            // Es un video, mostrar video y ocultar imagen
            if (mainVideo) {
                mainVideo.src = imageUrl;
                mainVideo.classList.remove('d-none');
            }
            if (mainImage) {
                mainImage.classList.add('d-none');
            }
        } else {
            // Es una imagen, mostrar imagen y ocultar video
            if (mainImage) {
                mainImage.src = imageUrl;
                mainImage.classList.remove('d-none');
            }
            if (mainVideo) {
                mainVideo.classList.add('d-none');
            }
        }
    });
});
```

**⚠️ PROBLEMA ACTUAL:** Similar al de index.html, hay errores de parsing de Thymeleaf que impiden que el template se renderice correctamente.

---

## 🐛 Problemas Conocidos

### 1. Error de Parsing en Templates

**Error:** `An error happened during template parsing (template: "class path resource [templates/index.html]")`

**Causa:** Thymeleaf no puede parsear las condiciones complejas con `product.getImagenPrincipalIsVideo()` en `th:if`.

**Ubicación:** 
- Líneas 754-764 en `index.html`
- Líneas 143-152 en `product-detail.html`

**Intentos de Solución:**
1. ✅ Se agregó el método `getImagenPrincipalIsVideo()` en la entidad `Product`
2. ❌ Usar directamente `th:if="${product.getImagenPrincipalIsVideo()}"` causa error de parsing
3. ❌ Usar operador ternario en `th:class` causa error de parsing

**Estado:** Requiere investigación adicional

### 2. Carrusel de Categorías Roto

**Síntoma:** Las imágenes de categorías se muestran agrandadas, sin el carrusel funcional

**Causa:** Error de parsing de Thymeleaf está rompiendo todo el DOM

**Estado:** Requiere corrección del error de parsing primero

### 3. Selección de Thumbnails No Funciona

**Síntoma:** Al hacer clic en thumbnails, no se actualiza el contenido principal

**Causa:** El JavaScript intenta manipular elementos que no están en el DOM debido al error de parsing

**Estado:** Requiere corrección del error de parsing primero

---

## 🎯 Próximos Pasos

1. **Corregir error de parsing de Thymeleaf**
   - Investigar por qué `product.getImagenPrincipalIsVideo()` causa error
   - Posible solución: Crear un atributo en el modelo o usar una expresión más simple

2. **Verificar que el carrusel de categorías funcione**
   - Una vez corregido el parsing, verificar que Swiper.js se inicialice correctamente

3. **Probar selección de thumbnails**
   - Verificar que el JavaScript funcione correctamente

4. **Implementar videos en catálogo**
   - Similar a lo hecho en index.html, pero para catalog.html

5. **Testing exhaustivo**
   - Probar con productos que tienen solo imágenes
   - Probar con productos que tienen solo videos
   - Probar con productos que tienen ambos
   - Probar con productos que no tienen medios

---

## 📝 Notas Técnicas

### ¿Por qué `isVideo` en lugar de una entidad separada?

Se decidió usar un flag `isVideo` en lugar de crear una entidad `ProductVideo` separada porque:
1. Simplifica la gestión de medios
2. Evita consultas complejas con múltiples colecciones
3. Facilita el código de administración
4. Reduce la posibilidad de inconsistencias (productos con imágenes principales que no existen)

### ¿Por qué no usar un método en Thymeleaf?

Thymeleaf no soporta llamar métodos personalizados en expresiones `th:if` con la sintaxis estándar. Intentos de usar `product.getImagenPrincipalIsVideo()` directamente causaron errores de parsing.

### Alternativa Considerada

Se consideró usar `product.images[0].isVideo`, pero esto falla cuando:
1. `images` está vacío
2. `images` es `null`
3. Thymeleaf no puede evaluar la expresión correctamente

---

## 📊 Archivos Modificados

1. `src/main/java/com/orioladenim/entity/ProductImage.java` - Agregado campo `isVideo`
2. `src/main/java/com/orioladenim/entity/Product.java` - Agregado método `getImagenPrincipalIsVideo()`
3. `src/main/java/com/orioladenim/controller/ProductController.java` - Lógica de upload unificado
4. `src/main/resources/templates/admin/product-images.html` - Interfaz de administración unificada
5. `src/main/resources/templates/index.html` - Soporte para videos (con error de parsing)
6. `src/main/resources/templates/product-detail.html` - Soporte para videos (con error de parsing)

---

## 🔍 Debugging

Para debuggear el error de parsing:

1. Buscar en los logs la línea exacta que causa el error
2. Probar expresiones más simples en Thymeleaf
3. Verificar que el método `getImagenPrincipalIsVideo()` no retorne `null`
4. Considerar usar `th:with` para pre-calcular valores

---

## 💡 Recomendaciones para Mañana

1. **Empezar por corregir el error de parsing**
   - Es la causa raíz de todos los problemas visibles
   
2. **Una posible solución:**
   ```html
   <div th:with="isVideo=${product.getImagenPrincipalIsVideo()}">
       <video th:if="${isVideo}" ...></video>
       <img th:unless="${isVideo}" ...>
   </div>
   ```

3. **Otra alternativa:**
   - Crear un atributo `isVideo` en el modelo del controlador
   - Pre-calcular el valor en el backend antes de pasar al template

---

---

## ✅ SOLUCIÓN IMPLEMENTADA (27 de enero de 2025)

### Problema Identificado

El sistema de videos no funcionaba correctamente debido a:

1. **Error de parsing de Thymeleaf**: No se podía llamar a `product.getImagenPrincipalIsVideo()` directamente en expresiones `th:if`
2. **Acceso incorrecto a imagen principal**: Se usaba `product.images[0]` en lugar de `product.getImagenPrincipal()`
3. **Elementos faltantes en el DOM**: El JavaScript no encontraba los elementos `mainImage` o `mainVideo`

### Solución Implementada

#### 1. Uso de `th:with` para pre-calcular valores

**Problema:** Thymeleaf no puede parsear métodos complejos en expresiones `th:if`  
**Solución:** Usar `th:with` para pre-calcular el valor antes de usarlo:

```thymeleaf
<div th:with="imagenPrincipal=${product.getImagenPrincipal()}, 
               isVideo=${imagenPrincipal != null ? (imagenPrincipal.isVideo != null ? imagenPrincipal.isVideo : false) : false}">
    <video th:if="${isVideo}" ...></video>
    <img th:if="${!isVideo and imagenPrincipal != null}" ...>
</div>
```

#### 2. Corrección del acceso a imagen principal

**Problema:** Se accedía directamente a `images[0]` que no es necesariamente la imagen principal  
**Solución:** Usar el método `getImagenPrincipal()` que busca la imagen con `isPrimary = true`:

```thymeleaf
<!-- ANTES (INCORRECTO) -->
imagenPrincipal=${product.images[0]}

<!-- AHORA (CORRECTO) -->
imagenPrincipal=${product.getImagenPrincipal()}
```

#### 3. Ambos elementos siempre en el DOM (product-detail.html)

**Problema:** El JavaScript no podía encontrar los elementos porque solo uno existía en el DOM  
**Solución:** Renderizar ambos elementos (video e imagen) pero ocultar uno con la clase `d-none`:

```thymeleaf
<!-- Video - Siempre presente pero oculto si no es video -->
<video th:if="${product.images != null and !product.images.isEmpty()}" 
     id="mainVideo"
     th:class="${isVideo} ? '' : 'd-none'"
     ...></video>

<!-- Imagen - Siempre presente pero oculto si es video -->
<img th:if="${product.images != null and !product.images.isEmpty()}" 
     id="mainImage" 
     th:class="${!isVideo and imagenPrincipal != null} ? 'img-fluid' : 'd-none img-fluid'"
     ...>
```

### Archivos Modificados (27 de enero de 2025)

1. ✅ `src/main/resources/templates/index.html` (líneas 753-768)
2. ✅ `src/main/resources/templates/catalog.html` (líneas 149-164)
3. ✅ `src/main/resources/templates/product-detail.html` (desktop líneas 143-159, móvil líneas 175-191)

### Resultados de las Correcciones

✅ **Videos se muestran correctamente** cuando son la imagen principal  
✅ **Imágenes se muestran correctamente** cuando son la imagen principal  
✅ **Carrusel de categorías funciona** correctamente  
✅ **Thumbnails intercambian correctamente** en detalle de producto  
✅ **Sin errores de parsing** de Thymeleaf  
✅ **Sin errores de linting**  

---

**Última actualización:** 27 de enero de 2025 - 14:30  
**Autor:** Asistente IA (Claude Sonnet 4.5)  
**Estado del Trabajo:** ✅ COMPLETADO Y FUNCIONAL

