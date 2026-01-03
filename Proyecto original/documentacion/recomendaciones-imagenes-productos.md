# Recomendaciones para Imágenes de Productos - Oriola

## Dimensiones Recomendadas

### Tamaño Óptimo
- **Ancho:** 800px - 1200px
- **Alto:** 1200px - 1800px
- **Relación de Aspecto:** 2:3 (vertical/portrait) - **Recomendado**
  - Esto corresponde aproximadamente a un formato de **2x3** o **vertical**

### Consideraciones según el Contenedor

El sistema muestra las imágenes en contenedores con las siguientes dimensiones:

| Vista | Ancho Contenedor | Alto Contenedor | Relación de Aspecto |
|-------|-----------------|-----------------|---------------------|
| **Desktop** | ~322px (1/4 del grid) | 480px | ~0.67:1 (vertical) |
| **Tablet** | ~424px (1/3 del grid) | 420px | ~1:1 (cuadrado) |
| **Móvil** | ~50% del ancho | 300px | ~variable |

### ¿Por qué 2:3 (Vertical)?

El sistema utiliza `object-fit: cover`, lo que significa que:
- Las imágenes se recortan para llenar el contenedor manteniendo su relación de aspecto
- El recorte se centra (`object-position: center`)
- Las imágenes verticales (2:3) se adaptan mejor a todos los tamaños de pantalla
- Evitan espacios vacíos en los laterales en vista móvil

## Tamaño de Archivo

### Recomendaciones
- **Tamaño máximo:** 500KB - 800KB por imagen
- **Formato:** WebP (preferido) o JPG de alta calidad
- **Resolución:** Mínimo 800px de ancho, ideal 1200px de ancho

### Formatos Aceptados
- **WebP** (recomendado): Mejor compresión, mantiene calidad
- **JPG/JPEG**: Buena compatibilidad, compresión aceptable
- **PNG**: Solo si requiere transparencia (no recomendado para fotos de productos)

## Especificaciones Técnicas

### Dimensiones Ideales
```
Ancho: 1000px - 1200px
Alto: 1500px - 1800px
Relación: 2:3 (aproximadamente)
```

### Ejemplo de Dimensiones Exactas
- **Óptima:** 1000px × 1500px (2:3)
- **Mínima:** 800px × 1200px (2:3)
- **Máxima:** 1200px × 1800px (2:3)

## Consejos de Fotografía

### Composición
1. **Centrar el producto**: El objeto principal debe estar centrado para evitar recortes importantes
2. **Fondo neutro**: Preferiblemente fondo blanco o uniforme
3. **Buena iluminación**: Evitar sombras duras o sobreexposición
4. **Espacio alrededor**: Dejar un margen alrededor del producto (será recortado en el contenedor)

### Orientación
- ✅ **Recomendado:** Formato **vertical (portrait)**
- ✅ **Aceptable:** Formato cuadrado (1:1)
- ⚠️ **Evitar:** Formato horizontal (landscape) muy ancho

## Resumen para el Cliente

**Dimensiones sugeridas:**
- Ancho: **1000px**
- Alto: **1500px**
- Formato: **WebP o JPG**
- Tamaño de archivo: **Máximo 800KB**
- Orientación: **Vertical (2:3)**

**Ejemplo de buena imagen:**
- La imagen `7e09e0f1-24f2-4dad-9eaa-83c2ae9e89f1.webp` de ejemplo tiene un tamaño de ~610KB, lo cual es apropiado.

## Notas Importantes

1. **Las imágenes horizontales muy anchas se verán recortadas** en los laterales
2. **Las imágenes demasiado pequeñas** perderán calidad al ampliarse
3. **Las imágenes muy pesadas (>1MB)** afectarán la velocidad de carga
4. El sistema **optimiza automáticamente** usando `object-fit: cover`, por lo que siempre llenará el contenedor, pero puede recortar partes de la imagen según su relación de aspecto

---

**Última actualización:** 2024
**Mantenido por:** Equipo de Desarrollo Oriola

