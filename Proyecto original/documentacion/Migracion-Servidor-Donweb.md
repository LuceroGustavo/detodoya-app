# Migración a Servidor Donweb - ORIOLA Indumentaria

**Fecha de creación:** 15 de enero de 2025  
**Proveedor:** Donweb  
**Propósito:** Migración del servidor de LightNode a Donweb (Argentina, pesos)  
**Estado:** 🔄 **EN PROCESO**

---

## 🎯 **OBJETIVO DE LA MIGRACIÓN**

Migrar la aplicación ORIOLA Indumentaria desde el servidor LightNode (`149.104.92.116`) a un nuevo servidor Donweb con características similares, manteniendo toda la funcionalidad y datos.

---

## 🖥️ **INFORMACIÓN DEL NUEVO SERVIDOR DONWEB**

### **Datos Básicos:**
- **IP Pública:** `[PENDIENTE - Obtener del panel]`
- **IP Privada:** `[PENDIENTE - Obtener del panel]`
- **Hostname:** `[PENDIENTE - Obtener del panel]`
- **Usuario:** `[PENDIENTE - Configurar]`
- **Ubicación:** Argentina 🇦🇷
- **Sistema Operativo:** ✅ **Ubuntu 24.04 UEFI** (Instalado)

### **Especificaciones Técnicas:**
- **CPU:** 1 vCore (similar a LightNode)
- **RAM:** 2 GB (similar a LightNode)
- **Almacenamiento:** 50 GB SSD (similar a LightNode)
- **Red:** Cloud Server Donweb
- **Arquitectura:** UEFI

### **Software Preinstalado:**
- **Git:** ✅ 2.43.0 (Ya instalado)
- **Linux Kernel:** 6.8.0
- **Perl:** 5.38.2
- **Postfix:** 3.8.6 (Servidor de correo)
- **Python:** 3.12.3

### **Acceso SSH:**
- **Usuario:** `root` ✅
- **Método:** ✅ **Contraseña** (Configurado inicialmente)
- **Contraseña:** `Qbasic.1977.server` ✅
- **Puerto SSH:** `5638` ✅ (⚠️ No es el puerto estándar 22)
- **Comando:** `ssh -p5638 root@149.50.144.53` ✅
- **Hostname alternativo:** `ssh -p5638 root@vps-5469468-x.dattaweb.com`
- **SSH Key:** ⏳ Se configurará manualmente después de crear el servidor
- **Archivo local (para configurar después):** `C:\Users\LUCERO-PC\.ssh\id_rsa.pub`

---

## 📋 **CHECKLIST DE MIGRACIÓN**

### **FASE 1: PREPARACIÓN DEL SERVIDOR DONWEB**

#### **1.1 Configuración Inicial del Servidor**
- [x] Configurar clave SSH en panel Donweb ✅
- [ ] Acceder al servidor vía SSH con clave
- [ ] Actualizar sistema operativo
- [ ] Configurar firewall (puertos 22, 80, 443, 8080)
- [ ] Crear usuario `oriola` (opcional, o usar root)

#### **1.2 Instalación de Software Base**
- [x] Git 2.43.0 (✅ Ya preinstalado)
- [ ] Instalar Java 17 (OpenJDK)
- [ ] Instalar MySQL 8.0
- [ ] Instalar Maven 3.8+
- [ ] Instalar Nginx (proxy reverso)
- [ ] Verificar versiones instaladas

#### **1.3 Configuración de Base de Datos**
- [ ] Configurar MySQL (usuario root)
- [ ] Crear base de datos `orioladenim`
- [ ] Crear usuario `oriola_user` con permisos
- [ ] Configurar acceso remoto (si es necesario)
- [ ] Probar conexión desde local

---

### **FASE 2: MIGRACIÓN DE DATOS**

#### **2.1 Backup del Servidor Actual (LightNode)**
- [ ] Exportar base de datos completa
- [ ] Descargar archivos de uploads (`/home/oriola/uploads`)
- [ ] Verificar integridad de backups

#### **2.2 Restauración en Donweb**
- [ ] Importar base de datos en nuevo servidor
- [ ] Subir archivos de uploads a `/home/oriola/uploads`
- [ ] Verificar permisos de archivos
- [ ] Probar acceso a archivos

---

### **FASE 3: CONFIGURACIÓN DE LA APLICACIÓN**

#### **3.1 Clonar y Configurar Proyecto**
- [ ] Clonar repositorio desde GitHub
- [ ] Crear archivo `application-donweb.properties`
- [ ] Configurar credenciales de base de datos
- [ ] Configurar rutas de archivos
- [ ] Verificar configuración de email

#### **3.2 Compilación y Despliegue**
- [ ] Compilar proyecto con Maven
- [ ] Verificar que el JAR se generó correctamente
- [ ] Ejecutar aplicación con perfil `donweb`
- [ ] Verificar que la aplicación inicia correctamente
- [ ] Probar acceso a la aplicación

---

### **FASE 4: CONFIGURACIÓN DE DOMINIO Y SSL**

#### **4.1 Configuración DNS**
- [ ] Actualizar registro A: `orioladenim.com.ar` → `[IP_DONWEB]`
- [ ] Verificar propagación DNS
- [ ] Configurar CNAME para www (si aplica)

#### **4.2 Configuración Nginx**
- [ ] Configurar proxy reverso a puerto 8080
- [ ] Configurar SSL con Let's Encrypt
- [ ] Configurar redirección HTTP → HTTPS
- [ ] Probar acceso vía dominio

---

### **FASE 5: VERIFICACIÓN Y PRUEBAS**

#### **5.1 Pruebas Funcionales**
- [ ] Acceder a página principal
- [ ] Probar catálogo de productos
- [ ] Probar detalle de producto
- [ ] Probar formulario de contacto
- [ ] Probar panel de administración
- [ ] Probar carga de imágenes
- [ ] Probar creación/edición de productos

#### **5.2 Pruebas de Rendimiento**
- [ ] Verificar tiempos de carga
- [ ] Verificar acceso a imágenes
- [ ] Verificar conexión a base de datos
- [ ] Verificar logs de errores

---

## 🛠️ **COMANDOS DE INSTALACIÓN**

### **1. Actualizar Sistema**
```bash
sudo apt update
sudo apt upgrade -y
```

### **2. Instalar Java 17**
```bash
sudo apt install openjdk-17-jdk -y
java -version
```

### **3. Instalar MySQL 8.0**
```bash
sudo apt install mysql-server -y
sudo systemctl start mysql
sudo systemctl enable mysql
sudo mysql_secure_installation
```

### **4. Instalar Maven**
```bash
sudo apt install maven -y
mvn -version
```

### **5. Instalar Git**
```bash
sudo apt install git -y
git --version
```

### **6. Instalar Nginx**
```bash
sudo apt install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx
```

---

## 🗄️ **CONFIGURACIÓN DE BASE DE DATOS**

### **1. Crear Base de Datos y Usuario**
```sql
-- Conectar como root
mysql -u root -p

-- Crear base de datos
CREATE DATABASE orioladenim CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario
CREATE USER 'oriola_user'@'localhost' IDENTIFIED BY 'OriolaDB2025!';

-- Otorgar permisos
GRANT ALL PRIVILEGES ON orioladenim.* TO 'oriola_user'@'localhost';
FLUSH PRIVILEGES;

-- Verificar
SHOW DATABASES;
SELECT user, host FROM mysql.user WHERE user = 'oriola_user';
```

### **2. Importar Base de Datos desde Backup**
```bash
# Si tienes un backup SQL
mysql -u root -p orioladenim < backup_orioladenim.sql

# O desde el servidor anterior
mysqldump -u root -p orioladenim > backup_orioladenim.sql
# Luego transferir y restaurar en nuevo servidor
```

---

## 📁 **CONFIGURACIÓN DE DIRECTORIOS**

### **1. Crear Directorios**
```bash
sudo mkdir -p /home/oriola/uploads
sudo mkdir -p /home/oriola/uploads/thumbnails
sudo mkdir -p /home/oriola/backups
sudo chown -R $USER:$USER /home/oriola
sudo chmod -R 755 /home/oriola
```

### **2. Transferir Archivos desde LightNode**
```bash
# Desde tu máquina local o desde LightNode
# Opción 1: Usando scp
scp -r root@149.104.92.116:/home/oriola/uploads/* [usuario]@[IP_DONWEB]:/home/oriola/uploads/

# Opción 2: Usando rsync
rsync -avz root@149.104.92.116:/home/oriola/uploads/ [usuario]@[IP_DONWEB]:/home/oriola/uploads/
```

---

## ⚙️ **CONFIGURACIÓN DE LA APLICACIÓN**

### **1. Clonar Repositorio**
```bash
cd /home/oriola
git clone https://github.com/LuceroGustavo/OriolaIndumentaria.git
cd OriolaIndumentaria
```

### **2. Crear Archivo de Configuración Donweb**
El archivo `application-donweb.properties` debe crearse con la configuración del nuevo servidor (ver sección siguiente).

### **3. Compilar Proyecto**
```bash
mvn clean package -DskipTests
```

### **4. Ejecutar Aplicación**
```bash
# Ejecutar en segundo plano
nohup java -jar target/oriola-denim-0.0.1-SNAPSHOT.jar --spring.profiles.active=donweb > app.log 2>&1 &

# Ver logs
tail -f app.log
```

---

## 🌐 **CONFIGURACIÓN DE NGINX**

### **1. Crear Configuración de Sitio**
```bash
sudo nano /etc/nginx/sites-available/orioladenim
```

### **2. Configuración Básica (HTTP)**
```nginx
server {
    listen 80;
    server_name orioladenim.com.ar www.orioladenim.com.ar;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Archivos estáticos
    location /uploads/ {
        alias /home/oriola/uploads/;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
```

### **3. Habilitar Sitio**
```bash
sudo ln -s /etc/nginx/sites-available/orioladenim /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### **4. Configurar SSL con Let's Encrypt**
```bash
sudo apt install certbot python3-certbot-nginx -y
sudo certbot --nginx -d orioladenim.com.ar -d www.orioladenim.com.ar
```

---

## 🔐 **CREDENCIALES Y ACCESOS**

### **Credenciales a Configurar en Donweb:**

#### **MySQL - Usuario Administrador:**
- **Usuario:** `root`
- **Contraseña:** `[CONFIGURAR]` (puede ser la misma o nueva)

#### **MySQL - Usuario de Aplicación:**
- **Usuario:** `oriola_user`
- **Contraseña:** `OriolaDB2025!` (mantener igual para compatibilidad)
- **Base de datos:** `orioladenim`

#### **SSH:**
- **Usuario:** `[CONFIGURAR]` (root o usuario personalizado)
- **Contraseña/Clave:** `[CONFIGURAR]`

---

## 📊 **COMPARACIÓN DE SERVIDORES**

| Aspecto | LightNode (Actual) | Donweb (Nuevo) |
|--------|-------------------|----------------|
| **IP Pública** | 149.104.92.116 | [PENDIENTE] |
| **Ubicación** | Buenos Aires, AR | Buenos Aires, AR |
| **CPU** | 1 vCore | 1 vCore |
| **RAM** | 2 GB | 2 GB |
| **Almacenamiento** | 50 GB SSD | 50 GB SSD |
| **Costo** | $7.71 USD/mes | [PENDIENTE] ARS |
| **Moneda** | USD | ARS (Pesos) |

---

## 🚨 **NOTAS IMPORTANTES**

1. **Backup Completo:** Asegúrate de hacer backup completo antes de migrar
2. **DNS:** El cambio de DNS puede tardar hasta 48 horas en propagarse
3. **Downtime:** Planifica un tiempo de inactividad mínimo durante la migración
4. **Pruebas:** Prueba exhaustivamente antes de cambiar el DNS
5. **Rollback:** Ten un plan de rollback por si algo sale mal

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

## ✅ **ESTADO DE LA MIGRACIÓN**

**Última actualización:** 15 de noviembre de 2025  
**Estado actual:** ✅ **APLICACIÓN FUNCIONANDO**  
**URL de acceso:** `http://149.50.144.53:8080`

### **Progreso Completado:**
- [x] Servidor Donweb creado ✅
- [x] Sistema Operativo: Ubuntu 24.04 UEFI instalado ✅
- [x] Git 2.43.0 preinstalado ✅
- [x] Configurar acceso SSH (puerto 5638) ✅
- [x] Configurar firewall UFW ✅
- [x] **Configurar firewall Donweb (puerto 8080)** ✅ **CRÍTICO**
- [x] Instalar software (Java 17, MySQL 8.0, Maven, Nginx) ✅
- [x] Configurar base de datos `orioladenim` ✅
- [x] Clonar repositorio ✅
- [x] Crear `application-donweb.properties` ✅
- [x] Configurar `server.address=0.0.0.0` ✅
- [x] Compilar aplicación ✅
- [x] Desplegar aplicación ✅
- [x] **Aplicación accesible desde Internet** ✅

### **Próximos Pasos:**
- [ ] Configurar Nginx como proxy reverso
- [ ] Configurar SSL con Let's Encrypt
- [ ] Actualizar DNS (orioladenim.com.ar → 149.50.144.53)
- [ ] Migrar datos desde LightNode (base de datos y archivos)
- [ ] Pruebas funcionales completas

---

**Nota:** Este documento debe actualizarse conforme se avance en la migración. Mantenerlo actualizado con los datos reales del servidor Donweb.


**Fecha de creación:** 15 de enero de 2025  
**Proveedor:** Donweb  
**Propósito:** Migración del servidor de LightNode a Donweb (Argentina, pesos)  
**Estado:** 🔄 **EN PROCESO**

---

## 🎯 **OBJETIVO DE LA MIGRACIÓN**

Migrar la aplicación ORIOLA Indumentaria desde el servidor LightNode (`149.104.92.116`) a un nuevo servidor Donweb con características similares, manteniendo toda la funcionalidad y datos.

---

## 🖥️ **INFORMACIÓN DEL NUEVO SERVIDOR DONWEB**

### **Datos Básicos:**
- **IP Pública:** `[PENDIENTE - Obtener del panel]`
- **IP Privada:** `[PENDIENTE - Obtener del panel]`
- **Hostname:** `[PENDIENTE - Obtener del panel]`
- **Usuario:** `[PENDIENTE - Configurar]`
- **Ubicación:** Argentina 🇦🇷
- **Sistema Operativo:** ✅ **Ubuntu 24.04 UEFI** (Instalado)

### **Especificaciones Técnicas:**
- **CPU:** 1 vCore (similar a LightNode)
- **RAM:** 2 GB (similar a LightNode)
- **Almacenamiento:** 50 GB SSD (similar a LightNode)
- **Red:** Cloud Server Donweb
- **Arquitectura:** UEFI

### **Software Preinstalado:**
- **Git:** ✅ 2.43.0 (Ya instalado)
- **Linux Kernel:** 6.8.0
- **Perl:** 5.38.2
- **Postfix:** 3.8.6 (Servidor de correo)
- **Python:** 3.12.3

### **Acceso SSH:**
- **Método:** ✅ **SSH Key** (Configurado)
- **Usuario:** `root`
- **Clave SSH:** Usar la misma clave que LightNode o crear nueva
- **Archivo local (LightNode):** `C:\Users\LUCERO-PC\.ssh\id_rsa.pub`
- **Comando:** `ssh root@[IP_PUBLICA]`

---

## 📋 **CHECKLIST DE MIGRACIÓN**

### **FASE 1: PREPARACIÓN DEL SERVIDOR DONWEB**

#### **1.1 Configuración Inicial del Servidor**
- [x] Configurar clave SSH en panel Donweb ✅
- [ ] Acceder al servidor vía SSH con clave
- [ ] Actualizar sistema operativo
- [ ] Configurar firewall (puertos 22, 80, 443, 8080)
- [ ] Crear usuario `oriola` (opcional, o usar root)

#### **1.2 Instalación de Software Base**
- [x] Git 2.43.0 (✅ Ya preinstalado)
- [ ] Instalar Java 17 (OpenJDK)
- [ ] Instalar MySQL 8.0
- [ ] Instalar Maven 3.8+
- [ ] Instalar Nginx (proxy reverso)
- [ ] Verificar versiones instaladas

#### **1.3 Configuración de Base de Datos**
- [ ] Configurar MySQL (usuario root)
- [ ] Crear base de datos `orioladenim`
- [ ] Crear usuario `oriola_user` con permisos
- [ ] Configurar acceso remoto (si es necesario)
- [ ] Probar conexión desde local

---

### **FASE 2: MIGRACIÓN DE DATOS**

#### **2.1 Backup del Servidor Actual (LightNode)**
- [ ] Exportar base de datos completa
- [ ] Descargar archivos de uploads (`/home/oriola/uploads`)
- [ ] Verificar integridad de backups

#### **2.2 Restauración en Donweb**
- [ ] Importar base de datos en nuevo servidor
- [ ] Subir archivos de uploads a `/home/oriola/uploads`
- [ ] Verificar permisos de archivos
- [ ] Probar acceso a archivos

---

### **FASE 3: CONFIGURACIÓN DE LA APLICACIÓN**

#### **3.1 Clonar y Configurar Proyecto**
- [ ] Clonar repositorio desde GitHub
- [ ] Crear archivo `application-donweb.properties`
- [ ] Configurar credenciales de base de datos
- [ ] Configurar rutas de archivos
- [ ] Verificar configuración de email

#### **3.2 Compilación y Despliegue**
- [ ] Compilar proyecto con Maven
- [ ] Verificar que el JAR se generó correctamente
- [ ] Ejecutar aplicación con perfil `donweb`
- [ ] Verificar que la aplicación inicia correctamente
- [ ] Probar acceso a la aplicación

---

### **FASE 4: CONFIGURACIÓN DE DOMINIO Y SSL**

#### **4.1 Configuración DNS**
- [ ] Actualizar registro A: `orioladenim.com.ar` → `[IP_DONWEB]`
- [ ] Verificar propagación DNS
- [ ] Configurar CNAME para www (si aplica)

#### **4.2 Configuración Nginx**
- [ ] Configurar proxy reverso a puerto 8080
- [ ] Configurar SSL con Let's Encrypt
- [ ] Configurar redirección HTTP → HTTPS
- [ ] Probar acceso vía dominio

---

### **FASE 5: VERIFICACIÓN Y PRUEBAS**

#### **5.1 Pruebas Funcionales**
- [ ] Acceder a página principal
- [ ] Probar catálogo de productos
- [ ] Probar detalle de producto
- [ ] Probar formulario de contacto
- [ ] Probar panel de administración
- [ ] Probar carga de imágenes
- [ ] Probar creación/edición de productos

#### **5.2 Pruebas de Rendimiento**
- [ ] Verificar tiempos de carga
- [ ] Verificar acceso a imágenes
- [ ] Verificar conexión a base de datos
- [ ] Verificar logs de errores

---

## 🛠️ **COMANDOS DE INSTALACIÓN**

### **1. Actualizar Sistema**
```bash
sudo apt update
sudo apt upgrade -y
```

### **2. Instalar Java 17**
```bash
sudo apt install openjdk-17-jdk -y
java -version
```

### **3. Instalar MySQL 8.0**
```bash
sudo apt install mysql-server -y
sudo systemctl start mysql
sudo systemctl enable mysql
sudo mysql_secure_installation
```

### **4. Instalar Maven**
```bash
sudo apt install maven -y
mvn -version
```

### **5. Instalar Git**
```bash
sudo apt install git -y
git --version
```

### **6. Instalar Nginx**
```bash
sudo apt install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx
```

---

## 🗄️ **CONFIGURACIÓN DE BASE DE DATOS**

### **1. Crear Base de Datos y Usuario**
```sql
-- Conectar como root
mysql -u root -p

-- Crear base de datos
CREATE DATABASE orioladenim CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario
CREATE USER 'oriola_user'@'localhost' IDENTIFIED BY 'OriolaDB2025!';

-- Otorgar permisos
GRANT ALL PRIVILEGES ON orioladenim.* TO 'oriola_user'@'localhost';
FLUSH PRIVILEGES;

-- Verificar
SHOW DATABASES;
SELECT user, host FROM mysql.user WHERE user = 'oriola_user';
```

### **2. Importar Base de Datos desde Backup**
```bash
# Si tienes un backup SQL
mysql -u root -p orioladenim < backup_orioladenim.sql

# O desde el servidor anterior
mysqldump -u root -p orioladenim > backup_orioladenim.sql
# Luego transferir y restaurar en nuevo servidor
```

---

## 📁 **CONFIGURACIÓN DE DIRECTORIOS**

### **1. Crear Directorios**
```bash
sudo mkdir -p /home/oriola/uploads
sudo mkdir -p /home/oriola/uploads/thumbnails
sudo mkdir -p /home/oriola/backups
sudo chown -R $USER:$USER /home/oriola
sudo chmod -R 755 /home/oriola
```

### **2. Transferir Archivos desde LightNode**
```bash
# Desde tu máquina local o desde LightNode
# Opción 1: Usando scp
scp -r root@149.104.92.116:/home/oriola/uploads/* [usuario]@[IP_DONWEB]:/home/oriola/uploads/

# Opción 2: Usando rsync
rsync -avz root@149.104.92.116:/home/oriola/uploads/ [usuario]@[IP_DONWEB]:/home/oriola/uploads/
```

---

## ⚙️ **CONFIGURACIÓN DE LA APLICACIÓN**

### **1. Clonar Repositorio**
```bash
cd /home/oriola
git clone https://github.com/LuceroGustavo/OriolaIndumentaria.git
cd OriolaIndumentaria
```

### **2. Crear Archivo de Configuración Donweb**
El archivo `application-donweb.properties` debe crearse con la configuración del nuevo servidor (ver sección siguiente).

### **3. Compilar Proyecto**
```bash
mvn clean package -DskipTests
```

### **4. Ejecutar Aplicación**
```bash
# Ejecutar en segundo plano
nohup java -jar target/oriola-denim-0.0.1-SNAPSHOT.jar --spring.profiles.active=donweb > app.log 2>&1 &

# Ver logs
tail -f app.log
```

---

## 🌐 **CONFIGURACIÓN DE NGINX**

### **1. Crear Configuración de Sitio**
```bash
sudo nano /etc/nginx/sites-available/orioladenim
```

### **2. Configuración Básica (HTTP)**
```nginx
server {
    listen 80;
    server_name orioladenim.com.ar www.orioladenim.com.ar;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Archivos estáticos
    location /uploads/ {
        alias /home/oriola/uploads/;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
```

### **3. Habilitar Sitio**
```bash
sudo ln -s /etc/nginx/sites-available/orioladenim /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### **4. Configurar SSL con Let's Encrypt**
```bash
sudo apt install certbot python3-certbot-nginx -y
sudo certbot --nginx -d orioladenim.com.ar -d www.orioladenim.com.ar
```

---

## 🔐 **CREDENCIALES Y ACCESOS**

### **Credenciales a Configurar en Donweb:**

#### **MySQL - Usuario Administrador:**
- **Usuario:** `root`
- **Contraseña:** `[CONFIGURAR]` (puede ser la misma o nueva)

#### **MySQL - Usuario de Aplicación:**
- **Usuario:** `oriola_user`
- **Contraseña:** `OriolaDB2025!` (mantener igual para compatibilidad)
- **Base de datos:** `orioladenim`

#### **SSH:**
- **Usuario:** `[CONFIGURAR]` (root o usuario personalizado)
- **Contraseña/Clave:** `[CONFIGURAR]`

---

## 📊 **COMPARACIÓN DE SERVIDORES**

| Aspecto | LightNode (Actual) | Donweb (Nuevo) |
|--------|-------------------|----------------|
| **IP Pública** | 149.104.92.116 | [PENDIENTE] |
| **Ubicación** | Buenos Aires, AR | Buenos Aires, AR |
| **CPU** | 1 vCore | 1 vCore |
| **RAM** | 2 GB | 2 GB |
| **Almacenamiento** | 50 GB SSD | 50 GB SSD |
| **Costo** | $7.71 USD/mes | [PENDIENTE] ARS |
| **Moneda** | USD | ARS (Pesos) |

---

## 🚨 **NOTAS IMPORTANTES**

1. **Backup Completo:** Asegúrate de hacer backup completo antes de migrar
2. **DNS:** El cambio de DNS puede tardar hasta 48 horas en propagarse
3. **Downtime:** Planifica un tiempo de inactividad mínimo durante la migración
4. **Pruebas:** Prueba exhaustivamente antes de cambiar el DNS
5. **Rollback:** Ten un plan de rollback por si algo sale mal

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

## ✅ **ESTADO DE LA MIGRACIÓN**

**Última actualización:** 15 de enero de 2025  
**Estado actual:** 🔄 **EN PROCESO - SERVIDOR EN CREACIÓN**  
**Próximo paso:** Esperar que termine la creación y obtener IP pública

### **Progreso:**
- [x] Servidor Donweb en proceso de creación
- [x] Sistema Operativo: Ubuntu 24.04 UEFI seleccionado
- [x] Usuario root configurado con contraseña
- [x] Git 2.43.0 preinstalado (se instalará automáticamente)
- [x] ⏳ Servidor creado exitosamente ✅
- [x] Obtener IP pública del servidor ✅ (149.50.144.53)
- [x] Probar conexión SSH con contraseña (puerto 5638) ✅
- [x] Verificar sistema Ubuntu 24.04.3 LTS ✅
- [ ] Actualizar sistema operativo
- [ ] Configurar clave SSH manualmente
- [ ] Configurar firewall
- [ ] Instalar software restante (Java, MySQL, Maven, Nginx)

---

**Nota:** Este documento debe actualizarse conforme se avance en la migración. Mantenerlo actualizado con los datos reales del servidor Donweb.

