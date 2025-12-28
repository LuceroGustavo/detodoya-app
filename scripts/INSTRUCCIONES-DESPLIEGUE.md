# 🚀 Instrucciones de Despliegue - Detodoya.com

## 📋 **PREPARACIÓN**

### **1. Verificar que tienes acceso SSH:**
```powershell
# En PowerShell, verificar SSH
ssh -V
```

Si no tienes SSH, puedes instalarlo o usar PuTTY.

### **2. Verificar que los scripts están listos:**
- ✅ `scripts/deploy-detodoya-donweb.sh`
- ✅ `scripts/menu-deploy-detodoya-donweb.sh`

---

## 🔐 **PASO 1: CONECTAR AL SERVIDOR**

```bash
ssh -p5638 root@149.50.144.53
# Contraseña: Qbasic.1977.server
```

---

## 📁 **PASO 2: PREPARAR DIRECTORIOS EN EL SERVIDOR**

Una vez conectado al servidor, ejecuta:

```bash
# Crear directorio para Detodoya
mkdir -p /home/detodoya
mkdir -p /home/detodoya/scripts
mkdir -p /home/detodoya/uploads
mkdir -p /home/detodoya/backups

# Dar permisos
chmod -R 755 /home/detodoya
```

---

## 📤 **PASO 3: SUBIR SCRIPTS AL SERVIDOR**

**Desde Windows (PowerShell), en el directorio del proyecto:**

```powershell
# Subir script de despliegue
scp -P5638 scripts/deploy-detodoya-donweb.sh root@149.50.144.53:/home/detodoya/scripts/

# Subir script del menú
scp -P5638 scripts/menu-deploy-detodoya-donweb.sh root@149.50.144.53:/home/detodoya/scripts/

# Hacer scripts ejecutables (en el servidor)
ssh -p5638 root@149.50.144.53 "chmod +x /home/detodoya/scripts/*.sh"
```

---

## 📦 **PASO 4: CLONAR O SUBIR EL PROYECTO**

### **Opción A: Clonar desde GitHub (Recomendado)**

```bash
# En el servidor
cd /home/detodoya
git clone https://github.com/LuceroGustavo/detodoya-app.git Detodoya.com
# O el repositorio donde esté el proyecto
```

### **Opción B: Subir proyecto completo (si no está en GitHub)**

```powershell
# Desde Windows, comprimir el proyecto (sin node_modules, target, etc.)
# Luego subir y descomprimir en el servidor
```

---

## 🗄️ **PASO 5: VERIFICAR BASE DE DATOS**

```bash
# En el servidor, conectar a MySQL
mysql -u root -p

# Dentro de MySQL:
SHOW DATABASES;  # Debe aparecer 'detodoya'
USE detodoya;
SHOW TABLES;  # Verificar tablas
exit;
```

Si la base de datos no existe, crearla:

```bash
mysql -u root -p << EOF
CREATE DATABASE IF NOT EXISTS detodoya CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'detodoya_user'@'localhost' IDENTIFIED BY 'DetodoyaDB2025!';
GRANT ALL PRIVILEGES ON detodoya.* TO 'detodoya_user'@'localhost';
FLUSH PRIVILEGES;
EOF
```

---

## 🚀 **PASO 6: DESPLEGAR LA APLICACIÓN**

### **Opción A: Despliegue Automático Completo**

```bash
# En el servidor
cd /home/detodoya/Detodoya.com
/home/detodoya/scripts/deploy-detodoya-donweb.sh
```

### **Opción B: Menú Interactivo**

```bash
# En el servidor
cd /home/detodoya/Detodoya.com
/home/detodoya/scripts/menu-deploy-detodoya-donweb.sh
```

---

## ✅ **PASO 7: VERIFICAR DESPLIEGUE**

```bash
# Verificar que la aplicación está corriendo
ps aux | grep detodoya

# Verificar puerto 8080
netstat -tlnp | grep 8080

# Verificar que Fulbito sigue en 8081 (NO TOCAR)
netstat -tlnp | grep 8081

# Ver logs
tail -f /home/detodoya/Detodoya.com/app.log
```

---

## 🌐 **PASO 8: PROBAR ACCESO**

- **IP directa:** http://149.50.144.53:8080
- **Dominio:** http://detodoya.com:8080 (si DNS está configurado)
- **Admin:** http://149.50.144.53:8080/admin

---

## ⚠️ **IMPORTANTE**

1. **NO TOCAR Fulbito** - Está en puerto 8081 y es una aplicación virtual
2. **Verificar puerto 8080** - Debe estar libre antes de desplegar
3. **Base de datos `detodoya`** - Debe existir antes de iniciar
4. **Usuario `detodoya_user`** - Debe tener permisos en la base de datos

---

## 🔧 **COMANDOS ÚTILES**

### **Ver estado de aplicaciones:**
```bash
ps aux | grep -E "(detodoya|8080|8081)"
netstat -tlnp | grep -E "(8080|8081)"
```

### **Parar Detodoya:**
```bash
pkill -f "detodoya-0.0.1-SNAPSHOT.jar"
```

### **Iniciar Detodoya manualmente:**
```bash
cd /home/detodoya/Detodoya.com
nohup java -jar target/detodoya-0.0.1-SNAPSHOT.jar --spring.profiles.active=donweb > app.log 2>&1 &
```

### **Ver logs en tiempo real:**
```bash
tail -f /home/detodoya/Detodoya.com/app.log
```

---

## 📞 **SOLUCIÓN DE PROBLEMAS**

### **Error: Puerto 8080 ocupado**
```bash
# Ver qué está usando el puerto
netstat -tlnp | grep 8080
# Si es Detodoya, pararlo primero
pkill -f "detodoya-0.0.1-SNAPSHOT.jar"
```

### **Error: Base de datos no encontrada**
```bash
# Crear base de datos (ver PASO 5)
mysql -u root -p
```

### **Error: Permisos denegados**
```bash
# Dar permisos a directorios
chmod -R 755 /home/detodoya
chown -R root:root /home/detodoya
```

---

**Última actualización:** 28 de diciembre de 2025

