# 🚀 Guía Completa - Configuración Nuevo Servidor Donweb

**Fecha de creación:** 15 de enero de 2025  
**Proyecto:** ORIOLA Indumentaria  
**Objetivo:** Configurar servidor nuevo desde cero hasta aplicación funcionando  
**Basado en:** Configuración exitosa del servidor anterior (149.50.144.53)

---

## 📋 **ÍNDICE**

1. [Creación del Servidor en Donweb](#1-creación-del-servidor-en-donweb)
2. [Configuración Inicial del Servidor](#2-configuración-inicial-del-servidor)
3. [Instalación de Software Base](#3-instalación-de-software-base)
4. [Configuración de Base de Datos](#4-configuración-de-base-de-datos)
5. [Configuración de Firewall](#5-configuración-de-firewall)
6. [Clonar y Configurar Aplicación](#6-clonar-y-configurar-aplicación)
7. [Despliegue de la Aplicación](#7-despliegue-de-la-aplicación)
8. [Verificación Final](#8-verificación-final)
9. [Migración de Datos (Opcional)](#9-migración-de-datos-opcional)

---

## 1. **CREACIÓN DEL SERVIDOR EN DONWEB**

### **1.1 Acceder al Panel de Donweb**

1. Ir a: https://micuenta.donweb.com/
2. Iniciar sesión con tus credenciales
3. Navegar a: **Cloud IaaS** → **Crear Servidor**

### **1.2 Configuración del Servidor**

**Especificaciones recomendadas:**
- **Ubicación:** Buenos Aires, Argentina 🇦🇷
- **Sistema Operativo:** Ubuntu 24.04 UEFI ✅
- **CPU:** 1 vCore (mínimo)
- **RAM:** 2 GB (mínimo)
- **Almacenamiento:** 50 GB SSD
- **Arquitectura:** UEFI

**Configuración de Acceso:**
- **Método de autenticación:** Contraseña (inicialmente)
- **Usuario:** `root`
- **Contraseña:** `Qbasic.1977.server` (o la que prefieras, pero documentarla)
- **Puerto SSH:** Puede ser personalizado (ej: 5638) ⚠️ **IMPORTANTE: Anotar el puerto SSH**

### **1.3 Obtener Información del Servidor**

Una vez creado el servidor, obtener del panel:
- ✅ **IP Pública:** `[ANOTAR AQUÍ]`
- ✅ **Hostname:** `[ANOTAR AQUÍ]`
- ✅ **Puerto SSH:** `[ANOTAR AQUÍ]` (puede ser 22 o personalizado)
- ✅ **ID del Servidor:** `[ANOTAR AQUÍ]` (para configurar firewall)

**⚠️ IMPORTANTE:** Guardar toda esta información, la necesitarás durante toda la configuración.

---

## 2. **CONFIGURACIÓN INICIAL DEL SERVIDOR**

### **2.1 Conectarse al Servidor vía SSH**

```bash
# Reemplazar [IP_PUBLICA] y [PUERTO_SSH] con los valores obtenidos
ssh -p[PUERTO_SSH] root@[IP_PUBLICA]

# Ejemplo (si el puerto es 5638):
ssh -p5638 root@149.50.144.53

# Si el puerto es el estándar 22:
ssh root@[IP_PUBLICA]
```

**Credenciales:**
- **Usuario:** `root`
- **Contraseña:** La que configuraste al crear el servidor

### **2.2 Verificar Sistema Operativo**

Una vez conectado, verificar:

```bash
# Ver información del sistema
uname -a
cat /etc/os-release

# Verificar recursos
free -h
df -h

# Verificar Git (debería estar preinstalado en Ubuntu 24.04)
git --version
```

**Deberías ver:**
- **Distribución:** Ubuntu 24.04.x LTS
- **Kernel:** Linux 6.8.x
- **Git:** 2.43.0 o superior (preinstalado)

### **2.3 Actualizar Sistema**

```bash
# Actualizar lista de paquetes
sudo apt update

# Actualizar sistema (puede tomar varios minutos)
sudo apt upgrade -y

# Verificar que todo está actualizado
sudo apt list --upgradable
```

**Nota:** Si hay actualizaciones críticas, puede pedir reiniciar. Si es necesario:
```bash
sudo reboot
# Esperar 1-2 minutos y reconectar
```

### **2.4 Configurar Clave SSH (Opcional pero Recomendado)**

**Desde tu máquina Windows (PowerShell):**

```powershell
# Copiar clave pública automáticamente al servidor
# Reemplazar [PUERTO_SSH] y [IP_PUBLICA] con tus valores
type C:\Users\LUCERO-PC\.ssh\id_rsa.pub | ssh -p[PUERTO_SSH] root@[IP_PUBLICA] "mkdir -p ~/.ssh && cat >> ~/.ssh/authorized_keys && chmod 700 ~/.ssh && chmod 600 ~/.ssh/authorized_keys"
```

**O manualmente desde el servidor:**

```bash
# 1. En el servidor, crear directorio
mkdir -p ~/.ssh
chmod 700 ~/.ssh

# 2. Editar archivo
nano ~/.ssh/authorized_keys

# 3. Pegar el contenido completo de tu clave pública (id_rsa.pub)
#    (Copiar desde: C:\Users\LUCERO-PC\.ssh\id_rsa.pub)

# 4. Guardar: Ctrl+O, Enter, Ctrl+X

# 5. Configurar permisos
chmod 600 ~/.ssh/authorized_keys

# 6. Salir y probar conexión sin contraseña
exit
ssh -p[PUERTO_SSH] root@[IP_PUBLICA]
```

---

## 3. **INSTALACIÓN DE SOFTWARE BASE**

### **3.1 Instalar Java 17 (OpenJDK)**

```bash
# Instalar OpenJDK 17
sudo apt install openjdk-17-jdk -y

# Verificar instalación
java -version
javac -version

# Debería mostrar: openjdk version "17.x.x"
```

**Verificación esperada:**
```
openjdk version "17.0.x" 2024-xx-xx
OpenJDK Runtime Environment (build 17.0.x+x-Ubuntu-...)
OpenJDK 64-Bit Server VM (build 17.0.x+x-Ubuntu-..., mixed mode, sharing)
```

### **3.2 Instalar MySQL 8.0**

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

**Durante `mysql_secure_installation`:**
- **Contraseña para root:** `OriolaMySQL2025!` (o la que prefieras, pero documentarla)
- **Remover usuarios anónimos:** `Y`
- **Deshabilitar login remoto root:** `Y`
- **Remover base de datos test:** `Y`
- **Recargar tabla de privilegios:** `Y`

**Verificar instalación:**
```bash
mysql --version
# Debería mostrar: mysql Ver 8.0.x
```

### **3.3 Instalar Maven**

```bash
# Instalar Maven
sudo apt install maven -y

# Verificar instalación
mvn -version

# Debería mostrar: Apache Maven 3.x.x
```

**Verificación esperada:**
```
Apache Maven 3.9.x
Maven home: /usr/share/maven
Java version: 17.0.x
```

### **3.4 Instalar Nginx (Proxy Reverso)**

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

**Verificar que Nginx está funcionando:**
```bash
# Debería mostrar "active (running)"
sudo systemctl status nginx
```

---

## 4. **CONFIGURACIÓN DE BASE DE DATOS**

### **4.1 Conectar a MySQL**

```bash
# Conectar como root
sudo mysql -u root -p
# Contraseña: OriolaMySQL2025! (o la que configuraste)
```

### **4.2 Crear Base de Datos y Usuario**

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

**Verificación:**
- Deberías ver la base de datos `orioladenim` en la lista
- Deberías ver el usuario `oriola_user` con host `localhost`

### **4.3 Probar Conexión**

```bash
# Probar conexión con el usuario de aplicación
mysql -u oriola_user -p orioladenim
# Contraseña: OriolaDB2025!

# Si conecta correctamente, salir
EXIT;
```

---

## 5. **CONFIGURACIÓN DE FIREWALL**

### **5.1 Instalar y Configurar UFW (Firewall Ubuntu)**

```bash
# Instalar UFW (si no está instalado)
sudo apt install ufw -y

# ⚠️ IMPORTANTE: Permitir SSH PRIMERO (usar el puerto correcto)
# Si tu puerto SSH es 5638:
sudo ufw allow 5638/tcp

# Si tu puerto SSH es el estándar 22:
sudo ufw allow 22/tcp

# Permitir HTTP y HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Permitir puerto de la aplicación (8080)
sudo ufw allow 8080/tcp

# Habilitar firewall
sudo ufw enable

# Verificar estado
sudo ufw status verbose
```

**⚠️ CRÍTICO:** Siempre permitir SSH ANTES de habilitar el firewall, o te quedarás fuera del servidor.

### **5.2 Configurar Firewall Donweb (Panel) - ⚠️ CRÍTICO**

**El firewall del panel de Donweb es INDEPENDIENTE del firewall UFW del servidor.**

**Pasos:**
1. Acceder a: https://micuenta.donweb.com/es-ar/servicios/cloud-iaas/vps/[ID_SERVIDOR]/configurar/firewall
   - Reemplazar `[ID_SERVIDOR]` con el ID de tu servidor
2. Hacer clic en "Agregar" o "Nueva regla"
3. Configurar regla para SSH:
   - **Protocolo:** TCP
   - **Puerto:** `[TU_PUERTO_SSH]` (5638 o 22)
   - **IPv4:** `0.0.0.0/0`
   - **IPv6:** `::/0`
4. Configurar regla para aplicación:
   - **Protocolo:** TCP
   - **Puerto:** `8080`
   - **IPv4:** `0.0.0.0/0`
   - **IPv6:** `::/0`
5. Guardar las reglas

**⚠️ SIN ESTA CONFIGURACIÓN, LA APLICACIÓN NO SERÁ ACCESIBLE DESDE INTERNET aunque UFW esté configurado correctamente.**

---

## 6. **CLONAR Y CONFIGURAR APLICACIÓN**

### **6.1 Crear Directorios para la Aplicación**

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

### **6.2 Clonar Repositorio**

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

**Verificar que existe:**
- `pom.xml`
- `src/`
- `documentacion/`

### **6.3 Verificar/Crear Archivo de Configuración Donweb**

```bash
# Verificar si existe application-donweb.properties
ls -la src/main/resources/application-donweb.properties

# Si no existe, crearlo basado en el template
cat src/main/resources/application-donweb.properties
```

**Si necesitas crear el archivo `application-donweb.properties`:**

```bash
# Crear archivo
nano src/main/resources/application-donweb.properties
```

**Contenido del archivo:**

```properties
# ===========================================
# CONFIGURACIÓN PARA DONWEB
# Servidor: [TU_IP_PUBLICA] - Buenos Aires, Argentina
# ===========================================

# Puerto del servidor
server.port=8080
server.address=0.0.0.0
server.servlet.context-path=/

# ===========================================
# BASE DE DATOS MYSQL (DONWEB)
# ===========================================
spring.datasource.url=jdbc:mysql://localhost:3306/orioladenim?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=oriola_user
spring.datasource.password=OriolaDB2025!
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ===========================================
# CONFIGURACIÓN JPA/HIBERNATE
# ===========================================
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# ===========================================
# CONFIGURACIÓN DE ARCHIVOS (PERSISTENTE)
# ===========================================
file.upload-dir=/home/oriola/uploads
backup.directory=/home/oriola/backups

# Configuración de archivos estáticos
spring.web.resources.static-locations=classpath:/static/,file:/home/oriola/uploads/
upload.path=/home/oriola/uploads
upload.thumbnail.path=/home/oriola/uploads/thumbnails

# ===========================================
# CONFIGURACIÓN DE SUBIDA DE ARCHIVOS
# ===========================================
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=200MB

# ===========================================
# CONFIGURACIÓN DE EMAIL
# ===========================================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:luceroprograma@gmail.com}
spring.mail.password=${MAIL_PASSWORD:kmqh ktkl lhyj gwlf}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com

# ===========================================
# CONFIGURACIÓN DE SEGURIDAD
# ===========================================
spring.security.user.name=admin
spring.security.user.password=${ADMIN_PASSWORD:OriolaAdmin2025!}
spring.security.user.roles=ADMIN

# ===========================================
# CONFIGURACIÓN DE LOGGING
# ===========================================
logging.level.com.orioladenim=INFO
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=WARN
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=WARN

# ===========================================
# CONFIGURACIÓN DE JACKSON (FECHAS)
# ===========================================
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.deserialization.fail-on-unknown-properties=false
```

**⚠️ IMPORTANTE:** 
- `server.address=0.0.0.0` es **CRÍTICO** - permite acceso desde Internet
- Guardar: `Ctrl+O`, `Enter`, `Ctrl+X`

---

## 7. **DESPLIEGUE DE LA APLICACIÓN**

### **7.1 Compilar Proyecto**

```bash
# Asegurarse de estar en el directorio del proyecto
cd /home/oriola/OriolaIndumentaria

# Compilar proyecto (puede tomar varios minutos)
mvn clean package -DskipTests
```

**Verificar compilación exitosa:**
```bash
# Verificar que se creó el JAR
ls -la target/oriola-denim-0.0.1-SNAPSHOT.jar

# Debería mostrar el archivo JAR con tamaño > 0
```

### **7.2 Verificar Configuración de Red**

```bash
# Verificar que application-donweb.properties tiene server.address=0.0.0.0
grep server.address src/main/resources/application-donweb.properties

# Debe mostrar: server.address=0.0.0.0
```

**Si no está, agregarlo:**
```bash
echo "server.address=0.0.0.0" >> src/main/resources/application-donweb.properties
# Recompilar
mvn clean package -DskipTests
```

### **7.3 Ejecutar Aplicación en Segundo Plano**

```bash
# Ejecutar aplicación en segundo plano
nohup java -jar target/oriola-denim-0.0.1-SNAPSHOT.jar --spring.profiles.active=donweb > app.log 2>&1 &

# Esperar unos segundos para que inicie
sleep 10

# Ver logs
tail -f app.log
```

**Presionar `Ctrl+C` para salir de los logs (la aplicación seguirá corriendo).**

### **7.4 Verificar que la Aplicación Está Corriendo**

```bash
# Verificar proceso
ps aux | grep oriola-denim

# Verificar puerto
netstat -tlnp | grep 8080
# O con ss:
ss -tlnp | grep 8080

# Deberías ver algo como:
# tcp6  0  0 :::8080  :::*  LISTEN  [PID]/java
```

**Verificar que está escuchando en todas las interfaces:**
- Debe mostrar `:::8080` o `0.0.0.0:8080` (no solo `127.0.0.1:8080`)

---

## 8. **VERIFICACIÓN FINAL**

### **8.1 Verificar Todas las Instalaciones**

```bash
# Verificar Java
java -version

# Verificar MySQL
mysql --version

# Verificar Maven
mvn -version

# Verificar Nginx
nginx -v

# Verificar Git
git --version
```

### **8.2 Verificar Servicios**

```bash
# Verificar MySQL
sudo systemctl status mysql

# Verificar Nginx
sudo systemctl status nginx

# Verificar aplicación (debe estar corriendo)
ps aux | grep oriola-denim
```

### **8.3 Probar Acceso a la Aplicación**

**Desde el servidor:**
```bash
# Probar acceso local
curl -I http://localhost:8080

# Debería mostrar: HTTP/1.1 200 OK (o similar)
```

**Desde tu máquina local:**
```bash
# Abrir navegador y probar:
http://[TU_IP_PUBLICA]:8080

# Ejemplo:
http://149.50.144.53:8080
```

**⚠️ Si no puedes acceder desde Internet:**
1. Verificar firewall UFW: `sudo ufw status verbose`
2. **Verificar firewall Donweb en el panel** (más común)
3. Verificar que `server.address=0.0.0.0` está configurado
4. Verificar logs: `tail -f /home/oriola/OriolaIndumentaria/app.log`

### **8.4 Verificar Base de Datos**

```bash
# Conectar a MySQL
sudo mysql -u root -p

# Verificar base de datos
SHOW DATABASES;
USE orioladenim;
SHOW TABLES;

# Si Spring Boot creó las tablas, deberías ver varias tablas
# Salir
EXIT;
```

---

## 9. **MIGRACIÓN DE DATOS (OPCIONAL)**

Si tienes datos en el servidor anterior y quieres migrarlos:

### **9.1 Backup del Servidor Anterior**

**Desde el servidor anterior:**
```bash
# Exportar base de datos
mysqldump -u root -p orioladenim > backup_orioladenim.sql

# Comprimir (opcional)
gzip backup_orioladenim.sql
```

**Transferir archivos de uploads:**
```bash
# Desde tu máquina local, usando scp
scp -P[PUERTO_SSH_ANTERIOR] -r root@[IP_ANTERIOR]:/home/oriola/uploads/* /ruta/local/temporal/

# O desde el servidor anterior directamente al nuevo
scp -P[PUERTO_SSH_NUEVO] -r /home/oriola/uploads/* root@[IP_NUEVA]:/home/oriola/uploads/
```

### **9.2 Restaurar en Nuevo Servidor**

**Base de datos:**
```bash
# Transferir backup al nuevo servidor
scp -P[PUERTO_SSH_NUEVO] backup_orioladenim.sql root@[IP_NUEVA]:/home/oriola/

# En el nuevo servidor
cd /home/oriola
mysql -u root -p orioladenim < backup_orioladenim.sql
```

**Archivos:**
```bash
# Los archivos de uploads ya deberían estar en /home/oriola/uploads
# Verificar permisos
sudo chown -R $USER:$USER /home/oriola/uploads
sudo chmod -R 755 /home/oriola/uploads
```

---

## ✅ **CHECKLIST FINAL**

Antes de considerar el despliegue completo, verificar:

- [ ] Servidor accesible vía SSH
- [ ] Sistema actualizado (`sudo apt update && sudo apt upgrade`)
- [ ] Java 17 instalado y funcionando
- [ ] MySQL 8.0 instalado y funcionando
- [ ] Maven instalado y funcionando
- [ ] Nginx instalado y funcionando
- [ ] Firewall UFW configurado (puertos: SSH, 80, 443, 8080)
- [ ] **Firewall Donweb configurado (puertos: SSH, 8080)** ⚠️ CRÍTICO
- [ ] Base de datos `orioladenim` creada
- [ ] Usuario `oriola_user` creado con permisos
- [ ] Directorios `/home/oriola/uploads` creados
- [ ] Repositorio clonado
- [ ] `application-donweb.properties` creado con `server.address=0.0.0.0`
- [ ] Aplicación compilada exitosamente
- [ ] Aplicación corriendo (verificar con `ps aux | grep oriola-denim`)
- [ ] Aplicación accesible desde Internet (`http://[IP_PUBLICA]:8080`)
- [ ] Base de datos con tablas creadas (si aplica)

---

## 🐛 **SOLUCIÓN DE PROBLEMAS COMUNES**

### **Problema: No puedo acceder a la aplicación desde Internet**

**Síntomas:**
- La aplicación está corriendo (`ps aux | grep oriola-denim` muestra proceso)
- El firewall UFW tiene el puerto 8080 abierto
- Pero no se puede acceder desde fuera del servidor

**Solución:**
1. **Verificar firewall Donweb:** https://micuenta.donweb.com/es-ar/servicios/cloud-iaas/vps/[ID]/configurar/firewall
2. Agregar regla TCP para puerto 8080 (IPv4: `0.0.0.0/0`, IPv6: `::/0`)
3. Verificar que `server.address=0.0.0.0` está en `application-donweb.properties`
4. Recompilar y reiniciar la aplicación

### **Problema: Error al conectar a MySQL**

**Síntomas:**
- La aplicación no inicia
- Logs muestran error de conexión a MySQL

**Solución:**
```bash
# Verificar que MySQL está corriendo
sudo systemctl status mysql

# Verificar que el usuario existe
sudo mysql -u root -p
SELECT user, host FROM mysql.user WHERE user = 'oriola_user';

# Verificar permisos
SHOW GRANTS FOR 'oriola_user'@'localhost';
```

### **Problema: Puerto 8080 ya está en uso**

**Síntomas:**
- Error al iniciar: "Address already in use"

**Solución:**
```bash
# Ver qué está usando el puerto 8080
sudo lsof -i :8080
# O
sudo netstat -tlnp | grep 8080

# Detener el proceso
sudo kill -9 [PID]

# O si es otra instancia de la aplicación:
pkill -f oriola-denim
```

---

## 📝 **NOTAS IMPORTANTES**

### **⚠️ ADVERTENCIAS:**
1. **Puerto SSH:** Siempre anotar el puerto SSH (puede ser 22 o personalizado)
2. **Firewall Donweb:** **CRÍTICO** - Configurar en el panel además de UFW
3. **server.address:** Debe ser `0.0.0.0` para acceso externo
4. **Contraseñas:** Documentar todas las contraseñas en lugar seguro
5. **Backup:** Hacer backup antes de cambios importantes

### **✅ RECOMENDACIONES:**
1. Configurar clave SSH para mayor seguridad
2. Configurar Nginx como proxy reverso (opcional)
3. Configurar SSL con Let's Encrypt (opcional)
4. Configurar backup automático de base de datos
5. Monitorear logs regularmente

---

## 📞 **CONTACTO Y SOPORTE**

### **Donweb:**
- **Sitio web:** https://micuenta.donweb.com/
- **Soporte:** Disponible en español
- **Panel de control:** Acceso desde https://micuenta.donweb.com/

### **Documentación del Proyecto:**
- **Repositorio:** https://github.com/LuceroGustavo/OriolaIndumentaria
- **Rama principal:** `master`

---

## 🎉 **¡FELICITACIONES!**

Si llegaste hasta aquí y todos los checks están completos, tu aplicación debería estar funcionando correctamente en el nuevo servidor.

**Próximos pasos opcionales:**
- Configurar Nginx como proxy reverso
- Configurar SSL con Let's Encrypt
- Configurar dominio personalizado
- Configurar backup automático
- Optimizar rendimiento

---

**Última actualización:** 15 de enero de 2025  
**Versión:** 1.0  
**Basado en:** Configuración exitosa del servidor 149.50.144.53


