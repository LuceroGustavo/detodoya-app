# Script PowerShell para desplegar Detodoya.com en servidor Donweb
# Ejecutar desde PowerShell en Windows

Write-Host "🚀 ==========================================" -ForegroundColor Cyan
Write-Host "   DETODOYA.COM - DESPLIEGUE EN SERVIDOR" -ForegroundColor Cyan
Write-Host "🚀 ==========================================" -ForegroundColor Cyan
Write-Host ""

$SERVER_IP = "149.50.144.53"
$SSH_PORT = "5638"
$SERVER_USER = "root"
$SERVER_PASSWORD = "Qbasic.1977.server"
$PROJECT_DIR = "/home/detodoya/Detodoya.com"
$SCRIPTS_DIR = "/home/detodoya/scripts"

Write-Host "📋 Información del servidor:" -ForegroundColor Yellow
Write-Host "   IP: $SERVER_IP"
Write-Host "   Puerto SSH: $SSH_PORT"
Write-Host "   Usuario: $SERVER_USER"
Write-Host "   Directorio proyecto: $PROJECT_DIR"
Write-Host ""

# Verificar que los scripts existen
if (-not (Test-Path "scripts/deploy-detodoya-donweb.sh")) {
    Write-Host "❌ Error: No se encontró scripts/deploy-detodoya-donweb.sh" -ForegroundColor Red
    exit 1
}

if (-not (Test-Path "scripts/menu-deploy-detodoya-donweb.sh")) {
    Write-Host "❌ Error: No se encontró scripts/menu-deploy-detodoya-donweb.sh" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Scripts encontrados" -ForegroundColor Green
Write-Host ""

# Paso 1: Subir scripts al servidor
Write-Host "1️⃣ Subiendo scripts al servidor..." -ForegroundColor Yellow

# Usar plink (PuTTY) o ssh si está disponible
$SSH_CMD = "ssh"
if (Get-Command ssh -ErrorAction SilentlyContinue) {
    Write-Host "   Usando SSH nativo..." -ForegroundColor Blue
    
    # Crear directorio de scripts en el servidor
    Write-Host "   Creando directorio de scripts en el servidor..." -ForegroundColor Blue
    ssh -p $SSH_PORT ${SERVER_USER}@${SERVER_IP} "mkdir -p $SCRIPTS_DIR"
    
    # Subir scripts
    Write-Host "   Subiendo deploy-detodoya-donweb.sh..." -ForegroundColor Blue
    scp -P $SSH_PORT scripts/deploy-detodoya-donweb.sh ${SERVER_USER}@${SERVER_IP}:${SCRIPTS_DIR}/
    
    Write-Host "   Subiendo menu-deploy-detodoya-donweb.sh..." -ForegroundColor Blue
    scp -P $SSH_PORT scripts/menu-deploy-detodoya-donweb.sh ${SERVER_USER}@${SERVER_IP}:${SCRIPTS_DIR}/
    
    # Hacer scripts ejecutables
    Write-Host "   Configurando permisos de ejecución..." -ForegroundColor Blue
    ssh -p $SSH_PORT ${SERVER_USER}@${SERVER_IP} "chmod +x $SCRIPTS_DIR/*.sh"
    
    Write-Host "✅ Scripts subidos correctamente" -ForegroundColor Green
} else {
    Write-Host "⚠️  SSH no está disponible. Usa estos comandos manualmente:" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "   # Conectar al servidor:" -ForegroundColor Cyan
    Write-Host "   ssh -p $SSH_PORT $SERVER_USER@$SERVER_IP" -ForegroundColor White
    Write-Host ""
    Write-Host "   # En el servidor, crear directorios:" -ForegroundColor Cyan
    Write-Host "   mkdir -p $SCRIPTS_DIR" -ForegroundColor White
    Write-Host "   mkdir -p $PROJECT_DIR" -ForegroundColor White
    Write-Host ""
    Write-Host "   # Desde Windows, subir scripts:" -ForegroundColor Cyan
    Write-Host "   scp -P $SSH_PORT scripts/deploy-detodoya-donweb.sh $SERVER_USER@$SERVER_IP:$SCRIPTS_DIR/" -ForegroundColor White
    Write-Host "   scp -P $SSH_PORT scripts/menu-deploy-detodoya-donweb.sh $SERVER_USER@$SERVER_IP:$SCRIPTS_DIR/" -ForegroundColor White
    Write-Host ""
    Write-Host "   # En el servidor, hacer ejecutables:" -ForegroundColor Cyan
    Write-Host "   chmod +x $SCRIPTS_DIR/*.sh" -ForegroundColor White
}

Write-Host ""
Write-Host "📝 Próximos pasos:" -ForegroundColor Yellow
Write-Host "   1. Conectar al servidor: ssh -p $SSH_PORT $SERVER_USER@$SERVER_IP" -ForegroundColor White
Write-Host "   2. Crear directorio del proyecto: mkdir -p $PROJECT_DIR" -ForegroundColor White
Write-Host "   3. Clonar repositorio (o subir código)" -ForegroundColor White
Write-Host "   4. Ejecutar despliegue: cd $PROJECT_DIR && $SCRIPTS_DIR/deploy-detodoya-donweb.sh" -ForegroundColor White
Write-Host ""

