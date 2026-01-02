# Instrucciones para Ejecutar Verificación desde Casa

**Fecha:** Enero 2025  
**Servidor:** 149.50.144.53 (Donweb)  
**Puerto SSH:** 5638

---

## 🏠 **EJECUTAR DESDE CASA (Red sin restricciones)**

Cuando estés en casa con tu red liberada, sigue estos pasos:

---

## 🚀 **OPCIÓN 1: Ejecutar Script Completo (Recomendado)**

### **Paso 1: Conectar al Servidor**

```bash
ssh -p5638 root@149.50.144.53
```

### **Paso 2: Subir Script de Verificación**

Desde tu máquina Windows (en casa):

```powershell
# Subir script al servidor
scp -P5638 scripts/ejecutar-verificacion-completa.sh root@149.50.144.53:/root/
```

### **Paso 3: Ejecutar Script**

En el servidor:

```bash
# Hacer ejecutable
chmod +x /root/ejecutar-verificacion-completa.sh

# Ejecutar
/root/ejecutar-verificacion-completa.sh
```

**El script mostrará:**
- ✅ Estado de la aplicación Spring Boot
- ✅ Estado de Nginx
- ✅ Certificado SSL
- ✅ Redirección HTTP → HTTPS
- ✅ DNS
- ✅ Firewall
- ✅ Recursos del servidor
- ✅ Logs recientes

---

## 🔧 **OPCIÓN 2: Ejecutar Comandos Individuales**

Si prefieres ejecutar comandos uno por uno:

### **1. Verificar Aplicación**

```bash
ssh -p5638 root@149.50.144.53

# Ver procesos
ps aux | grep detodoya
ps aux | grep java | grep 8080

# Ver puerto
ss -tlnp | grep 8080

# Probar respuesta
curl -I http://localhost:8080
```

### **2. Verificar Nginx**

```bash
systemctl status nginx
nginx -t
ss -tlnp | grep -E ':(80|443) '
ls -la /etc/nginx/sites-available/
ls -la /etc/nginx/sites-enabled/
```

### **3. Verificar Certificado SSL**

```bash
ls -la /etc/letsencrypt/live/detodoya.com.ar/
openssl x509 -in /etc/letsencrypt/live/detodoya.com.ar/fullchain.pem -noout -dates
```

### **4. Verificar HTTP/HTTPS**

```bash
curl -I http://detodoya.com.ar
curl -I https://detodoya.com.ar
```

---

## 📋 **CHECKLIST RÁPIDO**

Ejecuta estos comandos y marca lo que esté correcto:

```bash
ssh -p5638 root@149.50.144.53

# Checklist
echo "=== CHECKLIST ==="
echo ""
echo "1. Aplicación Spring Boot:"
ps aux | grep detodoya | grep -v grep && echo "✅ Corriendo" || echo "❌ NO está corriendo"
echo ""
echo "2. Puerto 8080:"
ss -tlnp | grep 8080 && echo "✅ En uso" || echo "❌ NO en uso"
echo ""
echo "3. Nginx:"
systemctl is-active nginx && echo "✅ Activo" || echo "❌ Inactivo"
echo ""
echo "4. Certificado SSL:"
test -d /etc/letsencrypt/live/detodoya.com.ar && echo "✅ Existe" || echo "❌ NO existe"
echo ""
echo "5. HTTP → HTTPS:"
curl -I http://detodoya.com.ar 2>/dev/null | grep -i "301\|302" && echo "✅ Redirige" || echo "❌ NO redirige"
echo ""
echo "6. HTTPS:"
curl -I https://detodoya.com.ar 2>/dev/null | grep -i "200\|302" && echo "✅ Responde" || echo "❌ NO responde"
```

---

## 🔧 **SI FALTA ALGO - CONFIGURAR**

### **Si NO existe configuración de Nginx:**

Ver archivo: `documentacion/servidor/TAREAS-VERIFICACION-PENDIENTES.md`  
Sección: "TAREA 1: Configurar Nginx para Detodoya"

### **Si NO existe certificado SSL:**

Ver archivo: `documentacion/servidor/TAREAS-VERIFICACION-PENDIENTES.md`  
Sección: "TAREA 2: Instalar Certificado SSL"

### **Si la aplicación NO está corriendo:**

Ver archivo: `documentacion/servidor/TAREAS-VERIFICACION-PENDIENTES.md`  
Sección: "TAREA 3: Iniciar Aplicación Spring Boot"

---

## 📝 **GUARDAR RESULTADOS**

Después de ejecutar la verificación, guarda los resultados:

```bash
# Ejecutar script y guardar en archivo
/root/ejecutar-verificacion-completa.sh > /root/verificacion-$(date +%Y%m%d).txt 2>&1

# Ver resultados
cat /root/verificacion-*.txt
```

---

## 🎯 **OBJETIVO FINAL**

Después de completar todas las verificaciones:

1. ✅ Aplicación corriendo en puerto 8080
2. ✅ Nginx configurado y activo
3. ✅ Certificado SSL instalado
4. ✅ HTTP → HTTPS funcionando
5. ✅ HTTPS accesible desde Internet
6. ✅ Sin errores en logs

**Esto ayudará a:**
- Resolver el problema de Fortinet (sitio accesible y categorizable)
- Mejorar la seguridad (HTTPS)
- Mejorar el SEO (HTTPS es mejor para buscadores)

---

## 📞 **SI HAY PROBLEMAS**

1. Revisar logs: `/var/log/nginx/error.log`
2. Verificar estado de servicios: `systemctl status nginx`
3. Verificar configuración: `nginx -t`
4. Consultar documentación: `documentacion/servidor/verificacion-servidor-detodoya.md`

---

**Última actualización:** Enero 2025  
**Ejecutar desde:** Red sin restricciones (casa)

