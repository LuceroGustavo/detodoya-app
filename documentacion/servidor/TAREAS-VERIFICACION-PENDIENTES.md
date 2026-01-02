# Tareas de Verificación Pendientes - Servidor Detodoya

**Fecha:** Enero 2025  
**Servidor:** 149.50.144.53 (Donweb)  
**Puerto SSH:** 5638  
**Estado:** ⏳ Pendiente de ejecutar desde red sin restricciones

---

## 🎯 **OBJETIVO**

Verificar que el servidor Detodoya.com está correctamente configurado para resolver el problema de Fortinet (categorización "Sin calificación") y asegurar que HTTPS funciona correctamente.

---

## 📋 **CHECKLIST DE VERIFICACIÓN**

### **1. Verificar Aplicación Spring Boot** ⏳

**Comandos a ejecutar:**
```bash
ssh -p5638 root@149.50.144.53

# Verificar que la aplicación está corriendo
ps aux | grep detodoya
ps aux | grep java | grep 8080

# Verificar puerto 8080
ss -tlnp | grep 8080
# Debe mostrar algo como: LISTEN 0 128 0.0.0.0:8080

# Probar respuesta local
curl -I http://localhost:8080
# Debe responder con HTTP 200 o 302
```

**Qué verificar:**
- [ ] Aplicación Detodoya está corriendo
- [ ] Puerto 8080 está en uso
- [ ] Aplicación responde en localhost:8080

**Si NO está corriendo:**
```bash
cd /home/detodoya/Detodoya.com
# O la ruta donde esté el proyecto
nohup java -jar target/detodoya-0.0.1-SNAPSHOT.jar --spring.profiles.active=donweb > app.log 2>&1 &
```

---

### **2. Verificar Nginx** ⏳

**Comandos a ejecutar:**
```bash
# Estado del servicio
systemctl status nginx

# Verificar configuración
nginx -t
# Debe mostrar: "syntax is ok" y "test is successful"

# Verificar puertos
ss -tlnp | grep -E ':(80|443) '
# Debe mostrar puertos 80 y 443 en LISTEN

# Ver configuraciones disponibles
ls -la /etc/nginx/sites-available/

# Ver configuraciones habilitadas
ls -la /etc/nginx/sites-enabled/
```

**Qué verificar:**
- [ ] Nginx está activo (running)
- [ ] Configuración es válida
- [ ] Puertos 80 y 443 están escuchando
- [ ] Existe configuración para detodoya.com.ar

**Si NO existe configuración para detodoya:**
Ver sección "Configurar Nginx" más abajo.

---

### **3. Verificar Certificado SSL** ⏳

**Comandos a ejecutar:**
```bash
# Ver certificados instalados
ls -la /etc/letsencrypt/live/

# Verificar certificado de detodoya
ls -la /etc/letsencrypt/live/detodoya.com.ar/

# Ver validez del certificado
openssl x509 -in /etc/letsencrypt/live/detodoya.com.ar/fullchain.pem -noout -dates
# Debe mostrar notBefore y notAfter (fechas válidas)
```

**Qué verificar:**
- [ ] Certificado existe en `/etc/letsencrypt/live/detodoya.com.ar/`
- [ ] Archivos `fullchain.pem` y `privkey.pem` existen
- [ ] Certificado no está expirado

**Si NO existe certificado:**
Ver sección "Instalar Certificado SSL" más abajo.

---

### **4. Verificar Redirección HTTP → HTTPS** ⏳

**Comandos a ejecutar:**
```bash
# Probar HTTP (debe redirigir a HTTPS)
curl -I http://detodoya.com.ar
# Debe mostrar: HTTP/1.1 301 Moved Permanently o 302 Found
# Y Location: https://detodoya.com.ar/...

# Probar HTTPS (debe responder)
curl -I https://detodoya.com.ar
# Debe mostrar: HTTP/1.1 200 OK o 302 Found
```

**Qué verificar:**
- [ ] HTTP redirige a HTTPS (301 o 302)
- [ ] HTTPS responde correctamente (200 o 302)
- [ ] No hay errores de certificado SSL

---

### **5. Verificar DNS** ⏳

**Comandos a ejecutar:**
```bash
# Verificar resolución DNS
dig +short detodoya.com.ar
# Debe mostrar: 149.50.144.53

dig +short www.detodoya.com.ar
# Debe mostrar: 149.50.144.53 o CNAME a detodoya.com.ar
```

**Qué verificar:**
- [ ] `detodoya.com.ar` apunta a `149.50.144.53`
- [ ] `www.detodoya.com.ar` apunta correctamente

---

### **6. Verificar Firewall** ⏳

**Comandos a ejecutar:**
```bash
# Ver estado del firewall UFW
ufw status verbose

# Verificar reglas específicas
ufw status numbered | grep -E "(80|443|5638|8080)"
```

**Qué verificar:**
- [ ] Firewall UFW está activo
- [ ] Puertos 80, 443, 5638 están permitidos
- [ ] Puerto 8080 puede estar bloqueado (normal si Nginx hace proxy)

**NOTA:** También verificar firewall de Donweb en el panel:
- https://micuenta.donweb.com/es-ar/servicios/cloud-iaas/vps/5469468/configurar/firewall
- Debe tener reglas para puertos 80, 443, 5638

---

### **7. Verificar Logs** ⏳

**Comandos a ejecutar:**
```bash
# Ver errores recientes de Nginx
tail -50 /var/log/nginx/error.log | grep -i error

# Ver logs de acceso
tail -20 /var/log/nginx/access.log

# Ver logs de la aplicación (si existe)
tail -50 /home/detodoya/Detodoya.com/app.log 2>/dev/null
```

**Qué verificar:**
- [ ] No hay errores críticos en logs de Nginx
- [ ] No hay errores en logs de la aplicación

---

### **8. Verificar Recursos del Servidor** ⏳

**Comandos a ejecutar:**
```bash
# Memoria
free -h

# Disco
df -h

# Carga del sistema
uptime
```

**Qué verificar:**
- [ ] Memoria disponible suficiente
- [ ] Espacio en disco suficiente
- [ ] Carga del sistema normal

---

## 🔧 **TAREAS DE CONFIGURACIÓN (Si faltan)**

### **TAREA 1: Configurar Nginx para Detodoya** ⏳

**Si NO existe configuración de Nginx para detodoya.com.ar:**

```bash
# Crear archivo de configuración
sudo nano /etc/nginx/sites-available/detodoya
```

**Contenido del archivo:**
```nginx
# Redirección HTTP → HTTPS
server {
    listen 80;
    listen [::]:80;
    server_name detodoya.com.ar www.detodoya.com.ar;
    
    # Redirigir todo a HTTPS
    return 301 https://$server_name$request_uri;
}

# Configuración HTTPS
server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name detodoya.com.ar www.detodoya.com.ar;

    # Certificados SSL
    ssl_certificate /etc/letsencrypt/live/detodoya.com.ar/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/detodoya.com.ar/privkey.pem;
    
    # Configuración SSL moderna
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers 'ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384';
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # Headers de seguridad
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

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
        alias /home/detodoya/uploads/;
        expires 30d;
        add_header Cache-Control "public, immutable";
        access_log off;
    }

    # Logs
    access_log /var/log/nginx/detodoya-access.log;
    error_log /var/log/nginx/detodoya-error.log;
}
```

**Habilitar configuración:**
```bash
# Crear enlace simbólico
sudo ln -s /etc/nginx/sites-available/detodoya /etc/nginx/sites-enabled/

# Verificar configuración
sudo nginx -t

# Recargar Nginx
sudo systemctl reload nginx
```

---

### **TAREA 2: Instalar Certificado SSL** ⏳

**Si NO existe certificado SSL:**

```bash
# Instalar Certbot (si no está instalado)
sudo apt update
sudo apt install certbot python3-certbot-nginx -y

# Obtener certificado SSL
sudo certbot --nginx -d detodoya.com.ar -d www.detodoya.com.ar
```

**Durante la instalación:**
- Email: Ingresa tu email
- Términos: Acepta (A)
- Compartir email: Opcional (N)
- Redirección HTTP → HTTPS: Selecciona opción 2 (Redirect)

**Verificar renovación automática:**
```bash
# Probar renovación
sudo certbot renew --dry-run

# Verificar que el timer esté activo
systemctl status certbot.timer
```

---

### **TAREA 3: Iniciar Aplicación Spring Boot** ⏳

**Si la aplicación NO está corriendo:**

```bash
# Ir al directorio del proyecto
cd /home/detodoya/Detodoya.com
# O la ruta donde esté el proyecto

# Verificar que existe el JAR
ls -la target/detodoya-0.0.1-SNAPSHOT.jar

# Si no existe, compilar:
mvn clean package -DskipTests

# Iniciar aplicación en segundo plano
nohup java -jar target/detodoya-0.0.1-SNAPSHOT.jar --spring.profiles.active=donweb > app.log 2>&1 &

# Verificar que está corriendo
ps aux | grep detodoya
ss -tlnp | grep 8080
```

---

## 📊 **SCRIPT DE VERIFICACIÓN COMPLETA**

**Para ejecutar todo de una vez:**

```bash
# Conectar al servidor
ssh -p5638 root@149.50.144.53

# Subir el script (desde tu máquina)
scp -P5638 scripts/ejecutar-verificacion-completa.sh root@149.50.144.53:/root/

# En el servidor, ejecutar:
chmod +x /root/ejecutar-verificacion-completa.sh
/root/ejecutar-verificacion-completa.sh
```

**O ejecutar directamente:**
```bash
ssh -p5638 root@149.50.144.53 'bash -s' < scripts/ejecutar-verificacion-completa.sh
```

---

## ✅ **RESULTADO ESPERADO**

Después de completar todas las verificaciones y configuraciones:

1. ✅ Aplicación Spring Boot corriendo en puerto 8080
2. ✅ Nginx activo con configuración para detodoya.com.ar
3. ✅ Certificado SSL instalado y válido
4. ✅ HTTP redirige a HTTPS
5. ✅ HTTPS responde correctamente
6. ✅ DNS apunta correctamente
7. ✅ Firewall configurado
8. ✅ Sin errores en logs

---

## 🎯 **PRIORIDADES**

### **🔴 ALTA PRIORIDAD (Hacer primero):**
1. Verificar que la aplicación está corriendo
2. Verificar/Configurar Nginx
3. Verificar/Instalar certificado SSL
4. Verificar redirección HTTP → HTTPS

### **🟡 MEDIA PRIORIDAD:**
5. Verificar DNS
6. Verificar firewall
7. Revisar logs

### **🟢 BAJA PRIORIDAD:**
8. Verificar recursos del servidor
9. Optimizaciones

---

## 📝 **NOTAS IMPORTANTES**

- ⚠️ **NO TOCAR puerto 8081** - Aplicación virtual (Fulbito) en uso
- ✅ **Puerto 8080** - Para Detodoya solamente
- ✅ **Puertos 80 y 443** - Para Nginx (proxy a 8080)
- ✅ **Puerto 5638** - SSH (ya configurado)

---

## 🔗 **ARCHIVOS RELACIONADOS**

- `scripts/ejecutar-verificacion-completa.sh` - Script de verificación
- `scripts/verificar-servidor-remoto.sh` - Script completo con colores
- `documentacion/servidor/verificacion-servidor-detodoya.md` - Guía detallada
- `documentacion/servidor/COMANDOS-VERIFICACION-RAPIDA.md` - Comandos rápidos

---

**Última actualización:** Enero 2025  
**Estado:** ⏳ Pendiente de ejecutar desde red sin restricciones

