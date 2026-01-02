# Resultados de Verificación del Servidor Detodoya

**Fecha:** Enero 2025  
**Servidor:** 149.50.144.53 (Donweb)  
**Puerto SSH:** 5638

---

## ✅ **VERIFICACIONES REALIZADAS**

### **1. Conectividad SSH**
- ✅ **Estado:** Conexión SSH exitosa al puerto 5638
- ✅ **Acceso:** Funcional desde PowerShell

### **2. Puertos Accesibles desde Internet**
- ✅ **Puerto 80 (HTTP):** Accesible (TcpTestSucceeded: True)
- ✅ **Puerto 443 (HTTPS):** Accesible (TcpTestSucceeded: True)
- ❌ **Puerto 8080:** No accesible desde Internet (bloqueado por firewall)
- ⚠️ **Puerto 5638 (SSH):** Bloqueado por Fortinet (solicitud en proceso)

---

## 📋 **VERIFICACIONES PENDIENTES (Ejecutar en el Servidor)**

Debido a limitaciones con la captura de salida en PowerShell, ejecuta estos comandos directamente en el servidor:

### **Comandos para Ejecutar:**

```bash
# 1. Información del servidor
hostname
uname -a
uptime

# 2. Verificar aplicación Spring Boot
ps aux | grep detodoya
ps aux | grep java | grep 8080

# 3. Verificar puertos
ss -tlnp | grep 8080
ss -tlnp | grep -E ':(80|443) '

# 4. Verificar Nginx
systemctl status nginx
nginx -t
ls -la /etc/nginx/sites-available/
ls -la /etc/nginx/sites-enabled/

# 5. Verificar certificado SSL
ls -la /etc/letsencrypt/live/
ls -la /etc/letsencrypt/live/detodoya.com.ar/ 2>/dev/null || echo "Certificado no encontrado"

# 6. Verificar redirección HTTP → HTTPS
curl -I http://detodoya.com.ar
curl -I https://detodoya.com.ar

# 7. Verificar aplicación local
curl -I http://localhost:8080

# 8. Verificar DNS
dig +short detodoya.com.ar
dig +short www.detodoya.com.ar

# 9. Verificar recursos
free -h
df -h
```

---

## 🔍 **ANÁLISIS DE RESULTADOS**

### **✅ Lo que SÍ funciona:**
1. **Conexión SSH:** Funcional (puerto 5638)
2. **Puertos HTTP/HTTPS:** Accesibles desde Internet (80 y 443)
3. **Nginx:** Instalado y configurado (según comandos ejecutados)

### **⚠️ Lo que necesita verificación:**
1. **Aplicación Spring Boot:** Verificar que está corriendo en puerto 8080
2. **Certificado SSL:** Verificar si existe para detodoya.com.ar
3. **Configuración Nginx:** Verificar si hay configuración para detodoya.com.ar
4. **Redirección HTTP → HTTPS:** Verificar que funciona correctamente

### **❌ Problemas identificados:**
1. **Puerto 8080 bloqueado:** No accesible desde Internet (normal si Nginx está configurado como proxy)
2. **Fortinet bloqueando SSH:** Puerto 5638 bloqueado (solicitud en proceso)

---

## 🎯 **RECOMENDACIONES**

### **1. Verificar Configuración de Nginx para Detodoya**

Si no existe configuración, crear `/etc/nginx/sites-available/detodoya`:

```nginx
# Redirección HTTP → HTTPS
server {
    listen 80;
    listen [::]:80;
    server_name detodoya.com.ar www.detodoya.com.ar;
    return 301 https://$server_name$request_uri;
}

# Configuración HTTPS
server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name detodoya.com.ar www.detodoya.com.ar;

    ssl_certificate /etc/letsencrypt/live/detodoya.com.ar/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/detodoya.com.ar/privkey.pem;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /uploads/ {
        alias /home/detodoya/uploads/;
        expires 30d;
    }
}
```

### **2. Instalar Certificado SSL (Si no existe)**

```bash
sudo apt install certbot python3-certbot-nginx -y
sudo certbot --nginx -d detodoya.com.ar -d www.detodoya.com.ar
```

### **3. Verificar que la Aplicación está Corriendo**

```bash
# Ver procesos
ps aux | grep detodoya

# Si no está corriendo, iniciarla:
cd /home/detodoya/Detodoya.com
nohup java -jar target/detodoya-0.0.1-SNAPSHOT.jar --spring.profiles.active=donweb > app.log 2>&1 &
```

---

## 📝 **NOTAS IMPORTANTES**

- ⚠️ **NO TOCAR puerto 8081** - Aplicación virtual (Fulbito) en uso
- ✅ **Puerto 8080** - Para Detodoya (verificar que está corriendo)
- ✅ **Puertos 80 y 443** - Accesibles desde Internet
- ⚠️ **Puerto 5638** - SSH bloqueado por Fortinet (solicitud en proceso)

---

**Próximos pasos:** Ejecutar los comandos de verificación directamente en el servidor para obtener resultados completos.

