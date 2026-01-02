# Verificación del Servidor Detodoya.com - Donweb

**Fecha:** Enero 2025  
**Servidor:** 149.50.144.53  
**Dominio:** detodoya.com.ar  
**Puerto SSH:** 5638

---

## 🔍 **SCRIPT DE VERIFICACIÓN COMPLETA**

Ejecuta estos comandos en el servidor para verificar que todo esté correcto:

### **1. Conectar al Servidor**

```bash
ssh -p5638 root@149.50.144.53
```

---

## ✅ **VERIFICACIÓN 1: Estado de la Aplicación Spring Boot**

### **Verificar que la aplicación está corriendo:**

```bash
# Ver procesos Java
ps aux | grep detodoya
ps aux | grep java

# Verificar puerto 8080
netstat -tlnp | grep 8080
# O con ss (más moderno)
ss -tlnp | grep 8080

# Verificar que responde
curl -I http://localhost:8080
```

**Resultado esperado:**
- ✅ Proceso Java corriendo con `detodoya`
- ✅ Puerto 8080 en estado LISTEN
- ✅ HTTP 200 o 302 (redirect) desde localhost

---

## ✅ **VERIFICACIÓN 2: Estado de Nginx**

### **Verificar que Nginx está corriendo:**

```bash
# Estado del servicio
systemctl status nginx

# Verificar configuración
nginx -t

# Ver procesos
ps aux | grep nginx

# Verificar puertos 80 y 443
netstat -tlnp | grep -E ':(80|443)'
ss -tlnp | grep -E ':(80|443)'
```

**Resultado esperado:**
- ✅ Nginx activo (running)
- ✅ Configuración válida (syntax is ok)
- ✅ Puertos 80 y 443 en LISTEN

---

## ✅ **VERIFICACIÓN 3: Configuración de Nginx para Detodoya**

### **Verificar archivo de configuración:**

```bash
# Ver configuración de detodoya
cat /etc/nginx/sites-available/detodoya
# O si está en otro nombre
ls -la /etc/nginx/sites-available/ | grep -i detodoya
ls -la /etc/nginx/sites-enabled/ | grep -i detodoya

# Ver todas las configuraciones activas
ls -la /etc/nginx/sites-enabled/
```

**Buscar en la configuración:**
- ✅ `server_name detodoya.com.ar www.detodoya.com.ar;`
- ✅ Redirección HTTP → HTTPS (puerto 80)
- ✅ Configuración SSL (puerto 443)
- ✅ `proxy_pass http://localhost:8080;`

---

## ✅ **VERIFICACIÓN 4: Certificado SSL**

### **Verificar certificado SSL instalado:**

```bash
# Ver certificados de Let's Encrypt
ls -la /etc/letsencrypt/live/

# Verificar certificado específico
ls -la /etc/letsencrypt/live/detodoya.com.ar/

# Ver detalles del certificado
openssl x509 -in /etc/letsencrypt/live/detodoya.com.ar/fullchain.pem -text -noout | grep -A 2 "Validity"

# Verificar desde el servidor
openssl s_client -connect detodoya.com.ar:443 -servername detodoya.com.ar < /dev/null 2>/dev/null | openssl x509 -noout -dates
```

**Resultado esperado:**
- ✅ Certificado existe en `/etc/letsencrypt/live/detodoya.com.ar/`
- ✅ Certificado válido (notBefore y notAfter correctos)
- ✅ Certificado no expirado

---

## ✅ **VERIFICACIÓN 5: Redirección HTTP → HTTPS**

### **Probar redirección:**

```bash
# Probar redirección HTTP (debe redirigir a HTTPS)
curl -I http://detodoya.com.ar
curl -I http://www.detodoya.com.ar

# Probar HTTPS (debe responder correctamente)
curl -I https://detodoya.com.ar
curl -I https://www.detodoya.com.ar
```

**Resultado esperado:**
- ✅ HTTP responde con `301 Moved Permanently` o `302 Found`
- ✅ Location header apunta a `https://detodoya.com.ar`
- ✅ HTTPS responde con `200 OK` o `302 Found`

---

## ✅ **VERIFICACIÓN 6: Firewall (UFW)**

### **Verificar reglas del firewall:**

```bash
# Ver estado del firewall
ufw status verbose

# Ver reglas específicas
ufw status numbered
```

**Resultado esperado:**
- ✅ Puerto 80 (HTTP) permitido
- ✅ Puerto 443 (HTTPS) permitido
- ✅ Puerto 5638 (SSH) permitido
- ✅ Puerto 8080 puede estar bloqueado (si Nginx está configurado)

---

## ✅ **VERIFICACIÓN 7: Firewall Donweb (Panel)**

### **Verificar en el panel de Donweb:**

1. Acceder a: https://micuenta.donweb.com/es-ar/servicios/cloud-iaas/vps/5469468/configurar/firewall
2. Verificar que existan reglas para:
   - ✅ Puerto 80 (HTTP) - TCP
   - ✅ Puerto 443 (HTTPS) - TCP
   - ✅ Puerto 5638 (SSH) - TCP

---

## ✅ **VERIFICACIÓN 8: DNS**

### **Verificar resolución DNS:**

```bash
# Verificar DNS desde el servidor
nslookup detodoya.com.ar
nslookup www.detodoya.com.ar

# O con dig
dig detodoya.com.ar
dig www.detodoya.com.ar

# Verificar que apunta a la IP correcta
dig +short detodoya.com.ar
# Debe mostrar: 149.50.144.53
```

**Resultado esperado:**
- ✅ `detodoya.com.ar` resuelve a `149.50.144.53`
- ✅ `www.detodoya.com.ar` resuelve a `149.50.144.53` o CNAME a `detodoya.com.ar`

---

## ✅ **VERIFICACIÓN 9: Acceso desde Internet**

### **Probar acceso externo:**

```bash
# Desde tu máquina local (fuera del servidor)
# Probar HTTP
curl -I http://detodoya.com.ar
curl -I http://www.detodoya.com.ar

# Probar HTTPS
curl -I https://detodoya.com.ar
curl -I https://www.detodoya.com.ar

# Verificar certificado SSL
openssl s_client -connect detodoya.com.ar:443 -servername detodoya.com.ar < /dev/null
```

**Resultado esperado:**
- ✅ HTTP redirige a HTTPS
- ✅ HTTPS responde correctamente
- ✅ Certificado SSL válido y no expirado

---

## ✅ **VERIFICACIÓN 10: Logs de Nginx**

### **Revisar logs por errores:**

```bash
# Ver logs de acceso
tail -n 50 /var/log/nginx/access.log

# Ver logs de errores
tail -n 50 /var/log/nginx/error.log

# Buscar errores relacionados con SSL
grep -i ssl /var/log/nginx/error.log | tail -20

# Buscar errores relacionados con detodoya
grep -i detodoya /var/log/nginx/error.log | tail -20
```

---

## ✅ **VERIFICACIÓN 11: Logs de la Aplicación**

### **Revisar logs de Spring Boot:**

```bash
# Ver logs de la aplicación (depende de dónde esté corriendo)
# Si está con nohup:
tail -f /home/detodoya/Detodoya.com/app.log

# O si está con systemd:
journalctl -u detodoya-app -n 50

# Buscar errores
grep -i error /home/detodoya/Detodoya.com/app.log | tail -20
```

---

## 🔧 **CONFIGURACIÓN RECOMENDADA DE NGINX**

Si no existe la configuración, crear `/etc/nginx/sites-available/detodoya`:

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

---

## 🚀 **PASOS PARA CONFIGURAR SSL (Si no está configurado)**

### **1. Instalar Certbot:**

```bash
sudo apt update
sudo apt install certbot python3-certbot-nginx -y
```

### **2. Obtener Certificado SSL:**

```bash
sudo certbot --nginx -d detodoya.com.ar -d www.detodoya.com.ar
```

**Durante la instalación:**
- Email: Ingresa tu email
- Términos: Acepta (A)
- Compartir email: Opcional (N)
- Redirección HTTP → HTTPS: Selecciona opción 2 (Redirect)

### **3. Verificar Renovación Automática:**

```bash
# Probar renovación
sudo certbot renew --dry-run

# Verificar que el timer esté activo
systemctl status certbot.timer
```

---

## 📋 **CHECKLIST DE VERIFICACIÓN**

Ejecuta este checklist y marca lo que esté correcto:

- [ ] Aplicación Spring Boot corriendo en puerto 8080
- [ ] Nginx corriendo y activo
- [ ] Configuración de Nginx para detodoya.com.ar existe
- [ ] Certificado SSL instalado y válido
- [ ] Redirección HTTP → HTTPS funcionando
- [ ] Firewall UFW configurado (puertos 80, 443, 5638)
- [ ] Firewall Donweb configurado (puertos 80, 443, 5638)
- [ ] DNS apunta a 149.50.144.53
- [ ] Acceso HTTPS desde Internet funciona
- [ ] Logs sin errores críticos

---

## 🐛 **SOLUCIÓN DE PROBLEMAS COMUNES**

### **Problema: Certificado SSL no encontrado**

```bash
# Verificar si existe
ls -la /etc/letsencrypt/live/

# Si no existe, instalar:
sudo certbot --nginx -d detodoya.com.ar -d www.detodoya.com.ar
```

### **Problema: Nginx no redirige a HTTPS**

```bash
# Verificar configuración
cat /etc/nginx/sites-available/detodoya | grep -A 5 "listen 80"

# Debe tener: return 301 https://$server_name$request_uri;
```

### **Problema: Error 502 Bad Gateway**

```bash
# Verificar que Spring Boot está corriendo
ps aux | grep java

# Verificar que responde en localhost
curl http://localhost:8080

# Ver logs de Nginx
tail -f /var/log/nginx/error.log
```

### **Problema: Certificado expirado**

```bash
# Renovar certificado
sudo certbot renew

# Reiniciar Nginx
sudo systemctl reload nginx
```

---

## 📝 **NOTAS IMPORTANTES**

1. **Certificado SSL:** Let's Encrypt renueva automáticamente cada 90 días
2. **Nginx:** Debe reiniciarse después de cambios: `sudo systemctl reload nginx`
3. **Spring Boot:** Debe estar corriendo en `localhost:8080` para que Nginx pueda hacer proxy
4. **Firewall:** Tanto UFW como el firewall de Donweb deben permitir los puertos necesarios

---

**Última actualización:** Enero 2025  
**Servidor:** Donweb - 149.50.144.53

