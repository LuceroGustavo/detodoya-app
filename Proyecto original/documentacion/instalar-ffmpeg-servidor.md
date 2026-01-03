# Instalación de FFmpeg en Servidor NodeLight

**Fecha:** 3 de noviembre de 2025  
**Servidor:** NodeLight - 149.104.92.116  
**Objetivo:** Instalar FFmpeg para generar thumbnails reales de videos en producción

---

## 🚀 **COMANDOS PARA INSTALAR FFMPEG**

### **1. Conectar al Servidor**
```bash
ssh root@149.104.92.116
# Contraseña: Qbasic.1977.server!
```

### **2. Actualizar Paquetes**
```bash
apt update
```

### **3. Instalar FFmpeg**
```bash
apt install ffmpeg -y
```

### **4. Verificar Instalación**
```bash
ffmpeg -version
```

**Deberías ver:**
```
ffmpeg version 4.x.x or higher
...
```

---

## ✅ **VERIFICACIÓN**

### **Probar Extracción de Frame Manual**
```bash
# Ir al directorio de uploads
cd /home/oriola/uploads

# Probar con un video existente
ffmpeg -i historias/historia_*.mp4 \
       -ss 00:00:00.5 \
       -vframes 1 \
       -q:v 2 \
       -vf scale=640:-1 \
       -f image2 \
       -update 1 \
       thumbnails/historias/test_manual.jpg

# Verificar que se creó
ls -lh thumbnails/historias/test_manual.jpg
```

---

## 🔄 **REgenerar Thumbnails Existentes**

Después de instalar FFmpeg, necesitas regenerar los thumbnails de las historias existentes que tienen placeholders.

### **Opción 1: Usar el Endpoint desde el Servidor**
```bash
# Si la aplicación está corriendo en el servidor
curl -X POST http://localhost:8080/admin/historias/regenerar-todos-thumbnails
```

### **Opción 2: Usar el Panel Admin**
1. Conectar a: `http://149.104.92.116:8080/admin/historias`
2. Usar el endpoint: `POST /admin/historias/regenerar-todos-thumbnails`

---

## 📝 **NOTAS IMPORTANTES**

- ✅ **FFmpeg se instala globalmente** en el servidor
- ✅ **No necesita reiniciar** la aplicación Java, debería detectarlo automáticamente
- ✅ **Regenera thumbnails** de historias existentes después de instalar
- ✅ **Nuevas historias** generarán thumbnails reales automáticamente

---

## 🔍 **VERIFICAR EN LOGS**

Después de instalar FFmpeg y crear una nueva historia, revisa los logs de la aplicación:

```bash
# Ver logs de Spring Boot
# (Si están en un archivo de log)
tail -f /path/to/application.log

# O si están en nohup
tail -f nohup.out
```

**Deberías ver:**
```
✅ [FFMPEG] FFmpeg disponible: ffmpeg version...
✅ [FFMPEG] FFmpeg ejecutado exitosamente
✅ [FFMPEG] Thumbnail válido generado: XXXX bytes, 640x1139
```

---

## 🎯 **RESUMEN DE PASOS**

1. ✅ Conectar al servidor: `ssh root@149.104.92.116`
2. ✅ Instalar FFmpeg: `apt install ffmpeg -y`
3. ✅ Verificar: `ffmpeg -version`
4. ✅ Probar manualmente (opcional): Extraer un frame de prueba
5. ✅ Regenerar thumbnails existentes desde el panel admin
6. ✅ Crear nueva historia para verificar

---

**Última actualización:** 3 de noviembre de 2025  
**Estado:** ✅ **INSTALADO Y FUNCIONANDO EN PRODUCCIÓN**

### **Instalación Completada:**
- **Fecha de instalación:** 3 de noviembre de 2025
- **Versión instalada:** `ffmpeg version 4.4.2-0ubuntu0.22.04.1`
- **Ubicación:** `/usr/bin/ffmpeg`
- **Estado:** ✅ Funcionando correctamente, generando thumbnails reales de videos

