#!/usr/bin/env python3
"""
Script para descargar video e imágenes de productos de Lovely Denim
Uso: python descargar-producto-lovely.py <URL>
Ejemplo: python descargar-producto-lovely.py "https://www.lovelydenim.com.ar/short-jose-star2/p?skuId=1009855"
"""

import requests
from bs4 import BeautifulSoup
import re
import os
import sys
from urllib.parse import urljoin, urlparse, unquote
from pathlib import Path

def sanitize_filename(filename):
    """Limpia el nombre de archivo para que sea válido en el sistema"""
    # Remover caracteres inválidos
    filename = re.sub(r'[<>:"/\\|?*]', '', filename)
    # Reemplazar espacios con guiones
    filename = filename.replace(' ', '-')
    # Limitar longitud
    if len(filename) > 100:
        filename = filename[:100]
    return filename

def create_directories(product_name):
    """Crea la estructura de directorios"""
    base_dir = Path("descarga lovely")
    product_dir = base_dir / product_name
    
    base_dir.mkdir(exist_ok=True)
    product_dir.mkdir(exist_ok=True)
    
    return product_dir

def download_file(url, filepath, description="archivo"):
    """Descarga un archivo desde una URL con barra de progreso"""
    try:
        print(f"📥 Descargando {description}...")
        print(f"   URL: {url}")
        
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }
        
        response = requests.get(url, headers=headers, stream=True, timeout=30)
        response.raise_for_status()
        
        total_size = int(response.headers.get('content-length', 0))
        downloaded = 0
        
        with open(filepath, 'wb') as f:
            for chunk in response.iter_content(chunk_size=8192):
                if chunk:
                    f.write(chunk)
                    downloaded += len(chunk)
                    if total_size > 0:
                        percent = (downloaded / total_size) * 100
                        print(f"\r   ⏳ Progreso: {percent:.1f}% ({downloaded / (1024*1024):.2f} MB)", end='', flush=True)
        
        file_size_mb = os.path.getsize(filepath) / (1024 * 1024)
        print(f"\r   ✅ {description.capitalize()} descargado: {filepath.name} ({file_size_mb:.2f} MB)")
        return True
        
    except Exception as e:
        print(f"\r   ❌ Error al descargar {description}: {e}")
        return False

def extract_product_name(url, soup):
    """Extrae el nombre del producto de la URL o del HTML"""
    # Intentar extraer de la URL
    parsed_url = urlparse(url)
    path_parts = parsed_url.path.split('/')
    
    # Buscar en el path (ej: /short-jose-star2/p)
    for part in path_parts:
        if part and part != 'p':
            product_name = part.replace('-', ' ').title()
            break
    else:
        product_name = "producto-lovely"
    
    # Intentar obtener del HTML
    try:
        # Buscar título del producto
        title_tag = soup.find('h1') or soup.find('title')
        if title_tag:
            title_text = title_tag.get_text().strip()
            # Limpiar y usar como nombre alternativo
            if len(title_text) < 50:
                product_name = sanitize_filename(title_text)
    except:
        pass
    
    return sanitize_filename(product_name)

def extract_video_urls(soup, base_url, html_content):
    """Extrae URLs de video de la página"""
    video_urls = []
    
    # Buscar etiquetas <video>
    videos = soup.find_all('video')
    for video in videos:
        # Buscar src directo
        if video.get('src'):
            video_urls.append(urljoin(base_url, video['src']))
        # Buscar en <source>
        for source in video.find_all('source'):
            if source.get('src'):
                video_urls.append(urljoin(base_url, source['src']))
    
    # Buscar URLs de video en atributos data-*
    for element in soup.find_all(attrs={'data-video-url': True}):
        video_urls.append(urljoin(base_url, element['data-video-url']))
    
    # Buscar en todos los atributos data-* que puedan contener videos
    for element in soup.find_all(True):  # Buscar todos los elementos
        if hasattr(element, 'attrs') and element.attrs:
            for attr, value in element.attrs.items():
                if isinstance(value, str) and any(ext in value.lower() for ext in ['.mp4', '.webm', '.mov', 'video']):
                    if value.startswith('http'):
                        video_urls.append(value)
                    elif value.startswith('/') or value.startswith('./'):
                        video_urls.append(urljoin(base_url, value))
    
    # Buscar URLs de video en scripts JavaScript (búsqueda más exhaustiva)
    scripts = soup.find_all('script')
    for script in scripts:
        if script.string:
            # Buscar URLs .mp4, .webm, .m3u8
            urls = re.findall(r'https?://[^\s"\'<>)]+\.(?:mp4|webm|m3u8)', script.string)
            video_urls.extend(urls)
            # Buscar en JSON dentro de scripts
            urls_json = re.findall(r'["\']https?://[^"\']+\.(?:mp4|webm|m3u8)["\']', script.string)
            video_urls.extend([url.strip('"\'') for url in urls_json])
    
    # Buscar en el HTML completo (sin parsear) para encontrar videos cargados dinámicamente
    if html_content:
        # Buscar patrones más amplios
        video_patterns = [
            r'https?://[^\s"\'<>)]+\.mp4[^\s"\'<>)]*',
            r'https?://[^\s"\'<>)]+\.webm[^\s"\'<>)]*',
            r'https?://[^\s"\'<>)]+\.mov[^\s"\'<>)]*',
            r'["\']https?://[^"\']+\.(?:mp4|webm|mov)[^"\']*["\']',
            r'videoUrl["\']?\s*[:=]\s*["\']([^"\']+\.(?:mp4|webm|mov))',
            r'video["\']?\s*[:=]\s*["\']([^"\']+\.(?:mp4|webm|mov))',
        ]
        for pattern in video_patterns:
            matches = re.findall(pattern, html_content, re.IGNORECASE)
            for match in matches:
                url = match.strip('"\'')
                if url.startswith('http'):
                    video_urls.append(url)
    
    # Buscar en estilos inline
    for element in soup.find_all(style=True):
        style = element['style']
        urls = re.findall(r'https?://[^\s)]+\.(?:mp4|webm|m3u8)', style)
        video_urls.extend(urls)
    
    # Eliminar duplicados manteniendo el orden y decodificar URLs
    seen = set()
    unique_urls = []
    for url in video_urls:
        # Decodificar caracteres Unicode escapados (ej: \u002F -> /)
        try:
            url = url.encode().decode('unicode_escape')
        except:
            pass
        # Limpiar URL
        url = url.split('?')[0]  # Remover query params
        if url not in seen and url.endswith(('.mp4', '.webm', '.mov', '.m3u8')):
            seen.add(url)
            unique_urls.append(url)
    
    return unique_urls

def extract_image_urls(soup, base_url):
    """Extrae URLs de imágenes del producto"""
    image_urls = []
    
    # Buscar imágenes en galerías comunes
    # Buscar en elementos con clase relacionada a imágenes
    img_selectors = [
        'img[src*="product"]',
        'img[src*="image"]',
        'img[src*="photo"]',
        'img[src*="gallery"]',
        '.product-image img',
        '.gallery img',
        '.carousel img',
        '[data-image]',
        '[data-src]'
    ]
    
    for selector in img_selectors:
        elements = soup.select(selector)
        for element in elements:
            # Obtener URL de src o data-src
            img_url = element.get('src') or element.get('data-src') or element.get('data-image')
            if img_url:
                full_url = urljoin(base_url, img_url)
                # Filtrar imágenes pequeñas (thumbnails) y logos
                if not any(exclude in full_url.lower() for exclude in ['logo', 'icon', 'avatar', 'thumbnail', 'thumb']):
                    image_urls.append(full_url)
    
    # Buscar URLs de imágenes en atributos de estilo
    for element in soup.find_all(attrs={'style': True}):
        style = element.get('style', '')
        urls = re.findall(r'url\(["\']?(https?://[^"\']+\.(?:jpg|jpeg|png|webp|gif))["\']?\)', style)
        image_urls.extend(urls)
    
    # Buscar en scripts (JSON, etc.)
    scripts = soup.find_all('script')
    for script in scripts:
        if script.string:
            # Buscar URLs de imágenes
            urls = re.findall(r'https?://[^\s"\'<>)]+\.(?:jpg|jpeg|png|webp|gif)', script.string)
            image_urls.extend(urls)
    
    # Eliminar duplicados y filtrar
    seen = set()
    unique_urls = []
    for url in image_urls:
        # Normalizar URL
        url = url.split('?')[0]  # Remover query parameters
        if url not in seen and url not in unique_urls:
            # Verificar que sea una imagen de producto (no logo, icon, etc.)
            if not any(exclude in url.lower() for exclude in ['logo', 'icon', 'avatar', 'favicon']):
                seen.add(url)
                unique_urls.append(url)
    
    return unique_urls

def main():
    if len(sys.argv) < 2:
        print("=" * 70)
        print("🎬 Descargador de Producto - Lovely Denim")
        print("=" * 70)
        print("\n❌ Error: Debes proporcionar una URL")
        print("\n📖 Uso:")
        print("   python descargar-producto-lovely.py <URL>")
        print("\n📝 Ejemplo:")
        print('   python descargar-producto-lovely.py "https://www.lovelydenim.com.ar/short-jose-star2/p?skuId=1009855"')
        sys.exit(1)
    
    url = sys.argv[1]
    
    print("=" * 70)
    print("🎬 Descargador de Producto - Lovely Denim")
    print("=" * 70)
    print(f"\n🔗 URL: {url}\n")
    
    # Obtener página
    try:
        print("📡 Obteniendo página...")
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }
        response = requests.get(url, headers=headers, timeout=30)
        response.raise_for_status()
        html_content = response.text
        soup = BeautifulSoup(html_content, 'html.parser')
        print("✅ Página obtenida correctamente\n")
    except Exception as e:
        print(f"❌ Error al obtener la página: {e}")
        sys.exit(1)
    
    # Extraer nombre del producto
    product_name = extract_product_name(url, soup)
    print(f"📦 Nombre del producto: {product_name}\n")
    
    # Crear directorios
    product_dir = create_directories(product_name)
    print(f"📁 Directorio creado: {product_dir}\n")
    
    # Extraer y descargar videos
    print("🔍 Buscando videos...")
    video_urls = extract_video_urls(soup, url, html_content)
    
    if video_urls:
        print(f"📹 Se encontraron {len(video_urls)} video(s):")
        for i, video_url in enumerate(video_urls, 1):
            print(f"   {i}. {video_url}")
        
        # Descargar el primer video (o todos)
        for i, video_url in enumerate(video_urls, 1):
            ext = 'mp4'
            if '.webm' in video_url.lower():
                ext = 'webm'
            elif '.mov' in video_url.lower():
                ext = 'mov'
            
            filename = product_dir / f"{product_name}-video{i}.{ext}"
            download_file(video_url, filename, f"video {i}")
    else:
        print("⚠️  No se encontraron videos\n")
    
    # Extraer y descargar imágenes
    print("\n🔍 Buscando imágenes...")
    image_urls = extract_image_urls(soup, url)
    
    if image_urls:
        print(f"🖼️  Se encontraron {len(image_urls)} imagen(es):")
        for i, img_url in enumerate(image_urls[:10], 1):  # Mostrar solo las primeras 10
            print(f"   {i}. {img_url}")
        if len(image_urls) > 10:
            print(f"   ... y {len(image_urls) - 10} más")
        
        # Descargar todas las imágenes
        print(f"\n📥 Descargando {len(image_urls)} imagen(es)...\n")
        downloaded = 0
        for i, img_url in enumerate(image_urls, 1):
            # Determinar extensión
            ext = 'jpg'
            if '.png' in img_url.lower():
                ext = 'png'
            elif '.webp' in img_url.lower():
                ext = 'webp'
            elif '.gif' in img_url.lower():
                ext = 'gif'
            
            filename = product_dir / f"{product_name}-imagen{i:03d}.{ext}"
            if download_file(img_url, filename, f"imagen {i}"):
                downloaded += 1
        
        print(f"\n✅ {downloaded}/{len(image_urls)} imágenes descargadas correctamente")
    else:
        print("⚠️  No se encontraron imágenes\n")
    
    # Resumen
    print("\n" + "=" * 70)
    print("📊 RESUMEN")
    print("=" * 70)
    print(f"📦 Producto: {product_name}")
    print(f"📁 Ubicación: {os.path.abspath(product_dir)}")
    
    # Contar archivos descargados
    files = list(product_dir.glob('*'))
    videos = [f for f in files if f.suffix in ['.mp4', '.webm', '.mov']]
    images = [f for f in files if f.suffix in ['.jpg', '.jpeg', '.png', '.webp', '.gif']]
    
    print(f"📹 Videos: {len(videos)}")
    print(f"🖼️  Imágenes: {len(images)}")
    print(f"📁 Total archivos: {len(files)}")
    
    total_size = sum(f.stat().st_size for f in files) / (1024 * 1024)
    print(f"💾 Tamaño total: {total_size:.2f} MB")
    print("=" * 70)
    print("\n✅ ¡Descarga completada!\n")

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n\n⚠️  Descarga cancelada por el usuario")
        sys.exit(1)
    except Exception as e:
        print(f"\n❌ Error inesperado: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

