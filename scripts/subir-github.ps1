# Script para subir Detodoya.com a GitHub
# Ejecutar después de crear el repositorio en GitHub

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Subir Detodoya.com a GitHub" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar que estamos en el directorio correcto
if (-not (Test-Path "pom.xml")) {
    Write-Host "❌ Error: No se encuentra pom.xml. Ejecuta este script desde la raíz del proyecto." -ForegroundColor Red
    exit 1
}

# Verificar que git está inicializado
if (-not (Test-Path ".git")) {
    Write-Host "❌ Error: Git no está inicializado. Ejecuta 'git init' primero." -ForegroundColor Red
    exit 1
}

Write-Host "📋 Pasos a seguir:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Ve a https://github.com/new" -ForegroundColor White
Write-Host "2. Nombre del repositorio: detodoya-app" -ForegroundColor White
Write-Host "3. Descripción: Catálogo profesional de productos Detodoya.com" -ForegroundColor White
Write-Host "4. Visibilidad: Private o Public (tu eleccion)" -ForegroundColor White
Write-Host "5. NO marques: Add a README file, Add .gitignore, Choose a license" -ForegroundColor White
Write-Host "6. Click en 'Create repository'" -ForegroundColor White
Write-Host ""
Write-Host "Presiona ENTER cuando hayas creado el repositorio en GitHub..." -ForegroundColor Yellow
Read-Host

# Verificar si ya existe el remote
$remoteExists = git remote -v 2>&1 | Select-String "origin"
if ($remoteExists) {
    Write-Host "⚠️  Ya existe un remote 'origin'. ¿Deseas reemplazarlo? (S/N)" -ForegroundColor Yellow
    $replace = Read-Host
    if ($replace -eq "S" -or $replace -eq "s") {
        git remote remove origin
        Write-Host "✅ Remote 'origin' eliminado" -ForegroundColor Green
    } else {
        Write-Host "❌ Operación cancelada" -ForegroundColor Red
        exit 1
    }
}

# Agregar remote
Write-Host ""
Write-Host "🔗 Agregando remote de GitHub..." -ForegroundColor Cyan
git remote add origin https://github.com/LuceroGustavo/detodoya-app.git

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Remote agregado correctamente" -ForegroundColor Green
} else {
    Write-Host "❌ Error al agregar remote. Verifica que el repositorio existe en GitHub." -ForegroundColor Red
    exit 1
}

# Verificar remotes
Write-Host ""
Write-Host "📡 Remotes configurados:" -ForegroundColor Cyan
git remote -v

# Subir código
Write-Host ""
Write-Host "📤 Subiendo código a GitHub..." -ForegroundColor Cyan
Write-Host "   (Puede pedirte autenticación)" -ForegroundColor Yellow
Write-Host ""

git push -u origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  ✅ ¡Código subido exitosamente!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "🌐 Repositorio: https://github.com/LuceroGustavo/detodoya-app" -ForegroundColor Cyan
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "❌ Error al subir el código. Posibles causas:" -ForegroundColor Red
    Write-Host "   - El repositorio no existe en GitHub" -ForegroundColor Yellow
    Write-Host "   - Problemas de autenticación (necesitas Personal Access Token)" -ForegroundColor Yellow
    Write-Host "   - El repositorio ya tiene contenido" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "💡 Soluciones:" -ForegroundColor Cyan
    Write-Host "   1. Verifica que el repositorio existe: https://github.com/LuceroGustavo/detodoya-app" -ForegroundColor White
    Write-Host "   2. Si el repositorio tiene contenido, usa: git push -u origin main --force" -ForegroundColor White
    Write-Host "   3. Para autenticación, crea un Personal Access Token en GitHub Settings" -ForegroundColor White
}

