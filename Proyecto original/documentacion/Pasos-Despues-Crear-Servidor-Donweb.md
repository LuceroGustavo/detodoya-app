# Pasos Después de Crear el Servidor Donweb

**Fecha:** 15 de enero de 2025  
**Estado:** ⏳ Servidor en creación

---

## 📋 **CHECKLIST POST-CREACIÓN**

Una vez que el servidor termine de crearse, seguir estos pasos en orden:

---

## 🔍 **PASO 1: Obtener Información del Servidor**

### **1.1 Obtener IP Pública** ✅ COMPLETADO
- [x] IP Pública: `149.50.144.53` ✅
- [x] Hostname: `vps-5469468-x.dattaweb.com` ✅
- [x] Puerto SSH: `5638` ✅ (⚠️ No es el puerto estándar 22)

### **1.2 Documentar en Configuración**
- [ ] Actualizar `documentacion/Configuracion-Servidor-Donweb.md` con la IP
- [ ] Actualizar `documentacion/Migracion-Servidor-Donweb.md` con la IP

---

## 🔐 **PASO 2: Probar Conexión SSH**

### **2.1 Conectar al Servidor**
```bash
# ⚠️ IMPORTANTE: Usar el puerto 5638, no el puerto estándar 22
ssh -p5638 root@149.50.144.53

# O usando el hostname:
ssh -p5638 root@vps-5469468-x.dattaweb.com
```

**Credenciales:**
- **Usuario:** `root`
- **Contraseña:** `Qbasic.1977.server`

### **2.2 Verificar Sistema**
Una vez conectado, verificar:
```bash
# Ver información del sistema
uname -a
cat /etc/os-release

# Verificar recursos
free -h
df -h

# Verificar Git (debería estar instalado)
git --version
```

---

## 🔑 **PASO 3: Configurar Clave SSH (Opcional pero Recomendado)**

### **3.1 Desde tu Máquina Windows**

**Opción A: Copiar automáticamente (Recomendado)**
```powershell
# En PowerShell de Windows:
# ⚠️ IMPORTANTE: Usar el puerto 5638
type C:\Users\LUCERO-PC\.ssh\id_rsa.pub | ssh -p5638 root@149.50.144.53 "mkdir -p ~/.ssh && cat >> ~/.ssh/authorized_keys && chmod 700 ~/.ssh && chmod 600 ~/.ssh/authorized_keys"
```

**Opción B: Copiar manualmente**
```bash
# 1. En Windows, ver la clave:
type C:\Users\LUCERO-PC\.ssh\id_rsa.pub

# 2. Conectarte al servidor (⚠️ usar puerto 5638):
ssh -p5638 root@149.50.144.53

# 3. En el servidor, crear directorio:
mkdir -p ~/.ssh
chmod 700 ~/.ssh

# 4. Editar archivo:
nano ~/.ssh/authorized_keys

# 5. Pegar el contenido completo de la clave pública
# 6. Guardar: Ctrl+O, Enter, Ctrl+X

# 7. Configurar permisos:
chmod 600 ~/.ssh/authorized_keys
```

### **3.2 Probar Conexión sin Contraseña**
```bash
# Salir del servidor
exit

# Intentar conectar de nuevo (debería funcionar sin contraseña)
ssh -p5638 root@149.50.144.53
```

---

## 🔥 **PASO 4: Configurar Firewall**

### **4.1 Instalar y Configurar UFW (Firewall)**
```bash
# Conectado al servidor:
sudo apt update
sudo apt install ufw -y

# Permitir SSH (⚠️ IMPORTANTE: usar el puerto 5638, no el 22)
sudo ufw allow 5638/tcp

# Permitir HTTP y HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Permitir puerto de la aplicación (8080)
sudo ufw allow 8080/tcp

# Habilitar firewall
sudo ufw enable

# Verificar estado
sudo ufw status
```

---

## 🛠️ **PASO 5: Actualizar Sistema**

```bash
# Actualizar lista de paquetes
sudo apt update

# Actualizar sistema
sudo apt upgrade -y

# Reiniciar si es necesario
sudo reboot
```

---

## ☕ **PASO 6: Instalar Java 17**

```bash
# Instalar OpenJDK 17
sudo apt install openjdk-17-jdk -y

# Verificar instalación
java -version
javac -version
```

**Debería mostrar:** `openjdk version "17.x.x"`

---

## 🗄️ **PASO 7: Instalar MySQL 8.0**

```bash
# Instalar MySQL Server
sudo apt install mysql-server -y

# Iniciar MySQL
sudo systemctl start mysql
sudo systemctl enable mysql

# Verificar estado
sudo systemctl status mysql

# Configurar seguridad (ejecutar script interactivo)
sudo mysql_secure_installation
```

**Durante la configuración:**
- Establecer contraseña para root: `OriolaMySQL2025!` (o la que prefieras)
- Responder "Y" a las preguntas de seguridad

---

## 📦 **PASO 8: Instalar Maven**

```bash
# Instalar Maven
sudo apt install maven -y

# Verificar instalación
mvn -version
```

**Debería mostrar:** `Apache Maven 3.x.x`

---

## 🌐 **PASO 9: Instalar Nginx**

```bash
# Instalar Nginx
sudo apt install nginx -y

# Iniciar Nginx
sudo systemctl start nginx
sudo systemctl enable nginx

# Verificar estado
sudo systemctl status nginx

# Verificar versión
nginx -v
```

---

## 📁 **PASO 10: Crear Directorios para la Aplicación**

```bash
# Crear directorios
sudo mkdir -p /home/oriola/uploads
sudo mkdir -p /home/oriola/uploads/thumbnails
sudo mkdir -p /home/oriola/backups

# Configurar permisos
sudo chown -R $USER:$USER /home/oriola
sudo chmod -R 755 /home/oriola
```

---

## 🗄️ **PASO 11: Configurar Base de Datos MySQL**

```bash
# Conectar a MySQL como root
sudo mysql -u root -p
# Contraseña: OriolaMySQL2025!
```

**Dentro de MySQL, ejecutar:**
```sql
-- Crear base de datos
CREATE DATABASE orioladenim CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario de aplicación
CREATE USER 'oriola_user'@'localhost' IDENTIFIED BY 'OriolaDB2025!';

-- Otorgar permisos
GRANT ALL PRIVILEGES ON orioladenim.* TO 'oriola_user'@'localhost';

-- Aplicar cambios
FLUSH PRIVILEGES;

-- Verificar
SHOW DATABASES;
SELECT user, host FROM mysql.user WHERE user = 'oriola_user';

-- Salir
EXIT;
```

---

## 📥 **PASO 12: Clonar Repositorio**

```bash
# Ir al directorio home
cd /home/oriola

# Clonar repositorio
git clone https://github.com/LuceroGustavo/OriolaIndumentaria.git

# Entrar al directorio
cd OriolaIndumentaria

# Verificar que se clonó correctamente
ls -la
```

---

## 🚀 **PASO 13: Compilar y Probar Aplicación**

```bash
# Compilar proyecto
mvn clean package -DskipTests

# Verificar que se creó el JAR
ls -la target/oriola-denim-0.0.1-SNAPSHOT.jar

# Ejecutar aplicación (prueba)
java -jar target/oriola-denim-0.0.1-SNAPSHOT.jar --spring.profiles.active=donweb
```

**Nota:** Esto iniciará la aplicación. Presiona `Ctrl+C` para detenerla después de verificar que funciona.

---

## ✅ **VERIFICACIÓN FINAL**

- [ ] Servidor accesible vía SSH
- [ ] Clave SSH configurada (opcional)
- [ ] Firewall configurado
- [ ] Java 17 instalado
- [ ] MySQL 8.0 instalado y configurado
- [ ] Maven instalado
- [ ] Nginx instalado
- [ ] Base de datos `orioladenim` creada
- [ ] Usuario `oriola_user` creado
- [ ] Repositorio clonado
- [ ] Aplicación compila correctamente

---

## 📝 **PRÓXIMOS PASOS**

Una vez completados estos pasos:
1. Migrar datos desde LightNode (backup de base de datos)
2. Migrar archivos de uploads
3. Configurar Nginx como proxy reverso
4. Configurar dominio y SSL
5. Desplegar aplicación en producción

---

**Última actualización:** 15 de enero de 2025

