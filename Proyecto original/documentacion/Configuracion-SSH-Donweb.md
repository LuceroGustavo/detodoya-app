# Configuración de Clave SSH para Donweb

**Fecha:** 15 de enero de 2025  
**Servidor:** Donweb Cloud Server  
**Método:** SSH Key Authentication

---

## 🔑 **INFORMACIÓN DE LA CLAVE SSH**

### **Clave SSH Existente (LightNode):**
- **Ubicación:** `C:\Users\LUCERO-PC\.ssh\id_rsa.pub`
- **Tipo:** RSA
- **Email asociado:** `lucerogustavosi@gmail.com`
- **Estado en LightNode:** ✅ Configurada y funcionando

### **Opciones para Donweb:**

#### **Opción 1: Usar la misma clave SSH (Recomendado)**
- **Ventaja:** No necesitas generar una nueva clave
- **Proceso:** Copiar el contenido de `id_rsa.pub` y agregarlo en el panel de Donweb

#### **Opción 2: Crear una nueva clave SSH**
- **Ventaja:** Clave específica para Donweb
- **Proceso:** Generar nueva clave y agregarla en Donweb

---

## 📋 **PASOS PARA CONFIGURAR SSH KEY EN DONWEB**

### **Opción A: Configurar durante la creación del servidor (Panel Donweb)**
*Si no funcionó, usar la Opción B*

### **Opción B: Configurar después de crear el servidor (Recomendado si hubo errores)**

**Ventaja:** Puedes crear el servidor primero y luego configurar SSH sin problemas.

---

### **Paso 1: Obtener la Clave Pública**

#### **Si usas la clave existente (Opción 1):**

**En Windows (PowerShell o CMD):**
```powershell
# Ver el contenido de la clave pública
type C:\Users\LUCERO-PC\.ssh\id_rsa.pub

# O copiar al portapapeles
Get-Content C:\Users\LUCERO-PC\.ssh\id_rsa.pub | Set-Clipboard
```

**Contenido de la clave (ya documentado):**
```
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQDhymR6Y/T5acskpzC7NHy3TJpQCzN6Opz1ySFDU0Xuz/0m6h7eUo+IgQaDJnUqcFgh0D/J0aalGdIfghWdI7q8x9Q07hFvEtQxe9Lyl9vrgc4yMunwr2cMlrYUvzUuQZFSYCZNvFGWPgwH8y3WfTftdEJ9wuOHxNE2azXR8TbJctKn5x2jBHBrbNfm94xSKUjMfxwGvomkqGWJrjfJ+S8e7WZl2uRB/cq+4QlL74nPh8pjIeJmAKoLkCrHr79Gj3sPpPP1lJXxrkHGascpLFEDQveVSakh+THaBd10NFQtG+E4ujFMR4/XMyYFwY5NK4obRs+j2IMKMGLyzj5K6OnhgTdxHvU/BNly0kiXgivsCILrS0xHxp5sShAqtFopkqleSh9rTkxuSLvjenuW8PFySxol+RBSqt57Q3F6uG38KeDs3p0nF9C6GiByd5gwhn6n2mrSgWF3v90LxKQJNFdzAAU7XnI5XrCFS7xE4lhPAmYwV2vw1FZN8CpA93Wrf50v17D3+sB/ZTwgctJOw6XcDWoxBbohLHEAQAxP60D50zasDCHESX719z7uO0ObxLFLtXTiE+n7SDVrLFJHa3Rq78O+F1pTcpcTGKb7EVqwaA66c29zWCNp4QlZRL/B1i6OMhwWaRsO+enJ8xRiTXPB2/Q6IDYXUHGJU4l56bvilQ== lucerogustavosi@gmail.com
```

#### **Si quieres crear una nueva clave (Opción 2):**

**En Windows (PowerShell):**
```powershell
# Generar nueva clave SSH
ssh-keygen -t rsa -b 4096 -C "lucerogustavosi@gmail.com"

# Cuando pregunte dónde guardar:
# Presiona Enter para usar la ubicación por defecto
# O especifica: C:\Users\LUCERO-PC\.ssh\id_rsa_donweb

# Cuando pregunte por passphrase:
# Presiona Enter dos veces para no usar passphrase (o ingresa una si prefieres)

# Ver la clave pública generada
type C:\Users\LUCERO-PC\.ssh\id_rsa_donweb.pub
```

---

### **Paso 2A: Agregar Clave en Panel Donweb (Durante creación)**
*Solo si usas la Opción A*

---

### **Paso 2B: Configurar Clave SSH Manualmente (Después de crear servidor)**
*Usar esta opción si creaste el servidor con contraseña*

Una vez que tengas el servidor creado y la IP pública:

**1. Conectarte al servidor con contraseña:**
```bash
ssh root@[IP_PUBLICA_DONWEB]
# Ingresar la contraseña que configuraste
```

**2. Crear directorio .ssh si no existe:**
```bash
mkdir -p ~/.ssh
chmod 700 ~/.ssh
```

**3. Agregar tu clave pública al servidor:**

**Opción 1: Desde tu máquina Windows (Recomendado)**
```powershell
# En PowerShell de Windows, ejecutar:
type C:\Users\LUCERO-PC\.ssh\id_rsa.pub | ssh root@[IP_PUBLICA_DONWEB] "cat >> ~/.ssh/authorized_keys"
```

**Opción 2: Copiar y pegar manualmente**
```bash
# En el servidor, ejecutar:
nano ~/.ssh/authorized_keys

# Pegar el contenido completo de tu clave pública (id_rsa.pub)
# Guardar con Ctrl+O, Enter, Ctrl+X
```

**4. Configurar permisos correctos:**
```bash
chmod 600 ~/.ssh/authorized_keys
chmod 700 ~/.ssh
```

**5. Verificar que funciona:**
```bash
# Salir del servidor
exit

# Intentar conectar de nuevo (debería funcionar sin contraseña)
ssh root@[IP_PUBLICA_DONWEB]
```

**6. (Opcional) Deshabilitar autenticación por contraseña:**
```bash
# Conectado al servidor, editar configuración SSH:
sudo nano /etc/ssh/sshd_config

# Buscar y cambiar:
# PasswordAuthentication no
# PubkeyAuthentication yes

# Reiniciar servicio SSH:
sudo systemctl restart sshd
```

---

### **Paso 2A: Agregar Clave en Panel Donweb (Durante creación)**

1. **En el panel de Donweb**, en el paso "Elige un método de autenticación":
   - Seleccionar **"SSH Key"** (botón con icono de llave)

2. **Agregar la clave pública:**
   - Si Donweb tiene un campo de texto: Pegar el contenido completo de `id_rsa.pub`
   - Si Donweb tiene opción de subir archivo: Seleccionar el archivo `id_rsa.pub`
   - Si Donweb tiene opción de generar: Usar la opción de agregar clave existente

3. **Verificar que la clave se agregó correctamente:**
   - Debería aparecer un mensaje de confirmación
   - La clave debería mostrarse (parcialmente) en el panel

---

### **Paso 3: Verificar Conexión SSH**

Una vez configurado el servidor y obtenida la IP:

```bash
# Conectar al servidor
ssh root@[IP_PUBLICA_DONWEB]

# La primera vez te preguntará:
# Are you sure you want to continue connecting (yes/no/[fingerprint])?
# Escribe: yes

# Si está bien configurado, deberías conectarte automáticamente sin pedir contraseña
```

---

## ✅ **VENTAJAS DE USAR SSH KEY**

1. **Más seguro:** No se puede adivinar la contraseña
2. **Más rápido:** Conexión automática sin ingresar contraseña
3. **Mejor práctica:** Estándar en servidores de producción
4. **Auditoría:** Mejor rastreo de accesos

---

## 🔧 **TROUBLESHOOTING**

### **Si no puedes conectar con la clave SSH:**

1. **Verificar que la clave pública está correctamente copiada:**
   ```powershell
   # Verificar formato
   type C:\Users\LUCERO-PC\.ssh\id_rsa.pub
   # Debe empezar con: ssh-rsa AAAAB3...
   ```

2. **Verificar permisos del archivo de clave privada:**
   ```powershell
   # En Windows, los permisos generalmente están bien
   # Pero si hay problemas, verificar que el archivo existe:
   Test-Path C:\Users\LUCERO-PC\.ssh\id_rsa
   ```

3. **Verificar que la clave se agregó correctamente en Donweb:**
   - Revisar en el panel que la clave aparece listada
   - Verificar que no hay espacios extra al copiar/pegar

4. **Si sigue sin funcionar, usar contraseña temporalmente:**
   - En el panel de Donweb, cambiar a "Usuario root con contraseña"
   - Configurar una contraseña segura
   - Conectarse y luego configurar la clave SSH manualmente en el servidor

---

## 📝 **NOTAS IMPORTANTES**

- **Mantener la clave privada segura:** Nunca compartas `id_rsa` (sin .pub)
- **Backup de claves:** Considera hacer backup de tus claves SSH
- **Múltiples servidores:** Puedes usar la misma clave pública en múltiples servidores
- **Revocación:** Si comprometes una clave, revócala en todos los servidores

---

## 🔐 **CONFIGURACIÓN ADICIONAL (Opcional)**

Una vez conectado al servidor, puedes mejorar la seguridad:

```bash
# Deshabilitar autenticación por contraseña (solo SSH key)
sudo nano /etc/ssh/sshd_config

# Cambiar:
# PasswordAuthentication no
# PubkeyAuthentication yes

# Reiniciar SSH
sudo systemctl restart sshd
```

---

**Última actualización:** 15 de enero de 2025  
**Estado:** ✅ Configurado en panel Donweb

