# Instalación de FFmpeg para Thumbnails de Historias

**Fecha:** 3 de noviembre de 2025  
**Problema:** Los thumbnails de historias se generan como placeholders en lugar de frames reales del video  
**Solución:** Instalar FFmpeg en el sistema

---

## 🎯 **PROBLEMA IDENTIFICADO**

El sistema de historias genera thumbnails automáticamente, pero si FFmpeg no está instalado, solo crea placeholders genéricos (imágenes grises con icono de play) en lugar de extraer frames reales del video.

---

## ✅ **MEJORAS IMPLEMENTADAS**

### **1. Sistema de Diagnóstico Mejorado**
- ✅ Verificación automática de FFmpeg antes de intentar extraer frames
- ✅ Logging detallado de errores para identificar problemas
- ✅ Captura de errores de stderr de FFmpeg
- ✅ Validación de imágenes generadas

### **2. Métodos de Regeneración**
- ✅ Regenerar thumbnail de una historia individual
- ✅ Regenerar thumbnails de todas las historias
- ✅ Endpoints en el controlador para regeneración

---

## 📦 **INSTALACIÓN DE FFMPEG**

### **Windows (PC del Trabajo)**

#### **Opción 1: Chocolatey (Recomendado)**
```powershell
# Abrir PowerShell como Administrador
choco install ffmpeg

# Verificar instalación
ffmpeg -version
```

#### **Opción 2: Descarga Manual**
1. Ir a: https://ffmpeg.org/download.html
2. Descargar FFmpeg para Windows
3. Extraer en una carpeta (ej: `C:\ffmpeg`)
4. Agregar a PATH del sistema:
   - Ir a: Panel de Control → Sistema → Variables de entorno
   - Editar variable `Path`
   - Agregar: `C:\ffmpeg\bin`
5. Verificar en nueva terminal: `ffmpeg -version`

#### **Opción 3: Usando Scoop**
```powershell
scoop install ffmpeg
```

---

### **Linux/Ubuntu (Servidor NodeLight)**

```bash
# Conectar al servidor
ssh root@149.104.92.116

# Actualizar paquetes
apt update

# Instalar FFmpeg
apt install ffmpeg -y

# Verificar instalación
ffmpeg -version

# Verificar que funciona
ffmpeg -i /home/oriola/uploads/historias/historia_*.mp4 -ss 00:00:00.5 -vframes 1 -q:v 2 test_thumbnail.jpg
```

---

## 🔧 **VERIFICACIÓN DEL SISTEMA**

### **1. Verificar que FFmpeg está instalado**

**En Windows:**
```cmd
ffmpeg -version
```

**En Linux:**
```bash
which ffmpeg
ffmpeg -version
```

### **2. Verificar en los logs de la aplicación**

Al crear una nueva historia, deberías ver en los logs:

**Si FFmpeg está disponible:**
```
✅ [FFMPEG] FFmpeg disponible: ffmpeg version 6.x.x
✅ [FFMPEG] FFmpeg ejecutado exitosamente
✅ [FFMPEG] Thumbnail válido generado: 45678 bytes, 640x480
✅ [THUMBNAIL] Thumbnail generado con FFmpeg: thumb_historia_20251103_120031.jpg
```

**Si FFmpeg NO está disponible:**
```
⚠️ [FFMPEG] FFmpeg no está instalado o no está en el PATH
⚠️ [THUMBNAIL] FFmpeg no disponible o falló, creando placeholder...
⚠️ [THUMBNAIL] Thumbnail placeholder creado: thumb_historia_20251103_120031.jpg
```

---

## 🔄 **REgenerar Thumbnails Existentes**

Después de instalar FFmpeg, puedes regenerar los thumbnails de historias existentes:

### **Opción 1: Regenerar todos los thumbnails**

**Desde el panel admin:**
1. Ir a: `http://localhost:8080/admin/historias`
2. Buscar botón "Regenerar Todos los Thumbnails" (si está implementado en el frontend)
3. O usar el endpoint directamente en el navegador (POST request)

**Desde código o terminal:**
```bash
# Usar curl o Postman para hacer POST request
curl -X POST http://localhost:8080/admin/historias/regenerar-todos-thumbnails
```

### **Opción 2: Regenerar thumbnail individual**

**Endpoint:**
```
POST /admin/historias/{id}/regenerar-thumbnail
```

**Ejemplo:**
```bash
# Regenerar thumbnail de historia con ID 1
curl -X POST http://localhost:8080/admin/historias/1/regenerar-thumbnail
```

---

## 🐛 **DIAGNÓSTICO DE PROBLEMAS**

### **Problema: FFmpeg no se encuentra**

**Síntoma:**
```
⚠️ [FFMPEG] Error verificando FFmpeg: Cannot run program "ffmpeg"
```

**Solución:**
1. Verificar que FFmpeg está instalado: `ffmpeg -version`
2. Si está instalado pero no se encuentra:
   - **Windows:** Agregar ruta de FFmpeg al PATH del sistema
   - **Linux:** Verificar que está en `/usr/bin/` o usar `which ffmpeg`

### **Problema: FFmpeg falla al extraer frame**

**Síntoma en logs:**
```
❌ [FFMPEG] FFmpeg falló con código: 1
❌ [FFMPEG] Output: [mensaje de error de FFmpeg]
```

**Posibles causas:**
1. Video corrupto o formato no soportado
2. Permisos insuficientes para escribir el thumbnail
3. Espacio en disco insuficiente

**Solución:**
1. Verificar que el video es válido
2. Verificar permisos de escritura en `uploads/thumbnails/historias/`
3. Verificar espacio en disco

### **Problema: Thumbnail se genera pero no se muestra**

**Síntoma:**
- El thumbnail se crea en el sistema de archivos
- Pero muestra placeholder en la interfaz

**Solución:**
1. Verificar que la ruta en la base de datos es correcta
2. Verificar que el archivo es una imagen válida (no corrupto)
3. Limpiar caché del navegador
4. Verificar permisos de lectura del archivo

---

## 📊 **COMANDOS ÚTILES PARA DEBUGGING**

### **Verificar thumbnails generados**

**Windows:**
```cmd
dir uploads\thumbnails\historias\
```

**Linux:**
```bash
ls -lh /home/oriola/uploads/thumbnails/historias/
```

### **Probar extracción manual de frame**

```bash
# Desde el directorio de uploads
ffmpeg -i historias/historia_20251103_120031.mp4 \
       -ss 00:00:00.5 \
       -vframes 1 \
       -q:v 2 \
       -vf scale=640:-1 \
       thumbnails/historias/test_manual.jpg
```

### **Ver información del video**

```bash
ffprobe -v error -show_format -show_streams historias/historia_20251103_120031.mp4
```

---

## ✅ **VERIFICACIÓN FINAL**

Después de instalar FFmpeg y regenerar thumbnails:

1. ✅ Crear una nueva historia → Debe generar thumbnail real
2. ✅ Verificar logs → Debe mostrar "✅ [FFMPEG] Thumbnail válido generado"
3. ✅ Verificar archivo → El .jpg debe ser una imagen válida del video
4. ✅ Ver en interfaz → Debe mostrar el frame del video, no el placeholder gris

---

## 📝 **NOTAS IMPORTANTES**

- **FFmpeg es requerido** para generar thumbnails reales del video
- **Sin FFmpeg**, el sistema creará placeholders genéricos automáticamente
- **Los placeholders existentes** pueden regenerarse después de instalar FFmpeg
- **El sistema funcionará** sin FFmpeg, pero solo mostrará placeholders
- **En producción (servidor)**, es **altamente recomendable** instalar FFmpeg

---

## 🚀 **PRÓXIMOS PASOS**

1. **Instalar FFmpeg** en tu PC del trabajo
2. **Instalar FFmpeg** en el servidor NodeLight
3. **Probar** creando una nueva historia
4. **Regenerar thumbnails** de historias existentes
5. **Verificar** que se muestran correctamente en la interfaz

---

**Última actualización:** 3 de noviembre de 2025  
**Estado:** ✅ Mejoras implementadas, documentación completa

