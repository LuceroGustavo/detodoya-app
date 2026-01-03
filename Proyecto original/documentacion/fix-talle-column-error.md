# Fix: Error "Data truncated for column 'talle'"

**Fecha:** 6 de Noviembre de 2025  
**Estado:** ✅ Resuelto  
**Prioridad:** Alta

---

## 📋 **Descripción del Problema**

Al intentar actualizar un producto con talles nuevos (como 32, 34, 36, etc.) desde la interfaz de administración (`/admin/products/edit/{id}`), se producía el siguiente error:

```
Data truncated for column 'talle' at row 1
[insert into product_talles (product_id,talle) values (?,?)]
```

### **Error Completo:**
```
org.springframework.orm.jpa.JpaSystemException: could not execute statement 
[Data truncated for column 'talle' at row 1] 
[insert into product_talles (product_id,talle) values (?,?)]
```

---

## 🔍 **Análisis del Problema**

### **1. Estructura del Enum Talle:**
```java
public enum Talle {
    XS("XS"), S("S"), M("M"), L("L"), XL("XL"), XXL("XXL"), XXXL("XXXL"),
    T32("32"), T34("34"), T36("36"), T38("38"), T40("40"), 
    T42("42"), T44("44"), T46("46"), T48("48"), T50("50"), 
    T52("52"), T54("54"), T56("56");
}
```

### **2. Mapeo en la Entidad Product:**
```java
@ElementCollection(targetClass = Talle.class, fetch = FetchType.LAZY)
@Enumerated(EnumType.STRING)
@CollectionTable(name = "product_talles", joinColumns = @JoinColumn(name = "product_id"))
@Column(name = "talle")
private List<Talle> talles = new ArrayList<>();
```

### **3. Causa Raíz:**
- Con `@Enumerated(EnumType.STRING)`, Hibernate guarda el **nombre del enum** en la base de datos, no el `displayName`.
- Ejemplos:
  - `Talle.T32` → Se guarda como `"T32"` (3 caracteres)
  - `Talle.XXXL` → Se guarda como `"XXXL"` (4 caracteres)
  - `Talle.T54` → Se guarda como `"T54"` (3 caracteres)
- La columna `talle` en la tabla `product_talles` fue creada inicialmente con `VARCHAR(1)` o `VARCHAR(2)` cuando solo había talles de 1-2 caracteres (S, M, L, XL).
- Al intentar insertar valores como `"T32"` (3 caracteres) o `"XXXL"` (4 caracteres), MySQL rechaza la inserción con el error de truncamiento.

---

## ✅ **Solución Implementada**

### **Script SQL de Corrección:**

**Archivo:** `scripts/fix-talle-column.sql`

```sql
-- Modificar la columna talle para permitir valores más largos
ALTER TABLE product_talles MODIFY COLUMN talle VARCHAR(10) NOT NULL;
```

### **Pasos para Aplicar la Solución:**

1. **Conectar a la base de datos MySQL:**
   ```bash
   mysql -u root -p oriola_indumentaria
   ```

2. **Ejecutar el script:**
   ```sql
   USE oriola_indumentaria;
   ALTER TABLE product_talles MODIFY COLUMN talle VARCHAR(10) NOT NULL;
   ```

3. **Verificar el cambio:**
   ```sql
   DESCRIBE product_talles;
   ```
   
   Debería mostrar:
   ```
   +------------+-------------+------+-----+---------+-------+
   | Field      | Type        | Null | Key | Default | Extra |
   +------------+-------------+------+-----+---------+-------+
   | product_id | int         | NO   | PRI | NULL    |       |
   | talle      | varchar(10) | NO   |     | NULL    |       |
   +------------+-------------+------+-----+---------+-------+
   ```

---

## 🧪 **Verificación**

### **1. Verificar valores actuales en la tabla:**
```sql
SELECT DISTINCT talle FROM product_talles ORDER BY talle;
```

### **2. Probar actualización de producto:**
1. Ir a `/admin/products/edit/{id}`
2. Seleccionar talles nuevos (32, 34, 36, etc.)
3. Guardar el producto
4. Verificar que no se produce el error

### **3. Verificar en la base de datos:**
```sql
SELECT pt.*, p.name 
FROM product_talles pt 
JOIN product p ON pt.product_id = p.p_id 
WHERE pt.talle IN ('T32', 'T34', 'XXXL')
ORDER BY p.name, pt.talle;
```

---

## 📝 **Notas Técnicas**

### **¿Por qué VARCHAR(10)?**
- El nombre más largo del enum actual es `"XXXL"` (4 caracteres)
- `VARCHAR(10)` proporciona suficiente espacio para futuros talles más largos
- Es un tamaño razonable que no desperdicia espacio en la base de datos

### **Alternativas Consideradas:**
1. **Cambiar a `@Enumerated(EnumType.ORDINAL)`:**
   - ❌ Guardaría números (0, 1, 2...) en lugar de nombres
   - ❌ Si se agregan nuevos talles, los números cambiarían
   - ❌ Menos legible en la base de datos

2. **Usar `displayName` en lugar del nombre del enum:**
   - ❌ Requeriría cambiar la lógica de mapeo
   - ❌ Más complejo de implementar
   - ❌ No es el comportamiento estándar de JPA

3. **Aumentar el tamaño de la columna (Elegida):**
   - ✅ Solución simple y directa
   - ✅ No requiere cambios en el código Java
   - ✅ Mantiene la legibilidad en la base de datos

---

## 🔗 **Archivos Relacionados**

- **Script SQL:** `scripts/fix-talle-column.sql`
- **Entidad:** `src/main/java/com/orioladenim/entity/Product.java`
- **Enum:** `src/main/java/com/orioladenim/enums/Talle.java`
- **Controlador:** `src/main/java/com/orioladenim/controller/ProductController.java`
- **Documentación:** `documentacion/remodelacion-sistema-productos-multiples.md`

---

## 📚 **Referencias**

- [JPA @Enumerated Annotation](https://docs.oracle.com/javaee/7/api/javax/persistence/Enumerated.html)
- [Hibernate ElementCollection](https://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/Hibernate_User_Guide.html#collections-element-collection)
- [MySQL ALTER TABLE](https://dev.mysql.com/doc/refman/8.0/en/alter-table.html)

---

**Resuelto por:** Asistente IA  
**Fecha de resolución:** 6 de Noviembre de 2025  
**Estado:** ✅ Completado

