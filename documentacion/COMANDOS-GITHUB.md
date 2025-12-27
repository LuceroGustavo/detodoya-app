# Comandos para Subir a GitHub - Detodoya.com

**Usuario GitHub:** `LuceroGustavo`  
**Repositorio:** `detodoya-app`

---

## 🚀 Opción 1: Usar el Script Automático

```powershell
# Desde la raíz del proyecto
.\scripts\subir-github.ps1
```

El script te guiará paso a paso.

---

## 📋 Opción 2: Comandos Manuales

### **Paso 1: Crear Repositorio en GitHub**

1. Ve a: https://github.com/new
2. **Repository name:** `detodoya-app`
3. **Description:** `Catálogo profesional de productos Detodoya.com`
4. **Visibility:** Private o Public (tu elección)
5. **NO marques:**
   - ❌ Add a README file
   - ❌ Add .gitignore
   - ❌ Choose a license
6. Click en **"Create repository"**

---

### **Paso 2: Conectar y Subir**

```powershell
# Conectar con GitHub
git remote add origin https://github.com/LuceroGustavo/detodoya-app.git

# Verificar que se agregó correctamente
git remote -v

# Subir el código
git push -u origin main
```

---

## 🔐 Autenticación

### **Si te pide usuario y contraseña:**

GitHub ya no acepta contraseñas. Necesitas un **Personal Access Token**:

1. Ve a: https://github.com/settings/tokens
2. Click en **"Generate new token (classic)"**
3. Nombre: `detodoya-app-token`
4. Selecciona permisos: ✅ **repo** (acceso completo)
5. Click en **"Generate token"**
6. **Copia el token** (solo se muestra una vez)
7. Cuando Git te pida:
   - **Username:** `LuceroGustavo`
   - **Password:** Pega el token (no tu contraseña)

---

## 🔑 Opción 3: Usar SSH (Recomendado)

Si tienes SSH configurado:

```powershell
# Conectar con SSH
git remote add origin git@github.com:LuceroGustavo/detodoya-app.git

# Subir
git push -u origin main
```

---

## ✅ Verificar que Funcionó

Después de subir, verifica en:
- https://github.com/LuceroGustavo/detodoya-app

Deberías ver todos los archivos del proyecto.

---

## 🛠️ Comandos Útiles

### **Ver estado:**
```powershell
git status
```

### **Ver remotes:**
```powershell
git remote -v
```

### **Cambiar remote (si es necesario):**
```powershell
git remote remove origin
git remote add origin https://github.com/LuceroGustavo/detodoya-app.git
```

### **Forzar push (si el repositorio tiene contenido):**
```powershell
git push -u origin main --force
```

⚠️ **Cuidado:** `--force` sobrescribe el contenido remoto.

---

## 📝 Notas

- El repositorio local ya tiene el commit inicial
- Todos los archivos están listos para subir
- El `.gitignore` está configurado correctamente
- Los archivos sensibles (`application-local.properties`) están ignorados

---

**Última actualización:** Enero 2025

