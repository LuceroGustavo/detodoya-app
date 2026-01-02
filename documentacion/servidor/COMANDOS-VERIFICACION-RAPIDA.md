# Comandos de Verificación Rápida - Servidor Detodoya

**Fecha:** Enero 2025  
**Servidor:** 149.50.144.53 (Donweb)  
**Puerto SSH:** 5638  
**Usuario:** root

---

## 🔐 **CONEXIÓN SSH**

### **Con Clave SSH (Recomendado):**

```bash
ssh -p5638 root@149.50.144.53
```

**Nota:** Si la clave SSH está configurada, se conectará automáticamente sin pedir contraseña.

### **Con Contraseña (Si no hay clave SSH):**

```bash
ssh -p5638 root@149.50.144.53
# Contraseña: Qbasic.1977.server
```

---

## ✅ **VERIFICACIONES RÁPIDAS**

### **1. Verificar Aplicación Spring Boot**

```bash
# Ver procesos
ps aux | grep detodoya

# Ver puerto 8080
ss -tlnp | grep 8080

# Probar respuesta
curl -I http://localhost:8080
```

### **2. Verificar Nginx**

```bash
# Estado
systemctl status nginx

# Configuración
nginx -t

# Puertos
ss -tlnp | grep -E ':(80|443)'
```

### **3. Verificar Certificado SSL**

```bash
# Ver certificados
ls -la /etc/letsencrypt/live/

# Verificar detodoya
ls -la /etc/letsencrypt/live/detodoya.com.ar/

# Verificar validez
openssl s_client -connect detodoya.com.ar:443 -servername detodoya.com.ar < /dev/null 2>/dev/null | openssl x509 -noout -dates
```

### **4. Verificar Redirección HTTP → HTTPS**

```bash
# HTTP (debe redirigir)
curl -I http://detodoya.com.ar

# HTTPS (debe responder)
curl -I https://detodoya.com.ar
```

### **5. Verificar DNS**

```bash
# Resolver DNS
dig +short detodoya.com.ar
# Debe mostrar: 149.50.144.53
```

### **6. Verificar Firewall UFW**

```bash
# Estado
ufw status verbose

# Ver reglas
ufw status numbered
```

### **7. Ver Logs Recientes**

```bash
# Logs de Nginx
tail -n 50 /var/log/nginx/error.log

# Logs de aplicación (si está en /home/detodoya)
tail -n 50 /home/detodoya/Detodoya.com/app.log
```

---

## 🚀 **EJECUTAR SCRIPT DE VERIFICACIÓN COMPLETA**

Si el script está en el servidor:

```bash
# Hacer ejecutable (si no lo es)
chmod +x /root/verificar-servidor-detodoya.sh

# Ejecutar
/root/verificar-servidor-detodoya.sh
```

---

## 📋 **INFORMACIÓN DEL SERVIDOR**

- **IP:** 149.50.144.53
- **Hostname:** vps-5469468-x.dattaweb.com
- **Sistema:** Ubuntu 24.04.3 LTS
- **Puerto SSH:** 5638 (⚠️ No es el puerto estándar 22)

---

**Última actualización:** Enero 2025

