# Configuración del Servidor LightNode - Oriola Indumentaria

**Fecha:** 30 de septiembre de 2025  
**Servidor:** LightNode - Buenos Aires, Argentina  
**Propósito:** Servidor de producción para aplicación Spring Boot  
**Estado:** ✅ DESPLEGADO Y FUNCIONANDO (11 de Octubre, 2025)  
**Commit:** 60

---

## 🖥️ **DATOS DEL SERVIDOR**

### **Información Básica:**
- **IP Pública:** `149.104.92.116`
- **IP Privada:** `172.16.1.167`
- **Hostname:** `ul9vrgdk`
- **Ubicación:** Buenos Aires, Argentina 🇦🇷
- **Sistema Operativo:** Ubuntu 22.04.5 LTS

### **Especificaciones:**
- **CPU:** 1 vCore (Compartido)
- **RAM:** 2 GB
- **Almacenamiento:** 50 GB SSD
- **Red:** BGP (Alta disponibilidad)

---

## 🔐 **CONTRASEÑAS Y ACCESOS**

### **Acceso SSH al Servidor:**
- **Usuario:** `root`
- **Contraseña:** `Qbasic.1977.server!`
- **Comando:** `ssh root@149.104.92.116`

### **MySQL - Usuario Administrador:**
- **Usuario:** `root`
- **Contraseña:** `OriolaMySQL2025!`
- **Uso:** Administración de MySQL
- **Comando:** `mysql -u root -p`

### **MySQL - Usuario de Aplicación:**
- **Usuario:** `oriola_user`
- **Contraseña:** `OriolaDB2025!`
- **Base de datos:** `orioladenim`
- **Uso:** Conexión desde Spring Boot

### **Clave SSH:**
- **Nombre:** `Mi-Clave-LightNode`
- **ID:** `key-350000givmn6`
- **Archivo local:** `C:\Users\LUCERO-PC\.ssh\id_rsa.pub`
- **Estado:** ✅ Vinculada en LightNode

---

## 🗄️ **BASE DE DATOS MYSQL**

### **Configuración:**
- **Base de datos:** `orioladenim`
- **Estado:** ✅ Creada y lista
- **Tablas:** Se crean automáticamente con Spring Boot
- **Configuración:** `spring.jpa.hibernate.ddl-auto=update`

### **Conexión desde Workbench:**
- **Host:** `149.104.92.116`
- **Puerto:** `3306`
- **Usuario:** `root` (para administración)
- **Contraseña:** `OriolaMySQL2025!`

---

## 🛠️ **SOFTWARE INSTALADO**

### **Java:**
- **Versión:** OpenJDK 17
- **Comando:** `java -version`

### **MySQL:**
- **Versión:** 8.0.43
- **Estado:** ✅ Activo
- **Comando:** `systemctl status mysql`

### **Maven:**
- **Versión:** 3.6.3
- **Comando:** `mvn -version`

### **Git:**
- **Versión:** 2.34.1
- **Comando:** `git --version`

### **Nginx:**
- **Versión:** 1.18.0
- **Estado:** ✅ Instalado
- **Comando:** `nginx -v`

---

## 🌐 **CONFIGURACIÓN DE DOMINIO**

### **Dominio:**
- **URL Principal:** `orioladenim.com.ar`
- **URL WWW:** `www.orioladenim.com.ar`
- **Registrado en:** NIC Argentina

### **DNS (Pendiente de configurar):**
- **A Record:** `orioladenim.com.ar` → `149.104.92.116`
- **CNAME:** `www.orioladenim.com.ar` → `orioladenim.com.ar`

---

## 💰 **INFORMACIÓN DE COSTOS**

### **Precio mensual:**
- **Servidor ECS:** $6.71 USD
- **IP Elástica IPv4:** $1.00 USD
- **Total:** $7.71 USD/mes
- **Costo por hora:** $0.012 USD

### **Configuración de red:**
- **Tipo:** Pago por tráfico
- **Tráfico incluido:** 1000 GB
- **Ubicación:** Buenos Aires, Argentina

---

## 🚀 **PRÓXIMOS PASOS**

### **Completados:**
- [x] Clonar proyecto desde GitHub
- [x] Configurar aplicación Spring Boot
- [x] Compilar y desplegar aplicación
- [x] Configurar archivos persistentes
- [x] Probar aplicación en producción

### **Pendientes:**
- [ ] Configurar Nginx como proxy
- [ ] Configurar dominio y SSL

### **Comandos pendientes:**
```bash
# Clonar proyecto
git clone https://github.com/LuceroGustavo/OriolaIndumentaria.git

# Compilar proyecto
mvn clean package -DskipTests

# Ejecutar aplicación
java -jar target/oriola-denim-0.0.1-SNAPSHOT.jar --spring.profiles.active=lightnode

# Iniciar Nginx
systemctl start nginx
systemctl enable nginx

# Configurar Nginx para proxy
# Configurar SSL con Let's Encrypt
```

## ⚙️ **CONFIGURACIÓN DE LA APLICACIÓN**

### **Archivo de configuración creado:**
- **`application-lightnode.properties`** - Configuración específica para LightNode

### **Configuración de base de datos:**
- **URL:** `jdbc:mysql://localhost:3306/orioladenim`
- **Usuario:** `oriola_user`
- **Contraseña:** `OriolaDB2025!`

### **Configuración de archivos:**
- **Uploads:** `/home/oriola/uploads`
- **Backups:** `/home/oriola/backups`
- **Persistencia:** Archivos se guardan en el servidor

### **Perfil activo:**
- **Comando:** `--spring.profiles.active=lightnode`

---

## 📞 **CONTACTO Y SOPORTE**

### **LightNode:**
- **Sitio web:** https://lightnode.com
- **Soporte:** Disponible en español
- **Panel de control:** Acceso desde https://lightnode.com

### **Documentación del proyecto:**
- **Repositorio:** https://github.com/LuceroGustavo/OriolaIndumentaria
- **Rama principal:** `master`
- **Rama de desarrollo:** `develop`
- **Rama de backup Railway:** `orioladenim-railway`

---

**Nota:** Este archivo contiene información sensible. Mantenerlo seguro y actualizado.
