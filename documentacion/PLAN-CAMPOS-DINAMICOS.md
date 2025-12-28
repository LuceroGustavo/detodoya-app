# Plan de Implementación: Campos Dinámicos por Tipo de Producto

**Fecha:** Enero 2025  
**Proyecto:** Detodoya.com  
**Objetivo:** Implementar sistema de campos dinámicos que se muestren/oculten según el tipo de producto seleccionado

---

## 🎯 Objetivo

Permitir que el formulario de productos muestre solo los campos relevantes según el tipo de producto seleccionado. Por ejemplo:
- **Libros:** Mostrar páginas, peso, ISBN, autor, editorial. Ocultar colores, temporada, talles.
- **Indumentaria:** Mostrar colores, temporada, talles, géneros. Ocultar peso, páginas, ISBN.
- **Electrónica:** Mostrar marca, modelo, garantía, especificaciones técnicas. Ocultar colores, temporada, talles.

---

## 📊 Mapeo de Campos por Tipo de Producto

### **Campos Universales (Siempre Visibles)**
Estos campos se muestran para TODOS los tipos de productos:
- ✅ `name` - Nombre del producto
- ✅ `price` - Precio
- ✅ `descripcion` - Descripción general
- ✅ `qty` - Cantidad disponible
- ✅ `categories` - Categorías
- ✅ `subcategorias` - Subcategorías
- ✅ `images` - Imágenes del producto
- ✅ `videos` - Videos del producto
- ✅ `esDestacado` - Producto destacado
- ✅ `esNuevo` - Producto nuevo
- ✅ `activo` - Estado activo/inactivo
- ✅ `descuentoPorcentaje` - Descuento
- ✅ `precioOriginal` - Precio original
- ✅ `codigoProducto` - Código SKU
- ✅ `linkVenta` - Link a marketplace
- ✅ `contactoVendedor` - Contacto del vendedor
- ✅ `ubicacion` - Ubicación

### **INDUMENTARIA** 👕
**Mostrar:**
- ✅ `colores` - Colores disponibles (Many-to-Many)
- ✅ `talles` - Talles disponibles (enum)
- ✅ `generos` - Géneros (enum)
- ✅ `temporadas` - Temporadas (enum)
- ✅ `medidas` - Medidas de la prenda (ej: "Pecho 100cm, Cintura 80cm")
- ✅ `material` - Material de la prenda
- ✅ `cuidados` - Instrucciones de cuidado
- ✅ `tallasDisponibles` - Texto libre de tallas
- ✅ `coloresDisponibles` - Texto libre de colores

**Ocultar:**
- ❌ `marca` (no aplica para indumentaria genérica)
- ❌ `modelo` (no aplica)
- ❌ `garantia` (no aplica)
- ❌ `especificaciones` (usar `medidas` en su lugar)
- ❌ `peso` (no relevante)
- ❌ `dimensiones` (usar `medidas` en su lugar)
- ❌ `paginas` (no aplica)
- ❌ `isbn` (no aplica)
- ❌ `autor` (no aplica)
- ❌ `editorial` (no aplica)
- ❌ `volumen` (no aplica)
- ❌ `potencia` (no aplica)
- ❌ `consumo` (no aplica)

### **ELECTRÓNICA** 📱
**Mostrar:**
- ✅ `marca` - Marca del producto
- ✅ `modelo` - Modelo del producto
- ✅ `garantia` - Garantía (ej: "12 meses")
- ✅ `especificaciones` - Especificaciones técnicas (TEXT)
- ✅ `peso` - Peso del producto (nuevo campo)
- ✅ `dimensiones` - Dimensiones físicas (nuevo campo, ej: "20x15x5 cm")
- ✅ `potencia` - Potencia (nuevo campo, para electrodomésticos)
- ✅ `consumo` - Consumo energético (nuevo campo, ej: "220V, 50W")

**Ocultar:**
- ❌ `colores` (no relevante para electrónica)
- ❌ `talles` (no aplica)
- ❌ `generos` (no aplica)
- ❌ `temporadas` (no aplica)
- ❌ `medidas` (usar `dimensiones` en su lugar)
- ❌ `material` (no relevante)
- ❌ `cuidados` (no relevante)
- ❌ `paginas` (no aplica)
- ❌ `isbn` (no aplica)
- ❌ `autor` (no aplica)
- ❌ `editorial` (no aplica)

### **LIBROS** 📚
**Mostrar:**
- ✅ `autor` - Autor del libro (nuevo campo)
- ✅ `editorial` - Editorial (nuevo campo)
- ✅ `isbn` - ISBN (nuevo campo)
- ✅ `paginas` - Número de páginas (nuevo campo)
- ✅ `peso` - Peso del libro (nuevo campo)
- ✅ `dimensiones` - Dimensiones del libro (nuevo campo, ej: "23x15x2 cm")
- ✅ `especificaciones` - Sinopsis o descripción adicional

**Ocultar:**
- ❌ `colores` (no relevante)
- ❌ `talles` (no aplica)
- ❌ `generos` (no aplica - confusión con género literario)
- ❌ `temporadas` (no aplica)
- ❌ `medidas` (usar `dimensiones` en su lugar)
- ❌ `material` (no relevante)
- ❌ `cuidados` (no relevante)
- ❌ `marca` (usar `editorial` en su lugar)
- ❌ `modelo` (no aplica)
- ❌ `garantia` (no aplica)
- ❌ `potencia` (no aplica)
- ❌ `consumo` (no aplica)

### **HOGAR Y MUEBLES** 🏠
**Mostrar:**
- ✅ `material` - Material del mueble/producto
- ✅ `dimensiones` - Dimensiones (nuevo campo, ej: "200x80x75 cm")
- ✅ `peso` - Peso del producto (nuevo campo)
- ✅ `especificaciones` - Especificaciones técnicas
- ✅ `cuidados` - Instrucciones de mantenimiento
- ✅ `colores` - Colores disponibles (Many-to-Many) - **SÍ aplica para muebles**

**Ocultar:**
- ❌ `talles` (no aplica)
- ❌ `generos` (no aplica)
- ❌ `temporadas` (no aplica)
- ❌ `medidas` (usar `dimensiones` en su lugar)
- ❌ `marca` (opcional, algunos muebles tienen marca)
- ❌ `modelo` (opcional)
- ❌ `garantia` (opcional)
- ❌ `paginas` (no aplica)
- ❌ `isbn` (no aplica)
- ❌ `autor` (no aplica)
- ❌ `editorial` (no aplica)
- ❌ `potencia` (no aplica)
- ❌ `consumo` (no aplica)

### **DEPORTES Y FITNESS** 🏃
**Mostrar:**
- ✅ `marca` - Marca del producto deportivo
- ✅ `modelo` - Modelo
- ✅ `talles` - Talles (para calzado y ropa deportiva)
- ✅ `colores` - Colores disponibles
- ✅ `material` - Material del producto
- ✅ `dimensiones` - Dimensiones (para equipos)
- ✅ `peso` - Peso (para equipos)
- ✅ `especificaciones` - Especificaciones técnicas

**Ocultar:**
- ❌ `generos` (opcional, algunos productos deportivos sí tienen género)
- ❌ `temporadas` (no aplica)
- ❌ `medidas` (usar `dimensiones` en su lugar)
- ❌ `cuidados` (opcional)
- ❌ `paginas` (no aplica)
- ❌ `isbn` (no aplica)
- ❌ `autor` (no aplica)
- ❌ `editorial` (no aplica)
- ❌ `garantia` (opcional)
- ❌ `potencia` (no aplica)
- ❌ `consumo` (no aplica)

### **JUGUETES** 🧸
**Mostrar:**
- ✅ `edadRecomendada` - Edad recomendada (ya existe)
- ✅ `material` - Material del juguete
- ✅ `dimensiones` - Dimensiones del juguete
- ✅ `peso` - Peso del juguete
- ✅ `especificaciones` - Descripción adicional
- ✅ `cuidados` - Instrucciones de cuidado/limpieza

**Ocultar:**
- ❌ `colores` (opcional, algunos juguetes sí tienen colores)
- ❌ `talles` (no aplica)
- ❌ `generos` (no aplica)
- ❌ `temporadas` (no aplica)
- ❌ `medidas` (usar `dimensiones` en su lugar)
- ❌ `marca` (opcional)
- ❌ `modelo` (opcional)
- ❌ `garantia` (opcional)
- ❌ `paginas` (no aplica)
- ❌ `isbn` (no aplica)
- ❌ `autor` (no aplica)
- ❌ `editorial` (no aplica)
- ❌ `potencia` (no aplica)
- ❌ `consumo` (no aplica)

### **BELLEZA Y CUIDADO PERSONAL** 💄
**Mostrar:**
- ✅ `marca` - Marca del producto
- ✅ `modelo` - Modelo o variante
- ✅ `especificaciones` - Ingredientes, volumen, etc.
- ✅ `dimensiones` - Dimensiones del envase
- ✅ `peso` - Peso/volumen del producto
- ✅ `cuidados` - Instrucciones de uso

**Ocultar:**
- ❌ `colores` (opcional, algunos productos de belleza sí tienen colores)
- ❌ `talles` (no aplica)
- ❌ `generos` (no aplica)
- ❌ `temporadas` (no aplica)
- ❌ `medidas` (usar `dimensiones` en su lugar)
- ❌ `material` (no relevante)
- ❌ `garantia` (no aplica)
- ❌ `paginas` (no aplica)
- ❌ `isbn` (no aplica)
- ❌ `autor` (no aplica)
- ❌ `editorial` (no aplica)
- ❌ `potencia` (no aplica)
- ❌ `consumo` (no aplica)

### **AUTOMOTOR** 🚗
**Mostrar:**
- ✅ `marca` - Marca del producto
- ✅ `modelo` - Modelo
- ✅ `especificaciones` - Especificaciones técnicas
- ✅ `dimensiones` - Dimensiones
- ✅ `peso` - Peso
- ✅ `garantia` - Garantía

**Ocultar:**
- ❌ `colores` (opcional, algunos productos automotrices sí tienen colores)
- ❌ `talles` (no aplica)
- ❌ `generos` (no aplica)
- ❌ `temporadas` (no aplica)
- ❌ `medidas` (usar `dimensiones` en su lugar)
- ❌ `material` (opcional)
- ❌ `cuidados` (opcional)
- ❌ `paginas` (no aplica)
- ❌ `isbn` (no aplica)
- ❌ `autor` (no aplica)
- ❌ `editorial` (no aplica)
- ❌ `potencia` (opcional, algunos productos sí tienen potencia)
- ❌ `consumo` (opcional)

### **OTROS** 📦
**Mostrar:**
- ✅ `especificaciones` - Especificaciones generales
- ✅ `dimensiones` - Dimensiones (si aplica)
- ✅ `peso` - Peso (si aplica)
- ✅ `material` - Material (si aplica)

**Ocultar:**
- ❌ Todos los campos específicos (se muestran solo los genéricos)

---

## ➕ Nuevos Campos a Agregar en Product

Necesitamos agregar los siguientes campos nuevos a la entidad `Product`:

```java
// Campos para libros
@Column(name = "autor", nullable = true, length = 200)
private String autor;

@Column(name = "editorial", nullable = true, length = 200)
private String editorial;

@Column(name = "isbn", nullable = true, length = 20)
private String isbn;

@Column(name = "paginas", nullable = true)
private Integer paginas;

// Campos genéricos para dimensiones y peso
@Column(name = "peso", nullable = true, length = 50)
private String peso; // Ej: "500g", "2.5kg", "1.2 libras"

@Column(name = "dimensiones", nullable = true, length = 100)
private String dimensiones; // Ej: "20x15x5 cm", "200x80x75 cm"

// Campos para electrónica/electrodomésticos
@Column(name = "potencia", nullable = true, length = 50)
private String potencia; // Ej: "220V", "50W", "1000W"

@Column(name = "consumo", nullable = true, length = 100)
private String consumo; // Ej: "220V, 50W", "Clase A+"
```

---

## 🔧 Implementación Técnica

### **Fase 1: Agregar Nuevos Campos a Product**

1. Modificar `Product.java`:
   - Agregar los nuevos campos (autor, editorial, isbn, paginas, peso, dimensiones, potencia, consumo)
   - Hacer todos nullable
   - Agregar getters/setters manuales (para compatibilidad con IDE)

2. Actualizar base de datos:
   - JPA creará automáticamente las nuevas columnas con `ddl-auto=update`

### **Fase 2: Actualizar Enum TipoProducto**

Agregar métodos helper para determinar qué campos mostrar:

```java
public enum TipoProducto {
    // ... valores existentes ...
    
    /**
     * Retorna los campos que deben mostrarse para este tipo de producto
     */
    public List<String> getCamposVisibles() {
        List<String> campos = new ArrayList<>();
        campos.add("universales"); // Siempre
        
        switch (this) {
            case INDUMENTARIA:
                campos.add("colores");
                campos.add("talles");
                campos.add("generos");
                campos.add("temporadas");
                campos.add("medidas");
                campos.add("material");
                campos.add("cuidados");
                break;
            case ELECTRONICA:
                campos.add("marca");
                campos.add("modelo");
                campos.add("garantia");
                campos.add("especificaciones");
                campos.add("peso");
                campos.add("dimensiones");
                campos.add("potencia");
                campos.add("consumo");
                break;
            case LIBROS:
                campos.add("autor");
                campos.add("editorial");
                campos.add("isbn");
                campos.add("paginas");
                campos.add("peso");
                campos.add("dimensiones");
                campos.add("especificaciones");
                break;
            // ... otros casos ...
        }
        return campos;
    }
    
    /**
     * Retorna los campos que deben ocultarse para este tipo de producto
     */
    public List<String> getCamposOcultos() {
        List<String> campos = new ArrayList<>();
        
        switch (this) {
            case INDUMENTARIA:
                campos.add("marca");
                campos.add("modelo");
                campos.add("garantia");
                campos.add("especificaciones");
                campos.add("peso");
                campos.add("dimensiones");
                campos.add("paginas");
                campos.add("isbn");
                campos.add("autor");
                campos.add("editorial");
                campos.add("potencia");
                campos.add("consumo");
                break;
            case LIBROS:
                campos.add("colores");
                campos.add("talles");
                campos.add("generos");
                campos.add("temporadas");
                campos.add("medidas");
                campos.add("material");
                campos.add("cuidados");
                campos.add("marca");
                campos.add("modelo");
                campos.add("garantia");
                campos.add("potencia");
                campos.add("consumo");
                break;
            // ... otros casos ...
        }
        return campos;
    }
}
```

### **Fase 3: Actualizar ProductController**

Agregar `TipoProducto` al modelo:

```java
@GetMapping("/new")
public String showForm(Model model) {
    Product product = new Product();
    model.addAttribute("product", product);
    model.addAttribute("tiposProducto", TipoProducto.values());
    // ... otros atributos ...
    return "admin/product-form";
}
```

### **Fase 4: Actualizar Template HTML (product-form.html)**

#### **4.1. Agregar Selector de Tipo de Producto**

```html
<div class="mb-3">
    <label for="tipoProducto" class="form-label">
        Tipo de Producto <span class="text-danger">*</span>
    </label>
    <select class="form-select" id="tipoProducto" name="tipoProducto" 
            th:value="${product?.tipoProducto}" 
            onchange="actualizarCamposSegunTipo()" required>
        <option value="">-- Seleccionar tipo de producto --</option>
        <option th:each="tipo : ${tiposProducto}" 
                th:value="${tipo}" 
                th:text="${tipo.displayName}"></option>
    </select>
    <small class="form-text text-muted">
        Selecciona el tipo de producto para mostrar solo los campos relevantes
    </small>
</div>
```

#### **4.2. Organizar Campos en Secciones con Clases CSS**

```html
<!-- Sección: Campos de INDUMENTARIA -->
<div id="seccion-indumentaria" class="seccion-tipo-producto" style="display: none;">
    <h5 class="mt-4 mb-3 text-primary">
        <i class="bi bi-tshirt"></i> Información de Indumentaria
    </h5>
    
    <!-- Colores -->
    <div class="mb-3">
        <label class="form-label">Colores Disponibles</label>
        <!-- ... selector de colores existente ... -->
    </div>
    
    <!-- Talles -->
    <div class="mb-3">
        <label class="form-label">Talles Disponibles</label>
        <!-- ... selector de talles existente ... -->
    </div>
    
    <!-- Géneros -->
    <div class="mb-3">
        <label class="form-label">Géneros</label>
        <!-- ... selector de géneros existente ... -->
    </div>
    
    <!-- Temporadas -->
    <div class="mb-3">
        <label class="form-label">Temporadas</label>
        <!-- ... selector de temporadas existente ... -->
    </div>
    
    <!-- Medidas -->
    <div class="mb-3">
        <label class="form-label">Medidas de la Prenda</label>
        <input type="text" class="form-control" name="medidas" 
               th:value="${product?.medidas}"
               placeholder="Ej: Pecho 100cm, Cintura 80cm, Largo 70cm">
    </div>
    
    <!-- Material -->
    <div class="mb-3">
        <label class="form-label">Material</label>
        <input type="text" class="form-control" name="material" 
               th:value="${product?.material}"
               placeholder="Ej: 100% Algodón, Poliéster">
    </div>
    
    <!-- Cuidados -->
    <div class="mb-3">
        <label class="form-label">Instrucciones de Cuidado</label>
        <textarea class="form-control" name="cuidados" rows="3"
                  th:text="${product?.cuidados}"
                  placeholder="Ej: Lavar a mano, No usar secadora"></textarea>
    </div>
</div>

<!-- Sección: Campos de ELECTRÓNICA -->
<div id="seccion-electronica" class="seccion-tipo-producto" style="display: none;">
    <h5 class="mt-4 mb-3 text-primary">
        <i class="bi bi-cpu"></i> Información de Electrónica
    </h5>
    
    <!-- Marca -->
    <div class="mb-3">
        <label class="form-label">Marca</label>
        <input type="text" class="form-control" name="marca" 
               th:value="${product?.marca}"
               placeholder="Ej: Samsung, Apple, Sony">
    </div>
    
    <!-- Modelo -->
    <div class="mb-3">
        <label class="form-label">Modelo</label>
        <input type="text" class="form-control" name="modelo" 
               th:value="${product?.modelo}"
               placeholder="Ej: Galaxy S21, iPhone 13">
    </div>
    
    <!-- Garantía -->
    <div class="mb-3">
        <label class="form-label">Garantía</label>
        <input type="text" class="form-control" name="garantia" 
               th:value="${product?.garantia}"
               placeholder="Ej: 12 meses, 2 años">
    </div>
    
    <!-- Especificaciones Técnicas -->
    <div class="mb-3">
        <label class="form-label">Especificaciones Técnicas</label>
        <textarea class="form-control" name="especificaciones" rows="5"
                  th:text="${product?.especificaciones}"
                  placeholder="Ej: Procesador: Snapdragon 888, RAM: 8GB, Almacenamiento: 128GB"></textarea>
    </div>
    
    <!-- Peso -->
    <div class="mb-3">
        <label class="form-label">Peso</label>
        <input type="text" class="form-control" name="peso" 
               th:value="${product?.peso}"
               placeholder="Ej: 200g, 2.5kg">
    </div>
    
    <!-- Dimensiones -->
    <div class="mb-3">
        <label class="form-label">Dimensiones</label>
        <input type="text" class="form-control" name="dimensiones" 
               th:value="${product?.dimensiones}"
               placeholder="Ej: 20x15x5 cm">
    </div>
    
    <!-- Potencia -->
    <div class="mb-3">
        <label class="form-label">Potencia</label>
        <input type="text" class="form-control" name="potencia" 
               th:value="${product?.potencia}"
               placeholder="Ej: 220V, 50W">
    </div>
    
    <!-- Consumo -->
    <div class="mb-3">
        <label class="form-label">Consumo Energético</label>
        <input type="text" class="form-control" name="consumo" 
               th:value="${product?.consumo}"
               placeholder="Ej: 220V, 50W, Clase A+">
    </div>
</div>

<!-- Sección: Campos de LIBROS -->
<div id="seccion-libros" class="seccion-tipo-producto" style="display: none;">
    <h5 class="mt-4 mb-3 text-primary">
        <i class="bi bi-book"></i> Información del Libro
    </h5>
    
    <!-- Autor -->
    <div class="mb-3">
        <label class="form-label">Autor</label>
        <input type="text" class="form-control" name="autor" 
               th:value="${product?.autor}"
               placeholder="Ej: Gabriel García Márquez">
    </div>
    
    <!-- Editorial -->
    <div class="mb-3">
        <label class="form-label">Editorial</label>
        <input type="text" class="form-control" name="editorial" 
               th:value="${product?.editorial}"
               placeholder="Ej: Sudamericana, Planeta">
    </div>
    
    <!-- ISBN -->
    <div class="mb-3">
        <label class="form-label">ISBN</label>
        <input type="text" class="form-control" name="isbn" 
               th:value="${product?.isbn}"
               placeholder="Ej: 978-950-07-1234-5">
    </div>
    
    <!-- Páginas -->
    <div class="mb-3">
        <label class="form-label">Número de Páginas</label>
        <input type="number" class="form-control" name="paginas" 
               th:value="${product?.paginas}"
               placeholder="Ej: 350" min="1">
    </div>
    
    <!-- Peso -->
    <div class="mb-3">
        <label class="form-label">Peso</label>
        <input type="text" class="form-control" name="peso" 
               th:value="${product?.peso}"
               placeholder="Ej: 500g, 1.2kg">
    </div>
    
    <!-- Dimensiones -->
    <div class="mb-3">
        <label class="form-label">Dimensiones</label>
        <input type="text" class="form-control" name="dimensiones" 
               th:value="${product?.dimensiones}"
               placeholder="Ej: 23x15x2 cm">
    </div>
    
    <!-- Especificaciones (Sinopsis) -->
    <div class="mb-3">
        <label class="form-label">Sinopsis / Descripción Adicional</label>
        <textarea class="form-control" name="especificaciones" rows="5"
                  th:text="${product?.especificaciones}"
                  placeholder="Sinopsis del libro..."></textarea>
    </div>
</div>

<!-- Sección: Campos Genéricos (HOGAR, DEPORTES, JUGUETES, etc.) -->
<div id="seccion-genericos" class="seccion-tipo-producto" style="display: none;">
    <h5 class="mt-4 mb-3 text-primary">
        <i class="bi bi-box"></i> Información Adicional
    </h5>
    
    <!-- Marca (opcional para algunos tipos) -->
    <div class="mb-3" id="campo-marca">
        <label class="form-label">Marca</label>
        <input type="text" class="form-control" name="marca" 
               th:value="${product?.marca}"
               placeholder="Ej: Samsung, Nike, Fisher-Price">
    </div>
    
    <!-- Modelo (opcional) -->
    <div class="mb-3" id="campo-modelo">
        <label class="form-label">Modelo</label>
        <input type="text" class="form-control" name="modelo" 
               th:value="${product?.modelo}"
               placeholder="Ej: Modelo XYZ">
    </div>
    
    <!-- Material -->
    <div class="mb-3" id="campo-material">
        <label class="form-label">Material</label>
        <input type="text" class="form-control" name="material" 
               th:value="${product?.material}"
               placeholder="Ej: Madera, Plástico, Metal">
    </div>
    
    <!-- Dimensiones -->
    <div class="mb-3" id="campo-dimensiones">
        <label class="form-label">Dimensiones</label>
        <input type="text" class="form-control" name="dimensiones" 
               th:value="${product?.dimensiones}"
               placeholder="Ej: 200x80x75 cm">
    </div>
    
    <!-- Peso -->
    <div class="mb-3" id="campo-peso">
        <label class="form-label">Peso</label>
        <input type="text" class="form-control" name="peso" 
               th:value="${product?.peso}"
               placeholder="Ej: 500g, 2.5kg">
    </div>
    
    <!-- Especificaciones -->
    <div class="mb-3" id="campo-especificaciones">
        <label class="form-label">Especificaciones</label>
        <textarea class="form-control" name="especificaciones" rows="5"
                  th:text="${product?.especificaciones}"
                  placeholder="Especificaciones técnicas o descripción adicional..."></textarea>
    </div>
    
    <!-- Cuidados (opcional) -->
    <div class="mb-3" id="campo-cuidados">
        <label class="form-label">Instrucciones de Cuidado / Mantenimiento</label>
        <textarea class="form-control" name="cuidados" rows="3"
                  th:text="${product?.cuidados}"
                  placeholder="Instrucciones de cuidado o mantenimiento..."></textarea>
    </div>
</div>
```

#### **4.3. JavaScript para Mostrar/Ocultar Campos**

```javascript
// Mapeo de tipos de producto a secciones a mostrar
const mapeoTiposProducto = {
    'INDUMENTARIA': {
        mostrar: ['seccion-indumentaria'],
        ocultar: ['seccion-electronica', 'seccion-libros', 'seccion-genericos']
    },
    'ELECTRONICA': {
        mostrar: ['seccion-electronica'],
        ocultar: ['seccion-indumentaria', 'seccion-libros', 'seccion-genericos']
    },
    'LIBROS': {
        mostrar: ['seccion-libros'],
        ocultar: ['seccion-indumentaria', 'seccion-electronica', 'seccion-genericos']
    },
    'HOGAR': {
        mostrar: ['seccion-genericos'],
        camposEspecificos: {
            mostrar: ['campo-marca', 'campo-modelo', 'campo-material', 'campo-dimensiones', 'campo-peso', 'campo-especificaciones', 'campo-cuidados']
        }
    },
    'DEPORTES': {
        mostrar: ['seccion-genericos'],
        camposEspecificos: {
            mostrar: ['campo-marca', 'campo-modelo', 'campo-material', 'campo-dimensiones', 'campo-peso', 'campo-especificaciones']
        }
    },
    'JUGUETES': {
        mostrar: ['seccion-genericos'],
        camposEspecificos: {
            mostrar: ['campo-material', 'campo-dimensiones', 'campo-peso', 'campo-especificaciones', 'campo-cuidados']
        }
    },
    'BELLEZA': {
        mostrar: ['seccion-genericos'],
        camposEspecificos: {
            mostrar: ['campo-marca', 'campo-modelo', 'campo-dimensiones', 'campo-peso', 'campo-especificaciones', 'campo-cuidados']
        }
    },
    'AUTOMOTOR': {
        mostrar: ['seccion-genericos'],
        camposEspecificos: {
            mostrar: ['campo-marca', 'campo-modelo', 'campo-dimensiones', 'campo-peso', 'campo-especificaciones', 'campo-garantia']
        }
    },
    'OTROS': {
        mostrar: ['seccion-genericos'],
        camposEspecificos: {
            mostrar: ['campo-especificaciones', 'campo-dimensiones', 'campo-peso']
        }
    }
};

function actualizarCamposSegunTipo() {
    const tipoProducto = document.getElementById('tipoProducto').value;
    
    // Ocultar todas las secciones primero
    document.querySelectorAll('.seccion-tipo-producto').forEach(seccion => {
        seccion.style.display = 'none';
    });
    
    // Si no hay tipo seleccionado, no mostrar nada
    if (!tipoProducto) {
        return;
    }
    
    // Obtener configuración para este tipo
    const config = mapeoTiposProducto[tipoProducto];
    
    if (!config) {
        console.warn('Tipo de producto no encontrado:', tipoProducto);
        return;
    }
    
    // Mostrar secciones correspondientes
    config.mostrar.forEach(seccionId => {
        const seccion = document.getElementById(seccionId);
        if (seccion) {
            seccion.style.display = 'block';
        }
    });
    
    // Ocultar secciones no correspondientes
    if (config.ocultar) {
        config.ocultar.forEach(seccionId => {
            const seccion = document.getElementById(seccionId);
            if (seccion) {
                seccion.style.display = 'none';
            }
        });
    }
    
    // Si hay campos específicos a mostrar/ocultar dentro de sección genérica
    if (config.camposEspecificos) {
        // Ocultar todos los campos dentro de sección genérica
        document.querySelectorAll('#seccion-genericos .mb-3').forEach(campo => {
            campo.style.display = 'none';
        });
        
        // Mostrar solo los campos especificados
        if (config.camposEspecificos.mostrar) {
            config.camposEspecificos.mostrar.forEach(campoId => {
                const campo = document.getElementById(campoId);
                if (campo) {
                    campo.style.display = 'block';
                }
            });
        }
    }
}

// Ejecutar al cargar la página (para edición de productos existentes)
document.addEventListener('DOMContentLoaded', function() {
    const tipoProducto = document.getElementById('tipoProducto');
    if (tipoProducto && tipoProducto.value) {
        actualizarCamposSegunTipo();
    }
});
```

---

## 📋 Plan de Implementación Paso a Paso

### **Paso 1: Agregar Nuevos Campos a Product.java**
- [ ] Agregar campos: `autor`, `editorial`, `isbn`, `paginas`, `peso`, `dimensiones`, `potencia`, `consumo`
- [ ] Agregar getters/setters manuales
- [ ] Verificar compilación

### **Paso 2: Actualizar Enum TipoProducto**
- [ ] Agregar métodos `getCamposVisibles()` y `getCamposOcultos()` (opcional, para validación backend)
- [ ] O mantener simple y manejar todo en frontend

### **Paso 3: Actualizar ProductController**
- [ ] Agregar `tiposProducto` al modelo en `showForm()` y `editProduct()`

### **Paso 4: Actualizar product-form.html**
- [ ] Agregar selector de tipo de producto al inicio del formulario
- [ ] Reorganizar campos existentes en secciones con IDs específicos
- [ ] Agregar nuevos campos (autor, editorial, isbn, etc.) en sus secciones correspondientes
- [ ] Agregar JavaScript `actualizarCamposSegunTipo()`
- [ ] Agregar evento `onchange` al selector de tipo
- [ ] Agregar lógica para cargar campos al editar producto existente

### **Paso 5: Probar**
- [ ] Probar cada tipo de producto
- [ ] Verificar que los campos se muestran/ocultan correctamente
- [ ] Verificar que los datos se guardan correctamente
- [ ] Verificar que al editar un producto, los campos se muestran según su tipo

### **Paso 6: Actualizar Vista Pública (Opcional)**
- [ ] Actualizar `product-detail.html` para mostrar solo campos relevantes según tipo

---

## ✅ Ventajas de Este Enfoque

1. **UX Mejorada:** El usuario solo ve campos relevantes, formulario más limpio
2. **Flexibilidad:** Fácil agregar nuevos tipos de productos
3. **Mantenibilidad:** Lógica centralizada en JavaScript
4. **Retrocompatibilidad:** Productos existentes siguen funcionando
5. **Extensibilidad:** Fácil agregar nuevos campos para nuevos tipos

---

## ⚠️ Consideraciones

1. **Validación:** Considerar validar campos requeridos según tipo de producto
2. **Migración de Datos:** Productos existentes pueden tener `tipoProducto = null`, manejar este caso
3. **Inferencia de Tipo:** Si no se selecciona tipo, se puede inferir desde la categoría seleccionada
4. **Campos Opcionales:** Algunos campos pueden ser opcionales incluso dentro de un tipo (ej: marca en juguetes)

---

**¿Te parece bien este plan? ¿Quieres que empecemos con la implementación?**

