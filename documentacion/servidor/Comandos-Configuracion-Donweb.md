# Comandos de Configuración - Servidor Donweb

**Fecha:** 15 de enero de 2025  
**Servidor:** 149.50.144.53 (vps-5469468-x.dattaweb.com)  
**Puerto SSH:** 5638

---

## 🔐 **CONEXIÓN SSH**

```bash
ssh -p5638 root@149.50.144.53
# Contraseña: Qbasic.1977.server
```

---

## ✅ **INFORMACIÓN DEL SERVIDOR (Verificada)**

- **Sistema Operativo:** Ubuntu 24.04.3 LTS ✅
- **Kernel:** 6.8.0-87-generic x86_64 ✅
- **Hostname:** vps-5469468-x ✅
- **IP Pública:** 149.50.144.53 ✅
- **Disco:** 23.84GB (11.3% usado) ✅
- **Memoria:** 8% usado ✅
- **Procesos:** 134 ✅

---

## 📋 **PASO 1: Actualizar Sistema**

```bash
# Actualizar lista de paquetes
sudo apt update

# Actualizar sistema
sudo apt upgrade -y

# Verificar que todo está actualizado
sudo apt list --upgradable
```

---

## 🔑 **PASO 2: Configurar Clave SSH (Opcional pero Recomendado)**

### **Desde tu máquina Windows (PowerShell):**

```powershell
# Copiar clave pública automáticamente al servidor
type C:\Users\LUCERO-PC\.ssh\id_rsa.pub | ssh -p5638 root@149.50.144.53 "mkdir -p ~/.ssh && cat >> ~/.ssh/authorized_keys && chmod 700 ~/.ssh && chmod 600 ~/.ssh/authorized_keys"
```

### **O manualmente desde el servidor:**

```bash
# 1. Conectarte al servidor
ssh -p5638 root@149.50.144.53

# 2. Crear directorio .ssh
mkdir -p ~/.ssh
chmod 700 ~/.ssh

# 3. Editar archivo authorized_keys
nano ~/.ssh/authorized_keys

# 4. Pegar el contenido completo de tu clave pública (id_rsa.pub)
# 5. Guardar: Ctrl+O, Enter, Ctrl+X

# 6. Configurar permisos
chmod 600 ~/.ssh/authorized_keys

# 7. Salir y probar conexión sin contraseña
exit
ssh -p5638 root@149.50.144.53
```

---

## 🔥 **PASO 3: Configurar Firewall (UFW)**

```bash
# Instalar UFW
sudo apt install ufw -y

# Permitir SSH (⚠️ IMPORTANTE: puerto 5638, no 22)
sudo ufw allow 5638/tcp

# Permitir HTTP y HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Permitir puerto de la aplicación
sudo ufw allow 8080/tcp

# Habilitar firewall
sudo ufw enable

# Verificar estado
sudo ufw status verbose
```

## ⚠️ **PASO 3.5: Configurar Firewall Donweb (CRÍTICO)**

**IMPORTANTE:** El firewall del panel de Donweb es independiente del firewall UFW.

### **Pasos:**
1. Acceder a: https://micuenta.donweb.com/es-ar/servicios/cloud-iaas/vps/5469468/configurar/firewall
2. Hacer clic en "Agregar" o "Nueva regla"
3. Configurar:
   - **Protocolo:** TCP
   - **Puerto:** 8080
   - **IPv4:** `0.0.0.0/0` (permitir cualquier IP)
   - **IPv6:** `::/0` (permitir cualquier IP)
4. Guardar la regla

**Sin esta configuración, la aplicación NO será accesible desde Internet aunque UFW esté configurado correctamente.**

---

## ☕ **PASO 4: Instalar Java 17**

```bash
# Instalar OpenJDK 17
sudo apt install openjdk-17-jdk -y

# Verificar instalación
java -version
javac -version

# Debería mostrar: openjdk version "17.x.x"
```

---

## 🗄️ **PASO 5: Instalar MySQL 8.0**

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
- Establecer contraseña para root: `OriolaMySQL2025!`
- Responder "Y" a las preguntas de seguridad

---

## 📦 **PASO 6: Instalar Maven**

```bash
# Instalar Maven
sudo apt install maven -y

# Verificar instalación
mvn -version

# Debería mostrar: Apache Maven 3.x.x
```

---

## 🌐 **PASO 7: Instalar Nginx**

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

## 📁 **PASO 8: Crear Directorios para la Aplicación**

```bash
# Crear directorios
sudo mkdir -p /home/oriola/uploads
sudo mkdir -p /home/oriola/uploads/thumbnails
sudo mkdir -p /home/oriola/backups

# Configurar permisos
sudo chown -R $USER:$USER /home/oriola
sudo chmod -R 755 /home/oriola

# Verificar
ls -la /home/oriola
```

---

## 🗄️ **PASO 9: Configurar Base de Datos MySQL**

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

## 📥 **PASO 10: Clonar Repositorio**

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

## 🚀 **PASO 11: Compilar y Probar Aplicación**

```bash
# Compilar proyecto
mvn clean package -DskipTests

# Verificar que se creó el JAR
ls -la target/oriola-denim-0.0.1-SNAPSHOT.jar

# Verificar que application-donweb.properties tiene server.address=0.0.0.0
grep server.address src/main/resources/application-donweb.properties

# Ejecutar aplicación en segundo plano
nohup java -jar target/oriola-denim-0.0.1-SNAPSHOT.jar --spring.profiles.active=donweb > app.log 2>&1 &

# Ver logs
tail -f app.log

# Verificar que está escuchando en 0.0.0.0:8080 (no solo :::8080)
netstat -tlnp | grep 8080
```

## ⚠️ **PASO 11.5: Verificar Configuración de Red**

### **Problema común:**
Si la aplicación solo escucha en `:::8080` (IPv6) y no en `0.0.0.0:8080` (todas las interfaces), verificar:

```bash
# Verificar configuración en application-donweb.properties
grep server.address src/main/resources/application-donweb.properties

# Debe mostrar: server.address=0.0.0.0

# Si no está, agregarlo:
echo "server.address=0.0.0.0" >> src/main/resources/application-donweb.properties

# Recompilar y reiniciar
mvn clean package -DskipTests
pkill -f oriola-denim
nohup java -jar target/oriola-denim-0.0.1-SNAPSHOT.jar --spring.profiles.active=donweb > app.log 2>&1 &
```

---

## ✅ **VERIFICACIÓN FINAL**

```bash
# Verificar todas las instalaciones
java -version
mysql --version
mvn -version
nginx -v
git --version

# Verificar servicios
sudo systemctl status mysql
sudo systemctl status nginx

# Verificar directorios
ls -la /home/oriola

# Verificar base de datos
sudo mysql -u root -p -e "SHOW DATABASES;"
```

---

## 📝 **NOTAS IMPORTANTES**

- ⚠️ **Puerto SSH:** Siempre usar `-p5638` (no el puerto estándar 22)
- ⚠️ **Firewall UFW:** Configurar puerto 5638 para SSH, no 22
- ⚠️ **Firewall Donweb:** **CRÍTICO** - Configurar puerto 8080 en el panel de Donweb
- ⚠️ **server.address:** Debe ser `0.0.0.0` en `application-donweb.properties` para acceso externo
- ✅ **Sistema actualizado:** Ejecutar `sudo apt update` primero
- ✅ **Backup:** Hacer backup antes de cambios importantes

## 🐛 **SOLUCIÓN DE PROBLEMAS**

### **Problema: La aplicación no es accesible desde Internet**

**Síntomas:**
- La aplicación está corriendo
- El firewall UFW tiene el puerto 8080 abierto
- Pero no se puede acceder desde fuera del servidor

**Solución:**
1. Verificar firewall Donweb: https://micuenta.donweb.com/es-ar/servicios/cloud-iaas/vps/5469468/configurar/firewall
2. Agregar regla TCP para puerto 8080 (IPv4: `0.0.0.0/0`, IPv6: `::/0`)
3. Verificar que `server.address=0.0.0.0` está en `application-donweb.properties`
4. Recompilar y reiniciar la aplicación

---

**Última actualización:** 15 de noviembre de 2025

