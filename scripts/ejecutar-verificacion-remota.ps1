# Script PowerShell para Ejecutar Verificación Remota
# Ejecuta el script de verificación directamente en el servidor

$SERVER_IP = "149.50.144.53"
$SSH_PORT = "5638"
$SSH_USER = "root"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "🔍 VERIFICACIÓN REMOTA DEL SERVIDOR" -ForegroundColor Cyan
Write-Host "Servidor: $SERVER_IP" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar conectividad primero
Write-Host "Verificando conectividad..." -ForegroundColor Yellow
$testConnection = Test-NetConnection -ComputerName $SERVER_IP -Port $SSH_PORT -WarningAction SilentlyContinue

if (-not $testConnection.TcpTestSucceeded) {
    Write-Host "❌ No se puede conectar al servidor en el puerto $SSH_PORT" -ForegroundColor Red
    Write-Host "   El firewall puede estar bloqueando la conexión" -ForegroundColor Yellow
    Write-Host "   Espera a que se apruebe la solicitud en Fortinet" -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ Conectividad OK" -ForegroundColor Green
Write-Host ""

# Ejecutar script de verificación remoto
Write-Host "Ejecutando verificación completa..." -ForegroundColor Yellow
Write-Host ""

# Leer el script y ejecutarlo remotamente
$scriptPath = Join-Path $PSScriptRoot "verificar-servidor-remoto.sh"
if (Test-Path $scriptPath) {
    ssh -p$SSH_PORT $SSH_USER@$SERVER_IP "bash -s" < $scriptPath
} else {
    Write-Host "⚠️ Script no encontrado, ejecutando comandos básicos..." -ForegroundColor Yellow
    Write-Host ""
    
    # Comandos básicos de verificación
    Write-Host "1️⃣ Verificando aplicación Spring Boot..." -ForegroundColor Cyan
    ssh -p$SSH_PORT $SSH_USER@$SERVER_IP "ps aux | grep detodoya | grep -v grep"
    Write-Host ""
    
    Write-Host "2️⃣ Verificando puerto 8080..." -ForegroundColor Cyan
    ssh -p$SSH_PORT $SSH_USER@$SERVER_IP "ss -tlnp | grep 8080"
    Write-Host ""
    
    Write-Host "3️⃣ Verificando Nginx..." -ForegroundColor Cyan
    ssh -p$SSH_PORT $SSH_USER@$SERVER_IP "systemctl status nginx --no-pager | head -5"
    Write-Host ""
    
    Write-Host "4️⃣ Verificando certificado SSL..." -ForegroundColor Cyan
    ssh -p$SSH_PORT $SSH_USER@$SERVER_IP "ls -la /etc/letsencrypt/live/detodoya.com.ar/ 2>/dev/null || echo 'Certificado no encontrado'"
    Write-Host ""
    
    Write-Host "5️⃣ Verificando redirección HTTP..." -ForegroundColor Cyan
    ssh -p$SSH_PORT $SSH_USER@$SERVER_IP "curl -I http://detodoya.com.ar 2>/dev/null | head -1"
    Write-Host ""
    
    Write-Host "6️⃣ Verificando HTTPS..." -ForegroundColor Cyan
    ssh -p$SSH_PORT $SSH_USER@$SERVER_IP "curl -I https://detodoya.com.ar 2>/dev/null | head -1"
    Write-Host ""
}

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "✅ Verificación completada" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan

