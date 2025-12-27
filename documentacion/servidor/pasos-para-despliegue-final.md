# 🚀 **PLAN DE DESPLIEGUE FINAL - ORIOLA INDUMENTARIA**

## **📅 Fecha de Creación:** Diciembre 2024  
## **🎯 Objetivo:** Desplegar aplicación a producción con dominio orioladenim.com.ar  
## **🔒 Estado:** Repositorio privado + Servidor seguro  

---

## **✅ 1. ESTADO ACTUAL - LO QUE YA TENEMOS**

### **🔧 Configuración de Aplicación**
- [x] **Spring Boot 3.4.4** configurado
- [x] **Perfiles de configuración** (local, lightnode) ✅
- [x] **Base de datos MySQL** configurada
- [x] **Sistema de autenticación** implementado
- [x] **Panel de administración** completo
- [x] **Gestión de productos y categorías** funcional
- [x] **Sistema de imágenes** (WebP, thumbnails)
- [x] **Sistema de historias** (Instagram-like)
- [x] **Responsive design** implementado
- [x] **Sistema de etiquetas promocionales**
- [x] **Configuración de email** (Gmail SMTP)
- [x] **Logging configurado** (diferentes niveles por perfil)

### **🌐 Frontend**
- [x] **Thymeleaf** como motor de plantillas
- [x] **Bootstrap 5** para UI
- [x] **CSS personalizado** (Lovely Denim style)
- [x] **JavaScript** para interactividad
- [x] **Sistema de carrusel** de categorías
- [x] **Página de inicio** optimizada
- [x] **Catálogo de productos** funcional
- [x] **Páginas de detalle** de productos

### **🗄️ Base de Datos**
- [x] **Entidades** (Product, Category, User, etc.)
- [x] **Relaciones** Many-to-Many configuradas
- [x] **Índices** optimizados
- [x] **Migraciones** automáticas (ddl-auto=update)
- [x] **Datos de prueba** cargados

---

## **📊 1.1 ANÁLISIS DETALLADO DE CONFIGURACIÓN ACTUAL**

### **🔧 Archivos de Configuración Existentes:**
- ✅ **`application.properties`** - Configuración base con perfil activo `local`
- ✅ **`application-local.properties`** - Desarrollo local (localhost:3306, user: root/root)
- ✅ **`application-lightnode.properties`** - **PRODUCCIÓN ACTUAL** (149.104.92.116, user: oriola_user)
- ✅ **Configuración de producción** - **YA EXISTE** (es lightnode)

### **🔐 Configuración de Seguridad Actual:**
```properties
# LOCAL (application-local.properties)
spring.security.user.name=admin
spring.security.user.password=admin123  ⚠️ MUY DÉBIL

# SERVIDOR (application-lightnode.properties)  
spring.security.user.name=admin
spring.security.user.password=OriolaAdmin2025!  ⚠️ ACEPTABLE PERO MEJORABLE
```

### **🗄️ Base de Datos Actual:**
```properties
# LOCAL
spring.datasource.url=jdbc:mysql://localhost:3306/oriola_indumentaria
spring.datasource.username=root
spring.datasource.password=root  ⚠️ USAR ROOT NO ES SEGURO

# SERVIDOR
spring.datasource.url=jdbc:mysql://localhost:3306/orioladenim
spring.datasource.username=oriola_user
spring.datasource.password=OriolaDB2025!  ✅ MEJOR
```

### **📁 Configuración de Archivos:**
```properties
# LOCAL
file.upload-dir=uploads
upload.path=uploads

# SERVIDOR  
file.upload-dir=/home/oriola/uploads
upload.path=/home/oriola/uploads  ✅ RUTAS ABSOLUTAS
```

### **📧 Configuración de Email:**
- ✅ **Gmail SMTP** configurado en ambos perfiles
- ✅ **Variables de entorno** para credenciales
- ✅ **Configuración TLS** correcta

---

## **❌ 2. LO QUE FALTA IMPLEMENTAR**

### **🔐 Seguridad Crítica**
- [ ] **Certificado SSL/HTTPS** para orioladenim.com.ar
- [ ] **Contraseñas seguras** (cambiar defaults) ⚠️ **ACTUAL:** admin123 (local), OriolaAdmin2025! (servidor)
- [ ] **Firewall del servidor** configurado
- [ ] **Usuario de BD específico** (no root) ⚠️ **ACTUAL:** root (local), oriola_user (servidor) ✅
- [ ] **Configuración CORS** para dominio específico
- [ ] **Deshabilitar endpoints** de desarrollo
- ✅ **Archivo de producción** - **YA EXISTE** (application-lightnode.properties)

### **🌐 Dominio y DNS**
- [ ] **Configuración DNS** (A record, CNAME)
- [ ] **Redirección www** a dominio principal
- [ ] **Propagación DNS** (24-48 horas)

### **🔒 Repositorio**
- [ ] **Hacer repositorio privado** en GitHub
- [ ] **Configurar SSH keys** en servidor
- [ ] **Deploy key** para acceso automático
- [ ] **Script de deploy** automático

### **⚖️ Aspectos Legales**
- [ ] **Términos y Condiciones**
- [ ] **Política de Privacidad**
- [ ] **Política de Cookies**
- [ ] **Aviso Legal**
- [ ] **Contrato de desarrollo** firmado

### **🛡️ Seguridad Adicional**
- [ ] **Backup automático** de BD
- [ ] **Monitoreo de uptime**
- [ ] **Logs de seguridad**
- [ ] **Alertas por email**
- [ ] **Cloudflare** (opcional pero recomendado)

---

## **🚨 2.1 PRIORIDADES CRÍTICAS**

### **🔴 ALTA PRIORIDAD (HACER PRIMERO)**
1. ✅ **Configuración de producción** - **YA EXISTE** (application-lightnode.properties)
2. **Configurar SSL/HTTPS** - Obligatorio para dominio público
3. **Hacer repositorio privado** - Seguridad del código
4. **Configurar DNS** - Para que funcione el dominio

### **🟡 MEDIA PRIORIDAD (HACER DESPUÉS)**
1. **Mejorar contraseñas** - Cambiar admin123 por algo seguro
2. **Configurar firewall** - Seguridad del servidor
3. **Crear usuario BD específico** - No usar root
4. **Configurar backup automático** - Protección de datos

### **🟢 BAJA PRIORIDAD (HACER AL FINAL)**
1. **Aspectos legales** - Términos, privacidad, etc.
2. **Monitoreo avanzado** - Uptime, alertas
3. **Cloudflare** - Optimización y CDN
4. **Documentación final** - Manuales de usuario

---

## **📋 3. PLAN DE IMPLEMENTACIÓN PASO A PASO**

### **FASE 1: PREPARACIÓN (1-2 días)**

#### **1.1 Configurar Repositorio Privado**
```bash
# En GitHub.com
Settings → General → Danger Zone → Change repository visibility → Private
```

#### **1.2 Crear Usuario de Base de Datos**
```sql
-- En el servidor MySQL
CREATE USER 'oriola_app'@'localhost' IDENTIFIED BY 'PASSWORD_SUPER_SEGURA';
GRANT SELECT, INSERT, UPDATE, DELETE ON oriola_indumentaria.* TO 'oriola_app'@'localhost';
FLUSH PRIVILEGES;
```

#### **1.3 Configurar SSH en Servidor**
```bash
# En el servidor
ssh-keygen -t rsa -b 4096 -C "servidor@orioladenim.com.ar"
# Copiar clave pública a GitHub
```

### **FASE 2: CONFIGURACIÓN DE DOMINIO (1 día)**

#### **2.1 Configurar DNS**
```
# En el panel de tu proveedor de dominio:
A Record: orioladenim.com.ar → IP_DEL_SERVIDOR
CNAME: www.orioladenim.com.ar → orioladenim.com.ar
```

#### **2.2 Instalar Certificado SSL**
```bash
# Opción 1: Let's Encrypt (GRATIS)
sudo apt install certbot
sudo certbot --nginx -d orioladenim.com.ar -d www.orioladenim.com.ar

# Opción 2: Cloudflare (RECOMENDADO)
# Configurar en Cloudflare.com
```

### **FASE 3: CONFIGURACIÓN DE SERVIDOR (1 día)**

#### **3.1 Configurar Firewall**
```bash
sudo ufw allow 22    # SSH
sudo ufw allow 80    # HTTP
sudo ufw allow 443   # HTTPS
sudo ufw deny 8080   # Bloquear acceso directo a Spring Boot
sudo ufw enable
```

#### **3.2 Configurar Nginx (si no está configurado)**
```nginx
server {
    listen 80;
    server_name orioladenim.com.ar www.orioladenim.com.ar;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl;
    server_name orioladenim.com.ar www.orioladenim.com.ar;
    
    ssl_certificate /etc/letsencrypt/live/orioladenim.com.ar/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/orioladenim.com.ar/privkey.pem;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### **FASE 4: CONFIGURACIÓN DE APLICACIÓN (1 día)**

#### **4.1 Configurar application-lightnode.properties para dominio** ✅ **YA EXISTE - SOLO AJUSTAR**
```properties
# ===========================================
# CONFIGURACIÓN PARA PRODUCCIÓN
# Dominio: orioladenim.com.ar
# ===========================================

# Puerto del servidor
server.port=8080
server.servlet.context-path=/

# ===========================================
# BASE DE DATOS MYSQL (PRODUCCIÓN)
# ===========================================
spring.datasource.url=jdbc:mysql://localhost:3306/oriola_indumentaria?useSSL=true&serverTimezone=UTC
spring.datasource.username=oriola_app
spring.datasource.password=PASSWORD_SUPER_SEGURA
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Pool de conexiones optimizado para producción
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.connection-timeout=20000

# ===========================================
# CONFIGURACIÓN JPA/HIBERNATE
# ===========================================
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=false

# ===========================================
# CONFIGURACIÓN DE ARCHIVOS (PRODUCCIÓN)
# ===========================================
file.upload-dir=/var/www/oriola/uploads
backup.directory=/var/www/oriola/backups

# Configuración de archivos estáticos
spring.web.resources.static-locations=classpath:/static/,file:/var/www/oriola/uploads/
upload.path=/var/www/oriola/uploads
upload.thumbnail.path=/var/www/oriola/uploads/thumbnails

# ===========================================
# CONFIGURACIÓN DE SUBIDA DE ARCHIVOS
# ===========================================
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=200MB

# ===========================================
# CONFIGURACIÓN DE EMAIL (PRODUCCIÓN)
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
# CONFIGURACIÓN DE SEGURIDAD (PRODUCCIÓN)
# ===========================================
spring.security.user.name=admin
spring.security.user.password=${ADMIN_PASSWORD:CONTRASEÑA_SUPER_SEGURA}
spring.security.user.roles=ADMIN

# Deshabilitar endpoints de desarrollo
management.endpoints.enabled=false
spring.devtools.restart.enabled=false

# CORS para dominio específico
spring.web.cors.allowed-origins=https://orioladenim.com.ar,https://www.orioladenim.com.ar
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*

# ===========================================
# CONFIGURACIÓN DE LOGGING (PRODUCCIÓN)
# ===========================================
logging.level.com.orioladenim=INFO
logging.level.org.springframework.web=WARN
logging.level.org.hibernate.SQL=WARN
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=WARN
logging.file.name=/var/log/oriola-app.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# ===========================================
# CONFIGURACIÓN DE JACKSON (FECHAS)
# ===========================================
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.deserialization.fail-on-unknown-properties=false
```

#### **4.2 Configurar Servicio Systemd**
```bash
# Crear archivo de servicio
sudo nano /etc/systemd/system/oriola-app.service
```

```ini
[Unit]
Description=Oriola Indumentaria App
After=network.target

[Service]
Type=simple
User=www-data
WorkingDirectory=/path/to/app
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod app.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### **FASE 5: BACKUP Y MONITOREO (1 día)**

#### **5.1 Script de Backup Automático**
```bash
#!/bin/bash
# backup.sh
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u oriola_app -p oriola_indumentaria > /backups/oriola_${DATE}.sql
# Subir a cloud storage
```

#### **5.2 Configurar Cron para Backup**
```bash
# Ejecutar backup diario a las 2 AM
0 2 * * * /path/to/backup.sh
```

### **FASE 6: ASPECTOS LEGALES (1-2 días)**

#### **6.1 Crear Páginas Legales**
- [ ] **Términos y Condiciones** (`/terminos`)
- [ ] **Política de Privacidad** (`/privacidad`)
- [ ] **Política de Cookies** (`/cookies`)
- [ ] **Aviso Legal** (`/aviso-legal`)

#### **6.2 Contrato de Desarrollo**
- [ ] **Propiedad del código**
- [ ] **Mantenimiento post-lanzamiento**
- [ ] **Soporte técnico**
- [ ] **Backup y recuperación**

---

## **🚀 4. SCRIPT DE DEPLOY AUTOMÁTICO**

### **deploy.sh**
```bash
#!/bin/bash
echo "🚀 Iniciando deploy de Oriola Indumentaria..."

# Ir al directorio de la app
cd /path/to/app

# Pull del repositorio
echo "📥 Actualizando código..."
git pull origin main

# Compilar aplicación
echo "🔨 Compilando aplicación..."
mvn clean package -DskipTests

# Reiniciar servicio
echo "🔄 Reiniciando aplicación..."
sudo systemctl restart oriola-app

# Verificar estado
echo "✅ Verificando estado..."
sudo systemctl status oriola-app

echo "🎉 Deploy completado!"
```

---

## **📊 5. CHECKLIST FINAL PRE-LANZAMIENTO**

### **✅ Seguridad**
- [ ] HTTPS configurado y funcionando
- [ ] Contraseñas seguras implementadas
- [ ] Firewall configurado
- [ ] Usuario de BD específico creado
- [ ] CORS configurado para dominio
- [ ] Endpoints de desarrollo deshabilitados

### **✅ Dominio**
- [ ] DNS configurado correctamente
- [ ] Certificado SSL instalado
- [ ] Redirección www funcionando
- [ ] Tiempo de propagación completado

### **✅ Aplicación**
- [ ] Profile `prod` activo
- [ ] Logs configurados
- [ ] Servicio systemd funcionando
- [ ] Backup automático configurado

### **✅ Legal**
- [ ] Páginas legales creadas
- [ ] Contrato firmado
- [ ] Documentación técnica completa

### **✅ Monitoreo**
- [ ] Uptime monitoring configurado
- [ ] Logs de errores funcionando
- [ ] Alertas por email configuradas

---

## **🎯 6. CRONOGRAMA ESTIMADO**

| Fase | Duración | Dependencias |
|------|----------|--------------|
| Fase 1: Preparación | 1-2 días | Repositorio, BD |
| Fase 2: Dominio | 1 día | DNS, SSL |
| Fase 3: Servidor | 1 día | Firewall, Nginx |
| Fase 4: Aplicación | 1 día | Configuración |
| Fase 5: Backup | 1 día | Scripts, Cron |
| Fase 6: Legal | 1-2 días | Páginas, Contrato |
| **TOTAL** | **6-8 días** | **Secuencial** |

---

## **💡 7. RECOMENDACIONES ADICIONALES**

### **A. Cloudflare (MUY RECOMENDADO)**
- CDN gratuito
- SSL automático
- Protección DDoS
- Cache inteligente
- Analytics básico

### **B. Monitoreo Avanzado**
- UptimeRobot (gratis)
- Google Analytics
- Logs de aplicación
- Alertas por email/SMS

### **C. Backup Strategy**
- Base de datos: Diario
- Código: En GitHub
- Imágenes: En cloud storage
- Configuración: Documentada

---

## **📞 8. CONTACTO DE EMERGENCIA**

### **En caso de problemas:**
1. **Logs de aplicación:** `/var/log/oriola-app.log`
2. **Estado del servicio:** `sudo systemctl status oriola-app`
3. **Logs de Nginx:** `/var/log/nginx/error.log`
4. **Estado de BD:** `sudo systemctl status mysql`

---

## **🎉 9. POST-LANZAMIENTO**

### **Primeras 48 horas:**
- [ ] Monitorear logs constantemente
- [ ] Verificar funcionalidades críticas
- [ ] Testear en diferentes dispositivos
- [ ] Verificar velocidad de carga

### **Primera semana:**
- [ ] Revisar analytics de uso
- [ ] Optimizar performance si es necesario
- [ ] Recopilar feedback del cliente
- [ ] Documentar lecciones aprendidas

---

---

## **📋 10. RESUMEN EJECUTIVO**

### **✅ LO QUE YA TENEMOS (80% COMPLETO)**
- **Aplicación funcional** con todas las características implementadas
- **Configuración de desarrollo** (local y servidor) funcionando
- **Base de datos** estructurada y con datos
- **Sistema de administración** completo
- **Frontend responsive** optimizado
- **Sistema de imágenes** y archivos funcionando

### **❌ LO QUE FALTA (20% RESTANTE)**
- ✅ **Configuración de producción** - **YA EXISTE** (application-lightnode.properties)
- **Seguridad HTTPS** (certificado SSL)
- **Repositorio privado** (seguridad del código)
- **Configuración de dominio** (DNS)
- **Aspectos legales** (términos, privacidad)

### **⏱️ TIEMPO ESTIMADO TOTAL: 6-8 días**
- **Fases 1-4:** 4-5 días (crítico)
- **Fases 5-6:** 2-3 días (importante)
- **Post-lanzamiento:** 1-2 días (monitoreo)

### **💰 COSTOS ADICIONALES**
- **Dominio:** Ya comprado (orioladenim.com.ar)
- **Servidor:** Ya configurado (LightNode)
- **SSL:** Gratis (Let's Encrypt) o Cloudflare
- **Monitoreo:** Gratis (UptimeRobot)
- **Total adicional:** $0 (si usás opciones gratuitas)

### **🎯 PRÓXIMOS PASOS INMEDIATOS**
1. **HOY:** Hacer repositorio privado y configurar DNS
2. **MAÑANA:** Instalar SSL y probar dominio
3. **PASADO MAÑANA:** Configurar firewall y backup
4. **RESTANTE:** Aspectos legales y monitoreo

---

**📝 Nota:** Este plan está diseñado para ser ejecutado paso a paso, sin saltar fases. Cada fase tiene dependencias de la anterior.

**🎯 Objetivo:** Despliegue seguro, estable y profesional de Oriola Indumentaria en orioladenim.com.ar

**💡 Recomendación:** Empezar con la Fase 1 (repositorio privado y DNS) ya que la configuración de producción ya existe.
