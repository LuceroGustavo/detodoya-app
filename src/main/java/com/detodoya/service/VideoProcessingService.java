package com.detodoya.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

@Service
public class VideoProcessingService {
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @Value("${upload.thumbnail.path}")
    private String thumbnailPath;
    
    // Formatos de video permitidos
    private static final String[] FORMATOS_PERMITIDOS = {
        "video/mp4", "video/webm", "video/quicktime", "video/x-msvideo"
    };
    
    // Tamaño máximo: 15MB
    private static final long TAMANO_MAXIMO = 15 * 1024 * 1024;
    
    // Duración máxima: 15 segundos
    private static final int DURACION_MAXIMA = 15;
    
    /**
     * Procesa y guarda un video de historia
     */
    public String procesarVideo(MultipartFile video) throws IOException {
        // Validar formato
        if (!esFormatoValido(video)) {
            throw new IllegalArgumentException("Formato de video no válido. Use MP4, WebM, MOV o AVI.");
        }
        
        // Validar tamaño
        if (video.getSize() > TAMANO_MAXIMO) {
            throw new IllegalArgumentException("El video no puede superar los 10MB.");
        }
        
        // Crear directorio de historias
        Path historiasDir = Paths.get(uploadPath, "historias");
        Files.createDirectories(historiasDir);
        
        // Generar nombre único
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = obtenerExtension(video.getOriginalFilename());
        String fileName = "historia_" + timestamp + extension;
        
        // Guardar archivo
        Path filePath = historiasDir.resolve(fileName);
        Files.copy(video.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return "historias/" + fileName;
    }
    
    /**
     * Genera un thumbnail para el video
     * Intenta extraer un frame usando FFmpeg, si no está disponible crea un placeholder de imagen
     */
    public String generarThumbnail(String videoPath) {
        try {
            // Crear directorio de thumbnails
            Path thumbnailsDir = Paths.get(thumbnailPath, "historias");
            Files.createDirectories(thumbnailsDir);
            
            // Generar nombre del thumbnail
            String videoFileName = Paths.get(videoPath).getFileName().toString();
            String thumbnailFileName = "thumb_" + videoFileName.replaceFirst("[.][^.]+$", ".jpg");
            Path thumbnailFilePath = thumbnailsDir.resolve(thumbnailFileName);
            
            // Obtener ruta completa del video
            Path videoFilePath = Paths.get(uploadPath, videoPath);
            
            System.out.println("🎬 [THUMBNAIL] Iniciando generación de thumbnail...");
            System.out.println("🎬 [THUMBNAIL] Video: " + videoFilePath.toAbsolutePath());
            System.out.println("🎬 [THUMBNAIL] Video existe: " + Files.exists(videoFilePath));
            
            // Intentar extraer frame con FFmpeg primero
            if (extraerFrameConFFmpeg(videoFilePath, thumbnailFilePath)) {
                System.out.println("✅ [THUMBNAIL] Thumbnail generado con FFmpeg: " + thumbnailFileName);
                System.out.println("✅ [THUMBNAIL] Tamaño: " + Files.size(thumbnailFilePath) + " bytes");
                return "thumbnails/historias/" + thumbnailFileName;
            }
            
            // Si FFmpeg no está disponible, crear un placeholder de imagen
            System.out.println("⚠️ [THUMBNAIL] FFmpeg no disponible o falló, creando placeholder...");
            crearPlaceholderImagen(thumbnailFilePath);
            System.out.println("⚠️ [THUMBNAIL] Thumbnail placeholder creado: " + thumbnailFileName);
            
            return "thumbnails/historias/" + thumbnailFileName;
        } catch (IOException e) {
            System.err.println("❌ [THUMBNAIL] Error generando thumbnail: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Intenta extraer un frame del video usando FFmpeg
     */
    private boolean extraerFrameConFFmpeg(Path videoFile, Path thumbnailFile) {
        try {
            // Verificar que el video existe
            if (!Files.exists(videoFile)) {
                System.err.println("❌ [FFMPEG] Video no encontrado: " + videoFile.toAbsolutePath());
                return false;
            }
            
            // Verificar si FFmpeg está disponible
            if (!verificarFFmpegDisponible()) {
                System.out.println("⚠️ [FFMPEG] FFmpeg no está instalado o no está en el PATH");
                return false;
            }
            
            System.out.println("🔧 [FFMPEG] Intentando extraer frame del video...");
            
            // Intentar primero con frame del inicio (más rápido y confiable)
            // Usar formato image2 con update para asegurar que funcione en todos los sistemas
            ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg",
                "-i", videoFile.toAbsolutePath().toString(),
                "-ss", "00:00:00.5",  // Frame en el medio segundo (más confiable que segundo 1)
                "-vframes", "1",       // Solo un frame
                "-q:v", "2",          // Alta calidad (1-31, menor es mejor calidad)
                "-vf", "scale=640:-1", // Escalar a ancho de 640 manteniendo aspecto
                "-f", "image2",        // Formato de salida: imagen
                "-update", "1",        // Actualizar si existe (necesario para image2)
                "-y",                 // Sobrescribir si existe
                thumbnailFile.toAbsolutePath().toString()
            );
            
            // Redirigir errores para capturarlos
            processBuilder.redirectErrorStream(true);
            
            Process process = processBuilder.start();
            
            // Capturar output para debugging
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream())
            );
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            int exitCode = process.waitFor();
            
            // Log del output de FFmpeg para debugging
            if (exitCode != 0) {
                System.err.println("❌ [FFMPEG] FFmpeg falló con código: " + exitCode);
                System.err.println("❌ [FFMPEG] Output: " + output.toString());
            } else {
                System.out.println("✅ [FFMPEG] FFmpeg ejecutado exitosamente");
            }
            
            // Verificar que el archivo se creó correctamente y es una imagen válida
            if (exitCode == 0 && Files.exists(thumbnailFile)) {
                long fileSize = Files.size(thumbnailFile);
                if (fileSize > 0) {
                    // Verificar que sea una imagen válida leyendo el header
                    try {
                        BufferedImage img = ImageIO.read(thumbnailFile.toFile());
                        if (img != null) {
                            System.out.println("✅ [FFMPEG] Thumbnail válido generado: " + fileSize + " bytes, " + 
                                             img.getWidth() + "x" + img.getHeight());
                            return true;
                        } else {
                            System.err.println("❌ [FFMPEG] El archivo generado no es una imagen válida");
                        }
                    } catch (IOException e) {
                        System.err.println("❌ [FFMPEG] Error leyendo imagen generada: " + e.getMessage());
                    }
                } else {
                    System.err.println("❌ [FFMPEG] El archivo generado está vacío");
                }
            } else {
                System.err.println("❌ [FFMPEG] No se generó el archivo thumbnail");
            }
            
            return false;
        } catch (Exception e) {
            // FFmpeg no está disponible o falló, continuar con placeholder
            System.err.println("❌ [FFMPEG] Error al ejecutar FFmpeg: " + e.getClass().getSimpleName());
            System.err.println("❌ [FFMPEG] Mensaje: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("❌ [FFMPEG] Causa: " + e.getCause().getMessage());
            }
            return false;
        }
    }
    
    /**
     * Verifica si FFmpeg está disponible en el sistema
     */
    private boolean verificarFFmpegDisponible() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-version");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            // Leer output
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream())
            );
            String firstLine = reader.readLine();
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && firstLine != null && firstLine.contains("ffmpeg")) {
                System.out.println("✅ [FFMPEG] FFmpeg disponible: " + firstLine);
                return true;
            } else {
                System.out.println("⚠️ [FFMPEG] FFmpeg no disponible (código: " + exitCode + ")");
                return false;
            }
        } catch (Exception e) {
            System.out.println("⚠️ [FFMPEG] Error verificando FFmpeg: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Crea un placeholder de imagen para el video
     */
    private void crearPlaceholderImagen(Path thumbnailPath) throws IOException {
        // Crear una imagen de placeholder
        int width = 400;
        int height = 300;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        // Fondo gris claro
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, width, height);
        
        // Dibujar un icono de video simple
        g.setColor(new Color(180, 180, 180));
        int iconSize = 80;
        int iconX = (width - iconSize) / 2;
        int iconY = (height - iconSize) / 2;
        
        // Dibujar rectángulo redondeado
        g.fillRoundRect(iconX, iconY, iconSize, iconSize, 10, 10);
        
        // Dibujar triángulo de play
        g.setColor(new Color(220, 220, 220));
        int[] xPoints = {iconX + 25, iconX + 25, iconX + 55};
        int[] yPoints = {iconY + 20, iconY + 60, iconY + 40};
        g.fillPolygon(xPoints, yPoints, 3);
        
        g.dispose();
        
        // Guardar como JPEG
        ImageIO.write(image, "jpg", thumbnailPath.toFile());
    }
    
    /**
     * Valida si el formato del video es válido
     */
    private boolean esFormatoValido(MultipartFile video) {
        String contentType = video.getContentType();
        if (contentType == null) return false;
        
        for (String formato : FORMATOS_PERMITIDOS) {
            if (contentType.equals(formato)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtiene la extensión del archivo
     */
    private String obtenerExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return ".mp4";
    }
    
    /**
     * Elimina un video del sistema de archivos
     */
    public void eliminarVideo(String videoPath) {
        try {
            Path videoFile = Paths.get(uploadPath, videoPath);
            if (Files.exists(videoFile)) {
                Files.delete(videoFile);
                System.out.println("✅ Video eliminado: " + videoPath);
            } else {
                System.out.println("⚠️ Video no encontrado: " + videoPath);
            }
        } catch (IOException e) {
            System.err.println("❌ Error al eliminar video: " + e.getMessage());
        }
    }
    
    /**
     * Elimina un thumbnail del sistema de archivos
     */
    public void eliminarThumbnail(String thumbnailPath) {
        try {
            Path thumbnailFile;
            
            // Si la ruta ya incluye thumbnails/, construirla directamente
            if (thumbnailPath.startsWith("thumbnails/")) {
                thumbnailFile = Paths.get(uploadPath, thumbnailPath);
            } else {
                // Si es solo "historias/...", agregar thumbnails/
                thumbnailFile = Paths.get(uploadPath, "thumbnails", thumbnailPath);
            }
            
            if (Files.exists(thumbnailFile)) {
                Files.delete(thumbnailFile);
                System.out.println("✅ Thumbnail eliminado: " + thumbnailPath);
            } else {
                System.out.println("⚠️ Thumbnail no encontrado: " + thumbnailFile.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("❌ Error al eliminar thumbnail: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene información del video (duración, tamaño, etc.)
     */
    public VideoInfo obtenerInformacionVideo(MultipartFile video) {
        return new VideoInfo(
            video.getSize(),
            DURACION_MAXIMA, // En una implementación real, extraerías la duración real
            video.getContentType(),
            video.getOriginalFilename()
        );
    }
    
    /**
     * Clase para información del video
     */
    public static class VideoInfo {
        private final long tamano;
        private final int duracion;
        private final String tipo;
        private final String nombre;
        
        public VideoInfo(long tamano, int duracion, String tipo, String nombre) {
            this.tamano = tamano;
            this.duracion = duracion;
            this.tipo = tipo;
            this.nombre = nombre;
        }
        
        public long getTamano() { return tamano; }
        public int getDuracion() { return duracion; }
        public String getTipo() { return tipo; }
        public String getNombre() { return nombre; }
        
        public String getTamanoFormateado() {
            double mb = tamano / (1024.0 * 1024.0);
            return String.format("%.1f MB", mb);
        }
    }
    
    /**
     * Procesa y guarda un video de producto
     * Similar a procesarVideo pero guarda en directorio de productos
     */
    public String procesarVideoProducto(MultipartFile video, Integer productId) throws IOException {
        // Validar formato
        if (!esFormatoValido(video)) {
            throw new IllegalArgumentException("Formato de video no válido. Use MP4, WebM, MOV o AVI.");
        }
        
        // Validar tamaño (productos pueden tener videos más largos)
        long tamanoMaximoProducto = 50 * 1024 * 1024; // 50MB
        if (video.getSize() > tamanoMaximoProducto) {
            throw new IllegalArgumentException("El video no puede superar los 50MB.");
        }
        
        // Crear directorio de videos de productos
        Path productosDir = Paths.get(uploadPath, "productos", "videos");
        Files.createDirectories(productosDir);
        
        // Generar nombre único
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = obtenerExtension(video.getOriginalFilename());
        String fileName = "producto_" + productId + "_" + timestamp + extension;
        
        // Guardar archivo
        Path filePath = productosDir.resolve(fileName);
        Files.copy(video.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return "productos/videos/" + fileName;
    }
    
    /**
     * Genera un thumbnail para el video de producto
     */
    public String generarThumbnailProducto(String videoPath, Integer productId) {
        try {
            // Crear directorio de thumbnails
            Path thumbnailsDir = Paths.get(thumbnailPath, "productos", "videos");
            Files.createDirectories(thumbnailsDir);
            
            // Generar nombre del thumbnail
            String videoFileName = Paths.get(videoPath).getFileName().toString();
            String thumbnailFileName = "thumb_" + videoFileName.replaceFirst("[.][^.]+$", ".jpg");
            
            // Por ahora, creamos un placeholder
            // En una implementación real, usarías FFmpeg para extraer un frame
            Path thumbnailPath = thumbnailsDir.resolve(thumbnailFileName);
            
            // Crear un archivo placeholder (en producción usar FFmpeg)
            Files.write(thumbnailPath, "thumbnail placeholder".getBytes());
            
            return "productos/videos/" + thumbnailFileName;
        } catch (IOException e) {
            // Si falla la generación del thumbnail, retornar null
            return null;
        }
    }
}
