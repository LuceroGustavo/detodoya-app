# Instrucciones Git - Detodoya.com

**Fecha:** Enero 2025  
**Estado:** ✅ Repositorio inicializado

---

## ✅ Estado Actual

- ✅ Repositorio Git inicializado
- ✅ Primer commit realizado (240 archivos, 56,691 líneas)
- ✅ Rama principal: `main`
- ✅ `.gitignore` configurado correctamente

---

## 📋 Comandos para Subir a GitHub

### **1. Crear Repositorio en GitHub**

1. Ve a [GitHub.com](https://github.com)
2. Click en "New repository" (o "+" → "New repository")
3. Nombre: `detodoya-app` (o el que prefieras)
4. Descripción: "Catálogo profesional de productos Detodoya.com"
5. **NO** inicialices con README, .gitignore o licencia (ya los tenemos)
6. Click en "Create repository"

---

### **2. Conectar Repositorio Local con GitHub**

Una vez creado el repositorio en GitHub, ejecuta estos comandos:

```bash
# Agregar el remote (reemplaza USERNAME con tu usuario de GitHub)
git remote add origin https://github.com/USERNAME/detodoya-app.git

# Verificar que se agregó correctamente
git remote -v
```

**O si prefieres usar SSH:**
```bash
git remote add origin git@github.com:USERNAME/detodoya-app.git
```

---

### **3. Subir el Código a GitHub**

```bash
# Subir la rama main al repositorio remoto
git push -u origin main
```

Si GitHub te pide autenticación:
- **HTTPS:** Te pedirá usuario y contraseña (o token personal)
- **SSH:** Debes tener configurada tu clave SSH

---

## 🔐 Autenticación en GitHub

### **Opción 1: Personal Access Token (HTTPS)**

1. GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token (classic)
3. Selecciona permisos: `repo` (acceso completo)
4. Copia el token generado
5. Úsalo como contraseña cuando Git te la pida

### **Opción 2: SSH Key (Recomendado)**

Si ya tienes una clave SSH configurada, úsala. Si no:

```bash
# Generar nueva clave SSH (si no tienes)
ssh-keygen -t ed25519 -C "tu-email@ejemplo.com"

# Copiar la clave pública
cat ~/.ssh/id_ed25519.pub

# Agregar la clave en GitHub:
# Settings → SSH and GPG keys → New SSH key
```

---

## 📝 Comandos Útiles

### **Ver estado del repositorio:**
```bash
git status
```

### **Ver historial de commits:**
```bash
git log --oneline
```

### **Ver ramas:**
```bash
git branch
```

### **Agregar cambios y hacer commit:**
```bash
git add .
git commit -m "Descripción de los cambios"
git push
```

### **Ver remotes configurados:**
```bash
git remote -v
```

---

## ⚠️ Archivos Ignorados

El `.gitignore` está configurado para ignorar:
- ✅ `target/` - Archivos compilados
- ✅ `application-local.properties` - Configuración local
- ✅ `uploads/*` - Archivos subidos
- ✅ `backups/*` - Backups
- ✅ `.idea/`, `.vscode/` - Configuración de IDEs

---

## 🎯 Próximos Pasos

1. ✅ Crear repositorio en GitHub
2. ✅ Conectar con `git remote add origin`
3. ✅ Subir con `git push -u origin main`
4. ✅ Configurar GitHub Actions (opcional, para CI/CD)

---

**Última actualización:** Enero 2025

