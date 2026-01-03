# Scripts para Descargar Video de Lovely Denim

## 📋 Opciones Disponibles

### Opción 1: Script Simple (Recomendado)
**Archivo:** `descargar-video-simple.py`

Este script descarga directamente el video desde la URL conocida: `https://i.imgur.com/qSdcfNx.mp4`

**Uso:**
```bash
python descargar-video-simple.py
```

### Opción 2: Script Completo (con extracción)
**Archivo:** `descargar-video-lovely.py`

Este script analiza la página y extrae todas las URLs de video encontradas.

**Uso:**
```bash
python descargar-video-lovely.py
```

## 🔧 Requisitos

### Instalar dependencias Python:
```bash
pip install requests beautifulsoup4
```

O si usas Python 3 específicamente:
```bash
pip3 install requests beautifulsoup4
```

## 📥 Alternativa con Node.js

Si prefieres Node.js, crea un archivo `download-video.js`:

```javascript
const https = require('https');
const fs = require('fs');

const url = 'https://i.imgur.com/qSdcfNx.mp4';
const file = fs.createWriteStream('lovely-musculosa-primrose.mp4');

https.get(url, (response) => {
    response.pipe(file);
    file.on('finish', () => {
        file.close();
        console.log('✅ Video descargado: lovely-musculosa-primrose.mp4');
    });
}).on('error', (err) => {
    fs.unlink('lovely-musculosa-primrose.mp4');
    console.error('❌ Error:', err.message);
});
```

Ejecutar:
```bash
node download-video.js
```

## 🌐 Alternativa con wget (Linux/Mac/Git Bash)

Si tienes `wget` instalado:
```bash
wget https://i.imgur.com/qSdcfNx.mp4 -O lovely-musculosa-primrose.mp4
```

## 📦 Alternativa con curl

```bash
curl -o lovely-musculosa-primrose.mp4 https://i.imgur.com/qSdcfNx.mp4
```

## ✅ Resultado

El video se descargará como: `lovely-musculosa-primrose.mp4` en el directorio actual.

