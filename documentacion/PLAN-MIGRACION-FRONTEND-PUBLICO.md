# Plan de Migración del Frontend Público - Detodoya.com

## 📋 Resumen Ejecutivo

Este documento detalla el plan para migrar el estilo visual de la parte pública del sitio web (index, catálogo y detalle de producto) desde el diseño actual basado en Bootstrap/CSS personalizado hacia el nuevo diseño moderno con Tailwind CSS.

**Fecha de creación:** 2025-01-XX  
**Estado:** En planificación  
**Prioridad:** Alta

---

## 🎯 Objetivos

1. **Migrar el diseño visual** de las 3 páginas públicas principales:
   - `index.html` (Página de inicio)
   - `catalog.html` (Catálogo de productos)
   - `product-detail.html` (Detalle de producto)

2. **Mantener toda la funcionalidad existente** del backend:
   - Integración con controladores Spring Boot
   - Sistema de colores propio (entidad `Color`)
   - Vista dinámica según `TipoProducto` y categorías
   - Sistema de imágenes y videos

3. **Identificar y documentar funcionalidades nuevas** que requieren implementación:
   - Botón "Me gusta" / Favoritos
   - Carrito de compras
   - Sistema de reseñas/calificaciones

---

## 📊 Análisis Comparativo

### Estilos Actuales vs Nuevos

| Aspecto | Actual | Nuevo |
|---------|--------|-------|
| **Framework CSS** | Bootstrap 5.3.0 + CSS personalizado | Tailwind CSS (CDN) |
| **Iconos** | Bootstrap Icons | Material Symbols Outlined |
| **Fuentes** | Inter (Google Fonts) | Inter (Google Fonts) ✅ |
| **Diseño** | Lovely Denim replica | Moderno, limpio, minimalista |
| **Responsive** | Media queries personalizadas | Tailwind responsive utilities |
| **Tema** | Solo claro | Soporte claro/oscuro (dark mode) |

### Estructura de Archivos

**Archivos actuales:**
- `src/main/resources/templates/index.html`
- `src/main/resources/templates/catalog.html`
- `src/main/resources/templates/product-detail.html`

**Archivos nuevos (referencia):**
- `src/main/resources/templates/nueva vista/index-prueba.html`
- `src/main/resources/templates/nueva vista/catalog-prueba.html`
- `src/main/resources/templates/nueva vista/product-detail-prueba.html`

---

## 🔍 Análisis de Funcionalidades

### ✅ Funcionalidades Existentes (Mantener)

1. **Sistema de Productos:**
   - Listado de productos destacados (`esNuevo = true`)
   - Filtrado por categoría
   - Búsqueda de productos
   - Detalle de producto con galería de imágenes/videos

2. **Sistema de Colores:**
   - Entidad `Color` con `hexCode` e `imagePath`
   - Relación Many-to-Many con productos (`product_colors`)
   - Colores predeterminados y personalizados
   - **Integración necesaria:** Mostrar colores disponibles en detalle de producto

3. **Sistema de Categorías:**
   - Carrusel de categorías en inicio
   - Filtrado por categoría en catálogo
   - Navegación por categorías

4. **Sistema de Imágenes:**
   - Imagen principal del producto
   - Galería de imágenes secundarias
   - Soporte para videos (historias, imágenes de producto)
   - Miniaturas en detalle de producto

5. **Vista Dinámica por Tipo de Producto:**
   - `TipoProducto` enum determina qué campos mostrar
   - Campos específicos según categoría:
     - **INDUMENTARIA:** Talles, Géneros, Temporadas, Colores
     - **LIBROS:** ISBN, Autor, Editorial, Páginas
     - **ELECTRONICA:** Potencia, Consumo, Dimensiones, Peso
     - **HOGAR:** Dimensiones, Peso, Material
   - **Integración necesaria:** Implementar lógica condicional en `product-detail.html`

### ❌ Funcionalidades Nuevas (No Implementadas)

1. **Sistema de Favoritos / "Me gusta":**
   - **Estado actual:** Botón presente en nuevos HTML pero sin funcionalidad
   - **Requisitos:**
     - Entidad `Favorite` o `Wishlist` (usuario, producto)
     - Endpoint API: `POST /api/favorites/{productId}`, `DELETE /api/favorites/{productId}`
     - Persistencia en base de datos
     - Autenticación de usuario (¿requerida o con cookies/session?)
   - **Decisión pendiente:** ¿Implementar ahora o dejar botón deshabilitado?

2. **Carrito de Compras:**
   - **Estado actual:** Botón presente en nuevos HTML pero sin funcionalidad
   - **Requisitos:**
     - Entidad `Cart` y `CartItem`
     - Endpoints API para agregar/eliminar/actualizar items
     - Persistencia (¿session, cookies, o base de datos?)
     - Vista de carrito
     - Proceso de checkout (futuro)
   - **Decisión pendiente:** ¿Implementar ahora o dejar botón deshabilitado?

3. **Sistema de Reseñas/Calificaciones:**
   - **Estado actual:** Sección presente en `product-detail-prueba.html` pero sin datos reales
   - **Requisitos:**
     - Entidad `Review` (usuario, producto, rating, comentario, fecha)
     - Endpoints API para crear/listar reseñas
     - Cálculo de promedio de calificaciones
     - Validación (solo usuarios autenticados, una reseña por usuario/producto)
   - **Decisión pendiente:** ¿Implementar ahora o mostrar placeholder?

---

## 🛠️ Plan de Migración por Página

### 1. Migración de `index.html`

#### Cambios Principales:
- ✅ Reemplazar Bootstrap por Tailwind CSS
- ✅ Actualizar header con nuevo diseño
- ✅ Migrar carrusel de categorías
- ✅ Migrar grid de productos destacados
- ✅ Mantener integración con `PublicController.home()`
- ✅ Mantener lógica de historias (si aplica)

#### Integraciones Backend Necesarias:
```java
// Ya existe en PublicController.java
@GetMapping("/")
public String home(Model model) {
    model.addAttribute("products", productRepository.findByEsNuevoTrueAndActivoTrue());
    model.addAttribute("categories", categoryService.getCategoriesWithProducts());
    model.addAttribute("carouselCategories", categoryService.findReadyForCarousel());
    return "index";
}
```

#### Elementos a Migrar:
0. **Sección de Historias (MANTENER - Solo móvil):**
   - **IMPORTANTE:** Mantener funcionalidad existente
   - Sección visible solo en móvil (`display: none` por defecto, activa con media query)
   - Video desde `historiaPrincipal.videoPath` (método `historiaService.findActivaPrincipal()`)
   - Video con autoplay, muted, loop, playsinline
   - Contenedor con altura 60vh
   - **Cambio visual:** Adaptar estilos con Tailwind pero mantener lógica condicional

1. **Header:**
   - Logo "detodoya.com" ✅ (ya actualizado)
   - Navegación (Inicio, Catálogo, Categorías)
   - Botones de búsqueda, redes sociales, admin
   - **Nuevo:** Botón de favoritos (deshabilitado por ahora)
   - **Nuevo:** Botón de carrito (deshabilitado por ahora)

2. **Hero Section:**
   - Banner principal con imagen de fondo
   - Texto promocional
   - Botones CTA (Ver Catálogo, Buscar Producto)

3. **Carrusel de Categorías (MANTENER FUNCIONALIDAD ACTUAL):**
   - **IMPORTANTE:** Mantener Swiper.js para el carrusel (ya implementado y funcionando)
   - Grid horizontal con Swiper
   - Imágenes de categorías desde `carouselCategories` (método `categoryService.findReadyForCarousel()`)
   - Enlaces a `/catalog?category={name}` con función `goToCategory()`
   - Navegación con flechas (prev/next)
   - Responsive: 2 slides móvil, 3 tablet, 4 desktop
   - **Nota:** Verificar proporciones de imágenes según `visualizacion.md`
   - **Cambio visual:** Adaptar estilos de Tailwind pero mantener estructura Swiper

4. **Productos Destacados:**
   - Grid responsive (1 col móvil, 2 tablet, 4 desktop)
   - Tarjetas de producto con:
     - Imagen principal (con soporte para video)
     - Nombre del producto
     - Precio (con descuento si aplica)
     - Badge de estado (Nuevo, Oferta, Agotado)
     - Botón "Me gusta" (deshabilitado por ahora)
     - Botón "Ver Detalles" (hover)
   - Enlace a `/product/{pId}`

5. **Footer:**
   - Información de contacto
   - Enlaces rápidos
   - Redes sociales
   - Newsletter (formulario - funcionalidad futura)

#### Consideraciones Especiales:
- **Historias móviles:** Mantener sección completa con lógica condicional `th:if="${historiaPrincipal != null}"`
- **Carrusel de categorías:** 
  - **MANTENER Swiper.js** (no reemplazar por scroll simple)
  - Incluir CDN de Swiper: `https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css` y `.min.js`
  - Mantener función `initCarousel()` y event listeners
  - Adaptar estilos visuales con Tailwind pero mantener estructura Swiper
- **Imágenes de productos:** Mantener soporte para videos (`isVideo` flag)
- **Proporciones:** Seguir guía de `visualizacion.md`:
  - Productos: Vertical (2:3 o 3:4), ~600x900px o 800x1200px
  - Categorías: Cuadradas o 4:3, ~300x280px a 800x600px

---

### 2. Migración de `catalog.html`

#### Cambios Principales:
- ✅ Reemplazar Bootstrap por Tailwind CSS
- ✅ Actualizar header (mismo que index)
- ✅ Migrar barra de filtros y búsqueda
- ✅ Migrar grid de productos
- ✅ Mantener integración con `PublicController.catalog()`
- ✅ Implementar paginación (si aplica)

#### Integraciones Backend Necesarias:
```java
// Ya existe en PublicController.java
@GetMapping("/catalog")
public String catalog(
    @RequestParam(required = false) String category,
    @RequestParam(required = false) String search,
    Model model) {
    // Lógica de filtrado existente
    model.addAttribute("products", products);
    model.addAttribute("categories", categories);
    model.addAttribute("selectedCategory", category);
    model.addAttribute("search", search);
    return "catalog";
}
```

#### Elementos a Migrar:
1. **Barra de Filtros:**
   - Búsqueda por texto
   - Dropdown de categorías (populado desde backend)
   - Filtro de precio (rango)
   - Ordenamiento (Relevancia, Precio, Nuevos)
   - Vista de grid/lista (toggle)
   - Chips de filtros activos

2. **Grid de Productos:**
   - Mismo diseño que productos destacados en index
   - Paginación (si hay muchos productos)
   - Contador de resultados ("Mostrando X de Y productos")

3. **Breadcrumbs:**
   - Inicio > Catálogo > [Categoría] (si aplica)

#### Consideraciones Especiales:
- **Filtrado:** Mantener funcionalidad actual (categoría, búsqueda)
- **Paginación:** Evaluar si implementar ahora o en fase 2
- **Ordenamiento:** Implementar lógica en backend si no existe

---

### 3. Migración de `product-detail.html`

#### Cambios Principales:
- ✅ Reemplazar Bootstrap por Tailwind CSS
- ✅ Actualizar header (mismo que index)
- ✅ Migrar galería de imágenes/videos
- ✅ Migrar información del producto
- ✅ **Implementar vista dinámica según `TipoProducto`**
- ✅ Mantener integración con `PublicController.productDetail()`

#### Integraciones Backend Necesarias:
```java
// Ya existe en PublicController.java
@GetMapping("/product/{id}")
public String productDetail(@PathVariable Integer id, Model model) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    model.addAttribute("product", product);
    return "product-detail";
}
```

#### Elementos a Migrar:
1. **Galería de Imágenes:**
   - Imagen principal grande (con zoom si es posible)
   - Miniaturas horizontales (scroll)
   - Soporte para videos (thumbnail con icono de play)
   - Botón "Me gusta" en imagen principal

2. **Información del Producto:**
   - Nombre del producto
   - Categoría/Subcategoría
   - Precio (con descuento si aplica)
   - Estado de stock
   - Calificación promedio (si hay reseñas)
   - Descripción corta

3. **Selección de Variantes (Condicional):**
   - **Si `TipoProducto == INDUMENTARIA`:**
     - Selector de colores (usando `product.colores` y `Color.hexCode`)
     - Selector de talles (usando `product.talles` enum)
     - Selector de género (usando `product.generos` enum)
     - Selector de temporada (usando `product.temporadas` enum)
   - **Si `TipoProducto == LIBROS`:**
     - Mostrar: ISBN, Autor, Editorial, Páginas
     - No mostrar: Colores, Talles, Géneros, Temporadas
   - **Si `TipoProducto == ELECTRONICA`:**
     - Mostrar: Potencia, Consumo, Dimensiones, Peso
     - Mostrar colores si aplica (ej: auriculares)
   - **Si `TipoProducto == OTROS`:**
     - Mostrar solo campos universales

4. **Botones de Acción:**
   - "Consultar WhatsApp" (ya existe, mantener)
   - "Enviar Consulta" (ya existe, mantener)
   - **Nuevo:** "Agregar al Carrito" (deshabilitado por ahora)
   - **Nuevo:** "Me gusta" (deshabilitado por ahora)

5. **Indicadores de Confianza:**
   - Envío gratis
   - Garantía
   - Pagos seguros
   - Soporte técnico

6. **Tabs de Información:**
   - Descripción (usando `product.description`)
   - Especificaciones (campos según `TipoProducto`)
   - Cuidados/Instrucciones (si aplica)
   - Reseñas (placeholder por ahora)

7. **Productos Relacionados:**
   - Carrusel horizontal
   - Productos de la misma categoría
   - **Integración:** Endpoint para productos relacionados (futuro)

#### Lógica Condicional por TipoProducto:

```thymeleaf
<!-- En product-detail.html -->
<div th:if="${product.tipoProducto == T(com.detodoya.enums.TipoProducto).INDUMENTARIA}">
    <!-- Mostrar selectores de colores, talles, géneros, temporadas -->
    <div th:each="color : ${product.colores}">
        <button style="background-color: ${color.hexCode}"></button>
    </div>
</div>

<div th:if="${product.tipoProducto == T(com.detodoya.enums.TipoProducto).LIBROS}">
    <!-- Mostrar ISBN, Autor, Editorial, Páginas -->
    <p>ISBN: <span th:text="${product.isbn}"></span></p>
    <p>Autor: <span th:text="${product.autor}"></span></p>
    <p>Editorial: <span th:text="${product.editorial}"></span></p>
    <p>Páginas: <span th:text="${product.paginas}"></span></p>
</div>

<div th:if="${product.tipoProducto == T(com.detodoya.enums.TipoProducto).ELECTRONICA}">
    <!-- Mostrar Potencia, Consumo, Dimensiones, Peso -->
    <p>Potencia: <span th:text="${product.potencia}"></span></p>
    <p>Consumo: <span th:text="${product.consumo}"></span></p>
    <p>Dimensiones: <span th:text="${product.dimensiones}"></span></p>
    <p>Peso: <span th:text="${product.peso}"></span></p>
</div>
```

#### Consideraciones Especiales:
- **Colores:** Usar `Color.hexCode` para mostrar círculos de color, o `Color.imagePath` para patrones
- **Videos:** Detectar `Image.isVideo` y mostrar reproductor en lugar de imagen
- **Proporciones:** Seguir guía de `visualizacion.md`:
  - Imagen principal: Variable, hasta 1000x1000px o 1200x800px
  - Miniaturas: Vertical (2:3 o 3:4), ~120x188px

---

## 🔧 Integraciones Backend Necesarias

### 1. Sistema de Colores en Detalle de Producto

**Estado actual:** Los productos tienen relación Many-to-Many con `Color` a través de `product_colors`.

**Necesario:**
- Asegurar que `PublicController.productDetail()` cargue `product.colores` (usar `FetchType.EAGER` o `@EntityGraph`)
- Pasar lista de colores al template para mostrar selectores

**Código sugerido:**
```java
@GetMapping("/product/{id}")
public String productDetail(@PathVariable Integer id, Model model) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    
    // Cargar colores si es necesario
    if (product.getColores() != null) {
        product.getColores().size(); // Force lazy loading
    }
    
    model.addAttribute("product", product);
    return "product-detail";
}
```

### 2. Vista Dinámica por TipoProducto

**Estado actual:** `Product` tiene campo `tipoProducto` de tipo `TipoProducto` enum.

**Necesario:**
- Verificar que `tipoProducto` se carga correctamente
- Implementar lógica condicional en Thymeleaf (ver ejemplo arriba)
- Asegurar que campos específicos (ISBN, autor, etc.) se muestran solo cuando aplica

### 3. Productos Relacionados

**Estado actual:** No existe endpoint para productos relacionados.

**Necesario (Fase 2):**
- Crear método en `ProductRepository`:
  ```java
  @Query("SELECT p FROM Product p WHERE p.categories IN :categories AND p.pId != :excludeId AND p.activo = true LIMIT 4")
  List<Product> findRelatedProducts(@Param("categories") List<Category> categories, @Param("excludeId") Integer excludeId);
  ```
- Agregar a `PublicController.productDetail()`:
  ```java
  List<Product> relatedProducts = productRepository.findRelatedProducts(product.getCategories(), product.getPId());
  model.addAttribute("relatedProducts", relatedProducts);
  ```

---

## 🚫 Funcionalidades No Implementadas - Decisiones

### Opción 1: Dejar Botones Deshabilitados (Recomendado para Fase 1)

**Ventajas:**
- Permite migrar diseño sin bloquear
- Usuario ve la UI completa
- Se puede implementar funcionalidad después

**Desventajas:**
- Botones no funcionales pueden confundir
- Requiere indicar visualmente que están deshabilitados

**Implementación:**
```html
<button class="..." disabled title="Próximamente">
    <span class="material-symbols-outlined">favorite</span>
</button>
```

### Opción 2: Ocultar Botones Temporalmente

**Ventajas:**
- UI más limpia
- No confunde al usuario

**Desventajas:**
- Diseño incompleto
- Requiere cambios cuando se implemente

### Opción 3: Implementar Funcionalidad Básica (Fase 2)

**Requisitos:**
- Sistema de autenticación (¿requerido?)
- Entidades y repositorios
- Endpoints API REST
- Frontend JavaScript para llamadas AJAX

**Estimación:** 2-3 días de desarrollo por funcionalidad

---

## 📝 Checklist de Migración

### Fase 1: Preparación
- [ ] Revisar y entender nuevos HTML de referencia
- [ ] Documentar diferencias entre actual y nuevo
- [ ] Identificar todas las integraciones backend necesarias
- [ ] Crear branch de desarrollo: `feature/migracion-frontend-publico`

### Fase 2: Migración de `index.html`
- [ ] Reemplazar Bootstrap por Tailwind CSS
- [ ] Migrar header
- [ ] **MANTENER sección de historias móviles** (adaptar estilos, mantener lógica)
- [ ] Migrar hero section
- [ ] **MANTENER carrusel de categorías con Swiper.js** (adaptar estilos visuales, mantener funcionalidad)
- [ ] Migrar grid de productos destacados
- [ ] Migrar footer
- [ ] Verificar que Swiper.js se carga correctamente
- [ ] Verificar que historias se muestran solo en móvil
- [ ] Probar integración con backend (`carouselCategories`, `historiaPrincipal`)
- [ ] Verificar responsive (móvil, tablet, desktop)
- [ ] Probar con datos reales

### Fase 3: Migración de `catalog.html`
- [ ] Reemplazar Bootstrap por Tailwind CSS
- [ ] Migrar header (reutilizar de index)
- [ ] Migrar barra de filtros
- [ ] Migrar grid de productos
- [ ] Implementar paginación (si aplica)
- [ ] Probar filtrado por categoría
- [ ] Probar búsqueda
- [ ] Verificar responsive

### Fase 4: Migración de `product-detail.html`
- [ ] Reemplazar Bootstrap por Tailwind CSS
- [ ] Migrar header (reutilizar de index)
- [ ] Migrar galería de imágenes/videos
- [ ] Migrar información del producto
- [ ] **Implementar lógica condicional por TipoProducto**
- [ ] Implementar selector de colores (usando `Color.hexCode`)
- [ ] Implementar selector de talles/géneros/temporadas (solo indumentaria)
- [ ] Implementar tabs (Descripción, Especificaciones, Reseñas)
- [ ] Migrar productos relacionados (placeholder por ahora)
- [ ] Probar con diferentes tipos de productos
- [ ] Verificar responsive

### Fase 5: Ajustes y Optimizaciones
- [ ] Optimizar carga de imágenes (lazy loading)
- [ ] Verificar rendimiento
- [ ] Ajustar estilos según feedback
- [ ] Probar en diferentes navegadores
- [ ] Verificar accesibilidad básica

### Fase 6: Funcionalidades Futuras (Opcional)
- [ ] Implementar sistema de favoritos
- [ ] Implementar carrito de compras
- [ ] Implementar sistema de reseñas
- [ ] Implementar productos relacionados (backend + frontend)

---

## 🎨 Consideraciones de Diseño

### Proporciones de Imágenes (según `visualizacion.md`)

1. **Categorías (Carrusel):**
   - Proporción: 4:3 (cuadradas o ligeramente horizontales)
   - Tamaño: 300x280px (miniaturas) hasta 800x600px

2. **Productos (Grid):**
   - Proporción: Vertical (2:3 o 3:4)
   - Tamaño: 600x900px o 800x1200px
   - Altura considerable para llenar tarjeta

3. **Imagen Principal (Detalle):**
   - Proporción: Variable (1:1, 2:3, 3:4, 4:3)
   - Tamaño: Hasta 1000x1000px o 1200x800px
   - Alta resolución para zoom

4. **Miniaturas (Detalle):**
   - Proporción: Vertical (2:3 o 3:4)
   - Tamaño: ~120x188px

### Paleta de Colores (Tailwind Config)

Los nuevos HTML usan:
- Primary: `#137fec`
- Primary Hover: `#0f6bc9`
- Background Light: `#ffffff`
- Background Off: `#f9fafb`
- Text Main: `#111418`
- Text Secondary: `#637588`

**Asegurar consistencia** en todos los templates migrados.

---

## 🔄 Funcionalidades Específicas a Mantener

### 1. Carrusel de Categorías con Swiper.js

**Estado actual:**
- Usa Swiper.js v11 (CDN)
- Método backend: `categoryService.findReadyForCarousel()`
- Variable en modelo: `carouselCategories`
- Función JavaScript: `initCarousel()` con configuración responsive
- Navegación: Flechas prev/next con IDs `#prevBtn` y `#nextBtn`

**Estrategia de migración:**
- **MANTENER** toda la estructura Swiper
- **ADAPTAR** estilos visuales con Tailwind (colores, bordes, sombras)
- **MANTENER** JavaScript existente
- **MANTENER** CDN de Swiper.js

**Código a preservar:**
```html
<!-- Swiper JS -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
<script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>

<!-- Estructura Swiper -->
<div class="swiper" id="categoriesSwiper">
    <div class="swiper-wrapper">
        <div th:each="cat : ${carouselCategories}" class="swiper-slide">
            <!-- Contenido de categoría -->
        </div>
    </div>
</div>
<button class="carousel-nav prev" id="prevBtn">‹</button>
<button class="carousel-nav next" id="nextBtn">›</button>
```

### 2. Historias Móviles

**Estado actual:**
- Sección visible solo en móvil (media query)
- Método backend: `historiaService.findActivaPrincipal()`
- Variable en modelo: `historiaPrincipal`
- Video con autoplay, muted, loop, playsinline
- Altura: 60vh

**Estrategia de migración:**
- **MANTENER** lógica condicional `th:if="${historiaPrincipal != null}"`
- **MANTENER** estructura de video
- **ADAPTAR** estilos con Tailwind (clases responsive)
- **MANTENER** media query para mostrar solo en móvil

**Código a preservar:**
```html
<!-- Sección de Historias - Solo móvil -->
<section class="lovely-stories md:hidden" th:if="${historiaPrincipal != null}">
    <div class="lovely-story-container">
        <video th:src="@{/uploads/{videoPath}(videoPath=${historiaPrincipal.videoPath})}" 
               class="lovely-story-video"
               autoplay muted loop playsinline>
            Tu navegador no soporta videos.
        </video>
    </div>
</section>
```

**CSS a adaptar:**
```css
/* Actual */
.lovely-stories {
    display: none; /* Se activa con media query */
}

@media (max-width: 768px) {
    .lovely-stories {
        display: block;
    }
}

/* Nuevo con Tailwind */
/* Usar clase: hidden md:hidden (o similar según diseño) */
```

---

## 🐛 Posibles Problemas y Soluciones

### Problema 1: Tailwind CSS no se carga correctamente
**Solución:** Verificar que el CDN de Tailwind esté accesible, considerar usar versión local si hay problemas de red.

### Problema 2: Swiper.js no funciona después de migración
**Solución:** 
- Verificar que el CDN de Swiper se carga después de Tailwind
- Asegurar que `initCarousel()` se ejecuta después de que el DOM esté listo
- Verificar que los IDs `#categoriesSwiper`, `#prevBtn`, `#nextBtn` existen en el HTML
- Considerar usar `Swiper` en lugar de `new Swiper` si hay conflictos de nombres

### Problema 2: Material Symbols no se muestran
**Solución:** Verificar que el link de Google Fonts esté correcto, considerar fallback a Bootstrap Icons si es necesario.

### Problema 3: Imágenes no se cargan
**Solución:** Verificar rutas relativas vs absolutas, asegurar que `/uploads/` esté configurado correctamente.

### Problema 4: Colores no se muestran correctamente
**Solución:** Verificar que `Color.hexCode` tenga formato `#RRGGBB`, manejar casos donde `hexCode` es null (usar `getHexCodeOrDefault()`).

### Problema 5: Vista condicional por TipoProducto no funciona
**Solución:** Verificar que `product.tipoProducto` no sea null, agregar validación en Thymeleaf, considerar fallback a vista genérica.

---

## 📚 Referencias

- **Nuevos HTML:** `src/main/resources/templates/nueva vista/`
- **Guía de visualización:** `src/main/resources/templates/nueva vista/visualizacion.md`
- **Controlador público:** `src/main/java/com/detodoya/controller/PublicController.java`
- **Entidad Product:** `src/main/java/com/detodoya/entity/Product.java`
- **Entidad Color:** `src/main/java/com/detodoya/entity/Color.java`
- **Enum TipoProducto:** `src/main/java/com/detodoya/enums/TipoProducto.java`

---

## ✅ Próximos Pasos

1. **Revisar este plan** con el equipo
2. **Decidir sobre funcionalidades no implementadas** (favoritos, carrito, reseñas)
3. **Crear branch de desarrollo**
4. **Comenzar con migración de `index.html`** (más simple, permite validar enfoque)
5. **Iterar y ajustar** según feedback

---

**Última actualización:** 2025-01-XX  
**Autor:** AI Assistant  
**Revisado por:** [Pendiente]

