# Plan de Cambios - Actualización de Contacto y Redes Sociales

**Fecha de creación:** 30 de diciembre de 2025  
**Estado:** ⏳ **PENDIENTE DE IMPLEMENTACIÓN**

---

## 📋 **RESUMEN**

Este documento detalla todos los cambios necesarios para actualizar la información de contacto y redes sociales en la aplicación Oriola Indumentaria, incluyendo la configuración del nuevo correo electrónico para el envío automático de mensajes.

---

## 📧 **CAMBIOS EN CORREO ELECTRÓNICO**

### **1. Actualizar Email de Contacto**

**Cambio:**
- **Actual:** `info@orioladenim.com`
- **Nuevo:** `orioladenim@gmail.com`

**Archivos a modificar:**
- `src/main/resources/templates/index.html`
- `src/main/resources/templates/fragments/footer-black.html`
- `src/main/resources/templates/contact.html`
- `src/main/resources/templates/about.html`
- `src/main/resources/templates/catalog.html`
- `src/main/resources/templates/product-detail.html`

**Búsqueda y reemplazo:**
- Buscar: `mailto:info@orioladenim.com`
- Reemplazar: `mailto:orioladenim@gmail.com`
- Buscar: `info@orioladenim.com` (en texto)
- Reemplazar: `orioladenim@gmail.com`

---

### **2. Configurar SMTP para Envío Automático de Mensajes**

**⚠️ REQUIERE ACCIÓN DEL CLIENTE:**

El cliente debe proporcionar una **App Password (Contraseña de Aplicación)** de Gmail para el correo `orioladenim@gmail.com`.

#### **Instrucciones para el Cliente:**

1. **Activar verificación en dos pasos** (si no está activada):
   - Acceder a: https://myaccount.google.com/security
   - Activar "Verificación en dos pasos"

2. **Generar App Password:**
   - Acceder a: https://myaccount.google.com/apppasswords
   - Seleccionar "Aplicación": Correo
   - Seleccionar "Dispositivo": Otro (personalizado)
   - Escribir: `Oriola App`
   - Hacer clic en "Generar"
   - **Copiar la contraseña generada** (formato: `xxxx xxxx xxxx xxxx` - 16 caracteres)

3. **Proporcionar la App Password:**
   - Enviar la contraseña de forma segura al desarrollador
   - **NO compartir esta contraseña públicamente**

#### **Archivos a modificar:**

**1. `src/main/resources/application-donweb.properties`:**
```properties
# ===========================================
# CONFIGURACIÓN DE EMAIL
# ===========================================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:orioladenim@gmail.com}
spring.mail.password=${MAIL_PASSWORD:[APP_PASSWORD_DEL_CLIENTE]}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

**2. `src/main/java/com/orioladenim/service/EmailService.java`:**
```java
@Value("${app.email.from:orioladenim@gmail.com}")
private String fromEmail;

@Value("${app.email.to:orioladenim@gmail.com}")
private String toEmail;
```

**Configuración actual:**
- Email: `luceroprograma@gmail.com`
- App Password: `kmqh ktkl lhyj gwlf`

**Configuración nueva:**
- Email: `orioladenim@gmail.com`
- App Password: `[SOLICITAR AL CLIENTE]` ⚠️

---

## 📱 **CAMBIOS EN REDES SOCIALES**

### **1. Instagram**

**Cambio:**
- **Actual:** `https://www.instagram.com/oriolaindumentaria`
- **Nuevo:** `https://www.instagram.com/oriola.denim` (cuenta principal activa)

**Archivos a modificar:**
- `src/main/resources/templates/index.html`
- `src/main/resources/templates/fragments/footer-black.html`
- `src/main/resources/templates/contact.html`
- `src/main/resources/templates/about.html`
- `src/main/resources/templates/catalog.html`
- `src/main/resources/templates/product-detail.html`
- `src/main/resources/templates/index-backup.html`

**Búsqueda y reemplazo:**
- Buscar: `instagram.com/oriolaindumentaria`
- Reemplazar: `instagram.com/oriola.denim`

---

### **2. Facebook**

**Cambio:**
- **Actual:** `https://www.facebook.com/oriolaindumentaria`
- **Nuevo:** `https://www.facebook.com/orioladenim`

**Archivos a modificar:**
- `src/main/resources/templates/index.html`
- `src/main/resources/templates/fragments/footer-black.html`
- `src/main/resources/templates/contact.html`
- `src/main/resources/templates/about.html`
- `src/main/resources/templates/catalog.html`
- `src/main/resources/templates/product-detail.html`
- `src/main/resources/templates/index-backup.html`

**Búsqueda y reemplazo:**
- Buscar: `facebook.com/oriolaindumentaria`
- Reemplazar: `facebook.com/orioladenim`

**Nota:** En `index.html` el enlace de Facebook está comentado. Considerar descomentarlo si se desea mostrar.

---

## ✅ **CHECKLIST DE IMPLEMENTACIÓN**

### **Fase 1: Solicitud al Cliente**
- [ ] Solicitar App Password de Gmail para `orioladenim@gmail.com`
- [ ] Recibir App Password del cliente de forma segura
- [ ] Verificar que el cliente activó verificación en dos pasos

### **Fase 2: Actualización de Código**
- [ ] Actualizar email en todos los templates HTML
- [ ] Actualizar configuración SMTP en `application-donweb.properties`
- [ ] Actualizar valores por defecto en `EmailService.java`
- [ ] Actualizar enlaces de Instagram en todos los templates
- [ ] Actualizar enlaces de Facebook en todos los templates
- [ ] Verificar que no queden referencias a los valores antiguos

### **Fase 3: Pruebas**
- [ ] Probar envío de formulario de contacto
- [ ] Verificar que llega el email a `orioladenim@gmail.com`
- [ ] Verificar que los enlaces de redes sociales funcionan correctamente
- [ ] Verificar que los enlaces de email (`mailto:`) funcionan
- [ ] Probar en diferentes navegadores

### **Fase 4: Despliegue**
- [ ] Commit de cambios
- [ ] Push a repositorio
- [ ] Desplegar en servidor (usar script de despliegue)
- [ ] Verificar funcionamiento en producción

---

## 🔐 **SEGURIDAD**

### **Manejo de App Password:**

1. **NO subir la App Password al repositorio:**
   - Usar variables de entorno o configuración externa
   - Mantener valores por defecto seguros en el código

2. **Configuración recomendada:**
   ```properties
   # Usar variable de entorno
   spring.mail.password=${MAIL_PASSWORD}
   ```

3. **En el servidor:**
   - Configurar variable de entorno `MAIL_PASSWORD`
   - O editar directamente `application-donweb.properties` (no subir al repo)

---

## 📝 **NOTAS ADICIONALES**

### **Sobre la App Password de Gmail:**
- La App Password es una contraseña de 16 caracteres generada por Google
- Se usa específicamente para aplicaciones que necesitan acceso al correo
- Es diferente a la contraseña normal de Gmail
- Si el cliente cambia su contraseña de Gmail, la App Password sigue funcionando
- Se puede revocar desde la configuración de Google

### **Sobre el envío automático:**
- La aplicación envía emails automáticamente cuando:
  - Un usuario completa el formulario de contacto
  - Se crea una nueva consulta en el sistema
- El email se envía a `orioladenim@gmail.com` (configurado en `app.email.to`)
- El email se envía desde `orioladenim@gmail.com` (configurado en `app.email.from`)

---

---

## 🌐 **CONFIGURACIÓN DE DOMINIO Y CERTIFICADO SSL**

### **1. Configurar DNS**

**⚠️ REQUIERE ACCIÓN DEL CLIENTE:**

El cliente debe configurar los registros DNS en su proveedor de dominio (NIC Argentina o donde esté registrado el dominio).

#### **Registros DNS necesarios:**

1. **Registro A (IPv4):**
   - **Nombre:** `@` o `orioladenim.com.ar`
   - **Tipo:** A
   - **Valor:** `66.97.45.252` (IP del servidor)
   - **TTL:** 3600 (o el valor por defecto)

2. **Registro CNAME (WWW):**
   - **Nombre:** `www`
   - **Tipo:** CNAME
   - **Valor:** `orioladenim.com.ar`
   - **TTL:** 3600 (o el valor por defecto)

#### **Verificación de DNS:**

Después de configurar, verificar que los registros apuntan correctamente:

```bash
# Verificar registro A
dig orioladenim.com.ar +short
# Debe mostrar: 66.97.45.252

# Verificar registro CNAME
dig www.orioladenim.com.ar +short
# Debe mostrar: orioladenim.com.ar
```

**⏱️ Tiempo de propagación:** Puede tardar entre 15 minutos y 48 horas.

---

### **2. Configurar Nginx como Reverse Proxy**

Una vez que el DNS esté propagado, configurar Nginx para que redirija el tráfico a la aplicación Spring Boot.

#### **2.1 Crear configuración de Nginx:**

```bash
# Conectarse al servidor
ssh -p5625 root@66.97.45.252

# Crear archivo de configuración
sudo nano /etc/nginx/sites-available/orioladenim
```

#### **2.2 Contenido del archivo de configuración:**

```nginx
# Redirección HTTP a HTTPS
server {
    listen 80;
    listen [::]:80;
    server_name orioladenim.com.ar www.orioladenim.com.ar;

    # Redirigir todo el tráfico HTTP a HTTPS
    return 301 https://$server_name$request_uri;
}

# Configuración HTTPS (se completará después de obtener el certificado SSL)
server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name orioladenim.com.ar www.orioladenim.com.ar;

    # Certificados SSL (se agregarán automáticamente por Certbot)
    # ssl_certificate /etc/letsencrypt/live/orioladenim.com.ar/fullchain.pem;
    # ssl_certificate_key /etc/letsencrypt/live/orioladenim.com.ar/privkey.pem;

    # Configuración SSL (se agregará automáticamente por Certbot)
    # include /etc/letsencrypt/options-ssl-nginx.conf;
    # ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;

    # Logs
    access_log /var/log/nginx/orioladenim-access.log;
    error_log /var/log/nginx/orioladenim-error.log;

    # Proxy a Spring Boot
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Port $server_port;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Archivos estáticos (uploads)
    location /uploads/ {
        alias /home/oriola/uploads/;
        expires 30d;
        add_header Cache-Control "public, immutable";
        
        # Permitir acceso a imágenes
        location ~* \.(jpg|jpeg|png|gif|ico|svg|webp)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }

    # Favicon
    location = /favicon.ico {
        access_log off;
        log_not_found off;
    }

    # Robots.txt
    location = /robots.txt {
        access_log off;
        log_not_found off;
    }
}
```

#### **2.3 Habilitar sitio:**

```bash
# Crear enlace simbólico
sudo ln -s /etc/nginx/sites-available/orioladenim /etc/nginx/sites-enabled/

# Eliminar configuración por defecto (opcional)
sudo rm /etc/nginx/sites-enabled/default

# Verificar configuración
sudo nginx -t

# Si todo está bien, recargar Nginx
sudo systemctl reload nginx
```

---

### **3. Instalar Certificado SSL con Let's Encrypt**

Let's Encrypt proporciona certificados SSL gratuitos y renovación automática.

#### **3.1 Instalar Certbot:**

```bash
# Actualizar sistema
sudo apt update

# Instalar Certbot y plugin de Nginx
sudo apt install certbot python3-certbot-nginx -y
```

#### **3.2 Obtener certificado SSL:**

```bash
# Obtener certificado para ambos dominios (con y sin www)
sudo certbot --nginx -d orioladenim.com.ar -d www.orioladenim.com.ar

# Durante la instalación, Certbot preguntará:
# - Email para notificaciones de renovación
# - Términos y condiciones (Aceptar)
# - Compartir email con EFF (opcional)
# - Redirección HTTP a HTTPS (Seleccionar: 2 - Redirect)
```

#### **3.3 Verificar renovación automática:**

```bash
# Probar renovación (dry-run)
sudo certbot renew --dry-run

# Verificar que el timer de renovación está activo
sudo systemctl status certbot.timer
```

**Nota:** Let's Encrypt renueva automáticamente los certificados cada 90 días. El certificado es válido por 90 días.

---

### **4. Verificar Configuración SSL**

#### **4.1 Verificar certificado:**

```bash
# Ver detalles del certificado
sudo certbot certificates

# Verificar que Nginx está usando el certificado
sudo nginx -t
```

#### **4.2 Probar desde navegador:**

1. Acceder a: `https://orioladenim.com.ar`
2. Verificar que muestra el candado verde 🔒
3. Verificar que redirige correctamente desde HTTP a HTTPS
4. Verificar que `www.orioladenim.com.ar` también funciona

#### **4.3 Verificar con herramientas online:**

- **SSL Labs:** https://www.ssllabs.com/ssltest/analyze.html?d=orioladenim.com.ar
- **SSL Checker:** https://www.sslshopper.com/ssl-checker.html

---

### **5. Configurar Renovación Automática**

Certbot configura automáticamente la renovación, pero es bueno verificar:

```bash
# Ver timer de renovación
sudo systemctl status certbot.timer

# Ver logs de renovación
sudo journalctl -u certbot.timer

# Si no está activo, habilitarlo
sudo systemctl enable certbot.timer
sudo systemctl start certbot.timer
```

---

## ✅ **CHECKLIST DE CONFIGURACIÓN DE DOMINIO Y SSL**

### **Fase 1: DNS**
- [ ] Cliente configura registro A en proveedor de dominio
- [ ] Cliente configura registro CNAME para www
- [ ] Verificar propagación DNS (dig/nslookup)
- [ ] Verificar que dominio apunta a IP correcta (66.97.45.252)

### **Fase 2: Nginx**
- [ ] Crear archivo de configuración `/etc/nginx/sites-available/orioladenim`
- [ ] Configurar redirección HTTP a HTTPS
- [ ] Configurar proxy a Spring Boot (puerto 8080)
- [ ] Configurar archivos estáticos (/uploads/)
- [ ] Habilitar sitio (crear symlink)
- [ ] Verificar configuración (`nginx -t`)
- [ ] Recargar Nginx

### **Fase 3: SSL**
- [ ] Instalar Certbot y plugin de Nginx
- [ ] Obtener certificado SSL con Certbot
- [ ] Verificar que certificado se instaló correctamente
- [ ] Verificar renovación automática
- [ ] Probar acceso HTTPS desde navegador
- [ ] Verificar redirección HTTP → HTTPS

### **Fase 4: Verificación Final**
- [ ] Probar acceso desde `https://orioladenim.com.ar`
- [ ] Probar acceso desde `https://www.orioladenim.com.ar`
- [ ] Verificar que aplicación funciona correctamente
- [ ] Verificar que archivos estáticos se sirven correctamente
- [ ] Verificar certificado SSL con SSL Labs
- [ ] Documentar configuración

---

## 🚀 **ORDEN DE IMPLEMENTACIÓN RECOMENDADO**

### **Fase 1: Contacto y Redes Sociales**
1. **Primero:** Solicitar App Password al cliente
2. **Segundo:** Actualizar enlaces de redes sociales (no requiere App Password)
3. **Tercero:** Actualizar emails de contacto en templates
4. **Cuarto:** Configurar SMTP con App Password recibida
5. **Quinto:** Probar envío de emails
6. **Sexto:** Desplegar en producción

### **Fase 2: Dominio y SSL**
1. **Primero:** Solicitar configuración DNS al cliente
2. **Segundo:** Verificar propagación DNS
3. **Tercero:** Configurar Nginx como reverse proxy
4. **Cuarto:** Instalar Certbot y obtener certificado SSL
5. **Quinto:** Verificar configuración SSL
6. **Sexto:** Probar acceso HTTPS completo

---

## 📝 **NOTAS IMPORTANTES**

### **Sobre DNS:**
- La propagación DNS puede tardar entre 15 minutos y 48 horas
- Verificar DNS antes de intentar obtener certificado SSL
- Certbot requiere que el dominio apunte correctamente al servidor

### **Sobre SSL:**
- Let's Encrypt es gratuito y renovación automática
- Certificado válido por 90 días
- Renovación automática cada 60 días (30 días antes de expirar)
- Requiere que el puerto 80 esté abierto para verificación

### **Sobre Nginx:**
- Nginx actúa como reverse proxy
- La aplicación Spring Boot sigue corriendo en puerto 8080
- Nginx maneja SSL/TLS y redirige a Spring Boot
- Archivos estáticos se sirven directamente desde Nginx

---

**Última actualización:** 30 de diciembre de 2025  
**Estado:** ⏳ **PENDIENTE DE IMPLEMENTACIÓN - ESPERANDO APP PASSWORD DEL CLIENTE Y CONFIGURACIÓN DE DNS**

