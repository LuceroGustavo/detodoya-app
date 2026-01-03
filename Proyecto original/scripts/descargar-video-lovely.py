#!/usr/bin/env python3
"""
Script para descargar video de Lovely Denim
Uso: python descargar-video-lovely.py
"""

import requests
from bs4 import BeautifulSoup
import re
import os
from urllib.parse import urljoin, urlparse

def download_video(url, filename=None):
    """Descarga un video desde una URL"""
    try:
        print(f"📥 Descargando video desde: {url}")
        response = requests.get(url, stream=True, timeout=30)
        response.raise_for_status()
        
        # Obtener nombre del archivo si no se proporciona
        if not filename:
            parsed_url = urlparse(url)
            filename = os.path.basename(parsed_url.path) or "video-lovely.mp4"
        
        # Asegurar extensión .mp4
        if not filename.endswith(('.mp4', '.webm', '.mov')):
            filename += ".mp4"
        
        # Descargar con barra de progreso simple
        total_size = int(response.headers.get('content-length', 0))
        downloaded = 0
        
        with open(filename, 'wb') as f:
            for chunk in response.iter_content(chunk_size=8192):
                if chunk:
                    f.write(chunk)
                    downloaded += len(chunk)
                    if total_size > 0:
                        percent = (downloaded / total_size) * 100
                        print(f"\r⏳ Progreso: {percent:.1f}%", end='', flush=True)
        
        print(f"\n✅ Video descargado exitosamente: {filename}")
        print(f"📊 Tamaño: {downloaded / (1024*1024):.2f} MB")
        return filename
        
    except Exception as e:
        print(f"❌ Error al descargar: {e}")
        return None

def extract_video_urls(url):
    """Extrae URLs de video de una página web"""
    try:
        print(f"🔍 Analizando página: {url}")
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }
        response = requests.get(url, headers=headers, timeout=30)
        response.raise_for_status()
        
        soup = BeautifulSoup(response.text, 'html.parser')
        video_urls = []
        
        # Buscar etiquetas <video>
        videos = soup.find_all('video')
        for video in videos:
            # Buscar src directo
            if video.get('src'):
                video_urls.append(video['src'])
            # Buscar en <source>
            for source in video.find_all('source'):
                if source.get('src'):
                    video_urls.append(source['src'])
        
        # Buscar URLs de video en atributos data-*
        for element in soup.find_all(attrs={'data-video-url': True}):
            video_urls.append(element['data-video-url'])
        
        # Buscar URLs de video en scripts JavaScript
        scripts = soup.find_all('script')
        for script in scripts:
            if script.string:
                # Buscar URLs .mp4, .webm, .m3u8
                urls = re.findall(r'https?://[^\s"\'<>]+\.(?:mp4|webm|m3u8)', script.string)
                video_urls.extend(urls)
        
        # Buscar en estilos inline
        for element in soup.find_all(style=True):
            style = element['style']
            urls = re.findall(r'https?://[^\s)]+\.(?:mp4|webm|m3u8)', style)
            video_urls.extend(urls)
        
        # Convertir URLs relativas a absolutas
        video_urls = [urljoin(url, v_url) for v_url in video_urls]
        
        # Eliminar duplicados
        video_urls = list(set(video_urls))
        
        return video_urls
        
    except Exception as e:
        print(f"❌ Error al analizar la página: {e}")
        return []

def main():
    url = "https://www.lovelydenim.com.ar/musculosa-primrose/p?skuId=1009450"
    
    print("=" * 60)
    print("🎬 Descargador de Video - Lovely Denim")
    print("=" * 60)
    
    # Extraer URLs de video
    video_urls = extract_video_urls(url)
    
    if not video_urls:
        print("\n⚠️  No se encontraron videos en la página")
        print("💡 Intentando descargar desde URL conocida...")
        # URL encontrada en la inspección: https://i.imgur.com/qSdcfNx.mp4
        known_url = "https://i.imgur.com/qSdcfNx.mp4"
        video_urls = [known_url]
    
    print(f"\n📹 Se encontraron {len(video_urls)} video(s):")
    for i, v_url in enumerate(video_urls, 1):
        print(f"   {i}. {v_url}")
    
    # Descargar el primer video (o todos)
    if video_urls:
        video_url = video_urls[0]  # Tomar el primer video encontrado
        
        # Si es URL de imgur, usar nombre más descriptivo
        if 'imgur.com' in video_url:
            filename = "lovely-musculosa-primrose.mp4"
        else:
            filename = None
        
        download_video(video_url, filename)
    else:
        print("\n❌ No se pudo encontrar ningún video")

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n\n⚠️  Descarga cancelada por el usuario")
    except Exception as e:
        print(f"\n❌ Error inesperado: {e}")

