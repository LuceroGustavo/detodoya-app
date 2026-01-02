#!/bin/bash

# Script de Verificación Remota del Servidor Detodoya.com
# Este script puede ejecutarse directamente en el servidor
# Fecha: Enero 2025

echo "=========================================="
echo "🔍 VERIFICACIÓN COMPLETA DEL SERVIDOR"
echo "Servidor: Detodoya.com - Donweb"
echo "=========================================="
echo ""

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Contador de problemas
PROBLEMAS=0

# Función para verificar
check() {
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅${NC} $1"
        return 0
    else
        echo -e "${RED}❌${NC} $1"
        PROBLEMAS=$((PROBLEMAS + 1))
        return 1
    fi
}

echo -e "${BLUE}1️⃣ VERIFICANDO SISTEMA${NC}"
echo "----------------------------------------"
echo "Hostname: $(hostname)"
echo "IP: $(hostname -I | awk '{print $1}')"
echo "Sistema: $(lsb_release -d 2>/dev/null | cut -f2 || uname -a)"
echo "Uptime: $(uptime -p 2>/dev/null || uptime)"
echo ""

echo -e "${BLUE}2️⃣ VERIFICANDO SERVICIOS${NC}"
echo "----------------------------------------"
systemctl is-active --quiet nginx && echo -e "${GREEN}✅${NC} Nginx está activo" || echo -e "${RED}❌${NC} Nginx NO está activo"
systemctl is-active --quiet mysql && echo -e "${GREEN}✅${NC} MySQL está activo" || echo -e "${RED}❌${NC} MySQL NO está activo"
echo ""

echo -e "${BLUE}3️⃣ VERIFICANDO APLICACIÓN SPRING BOOT${NC}"
echo "----------------------------------------"
if ps aux | grep -v grep | grep -q "[d]etodoya"; then
    echo -e "${GREEN}✅${NC} Aplicación Detodoya está corriendo"
    echo "   Proceso:"
    ps aux | grep -v grep | grep "[d]etodoya" | head -1 | awk '{print "   PID:", $2, "| CPU:", $3"% | MEM:", $4"%"}'
else
    echo -e "${RED}❌${NC} Aplicación Detodoya NO está corriendo"
    PROBLEMAS=$((PROBLEMAS + 1))
fi

if ss -tlnp | grep -q ":8080 "; then
    echo -e "${GREEN}✅${NC} Puerto 8080 está en uso"
    ss -tlnp | grep ":8080 " | head -1
else
    echo -e "${RED}❌${NC} Puerto 8080 NO está en uso"
    PROBLEMAS=$((PROBLEMAS + 1))
fi

# Probar respuesta local
HTTP_RESPONSE=$(curl -sI http://localhost:8080 2>/dev/null | head -1)
if [ ! -z "$HTTP_RESPONSE" ]; then
    echo -e "${GREEN}✅${NC} Aplicación responde: $HTTP_RESPONSE"
else
    echo -e "${RED}❌${NC} Aplicación NO responde en localhost:8080"
    PROBLEMAS=$((PROBLEMAS + 1))
fi
echo ""

echo -e "${BLUE}4️⃣ VERIFICANDO NGINX${NC}"
echo "----------------------------------------"
if command -v nginx &> /dev/null; then
    echo -e "${GREEN}✅${NC} Nginx está instalado: $(nginx -v 2>&1)"
    
    # Verificar configuración
    if nginx -t 2>&1 | grep -q "successful"; then
        echo -e "${GREEN}✅${NC} Configuración de Nginx es válida"
    else
        echo -e "${RED}❌${NC} Configuración de Nginx tiene errores"
        nginx -t
        PROBLEMAS=$((PROBLEMAS + 1))
    fi
    
    # Verificar puertos
    if ss -tlnp | grep -q ":80 "; then
        echo -e "${GREEN}✅${NC} Puerto 80 (HTTP) está escuchando"
    else
        echo -e "${RED}❌${NC} Puerto 80 NO está escuchando"
        PROBLEMAS=$((PROBLEMAS + 1))
    fi
    
    if ss -tlnp | grep -q ":443 "; then
        echo -e "${GREEN}✅${NC} Puerto 443 (HTTPS) está escuchando"
    else
        echo -e "${RED}❌${NC} Puerto 443 NO está escuchando"
        PROBLEMAS=$((PROBLEMAS + 1))
    fi
    
    # Verificar configuración de detodoya
    if [ -f /etc/nginx/sites-available/detodoya ] || [ -f /etc/nginx/sites-enabled/detodoya ]; then
        echo -e "${GREEN}✅${NC} Configuración de Nginx para detodoya existe"
        CONFIG_FILE=$(ls /etc/nginx/sites-available/detodoya /etc/nginx/sites-enabled/detodoya 2>/dev/null | head -1)
        if grep -q "detodoya.com.ar" "$CONFIG_FILE" 2>/dev/null; then
            echo -e "${GREEN}✅${NC} Configuración incluye detodoya.com.ar"
        fi
    else
        echo -e "${YELLOW}⚠️${NC} Configuración de Nginx para detodoya NO encontrada"
        echo "   Archivos disponibles:"
        ls -la /etc/nginx/sites-available/ 2>/dev/null | head -5
    fi
else
    echo -e "${RED}❌${NC} Nginx NO está instalado"
    PROBLEMAS=$((PROBLEMAS + 1))
fi
echo ""

echo -e "${BLUE}5️⃣ VERIFICANDO CERTIFICADO SSL${NC}"
echo "----------------------------------------"
if [ -d /etc/letsencrypt/live/detodoya.com.ar ]; then
    echo -e "${GREEN}✅${NC} Certificado SSL existe"
    
    if [ -f /etc/letsencrypt/live/detodoya.com.ar/fullchain.pem ]; then
        echo -e "${GREEN}✅${NC} Archivo fullchain.pem existe"
        
        # Verificar validez
        EXPIRY=$(openssl x509 -in /etc/letsencrypt/live/detodoya.com.ar/fullchain.pem -noout -enddate 2>/dev/null | cut -d= -f2)
        if [ ! -z "$EXPIRY" ]; then
            echo "   Certificado expira: $EXPIRY"
            
            # Verificar si está próximo a expirar (menos de 30 días)
            EXPIRY_EPOCH=$(date -d "$EXPIRY" +%s 2>/dev/null || echo "0")
            NOW_EPOCH=$(date +%s)
            DAYS_LEFT=$(( (EXPIRY_EPOCH - NOW_EPOCH) / 86400 ))
            
            if [ $DAYS_LEFT -lt 0 ]; then
                echo -e "${RED}❌${NC} Certificado EXPIRADO"
                PROBLEMAS=$((PROBLEMAS + 1))
            elif [ $DAYS_LEFT -lt 30 ]; then
                echo -e "${YELLOW}⚠️${NC} Certificado expira en $DAYS_LEFT días"
            else
                echo -e "${GREEN}✅${NC} Certificado válido por $DAYS_LEFT días más"
            fi
        fi
    else
        echo -e "${RED}❌${NC} Archivo fullchain.pem NO existe"
        PROBLEMAS=$((PROBLEMAS + 1))
    fi
else
    echo -e "${RED}❌${NC} Certificado SSL NO encontrado"
    echo "   Directorio esperado: /etc/letsencrypt/live/detodoya.com.ar"
    echo "   Certificados disponibles:"
    ls -la /etc/letsencrypt/live/ 2>/dev/null | head -5 || echo "   No hay certificados instalados"
    PROBLEMAS=$((PROBLEMAS + 1))
fi
echo ""

echo -e "${BLUE}6️⃣ VERIFICANDO REDIRECCIÓN HTTP → HTTPS${NC}"
echo "----------------------------------------"
HTTP_REDIRECT=$(curl -sI http://detodoya.com.ar 2>/dev/null | grep -i "location\|301\|302" | head -1)
if echo "$HTTP_REDIRECT" | grep -qi "https"; then
    echo -e "${GREEN}✅${NC} HTTP redirige a HTTPS"
    echo "   $HTTP_REDIRECT"
else
    echo -e "${YELLOW}⚠️${NC} HTTP no redirige a HTTPS o no responde"
    echo "   Respuesta: $HTTP_REDIRECT"
fi

HTTPS_RESPONSE=$(curl -sI https://detodoya.com.ar 2>/dev/null | head -1)
if [ ! -z "$HTTPS_RESPONSE" ]; then
    if echo "$HTTPS_RESPONSE" | grep -qE "(200|302)"; then
        echo -e "${GREEN}✅${NC} HTTPS responde correctamente: $HTTPS_RESPONSE"
    else
        echo -e "${YELLOW}⚠️${NC} HTTPS responde: $HTTPS_RESPONSE"
    fi
else
    echo -e "${RED}❌${NC} HTTPS NO responde"
    PROBLEMAS=$((PROBLEMAS + 1))
fi
echo ""

echo -e "${BLUE}7️⃣ VERIFICANDO DNS${NC}"
echo "----------------------------------------"
if command -v dig &> /dev/null; then
    DNS_IP=$(dig +short detodoya.com.ar | tail -1)
    if [ "$DNS_IP" = "149.50.144.53" ]; then
        echo -e "${GREEN}✅${NC} DNS apunta correctamente a 149.50.144.53"
    else
        echo -e "${YELLOW}⚠️${NC} DNS apunta a: $DNS_IP (esperado: 149.50.144.53)"
    fi
    
    WWW_DNS=$(dig +short www.detodoya.com.ar | tail -1)
    if [ ! -z "$WWW_DNS" ]; then
        echo "   www.detodoya.com.ar → $WWW_DNS"
    fi
else
    echo -e "${YELLOW}⚠️${NC} dig no está instalado, usando nslookup"
    nslookup detodoya.com.ar 2>/dev/null | grep -A 1 "Name:" || echo "   No se pudo resolver"
fi
echo ""

echo -e "${BLUE}8️⃣ VERIFICANDO FIREWALL (UFW)${NC}"
echo "----------------------------------------"
if command -v ufw &> /dev/null; then
    UFW_STATUS=$(ufw status | head -1)
    if echo "$UFW_STATUS" | grep -qi "active"; then
        echo -e "${GREEN}✅${NC} Firewall UFW está activo"
        echo "   Reglas relevantes:"
        ufw status numbered | grep -E "(80|443|5638|8080)" || echo "   No se encontraron reglas para estos puertos"
    else
        echo -e "${YELLOW}⚠️${NC} Firewall UFW está inactivo"
    fi
else
    echo -e "${YELLOW}⚠️${NC} UFW no está instalado"
fi
echo ""

echo -e "${BLUE}9️⃣ VERIFICANDO LOGS RECIENTES${NC}"
echo "----------------------------------------"
if [ -f /var/log/nginx/error.log ]; then
    ERROR_COUNT=$(tail -100 /var/log/nginx/error.log | grep -i error | wc -l)
    if [ "$ERROR_COUNT" -gt 0 ]; then
        echo -e "${YELLOW}⚠️${NC} Se encontraron $ERROR_COUNT errores en los últimos 100 logs"
        echo "   Últimos 3 errores:"
        tail -100 /var/log/nginx/error.log | grep -i error | tail -3 | sed 's/^/   /'
    else
        echo -e "${GREEN}✅${NC} No hay errores recientes en logs de Nginx"
    fi
else
    echo -e "${YELLOW}⚠️${NC} Archivo de log de errores no encontrado"
fi
echo ""

echo -e "${BLUE}🔟 VERIFICANDO RECURSOS DEL SERVIDOR${NC}"
echo "----------------------------------------"
echo "Memoria:"
free -h | grep Mem | awk '{print "   Total:", $2, "| Usado:", $3, "| Libre:", $4, "|", $3/$2*100 "% usado"}'
echo "Disco:"
df -h / | tail -1 | awk '{print "   Total:", $2, "| Usado:", $3, "| Libre:", $4, "|", $5, "usado"}'
echo "Carga del sistema:"
uptime | awk -F'load average:' '{print "   " $2}'
echo ""

echo "=========================================="
echo -e "${BLUE}📊 RESUMEN${NC}"
echo "=========================================="
if [ $PROBLEMAS -eq 0 ]; then
    echo -e "${GREEN}✅ TODO ESTÁ CORRECTO${NC}"
    echo "   No se encontraron problemas críticos"
else
    echo -e "${RED}❌ SE ENCONTRARON $PROBLEMAS PROBLEMA(S)${NC}"
    echo "   Revisa los elementos marcados con ❌ arriba"
fi
echo ""
echo "Para más detalles, consulta:"
echo "documentacion/servidor/verificacion-servidor-detodoya.md"
echo ""

