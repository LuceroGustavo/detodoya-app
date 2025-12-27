# deploy.ps1 - Script de despliegue desde Windows a LightNode
# OriolaIndumentaria - Despliegue automático

param(
    [switch]$Menu,
    [string]$Action = "deploy"
)

# Configuración del servidor
$ServerIP = "149.104.92.116"
$ServerUser = "root"
$ProjectPath = "/home/oriola/OriolaIndumentaria"

# Colores para output
function Write-ColorOutput($ForegroundColor) {
    $fc = $host.UI.RawUI.ForegroundColor
    $host.UI.RawUI.ForegroundColor = $ForegroundColor
    if ($args) {
        Write-Output $args
    } else {
        $input | Write-Output
    }
    $host.UI.RawUI.ForegroundColor = $fc
}

function Write-Success($message) {
    Write-ColorOutput Green "✅ $message"
}

function Write-Info($message) {
    Write-ColorOutput Blue "[INFO] $message"
}

function Write-Warning($message) {
    Write-ColorOutput Yellow "⚠️  $message"
}

function Write-Error($message) {
    Write-ColorOutput Red "❌ $message"
}

function Write-Header($message) {
    Write-ColorOutput Cyan "=========================================="
    Write-ColorOutput Cyan "   $message"
    Write-ColorOutput Cyan "=========================================="
}

# Función para ejecutar comandos SSH
function Invoke-SSHCommand($command) {
    Write-Info "Ejecutando: $command"
    ssh "${ServerUser}@${ServerIP}" $command
}

# Función para verificar estado de la aplicación
function Get-AppStatus {
    Write-Info "Verificando estado de la aplicación..."
    $result = Invoke-SSHCommand "pgrep -f 'oriola-denim-0.0.1-SNAPSHOT.jar'"
    if ($result) {
        Write-Success "Aplicación corriendo (PID: $result)"
        return $true
    } else {
        Write-Warning "Aplicación no está corriendo"
        return $false
    }
}

# Función para parar la aplicación
function Stop-App {
    Write-Info "1️⃣ Parando aplicación..."
    Invoke-SSHCommand "cd $ProjectPath && pkill -f 'oriola-denim-0.0.1-SNAPSHOT.jar'"
    Start-Sleep -Seconds 3
    Write-Success "Aplicación detenida"
}

# Función para actualizar código
function Update-Code {
    Write-Info "2️⃣ Actualizando código desde GitHub..."
    Invoke-SSHCommand "cd $ProjectPath && git pull origin master"
    Write-Success "Código actualizado"
}

# Función para compilar
function Compile-App {
    Write-Info "3️⃣ Compilando aplicación..."
    Write-Warning "Esto puede tomar varios minutos..."
    Invoke-SSHCommand "cd $ProjectPath && mvn clean package -DskipTests"
    Write-Success "Compilación completada"
}

# Función para iniciar aplicación
function Start-App {
    Write-Info "4️⃣ Iniciando aplicación..."
    Invoke-SSHCommand "cd $ProjectPath && nohup java -jar target/oriola-denim-0.0.1-SNAPSHOT.jar --spring.profiles.active=lightnode > /dev/null 2>&1 &"
    Start-Sleep -Seconds 10
    
    if (Get-AppStatus) {
        Write-Success "Aplicación iniciada correctamente"
        Write-Info "🌐 Disponible en: http://$ServerIP:8080"
    } else {
        Write-Error "Error al iniciar la aplicación"
    }
}

# Función para despliegue completo
function Deploy-Full {
    Write-Header "ORIOLA INDUMENTARIA - DESPLIEGUE AUTOMÁTICO"
    Write-Info "Iniciando despliegue completo..."
    
    Stop-App
    Update-Code
    Compile-App
    Start-App
    
    Write-Success "🎉 Despliegue completado exitosamente"
    Write-Info "🌐 Aplicación disponible en: http://$ServerIP:8080"
}

# Función para ver logs
function Show-Logs {
    Write-Info "Mostrando logs de la aplicación..."
    Invoke-SSHCommand "cd $ProjectPath && tail -50 nohup.out"
}

# Función para reiniciar
function Restart-App {
    Write-Info "Reiniciando aplicación..."
    Stop-App
    Start-Sleep -Seconds 2
    Start-App
}

# Función para mostrar menú
function Show-Menu {
    do {
        Clear-Host
        Write-Header "ORIOLA INDUMENTARIA - MENÚ DE DESPLIEGUE"
        Write-Info "Estado actual:"
        Get-AppStatus
        Write-Host ""
        Write-Host "Selecciona una opción:"
        Write-Host "1. Parar aplicación"
        Write-Host "2. Actualizar código (git pull)"
        Write-Host "3. Compilar aplicación"
        Write-Host "4. Iniciar aplicación"
        Write-Host "5. Despliegue completo (1+2+3+4)"
        Write-Host "6. Ver logs de la aplicación"
        Write-Host "7. Reiniciar aplicación"
        Write-Host "8. Ver estado de la aplicación"
        Write-Host "9. Salir"
        Write-Host ""
        
        $choice = Read-Host "Ingresa tu opción (1-9)"
        
        switch ($choice) {
            "1" { Stop-App }
            "2" { Update-Code }
            "3" { Compile-App }
            "4" { Start-App }
            "5" { Deploy-Full }
            "6" { Show-Logs }
            "7" { Restart-App }
            "8" { Get-AppStatus }
            "9" { 
                Write-Success "Saliendo..."
                exit 0 
            }
            default { 
                Write-Error "Opción inválida"
            }
        }
        
        if ($choice -ne "9") {
            Write-Host ""
            Read-Host "Presiona Enter para continuar"
        }
    } while ($choice -ne "9")
}

# Función principal
function Main {
    if ($Menu) {
        Show-Menu
    } else {
        switch ($Action.ToLower()) {
            "deploy" { Deploy-Full }
            "stop" { Stop-App }
            "start" { Start-App }
            "restart" { Restart-App }
            "status" { Get-AppStatus }
            "logs" { Show-Logs }
            "update" { Update-Code }
            "compile" { Compile-App }
            default { 
                Write-Error "Acción no válida. Usa: deploy, stop, start, restart, status, logs, update, compile"
                Write-Info "Para usar el menú interactivo: .\deploy.ps1 -Menu"
            }
        }
    }
}

# Ejecutar función principal
Main
