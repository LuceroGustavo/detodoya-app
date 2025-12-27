#!/usr/bin/env python3

"""
Script simple para descargar el video de Lovely Denim
URL conocida: https://i.imgur.com/qSdcfNx.mp4
"""

import requests
import os

def download_video():
    """Descarga el video directamente desde la URL conocida"""
    video_url = "https://i.imgur.com/qSdcfNx.mp4"
    filename = "lovely-musculosa-primrose.mp4"
    
    print("=" * 60)
    print("🎬 Descargando video de Lovely Denim")
    print("=" * 60)
    print(f"📥 URL: {video_url}")
    print(f"💾 Archivo: {filename}\n")
    
    try:
        response = requests.get(video_url, stream=True, timeout=30)
        response.raise_for_status()
        
        total_size = int(response.headers.get('content-length', 0))
        
        with open(filename, 'wb') as f:
            downloaded = 0
            for chunk in response.iter_content(chunk_size=8192):
                if chunk:
                    f.write(chunk)
                    downloaded += len(chunk)
                    if total_size > 0:
                        percent = (downloaded / total_size) * 100
                        print(f"\r⏳ Descargando: {percent:.1f}% ({downloaded / (1024*1024):.2f} MB)", end='', flush=True)
        
        file_size = os.path.getsize(filename) / (1024 * 1024)
        print(f"\n\n✅ ¡Video descargado exitosamente!")
        print(f"📁 Archivo: {filename}")
        print(f"📊 Tamaño: {file_size:.2f} MB")
        print(f"📍 Ubicación: {os.path.abspath(filename)}")
        
    except Exception as e:
        print(f"\n❌ Error al descargar: {e}")
        print("💡 Verifica tu conexión a internet")

if __name__ == "__main__":
    download_video()

