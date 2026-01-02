#!/bin/bash
# Script de Verificación Completa - Ejecutar directamente en el servidor
# Uso: ssh -p5638 root@149.50.144.53 'bash -s' < scripts/ejecutar-verificacion-completa.sh

echo "=========================================="
echo "🔍 VERIFICACIÓN COMPLETA DEL SERVIDOR"
echo "Servidor: Detodoya.com - Donweb"
echo "=========================================="
echo ""

echo "1️⃣ INFORMACIÓN DEL SERVIDOR"
echo "----------------------------------------"
hostname
uname -a
uptime
echo ""

echo "2️⃣ APLICACIÓN SPRING BOOT"
echo "----------------------------------------"
echo "Procesos Java relacionados con Detodoya:"
ps aux | grep -E '[d]etodoya|[j]ava.*8080' || echo "No se encontraron procesos"
echo ""
echo "Puerto 8080:"
ss -tlnp | grep ':8080 ' || echo "Puerto 8080 no está en uso"
echo ""
echo "Respuesta de localhost:8080:"
curl -I http://localhost:8080 2>/dev/null | head -1 || echo "No responde"
echo ""

echo "3️⃣ NGINX"
echo "----------------------------------------"
echo "Estado:"
systemctl is-active nginx && echo "✅ Nginx está activo" || echo "❌ Nginx NO está activo"
echo ""
echo "Configuración:"
nginx -t 2>&1
echo ""
echo "Puertos:"
ss -tlnp | grep -E ':(80|443) ' || echo "Puertos 80/443 no están escuchando"
echo ""
echo "Configuraciones disponibles:"
ls -la /etc/nginx/sites-available/ 2>/dev/null | head -10
echo ""
echo "Configuraciones habilitadas:"
ls -la /etc/nginx/sites-enabled/ 2>/dev/null | head -10
echo ""

echo "4️⃣ CERTIFICADO SSL"
echo "----------------------------------------"
if [ -d /etc/letsencrypt/live/detodoya.com.ar ]; then
    echo "✅ Certificado existe"
    ls -la /etc/letsencrypt/live/detodoya.com.ar/
    echo ""
    echo "Validez del certificado:"
    openssl x509 -in /etc/letsencrypt/live/detodoya.com.ar/fullchain.pem -noout -dates 2>/dev/null
else
    echo "❌ Certificado NO encontrado"
    echo "Certificados disponibles:"
    ls -la /etc/letsencrypt/live/ 2>/dev/null || echo "No hay certificados instalados"
fi
echo ""

echo "5️⃣ REDIRECCIÓN HTTP → HTTPS"
echo "----------------------------------------"
echo "HTTP (debe redirigir):"
curl -I http://detodoya.com.ar 2>/dev/null | head -3
echo ""
echo "HTTPS (debe responder):"
curl -I https://detodoya.com.ar 2>/dev/null | head -3
echo ""

echo "6️⃣ DNS"
echo "----------------------------------------"
echo "detodoya.com.ar:"
dig +short detodoya.com.ar 2>/dev/null || nslookup detodoya.com.ar 2>/dev/null | grep -A 1 "Name:"
echo ""
echo "www.detodoya.com.ar:"
dig +short www.detodoya.com.ar 2>/dev/null || nslookup www.detodoya.com.ar 2>/dev/null | grep -A 1 "Name:"
echo ""

echo "7️⃣ FIREWALL (UFW)"
echo "----------------------------------------"
if command -v ufw &> /dev/null; then
    ufw status verbose | head -15
else
    echo "UFW no está instalado"
fi
echo ""

echo "8️⃣ RECURSOS DEL SERVIDOR"
echo "----------------------------------------"
echo "Memoria:"
free -h
echo ""
echo "Disco:"
df -h
echo ""
echo "Carga:"
uptime
echo ""

echo "9️⃣ LOGS RECIENTES"
echo "----------------------------------------"
echo "Errores de Nginx (últimos 5):"
tail -100 /var/log/nginx/error.log 2>/dev/null | grep -i error | tail -5 || echo "No hay errores recientes"
echo ""

echo "=========================================="
echo "✅ VERIFICACIÓN COMPLETA"
echo "=========================================="

