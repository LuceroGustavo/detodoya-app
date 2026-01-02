# Instrucciones para Ejecutar Verificación del Servidor

**Fecha:** Enero 2025  
**Servidor:** 149.50.144.53 (Donweb)

---

## 🚀 **OPCIÓN 1: Ejecutar Script Automático (Recomendado)**

### **Paso 1: Subir el Script al Servidor**

Desde tu máquina Windows (PowerShell):

```powershell
# Subir script al servidor
scp -P5638 scripts/verificar-servidor-remoto.sh root@149.50.144.53:/root/
```

### **Paso 2: Conectar al Servidor**

```bash
ssh -p5638 root@149.50.144.53
```

### **Paso 3: Ejecutar el Script**

```bash
# Hacer ejecutable
chmod +x /root/verificar-servidor-remoto.sh

# Ejecutar
/root/verificar-servidor-remoto.sh
```

---

## 🔧 **OPCIÓN 2: Ejecutar Comandos Manualmente**

Si prefieres ejecutar comandos uno por uno, usa el archivo:
`scripts/comandos-verificacion-manual.txt`

Copia y pega cada sección en el servidor.

---

## 📋 **OPCIÓN 3: Ejecutar desde PowerShell (Cuando tengas acceso)**

Una vez que el firewall permita la conexión, puedes ejecutar comandos directamente:

```powershell
# Verificar aplicación
ssh -p5638 root@149.50.144.53 "ps aux | grep detodoya"

# Verificar Nginx
ssh -p5638 root@149.50.144.53 "systemctl status nginx"

# Verificar certificado SSL
ssh -p5638 root@149.50.144.53 "ls -la /etc/letsencrypt/live/detodoya.com.ar/"

# Ejecutar script completo
ssh -p5638 root@149.50.144.53 "bash -s" < scripts/verificar-servidor-remoto.sh
```

---

## ✅ **QUÉ VERIFICAR**

El script verifica:

1. ✅ Sistema operativo y recursos
2. ✅ Servicios (Nginx, MySQL)
3. ✅ Aplicación Spring Boot (proceso y puerto 8080)
4. ✅ Nginx (configuración, puertos 80 y 443)
5. ✅ Certificado SSL (existencia y validez)
6. ✅ Redirección HTTP → HTTPS
7. ✅ DNS (resolución correcta)
8. ✅ Firewall UFW
9. ✅ Logs recientes
10. ✅ Recursos del servidor (memoria, disco, carga)

---

## 📝 **NOTAS**

- El script muestra ✅ para elementos correctos
- Muestra ❌ para problemas que necesitan atención
- Muestra ⚠️ para advertencias
- Al final muestra un resumen con el número de problemas encontrados

---

**Última actualización:** Enero 2025

