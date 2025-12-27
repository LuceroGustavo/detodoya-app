# Instrucciones - Primera Ejecución de Detodoya.com

**Fecha:** Enero 2025  
**Base de datos:** detodoya

---

## ✅ Pasos Completados

1. ✅ Base de datos `detodoya` creada en MySQL
2. ✅ Proyecto configurado con Java 21
3. ✅ Código compilado correctamente

---

## 🚀 Ejecutar la Aplicación

### **Comando:**
```bash
mvn spring-boot:run
```

### **¿Qué sucede al ejecutar?**

1. **Spring Boot inicia** la aplicación
2. **JPA/Hibernate detecta** la base de datos `detodoya`
3. **Crea automáticamente todas las tablas** (porque `ddl-auto=update`)
4. **La aplicación queda disponible** en `http://localhost:8080`

---

## 📊 Tablas que se Crearán Automáticamente

JPA creará las siguientes tablas automáticamente:

### **Tablas Principales:**
- `product` - Productos
- `categories` - Categorías
- `colors` - Colores
- `users` - Usuarios
- `contacts` - Consultas/contactos
- `product_image` - Imágenes de productos
- `product_video` - Videos de productos (legacy)
- `product_view` - Analytics de visitas
- `historia` - Historias (si se usa)
- `contact_response` - Respuestas a consultas
- `backup_info` - Información de backups

### **Tablas de Relación (Many-to-Many):**
- `product_categories` - Relación Product ↔ Category
- `product_colors` - Relación Product ↔ Color
- `product_talles` - Talles de productos (ElementCollection)
- `product_generos` - Géneros de productos (ElementCollection)
- `product_temporadas` - Temporadas de productos (ElementCollection)

### **Nuevas Columnas Agregadas:**
En la tabla `product`:
- `tipo_producto` - Tipo de producto (enum)
- `especificaciones` - Especificaciones técnicas
- `marca` - Marca del producto
- `modelo` - Modelo del producto
- `garantia` - Garantía
- `codigo_producto` - Código SKU
- `link_venta` - Link a marketplace
- `contacto_vendedor` - Contacto del vendedor
- `ubicacion` - Ubicación

En la tabla `categories`:
- `tipo_producto_default` - Tipo de producto por defecto

---

## 🔍 Verificar que Funcionó

### **1. Revisar Logs de la Aplicación:**
Busca en la consola mensajes como:
```
Hibernate: create table product (...)
Hibernate: create table categories (...)
Hibernate: create table colors (...)
```

### **2. Verificar en MySQL Workbench:**
- Abre la base de datos `detodoya`
- Expande "Tables"
- Deberías ver todas las tablas creadas

### **3. Acceder a la Aplicación:**
- Abre navegador: `http://localhost:8080`
- Deberías ver la página principal
- Panel admin: `http://localhost:8080/admin/login`
  - Usuario: `admin`
  - Contraseña: `admin123` (o la configurada en `application-local.properties`)

---

## ⚠️ Si Hay Errores

### **Error: "Unknown database 'detodoya'"**
- Verifica que la base de datos existe
- Verifica credenciales en `application-local.properties`

### **Error: "Access denied for user"**
- Verifica usuario y contraseña de MySQL
- Verifica que el usuario tiene permisos en la base de datos

### **Error: "Table already exists"**
- Normal si ya ejecutaste la aplicación antes
- JPA actualizará las tablas existentes (no las eliminará)

---

## 📝 Notas Importantes

- **`ddl-auto=update`** crea/actualiza tablas, NO las elimina
- **Los datos existentes se mantienen** (si los hay)
- **Las nuevas columnas se agregan** automáticamente
- **Si cambias la estructura**, JPA actualizará las tablas

---

## 🎯 Próximos Pasos Después de la Primera Ejecución

1. ✅ Verificar que las tablas se crearon
2. ✅ Acceder al panel admin
3. ✅ Crear categorías de prueba
4. ✅ Crear productos de prueba (diferentes tipos)
5. ✅ Verificar que los campos condicionales funcionan

---

**Última actualización:** Enero 2025

