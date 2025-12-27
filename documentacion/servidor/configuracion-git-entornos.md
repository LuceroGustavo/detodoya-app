# Configuración Git para Entornos Múltiples

**Fecha:** 13 de octubre de 2025  
**Commit:** #69 - 12426b9  
**Autor:** Sistema de Desarrollo  
**Estado:** Implementado y funcionando

---

## 🎯 **PROBLEMA IDENTIFICADO**

### **Situación anterior:**
- Las carpetas `uploads/` y `backups/` contenían archivos específicos de cada entorno
- Al hacer `git push` al GitHub, se subían estos archivos
- Al hacer `git pull` en otro equipo/servidor, se sobrescribían los archivos locales
- Esto causaba conflictos y pérdida de datos específicos de cada entorno

### **Entornos afectados:**
- **PC de casa** (desarrollo local)
- **PC del trabajo** (desarrollo local)
- **Servidor LightNode** (producción)
- **Railway** (staging/testing)

---

## 🔧 **SOLUCIÓN IMPLEMENTADA**

### **1. Configuración del .gitignore**

Se agregaron las siguientes reglas al archivo `.gitignore`:

```gitignore
# Archivos de datos específicos de entorno (no sincronizar entre equipos)
uploads/*
!uploads/.gitkeep
backups/*
!backups/.gitkeep

# Archivos temporales adicionales
*.tmp
*.temp
```

### **2. Archivos .gitkeep creados**

Para mantener la estructura de carpetas sin sincronizar archivos:

```bash
# Archivos creados
uploads/.gitkeep
backups/.gitkeep
```

### **3. Limpieza del repositorio**

Se removieron del tracking de Git todos los archivos existentes:

```bash
git rm -r --cached uploads/
git rm -r --cached backups/
```

**Archivos removidos:** 47 archivos (imágenes + thumbnails)

---

## 📁 **ESTRUCTURA RESULTANTE**

### **✅ Se sincronizan entre entornos:**
```
proyecto/
├── src/                    ← Código fuente
├── static/                 ← Archivos estáticos
├── templates/              ← Plantillas HTML
├── documentacion/          ← Documentación del proyecto
├── uploads/               ← Solo estructura (.gitkeep)
│   └── .gitkeep          ← Mantiene la carpeta
├── backups/               ← Solo estructura (.gitkeep)
│   └── .gitkeep          ← Mantiene la carpeta
└── .gitignore            ← Configuración de ignorados
```

### **❌ NO se sincronizan entre entornos:**
- `uploads/*` → Imágenes de productos específicas de cada entorno
- `backups/*` → Backups de base de datos específicos de cada entorno

---

## 🔄 **FLUJO DE TRABAJO**

### **Desarrollo diario:**
```bash
# Trabajar normalmente
git add .
git commit -m "70 - Descripción del cambio"
git push origin master
```

### **Actualización en servidor:**
```bash
# En el servidor LightNode
git pull origin master
```

### **Resultado:**
- ✅ **Código se sincroniza** entre todos los entornos
- ✅ **Archivos de datos** permanecen específicos de cada entorno
- ✅ **Estructura de carpetas** se mantiene en todos los entornos

---

## 🎯 **BENEFICIOS OBTENIDOS**

### **1. Aislamiento de datos:**
- Cada entorno mantiene sus propios archivos de `uploads/`
- Cada entorno mantiene sus propios archivos de `backups/`
- No hay conflictos entre entornos

### **2. Sincronización de código:**
- El código fuente se sincroniza correctamente
- Las configuraciones se comparten entre entornos
- La documentación se mantiene actualizada

### **3. Mantenimiento simplificado:**
- Un solo comando `git pull` actualiza el código
- No hay riesgo de sobrescribir datos importantes
- Estructura de carpetas consistente

---

## 🚨 **CONSIDERACIONES IMPORTANTES**

### **Para sincronizar datos entre entornos:**
- **Usar el sistema de backup/restore** ya implementado
- **Transferir archivos manualmente** cuando sea necesario
- **Usar scripts de sincronización** si se requiere automatización

### **Para nuevos desarrolladores:**
- Las carpetas `uploads/` y `backups/` se crearán automáticamente
- Los archivos `.gitkeep` mantienen la estructura
- No es necesario configurar nada adicional

---

## 📋 **COMANDOS DE VERIFICACIÓN**

### **Verificar configuración:**
```bash
# Ver archivos ignorados
git status --ignored

# Ver contenido de .gitignore
cat .gitignore | grep -A 5 "uploads\|backups"
```

### **Verificar estructura:**
```bash
# Verificar que existen los .gitkeep
ls -la uploads/.gitkeep
ls -la backups/.gitkeep
```

---

## 🔗 **ARCHIVOS RELACIONADOS**

- `.gitignore` → Configuración principal
- `uploads/.gitkeep` → Mantiene estructura de uploads
- `backups/.gitkeep` → Mantiene estructura de backups
- `Changelog.md` → Registro de cambios

---

## 📝 **NOTAS TÉCNICAS**

- **Commit implementado:** #69 - 12426b9
- **Fecha de implementación:** 13 de octubre de 2025
- **Archivos afectados:** 47 archivos removidos del tracking
- **Compatibilidad:** Total con el flujo de trabajo existente

---

**✅ Esta configuración resuelve definitivamente el problema de sincronización de archivos específicos de entorno mientras mantiene la sincronización del código fuente.**
