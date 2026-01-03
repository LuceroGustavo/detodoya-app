# Configuración y Despliegue - ORIOLA Indumentaria

**Fecha de consolidación:** 15 de enero de 2025  
**Estado:** ✅ Servidor configurado y funcionando completamente

---

## 🎯 **RESUMEN EJECUTIVO**

Este documento consolida toda la configuración de despliegue del proyecto ORIOLA Indumentaria, incluyendo configuración del servidor NodeLight, dominio, base de datos, scripts de automatización y optimizaciones de producción.

---

## 🖥️ **1. CONFIGURACIÓN DEL SERVIDOR NODELIGHT**

### **Información del Servidor:**
- **IP Pública:** `149.104.92.116`
- **IP Privada:** `172.16.1.167`
- **Hostname:** `ul9vrgdk`
- **Ubicación:** Buenos Aires, Argentina 🇦🇷
- **Sistema Operativo:** Ubuntu 22.04.5 LTS

### **Especificaciones Técnicas:**
- **CPU:** 1 vCore (Compartido)
- **RAM:** 2 GB
- **Almacenamiento:** 50 GB SSD
- **Red:** BGP (Alta disponibilidad)

### **Accesos y Credenciales:**
- **Usuario SSH:** `root`
- **Contraseña SSH:** `Qbasic.1977.server!`
- **Comando conexión:** `ssh root@149.104.92.116`

---

## 🗄️ **2. CONFIGURACIÓN DE BASE DE DATOS MYSQL**

### **Base de Datos Principal:**
- **Nombre:** `orioladenim`
- **Estado:** ✅ Creada y funcionando
- **Configuración:** `spring.jpa.hibernate.ddl-auto=update`

### **Usuarios MySQL:**
- **Root (Administrador):**
  - **Usuario:** `root`
  - **Contraseña:** `OriolaMySQL2025!`
  - **Uso:** Administración de MySQL

- **Usuario de Aplicación:**
  - **Usuario:** `oriola_user`
  - **Contraseña:** `OriolaDB2025!`
  - **Base de datos:** `orioladenim`
  - **Uso:** Conexión desde Spring Boot

### **Conexión desde Workbench:**
- **Host:** `149.104.92.116`
- **Puerto:** `3306`
- **Usuario:** `root` (para administración)
- **Contraseña:** `OriolaMySQL2025!`

---

## 🌐 **3. CONFIGURACIÓN DE DOMINIO**

### **Dominio Principal:**
- **URL Principal:** `orioladenim.com.ar`
- **URL WWW:** `www.orioladenim.com.ar`
- **Registrado en:** NIC Argentina
- **Estado:** ✅ Configurado y funcionando

### **Configuración DNS:**
- **A Record:** `orioladenim.com.ar` → `149.104.92.116`
- **CNAME:** `www.orioladenim.com.ar` → `orioladenim.com.ar`
- **SSL:** Configurado automáticamente

---

## 💰 **4. INFORMACIÓN DE COSTOS**

### **Precio Mensual:**
- **Servidor ECS:** $6.71 USD
- **IP Elástica IPv4:** $1.00 USD
- **Total:** $7.71 USD/mes
- **Costo por hora:** $0.012 USD

### **Configuración de Red:**
- **Tipo:** Pago por tráfico
- **Tráfico incluido:** 1000 GB
- **Ubicación:** Buenos Aires, Argentina

---

## 🛠️ **5. SOFTWARE INSTALADO EN EL SERVIDOR**

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

## 🚀 **6. SCRIPTS DE DESPLIEGUE AUTOMATIZADOS**

### **Script de Actualización Rápida:**
```bash
#!/bin/bash
# Método Rápido (Recomendado)
ssh root@149.104.92.116
cd /home/oriola/OriolaIndumentaria
pkill -f "oriola-denim"
git pull origin master
mvn clean package -DskipTests
nohup java -jar target/oriola-denim-0.0.1-SNAPSHOT.jar --spring.profiles.active=lightnode > /dev/null 2>&1 &
ps aux | grep java
exit
```

### **Script de Despliegue Completo:**
```bash
#!/bin/bash
# Método Completo (Para cambios grandes)
ssh root@149.104.92.116
rm -rf /home/oriola/OriolaIndumentaria
cd /home/oriola
git clone https://github.com/LuceroGustavo/OriolaIndumentaria.git
cd OriolaIndumentaria
mvn clean package -DskipTests
nohup java -jar target/oriola-denim-0.0.1-SNAPSHOT.jar --spring.profiles.active=lightnode > /dev/null 2>&1 &
ps aux | grep java
exit
```

---

## ⚙️ **7. CONFIGURACIÓN DE LA APLICACIÓN**

### **Archivo de Configuración:**
- **`application-lightnode.properties`** - Configuración específica para NodeLight

### **Configuración de Base de Datos:**
```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/orioladenim
spring.datasource.username=oriola_user
spring.datasource.password=OriolaDB2025!
```

### **Configuración de Archivos:**
```properties
# Archivos estáticos
upload.path=/home/oriola/uploads
upload.thumbnail.path=/home/oriola/uploads/thumbnails
```

### **Perfil Activo:**
- **Comando:** `--spring.profiles.active=lightnode`

---

## 📁 **8. ESTRUCTURA DE ARCHIVOS EN EL SERVIDOR**

```
/home/oriola/
├── OriolaIndumentaria/          # Código fuente
│   └── target/
│       └── oriola-denim-0.0.1-SNAPSHOT.jar
├── uploads/                     # Imágenes (PERSISTENTE)
│   ├── [archivos_imagenes].webp
│   └── thumbnails/
│       └── [thumbnails].webp
└── backups/                     # Backups (PERSISTENTE)
    └── [archivos_backup].zip
```

---

## 🔧 **9. COMANDOS DE DIAGNÓSTICO**

### **Ver Estado de la Aplicación:**
```bash
ps aux | grep java
```

### **Ver Puertos en Uso:**
```bash
netstat -tlnp | grep 8080
```

### **Ver Uso de Memoria:**
```bash
free -h
```

### **Ver Uso de Disco:**
```bash
df -h
```

### **Ver Logs del Sistema:**
```bash
journalctl -f
```

---

## 🌐 **10. URLs DE ACCESO**

### **Aplicación:**
- **URL Principal:** http://149.104.92.116:8080
- **URL con Dominio:** http://orioladenim.com.ar

### **Panel de Administración:**
- **URL:** http://149.104.92.116:8080/admin
- **Usuario:** admin
- **Contraseña:** OriolaAdmin2025!

---

## ⚠️ **11. NOTAS IMPORTANTES**

### **Persistencia de Archivos:**
- ✅ **Los archivos en `/home/oriola/uploads` y `/home/oriola/backups` persisten** entre reinicios
- ✅ **La aplicación se ejecuta con `nohup`** para que no se cierre al desconectar SSH
- ✅ **Siempre usar el perfil `lightnode`** en producción
- ✅ **La base de datos MySQL persiste** automáticamente

### **Configuración de Red:**
- ✅ **Clave SSH configurada** para acceso sin contraseña
- ✅ **Firewall configurado** para puertos necesarios
- ✅ **SSL configurado** automáticamente con Let's Encrypt

---

## 🔄 **12. FLUJO DE TRABAJO DE DESPLIEGUE**

### **Desarrollo Diario:**
1. **Desarrollar** en local
2. **Commit y push** a GitHub
3. **Conectar** al servidor NodeLight
4. **Ejecutar** script de actualización rápida
5. **Verificar** funcionamiento

### **Despliegue Completo:**
1. **Conectar** al servidor NodeLight
2. **Ejecutar** script de despliegue completo
3. **Configurar** base de datos si es necesario
4. **Verificar** todas las funcionalidades
5. **Monitorear** rendimiento

---

## 📊 **13. MONITOREO Y MANTENIMIENTO**

### **Métricas a Monitorear:**
- **Uso de CPU** y memoria
- **Espacio en disco** disponible
- **Conexiones** de base de datos
- **Tiempo de respuesta** de la aplicación
- **Logs de errores** del sistema

### **Tareas de Mantenimiento:**
- **Backup regular** de base de datos
- **Limpieza** de archivos temporales
- **Actualización** de dependencias
- **Monitoreo** de logs de aplicación
- **Verificación** de funcionamiento

---

## 🎯 **14. ESTADO ACTUAL DEL DESPLIEGUE**

### **Configuración Completada:**
- ✅ **Servidor NodeLight** configurado y funcionando
- ✅ **Base de datos MySQL** configurada y optimizada
- ✅ **Dominio personalizado** configurado y funcionando
- ✅ **SSL** configurado automáticamente
- ✅ **Scripts de despliegue** automatizados
- ✅ **Aplicación** desplegada y funcionando

### **URLs Funcionando:**
- ✅ **Aplicación principal:** http://orioladenim.com.ar
- ✅ **Panel admin:** http://orioladenim.com.ar/admin
- ✅ **Todas las funcionalidades** operativas

---

## 🎉 **RESULTADO FINAL**

### **Despliegue Completamente Funcional:**
- ✅ **Servidor optimizado** y configurado
- ✅ **Dominio personalizado** funcionando
- ✅ **Base de datos** optimizada y estable
- ✅ **Scripts automatizados** para despliegue
- ✅ **Monitoreo** y mantenimiento configurado

### **Beneficios Obtenidos:**
- **Despliegue automatizado** y rápido
- **Servidor estable** y optimizado
- **Dominio profesional** configurado
- **Base de datos** optimizada para producción
- **Monitoreo** y mantenimiento simplificado

---

**Desarrollado por:** Equipo de Desarrollo ORIOLA  
**Fecha de consolidación:** 15 de enero de 2025  
**Estado:** ✅ Servidor NodeLight completamente configurado y funcionando
