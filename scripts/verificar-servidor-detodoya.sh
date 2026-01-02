#!/bin/bash

# Script de Verificación del Servidor Detodoya.com
# Fecha: Enero 2025
# Servidor: Donweb - 149.50.144.53
# Puerto SSH: 5638
# Usuario: root
# Conexión: ssh -p5638 root@149.50.144.53

echo "=========================================="
echo "🔍 VERIFICACIÓN DEL SERVIDOR DETODOYA.COM"
echo "=========================================="
echo "Servidor: 149.50.144.53 (Donweb)"
echo "Puerto SSH: 5638"
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para verificar comando
check_command() {
    if command -v $1 &> /dev/null; then
        echo -e "${GREEN}✅${NC} $1 está instalado"
        return 0
    else
        echo -e "${RED}❌${NC} $1 NO está instalado"
        return 1
    fi
}

# Función para verificar servicio
check_service() {
    if systemctl is-active --quiet $1; then
        echo -e "${GREEN}✅${NC} Servicio $1 está activo"
        return 0
    else
        echo -e "${RED}❌${NC} Servicio $1 NO está activo"
        return 1
    fi
}

# Función para verificar puerto
check_port() {
    if ss -tlnp | grep -q ":$1 "; then
        echo -e "${GREEN}✅${NC} Puerto $1 está en uso"
        return 0
    else
        echo -e "${RED}❌${NC} Puerto $1 NO está en uso"
        return 1
    fi
}

echo "1️⃣ VERIFICANDO COMANDOS BÁSICOS"
echo "----------------------------------------"
check_command "java"
check_command "nginx"
check_command "mysql"
check_command "certbot"
echo ""

echo "2️⃣ VERIFICANDO SERVICIOS"
echo "----------------------------------------"
check_service "nginx"
check_service "mysql"
echo ""

echo "3️⃣ VERIFICANDO APLICACIÓN SPRING BOOT"
echo "----------------------------------------"
if ps aux | grep -v grep | grep -q "detodoya"; then
    echo -e "${GREEN}✅${NC} Aplicación Detodoya está corriendo"
    ps aux | grep -v grep | grep "detodoya" | head -1
else
    echo -e "${RED}❌${NC} Aplicación Detodoya NO está corriendo"
fi
check_port 8080
echo ""

echo "4️⃣ VERIFICANDO NGINX"
echo "----------------------------------------"
check_port 80
check_port 443
if [ -f /etc/nginx/sites-available/detodoya ]; then
    echo -e "${GREEN}✅${NC} Configuración de Nginx para detodoya existe"
else
    echo -e "${YELLOW}⚠️${NC} Configuración de Nginx para detodoya NO encontrada"
    echo "   Buscando otras configuraciones..."
    ls -la /etc/nginx/sites-available/ | grep -i detodoya || echo "   No se encontró ninguna configuración"
fi
echo ""

echo "5️⃣ VERIFICANDO CERTIFICADO SSL"
echo "----------------------------------------"
if [ -d /etc/letsencrypt/live/detodoya.com.ar ]; then
    echo -e "${GREEN}✅${NC} Certificado SSL existe"
    if [ -f /etc/letsencrypt/live/detodoya.com.ar/fullchain.pem ]; then
        echo "   Verificando validez del certificado..."
        EXPIRY=$(openssl x509 -in /etc/letsencrypt/live/detodoya.com.ar/fullchain.pem -noout -enddate 2>/dev/null | cut -d= -f2)
        if [ ! -z "$EXPIRY" ]; then
            echo "   Certificado expira: $EXPIRY"
        fi
    fi
else
    echo -e "${RED}❌${NC} Certificado SSL NO encontrado"
    echo "   Directorio: /etc/letsencrypt/live/detodoya.com.ar"
    echo "   Certificados disponibles:"
    ls -la /etc/letsencrypt/live/ 2>/dev/null || echo "   No hay certificados instalados"
fi
echo ""

echo "6️⃣ VERIFICANDO FIREWALL (UFW)"
echo "----------------------------------------"
if command -v ufw &> /dev/null; then
    UFW_STATUS=$(ufw status | head -1)
    echo "   Estado: $UFW_STATUS"
    if echo "$UFW_STATUS" | grep -q "inactive"; then
        echo -e "${YELLOW}⚠️${NC} Firewall UFW está inactivo"
    else
        echo -e "${GREEN}✅${NC} Firewall UFW está activo"
        echo "   Reglas:"
        ufw status numbered | grep -E "(80|443|5638|8080)" || echo "   No se encontraron reglas relevantes"
    fi
else
    echo -e "${YELLOW}⚠️${NC} UFW no está instalado"
fi
echo ""

echo "7️⃣ VERIFICANDO DNS"
echo "----------------------------------------"
if command -v dig &> /dev/null; then
    DNS_IP=$(dig +short detodoya.com.ar | tail -1)
    if [ "$DNS_IP" = "149.50.144.53" ]; then
        echo -e "${GREEN}✅${NC} DNS apunta correctamente a 149.50.144.53"
    else
        echo -e "${YELLOW}⚠️${NC} DNS apunta a: $DNS_IP (esperado: 149.50.144.53)"
    fi
else
    echo -e "${YELLOW}⚠️${NC} dig no está instalado, usando nslookup"
    nslookup detodoya.com.ar 2>/dev/null | grep -A 1 "Name:" || echo "   No se pudo resolver DNS"
fi
echo ""

echo "8️⃣ VERIFICANDO ACCESO HTTP/HTTPS"
echo "----------------------------------------"
echo "   Probando HTTP (debe redirigir a HTTPS)..."
HTTP_RESPONSE=$(curl -sI http://localhost 2>/dev/null | head -1)
if echo "$HTTP_RESPONSE" | grep -qE "(301|302)"; then
    echo -e "${GREEN}✅${NC} HTTP redirige correctamente: $HTTP_RESPONSE"
else
    echo -e "${YELLOW}⚠️${NC} HTTP respuesta: $HTTP_RESPONSE"
fi

echo "   Probando HTTPS..."
HTTPS_RESPONSE=$(curl -sI https://localhost 2>/dev/null | head -1)
if echo "$HTTPS_RESPONSE" | grep -qE "(200|302)"; then
    echo -e "${GREEN}✅${NC} HTTPS responde correctamente: $HTTPS_RESPONSE"
else
    echo -e "${RED}❌${NC} HTTPS no responde: $HTTPS_RESPONSE"
fi
echo ""

echo "9️⃣ VERIFICANDO LOGS RECIENTES"
echo "----------------------------------------"
if [ -f /var/log/nginx/error.log ]; then
    ERROR_COUNT=$(tail -100 /var/log/nginx/error.log | grep -i error | wc -l)
    if [ "$ERROR_COUNT" -gt 0 ]; then
        echo -e "${YELLOW}⚠️${NC} Se encontraron $ERROR_COUNT errores en los últimos 100 logs"
        echo "   Últimos errores:"
        tail -100 /var/log/nginx/error.log | grep -i error | tail -3
    else
        echo -e "${GREEN}✅${NC} No hay errores recientes en logs de Nginx"
    fi
else
    echo -e "${YELLOW}⚠️${NC} Archivo de log de errores no encontrado"
fi
echo ""

echo "=========================================="
echo "✅ VERIFICACIÓN COMPLETA"
echo "=========================================="
echo ""
echo "📋 RESUMEN:"
echo "   - Revisa los resultados arriba"
echo "   - Los elementos marcados con ✅ están correctos"
echo "   - Los elementos marcados con ❌ necesitan atención"
echo "   - Los elementos marcados con ⚠️ son advertencias"
echo ""
echo "📝 Para más detalles, consulta:"
echo "   documentacion/servidor/verificacion-servidor-detodoya.md"
echo ""

