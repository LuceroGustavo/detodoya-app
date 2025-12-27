# Plan de Modificación V2 - Sistema Flexible de Productos

**Fecha:** Enero 2025  
**Proyecto:** Detodoya.com  
**Objetivo:** Crear un catálogo profesional (showcase) flexible que soporte indumentaria Y otros tipos de productos

---

## 🎯 Objetivo del Proyecto

**Detodoya.com** es un **catálogo profesional** donde vendedores de marketplace (Facebook, MercadoLibre, etc.) pueden crear **páginas de producto completas y profesionales** con:

- ✅ Descripciones detalladas y profesionales
- ✅ Múltiples imágenes y videos de alta calidad
- ✅ Especificaciones técnicas completas
- ✅ Información organizada y estructurada

Los vendedores luego **linkean desde sus publicaciones** en Facebook Marketplace u otros canales hacia Detodoya, donde tienen una presentación mucho más profesional que la limitada de las plataformas de venta.

**No es un sistema de venta directa**, es un **showcase profesional** que complementa las ventas en otras plataformas.

---

## 📋 Resumen Ejecutivo

En lugar de eliminar campos específicos de indumentaria, vamos a **mantener todos los campos** y hacer el sistema **flexible y parametrizable** para que muestre campos diferentes según el tipo de producto o categoría seleccionada.

---

## 🎯 Filosofía del Nuevo Enfoque

### **Principio: "Campos Opcionales y Condicionales"**

- ✅ **Mantener TODOS los campos** (talles, géneros, temporadas, etc.)
- ✅ **Hacer campos opcionales** (nullable) para productos que no los necesiten
- ✅ **Mostrar campos condicionalmente** según el tipo de producto/categoría
- ✅ **Sistema flexible** que se adapta al tipo de producto

---

## 🔧 Propuesta de Implementación

### **Opción 1: Sistema de Tipos de Producto (Recomendado)**

Crear un enum o campo que identifique el "tipo" de producto:

```java
public enum TipoProducto {
    INDUMENTARIA("Indumentaria"),
    ELECTRONICA("Electrónica"),
    HOGAR("Hogar y Muebles"),
    DEPORTES("Deportes"),
    JUGUETES("Juguetes"),
    OTROS("Otros");
    
    private final String displayName;
}
```

**Ventajas:**
- Control explícito sobre qué campos mostrar
- Fácil de extender con nuevos tipos
- Lógica clara en el código

**Desventajas:**
- Requiere agregar un campo nuevo a Product
- Necesita mantenimiento cuando se agregan tipos

---

### **Opción 2: Basado en Categorías (Más Flexible)**

Usar las categorías existentes para determinar qué campos mostrar:

```java
// En Category.java agregar:
@Column(name = "tipo_producto")
private String tipoProducto; // "indumentaria", "electronica", "hogar", etc.

// O usar un campo booleano:
@Column(name = "es_indumentaria")
private Boolean esIndumentaria = false;
```

**Ventajas:**
- No requiere campo adicional en Product
- Más flexible (una categoría puede tener su propio conjunto de campos)
- Reutiliza la estructura existente

**Desventajas:**
- Lógica más compleja (verificar categorías del producto)
- Puede haber productos con múltiples categorías

---

### **Opción 3: Híbrida (Recomendada para este caso)**

Combinar ambas: usar categorías pero con un campo opcional en Product para override:

```java
// En Product.java:
@Column(name = "tipo_producto")
@Enumerated(EnumType.STRING)
private TipoProducto tipoProducto; // Opcional, si es null se infiere de categorías

// En Category.java:
@Column(name = "tipo_producto_default")
private String tipoProductoDefault; // Tipo por defecto para esta categoría
```

**Lógica:**
1. Si `product.tipoProducto` está definido → usar ese
2. Si no, verificar categorías del producto → usar el tipo de la categoría principal
3. Si ninguna categoría tiene tipo → mostrar campos genéricos

---

## 📊 Análisis de Campos por Tipo de Producto

### **Campos Universales (Todos los productos)**
- ✅ `name`, `price`, `descripcion`, `qty`
- ✅ `categories`, `colores`, `images`
- ✅ `esDestacado`, `esNuevo`, `activo`
- ✅ `descuentoPorcentaje`, `precioOriginal`
- ✅ `fechaCreacion`, `fechaActualizacion`

### **Campos para INDUMENTARIA**
- ✅ `talles` (List<Talle>)
- ✅ `generos` (List<Genero>)
- ✅ `temporadas` (List<Temporada>)
- ✅ `material`
- ✅ `cuidados`
- ✅ `medidas` (dimensiones de la prenda)
- ✅ `tallasDisponibles` (texto libre adicional)

### **Campos para ELECTRÓNICA**
- ✅ `especificaciones` (renombrado de `medidas` o campo adicional)
- ✅ `marca` (nuevo campo)
- ✅ `modelo` (nuevo campo)
- ✅ `garantia` (nuevo campo)
- ❌ No necesita: talles, géneros, temporadas

### **Campos para HOGAR/MUEBLES**
- ✅ `especificaciones` (dimensiones)
- ✅ `material`
- ✅ `color` (ya existe)
- ✅ `cuidados` (instrucciones de mantenimiento)
- ❌ No necesita: talles, géneros, temporadas

### **Campos para JUGUETES**
- ✅ `edadRecomendada` (ya existe)
- ✅ `especificaciones`
- ✅ `material`
- ❌ No necesita: talles, géneros, temporadas

### **Campos para TODOS los productos (Nuevos - Integración Marketplace)**
- ✅ `codigoProducto` - Código SKU o referencia del vendedor
- ✅ `linkVenta` - Link a Facebook Marketplace, MercadoLibre, WhatsApp, etc.
- ✅ `contactoVendedor` - WhatsApp, email, teléfono del vendedor
- ✅ `ubicacion` - Ubicación del vendedor o del producto

---

## 🔧 Modificaciones Propuestas

### **1. Entidad Product - Mantener Todo, Hacer Opcional**

```java
@Entity
public class Product {
    // ... campos universales (sin cambios) ...
    
    // Campos de INDUMENTARIA (opcionales, nullable)
    @ElementCollection(targetClass = Talle.class, fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "product_talles", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "talle")
    private List<Talle> talles = new ArrayList<>(); // ✅ MANTENER, nullable
    
    @ElementCollection(targetClass = Genero.class, fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "product_generos", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "genero")
    private List<Genero> generos = new ArrayList<>(); // ✅ MANTENER, nullable
    
    @ElementCollection(targetClass = Temporada.class, fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "product_temporadas", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "temporada")
    private List<Temporada> temporadas = new ArrayList<>(); // ✅ MANTENER, nullable
    
    // Campos flexibles (pueden usarse para cualquier tipo)
    @Column(name = "medidas", nullable = true) // ✅ MANTENER, hacer nullable
    private String medidas; // Para indumentaria: medidas de la prenda
    
    @Column(name = "especificaciones", nullable = true) // ✅ NUEVO campo
    private String especificaciones; // Para electrónica/hogar: specs técnicas
    
    @Column(name = "material", nullable = true) // ✅ MANTENER
    private String material;
    
    @Column(name = "cuidados", nullable = true) // ✅ MANTENER
    private String cuidados;
    
    // Nuevos campos genéricos
    @Column(name = "marca", nullable = true) // ✅ NUEVO
    private String marca;
    
    @Column(name = "modelo", nullable = true) // ✅ NUEVO
    private String modelo;
    
    @Column(name = "garantia", nullable = true) // ✅ NUEVO
    private String garantia;
    
    // Campo para tipo de producto (opcional)
    @Column(name = "tipo_producto")
    @Enumerated(EnumType.STRING)
    private TipoProducto tipoProducto; // ✅ NUEVO, nullable
}
```

---

### **2. Nuevo Enum: TipoProducto**

```java
package com.detodoya.enums;

public enum TipoProducto {
    INDUMENTARIA("Indumentaria"),
    ELECTRONICA("Electrónica"),
    HOGAR("Hogar y Muebles"),
    DEPORTES("Deportes y Fitness"),
    JUGUETES("Juguetes"),
    LIBROS("Libros"),
    BELLEZA("Belleza y Cuidado Personal"),
    AUTOMOTOR("Automotor"),
    OTROS("Otros");
    
    private final String displayName;
    
    TipoProducto(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    // Método para determinar si muestra campos de indumentaria
    public boolean requiereCamposIndumentaria() {
        return this == INDUMENTARIA;
    }
}
```

---

### **3. Entidad Category - Agregar Tipo**

```java
@Entity
public class Category {
    // ... campos existentes ...
    
    @Column(name = "tipo_producto_default")
    @Enumerated(EnumType.STRING)
    private TipoProducto tipoProductoDefault; // ✅ NUEVO, nullable
    
    // Método helper
    public TipoProducto getTipoProducto() {
        return tipoProductoDefault != null ? tipoProductoDefault : TipoProducto.OTROS;
    }
}
```

---

### **4. ProductController - Lógica Condicional**

```java
@GetMapping("/new")
public String showForm(Model model) {
    Product product = new Product();
    model.addAttribute("product", product);
    model.addAttribute("categories", categoryService.getActiveCategories());
    model.addAttribute("colors", colorService.getAllColors());
    model.addAttribute("tiposProducto", TipoProducto.values());
    
    // Cargar enums solo si es necesario (se cargarán dinámicamente con JS)
    model.addAttribute("talles", Talle.values());
    model.addAttribute("generos", Genero.values());
    model.addAttribute("temporadas", Temporada.values());
    
    return "admin/product-form";
}
```

---

### **5. Template HTML - Formulario Dinámico**

El formulario mostrará campos diferentes según el tipo de producto seleccionado:

```html
<!-- Selector de tipo de producto -->
<div class="mb-3">
    <label>Tipo de Producto</label>
    <select id="tipoProducto" name="tipoProducto" onchange="mostrarCamposSegunTipo()">
        <option value="">Seleccionar tipo...</option>
        <option th:each="tipo : ${tiposProducto}" 
                th:value="${tipo}" 
                th:text="${tipo.displayName}"></option>
    </select>
</div>

<!-- Campos de INDUMENTARIA (ocultos por defecto) -->
<div id="camposIndumentaria" class="campos-tipo" style="display: none;">
    <div class="mb-3">
        <label>Talles</label>
        <!-- Select múltiple de talles -->
    </div>
    <div class="mb-3">
        <label>Géneros</label>
        <!-- Select múltiple de géneros -->
    </div>
    <div class="mb-3">
        <label>Temporadas</label>
        <!-- Select múltiple de temporadas -->
    </div>
    <div class="mb-3">
        <label>Medidas</label>
        <input type="text" name="medidas" placeholder="Ej: Pecho 100cm, Cintura 80cm">
    </div>
</div>

<!-- Campos de ELECTRÓNICA (ocultos por defecto) -->
<div id="camposElectronica" class="campos-tipo" style="display: none;">
    <div class="mb-3">
        <label>Marca</label>
        <input type="text" name="marca">
    </div>
    <div class="mb-3">
        <label>Modelo</label>
        <input type="text" name="modelo">
    </div>
    <div class="mb-3">
        <label>Especificaciones Técnicas</label>
        <textarea name="especificaciones"></textarea>
    </div>
    <div class="mb-3">
        <label>Garantía</label>
        <input type="text" name="garantia" placeholder="Ej: 12 meses">
    </div>
</div>

<!-- Campos UNIVERSALES (siempre visibles) -->
<div class="mb-3">
    <label>Nombre</label>
    <input type="text" name="name" required>
</div>
<div class="mb-3">
    <label>Precio</label>
    <input type="number" name="price" required>
</div>
<!-- ... otros campos universales ... -->

<!-- Campos de INTEGRACIÓN MARKETPLACE (siempre visibles) -->
<div class="mb-3">
    <label>Código de Producto / SKU</label>
    <input type="text" name="codigoProducto" placeholder="Ej: REM-001, PS4-2024">
    <small class="form-text text-muted">Código de referencia del vendedor</small>
</div>
<div class="mb-3">
    <label>Link de Venta</label>
    <input type="url" name="linkVenta" placeholder="https://www.facebook.com/marketplace/item/...">
    <small class="form-text text-muted">Link a Facebook Marketplace, MercadoLibre, WhatsApp, etc.</small>
</div>
<div class="mb-3">
    <label>Contacto del Vendedor</label>
    <input type="text" name="contactoVendedor" placeholder="Ej: WhatsApp: +54 11 1234-5678">
    <small class="form-text text-muted">WhatsApp, email o teléfono de contacto</small>
</div>
<div class="mb-3">
    <label>Ubicación</label>
    <input type="text" name="ubicacion" placeholder="Ej: Buenos Aires, Capital Federal">
    <small class="form-text text-muted">Ubicación del vendedor o del producto</small>
</div>
```

**JavaScript para mostrar/ocultar campos:**

```javascript
function mostrarCamposSegunTipo() {
    const tipoProducto = document.getElementById('tipoProducto').value;
    
    // Ocultar todos los campos específicos
    document.querySelectorAll('.campos-tipo').forEach(div => {
        div.style.display = 'none';
    });
    
    // Mostrar campos según el tipo
    if (tipoProducto === 'INDUMENTARIA') {
        document.getElementById('camposIndumentaria').style.display = 'block';
    } else if (tipoProducto === 'ELECTRONICA') {
        document.getElementById('camposElectronica').style.display = 'block';
    }
    // ... otros tipos ...
}
```

---

## 📋 Plan de Implementación

### **Fase 1: Crear Enum TipoProducto**
1. Crear `TipoProducto.java` en `enums/`
2. Agregar método `requiereCamposIndumentaria()`

### **Fase 2: Modificar Entidad Product**
1. ✅ Mantener todos los campos existentes
2. Agregar campo `tipoProducto` (opcional)
3. Agregar nuevos campos genéricos: `marca`, `modelo`, `garantia`, `especificaciones`
4. Agregar campos de integración marketplace: `codigoProducto`, `linkVenta`, `contactoVendedor`, `ubicacion`
5. Hacer `medidas` nullable (ya lo es, verificar)
6. Mantener todos los métodos existentes

### **Fase 3: Modificar Entidad Category**
1. Agregar campo `tipoProductoDefault` (opcional)
2. Agregar método helper `getTipoProducto()`

### **Fase 4: Actualizar ProductController**
1. Agregar `TipoProducto` al modelo en `showForm()`
2. Mantener lógica de talles, géneros, temporadas (se usará condicionalmente)
3. Actualizar `addProduct()` y `updateProduct()` para manejar `tipoProducto`

### **Fase 5: Actualizar Templates HTML**
1. Agregar selector de tipo de producto
2. Crear secciones condicionales para cada tipo
3. Implementar JavaScript para mostrar/ocultar campos
4. Mantener todos los campos en el formulario (ocultos por defecto)

### **Fase 6: Actualizar Vista de Detalle**
1. Mostrar campos relevantes según tipo de producto
2. Ocultar campos no aplicables

---

## 🎯 Ventajas de Este Enfoque

✅ **Flexibilidad total** - Soporta cualquier tipo de producto  
✅ **No se pierde funcionalidad** - Todos los campos de indumentaria se mantienen  
✅ **Extensible** - Fácil agregar nuevos tipos de productos  
✅ **Retrocompatible** - Productos existentes siguen funcionando  
✅ **UX mejorada** - El usuario solo ve campos relevantes  

---

## ⚠️ Consideraciones

### **1. Base de Datos**
- Se agregarán nuevas columnas: `tipo_producto`, `marca`, `modelo`, `garantia`, `especificaciones`
- Las tablas de enums (`product_talles`, etc.) se mantienen
- Con `ddl-auto=update`, JPA creará las nuevas columnas automáticamente

### **2. Migración de Datos**
- Productos existentes tendrán `tipoProducto = null`
- Se puede inferir el tipo desde las categorías o dejar como "OTROS"
- Los campos de indumentaria existentes se mantienen intactos

### **3. Validaciones**
- Validar campos requeridos según el tipo de producto
- Por ejemplo: si es INDUMENTARIA, validar que tenga al menos un talle
- Si es ELECTRONICA, validar marca y modelo

---

## 📝 Resumen de Cambios

### **✅ MANTENER (Sin cambios)**
- Todos los campos de indumentaria (talles, géneros, temporadas)
- Todos los enums (Talle, Genero, Temporada)
- Todos los métodos relacionados

### **➕ AGREGAR**
- Enum `TipoProducto`
- Campo `tipoProducto` en Product
- Campo `tipoProductoDefault` en Category
- Campos genéricos: `marca`, `modelo`, `garantia`, `especificaciones`

### **🔄 MODIFICAR**
- Templates HTML para mostrar campos condicionalmente
- JavaScript para lógica de mostrar/ocultar
- Validaciones para ser condicionales según tipo

---

**¿Este enfoque te parece mejor? Mantenemos toda la funcionalidad de indumentaria y agregamos flexibilidad para otros tipos de productos.**

