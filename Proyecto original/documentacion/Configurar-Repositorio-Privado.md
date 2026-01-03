# Configuración de Repositorio Privado en el Servidor

**Fecha:** 30 de diciembre de 2025  
**Servidor:** 66.97.45.252  
**Repositorio:** https://github.com/LuceroGustavo/OriolaIndumentaria  
**Estado:** ✅ **CONFIGURACIÓN COMPLETADA**

---

## 📋 **PASOS PARA CONFIGURAR REPOSITORIO PRIVADO**

### **1. Cambiar repositorio a privado en GitHub**

1. Ve a: https://github.com/LuceroGustavo/OriolaIndumentaria/settings
2. Baja hasta la sección **"Danger Zone"**
3. Haz clic en **"Change visibility"**
4. Selecciona **"Make private"**
5. Confirma escribiendo el nombre del repositorio: `LuceroGustavo/OriolaIndumentaria`
6. Haz clic en **"I understand, change repository visibility"**

---

### **2. Crear Personal Access Token (PAT) en GitHub**

1. Ve a: https://github.com/settings/tokens
2. Haz clic en **"Generate new token"** → **"Generate new token (classic)"**
3. Completa el formulario:
   - **Note:** `Oriola-Server-Deploy`
   - **Expiration:** Elige una fecha (recomendado: 90 días o sin expiración)
   - **Select scopes:** Marca solo **`repo`** (acceso completo a repositorios privados)
4. Haz clic en **"Generate token"**
5. **⚠️ IMPORTANTE:** Copia el token inmediatamente (solo se muestra una vez)
   - Formato: `ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`

---

### **3. Configurar autenticación en el servidor**

Una vez que tengas el token, ejecuta estos comandos en el servidor:

#### **Opción A: Usar Personal Access Token (PAT) - Recomendado**

```bash
# Conectarse al servidor
ssh -p5625 root@66.97.45.252

# Ir al directorio del proyecto
cd /home/oriola/OriolaIndumentaria

# Configurar Git para usar el token
# Reemplaza [TU_TOKEN] con el token que copiaste
git remote set-url origin https://[TU_TOKEN]@github.com/LuceroGustavo/OriolaIndumentaria.git

# Verificar que funciona
git fetch origin

# Si funciona, verás información sobre las ramas remotas
```

#### **Opción B: Usar Git Credential Helper (Más seguro)**

```bash
# Conectarse al servidor
ssh -p5625 root@66.97.45.252

# Configurar Git Credential Helper
git config --global credential.helper store

# Hacer un pull para que pida credenciales
cd /home/oriola/OriolaIndumentaria
git pull origin master

# Cuando pida credenciales:
# Username: [TU_USUARIO_GITHUB]
# Password: [TU_TOKEN_PAT] (no uses tu contraseña, usa el token)
```

---

### **4. Verificar que funciona**

```bash
# Probar que puede hacer fetch/pull
cd /home/oriola/OriolaIndumentaria
git fetch origin
git pull origin master

# Si no da errores, está funcionando correctamente ✅
```

---

## 🔒 **SEGURIDAD**

### **⚠️ IMPORTANTE:**

1. **Nunca compartas tu token públicamente**
2. **No subas el token al repositorio**
3. **Si el token se compromete, revócalo inmediatamente en GitHub**
4. **Considera usar SSH keys en lugar de PAT para mayor seguridad**

---

## 🔑 **ALTERNATIVA: Usar SSH Keys (Más seguro)**

Si prefieres usar SSH keys en lugar de PAT:

### **1. Generar SSH key en el servidor:**

```bash
ssh -p5625 root@66.97.45.252

# Generar SSH key (si no existe)
ssh-keygen -t ed25519 -C "oriola-server@66.97.45.252" -f ~/.ssh/id_ed25519

# Mostrar la clave pública
cat ~/.ssh/id_ed25519.pub
```

### **2. Agregar SSH key a GitHub:**

1. Copia el contenido de `~/.ssh/id_ed25519.pub`
2. Ve a: https://github.com/settings/keys
3. Haz clic en **"New SSH key"**
4. **Title:** `Oriola Server - 66.97.45.252`
5. **Key:** Pega el contenido de la clave pública
6. Haz clic en **"Add SSH key"**

### **3. Cambiar URL del repositorio a SSH:**

```bash
cd /home/oriola/OriolaIndumentaria
git remote set-url origin git@github.com:LuceroGustavo/OriolaIndumentaria.git

# Verificar
git fetch origin
```

---

## ✅ **VERIFICACIÓN FINAL**

Después de configurar, verifica que todo funciona:

```bash
# En el servidor
cd /home/oriola/OriolaIndumentaria
git status
git fetch origin
git pull origin master

# Si todo funciona sin errores, está correctamente configurado ✅
```

---

## 📝 **NOTAS**

- El token PAT es más fácil de configurar pero menos seguro que SSH keys
- SSH keys no expiran (a menos que las revoques manualmente)
- Los tokens PAT pueden tener fecha de expiración
- Para producción, se recomienda SSH keys

---

## ✅ **ESTADO ACTUAL**

### **Configuración Completada:**
- ✅ Repositorio cambiado a privado en GitHub
- ✅ Personal Access Token creado: `Oriola-Server-Deploy`
- ✅ Autenticación configurada en servidor (66.97.45.252)
- ✅ Autenticación configurada localmente
- ✅ Verificación exitosa: Pull y Push funcionando correctamente

### **Detalles de Configuración:**
- **Método utilizado:** Personal Access Token (PAT)
- **Token configurado en:** URL del repositorio remoto
- **Servidor:** `/home/oriola/OriolaIndumentaria/.git/config`
- **Local:** `.git/config` (en el proyecto local)

### **Verificación:**
```bash
# En el servidor
cd /home/oriola/OriolaIndumentaria
git fetch origin  # ✅ Funciona
git pull origin master  # ✅ Funciona

# Localmente
git push origin master  # ✅ Funciona
```

---

**Última actualización:** 30 de diciembre de 2025  
**Estado:** ✅ **CONFIGURACIÓN COMPLETADA Y VERIFICADA**

