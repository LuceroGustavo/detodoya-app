# Configuración del Servidor LightNode

**Fecha de creación:** 30 de septiembre de 2025  
**Proveedor:** LightNode  
**Propósito:** Servidor de producción para Oriola Indumentaria

## 🖥️ **INFORMACIÓN DEL SERVIDOR**

### **Datos Básicos:**
- **Nombre del host:** Server-Lucero
- **IP Pública:** `149.104.92.116`
- **IP Privada:** `172.16.1.167`
- **Usuario:** `root`
- **Estado:** ✅ Corriendo (Running)

### **Ubicación:**
- **País:** Argentina 🇦🇷
- **Ciudad:** Buenos Aires
- **Latencia estimada:** 5-10ms (Argentina)

## ⚙️ **ESPECIFICACIONES TÉCNICAS**

### **Hardware:**
- **Procesador:** Intel 1 vCPU (Compartido)
- **Memoria RAM:** 2 GB
- **Disco del sistema:** 50 GB SSD
- **Disco de datos:** 0 GB (por configurar)

### **Sistema Operativo:**
- **OS:** Ubuntu 22.04 LTS
- **Arquitectura:** x64
- **Tipo de línea:** BGP

## 💰 **CONFIGURACIÓN DE COSTOS**

### **Precio mensual:**
- **Servidor ECS:** $6.71 USD
- **IP Elástica IPv4:** $1.00 USD
- **Total:** $7.71 USD/mes
- **Costo por hora:** $0.012 USD

### **Configuración de red:**
- **Tipo:** Pago por tráfico
- **Tráfico incluido:** 1000 GB
- **Ubicación:** Buenos Aires, Argentina

## 🔧 **CONFIGURACIÓN PENDIENTE**

### **Software a instalar:**
- [ ] Java 17 (OpenJDK)
- [ ] MySQL 8.0
- [ ] Maven 3.8+
- [ ] Nginx (proxy reverso)
- [ ] Git

### **Aplicación a desplegar:**
- [ ] Spring Boot (Oriola Indumentaria)
- [ ] Base de datos MySQL
- [ ] Configuración de dominio
- [ ] Certificado SSL

## 🌐 **CONFIGURACIÓN DE DOMINIO**

### **Dominio principal:**
- **URL:** orioladenim.com.ar
- **WWW:** www.orioladenim.com.ar
- **Registrado en:** NIC Argentina

### **Configuración DNS pendiente:**
- [ ] A Record: orioladenim.com.ar → 149.104.92.116
- [ ] CNAME: www.orioladenim.com.ar → orioladenim.com.ar

## 📋 **COMANDOS DE ACCESO**

### **Conexión SSH:**
```bash
ssh root@149.104.92.116
# Contraseña: Qbasic.1977.server!
```

### **Verificar estado del servidor:**
```bash
# Verificar sistema
uname -a
cat /etc/os-release

# Verificar recursos
free -h
df -h
```

## 🔐 **SEGURIDAD**

### **Configuración pendiente:**
- [ ] Configurar firewall (UFW)
- [ ] Crear usuario no-root
- [ ] Configurar SSH con claves
- [ ] Instalar fail2ban

## 📊 **MONITOREO**

### **Recursos del servidor:**
- **RAM disponible:** 2 GB
- **CPU:** 1 vCore compartido
- **Almacenamiento:** 50 GB SSD
- **Red:** BGP (alta disponibilidad)

## 🔐 **CONEXIÓN SSH REMOTA**

### **Comando de conexión:**
```bash
ssh root@149.104.92.116
```

### **Primera conexión:**
- Te preguntará: `Are you sure you want to continue connecting (yes/no/[fingerprint])?`
- Escribe: `yes`

### **Autenticación:**
- **Con clave SSH:** Se conecta automáticamente
- **Con contraseña:** `Qbasic.1977.server!`

## 🗄️ **CONFIGURACIÓN MYSQL COMPLETADA**

### **Base de datos creada:**
- **Nombre:** `orioladenim`
- **Estado:** ✅ Creada y lista para usar

### **Usuarios MySQL:**
- **Root (Administrador):**
  - **Usuario:** `root`
  - **Contraseña:** `OriolaMySQL2025!`
  - **Uso:** Administración de MySQL

- **Usuario de aplicación:**
  - **Usuario:** `oriola_user`
  - **Contraseña:** `OriolaDB2025!`
  - **Uso:** Conexión desde Spring Boot

### **Verificación de conexión:**
Cuando estés conectado verás:
```
root@ul9vrgdk:~#
```

## 🛠️ **CONFIGURACIÓN DEL SERVIDOR**

### **PASO 1: Actualizar sistema**
```bash
apt update && apt upgrade -y
```
**Nota:** Después de actualizar, reiniciar el servidor con `reboot`

### **PASO 2: Instalar Java 17**
```bash
apt install openjdk-17-jdk -y
```

### **PASO 3: Instalar MySQL**
```bash
apt install mysql-server -y
```

### **PASO 4: Instalar Maven**
```bash
apt install maven -y
```

### **PASO 5: Instalar Git**
```bash
apt install git -y
```

### **PASO 6: Instalar Nginx**
```bash
apt install nginx -y
```

## 📋 **COMANDOS DE VERIFICACIÓN**

### **Verificar instalaciones:**
```bash
# Verificar Java
java -version

# Verificar MySQL
mysql --version

# Verificar Maven
mvn -version

# Verificar Git
git --version

# Verificar Nginx
nginx -v
```

## 🚀 **PRÓXIMOS PASOS**

1. ✅ **Conectar por SSH** al servidor
2. ✅ **Instalar software** necesario (Java, MySQL, etc.)
3. **Configurar base de datos** MySQL
4. **Subir aplicación** Spring Boot
5. **Configurar Nginx** como proxy
6. **Configurar dominio** y SSL
7. **Probar aplicación** en producción

---

**Nota:** Este documento se actualizará conforme se vaya configurando el servidor.
