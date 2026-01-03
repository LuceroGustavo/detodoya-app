package com.orioladenim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.IIOImage;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ColorImageService {
    
    @Autowired
    private WebPConversionService webPConversionService;
    
    @Value("${upload.path:uploads}")
    private String uploadPath;
    
    @Value("${upload.thumbnail.path:uploads/thumbnails}")
    private String thumbnailPath;
    
    private static final int MAX_WIDTH = 800;  // Tamaño adecuado para patrones
    private static final int MAX_HEIGHT = 800;
    private static final int THUMBNAIL_SIZE = 200;  // Thumbnail para vista previa
    private static final int MAX_FILE_SIZE = 3 * 1024 * 1024; // 3MB máximo
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
    
    @PostConstruct
    public void init() {
        createDirectories();
    }
    
    private void createDirectories() {
        try {
            // Crear directorios para colores
            Files.createDirectories(Paths.get(uploadPath, "colors"));
            Files.createDirectories(Paths.get(thumbnailPath, "colors"));
        } catch (IOException e) {
            throw new RuntimeException("Error creando directorios de imágenes de colores", e);
        }
    }
    
    /**
     * Procesa y guarda una imagen de patrón para un color, convirtiéndola a WebP y creando thumbnail
     */
    public String saveColorImage(MultipartFile file, Long colorId) {
        try {
            // Validar archivo
            validateFile(file);
            
            // Generar nombre único
            String originalName = file.getOriginalFilename();
            String extension = getFileExtension(originalName);
            String uniqueName = "color_" + (colorId != null ? colorId + "_" : "") + UUID.randomUUID().toString();
            
            // Crear directorios si no existen
            Path colorUploadDir = Paths.get(uploadPath, "colors");
            Path colorThumbnailDir = Paths.get(thumbnailPath, "colors");
            Files.createDirectories(colorUploadDir);
            Files.createDirectories(colorThumbnailDir);
            
            // Procesar imagen para WebP
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new RuntimeException("No se pudo leer la imagen");
            }
            
            BufferedImage resizedImage = resizeImage(originalImage, MAX_WIDTH, MAX_HEIGHT);
            
            // Convertir a WebP y guardar imagen principal
            String imagePath = saveAsWebP(resizedImage, uniqueName, "colors", extension);
            
            // Crear y guardar thumbnail en la carpeta correcta
            BufferedImage thumbnail = createThumbnail(resizedImage, THUMBNAIL_SIZE);
            saveThumbnailAsWebP(thumbnail, uniqueName, extension);
            
            System.out.println("✅ Imagen de color guardada: " + imagePath);
            
            return imagePath;
            
        } catch (IOException e) {
            throw new RuntimeException("Error procesando imagen de color: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valida el archivo de imagen
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo es demasiado grande. Máximo 3MB");
        }
        
        String extension = getFileExtension(file.getOriginalFilename());
        if (extension == null || !isAllowedExtension(extension)) {
            throw new IllegalArgumentException("Formato de archivo no permitido. Use: " + 
                String.join(", ", ALLOWED_EXTENSIONS));
        }
    }
    
    /**
     * Obtiene la extensión del archivo
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return null;
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
    
    /**
     * Verifica si la extensión está permitida
     */
    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Redimensiona la imagen manteniendo la proporción
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int maxWidth, int maxHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        // Calcular nuevas dimensiones manteniendo proporción
        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);
        
        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);
        
        // Si la imagen ya es más pequeña, no redimensionar
        if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
            return originalImage;
        }
        
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        
        // Configurar calidad de renderizado
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        
        return resizedImage;
    }
    
    /**
     * Crea un thumbnail cuadrado de la imagen (centrado)
     */
    private BufferedImage createThumbnail(BufferedImage originalImage, int size) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        // Calcular dimensiones para thumbnail cuadrado
        int cropSize = Math.min(originalWidth, originalHeight);
        int x = (originalWidth - cropSize) / 2;
        int y = (originalHeight - cropSize) / 2;
        
        // Crear imagen cuadrada
        BufferedImage squareImage = new BufferedImage(cropSize, cropSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = squareImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, cropSize, cropSize, x, y, x + cropSize, y + cropSize, null);
        g2d.dispose();
        
        // Redimensionar al tamaño final
        return resizeImage(squareImage, size, size);
    }
    
    /**
     * Guarda el thumbnail como WebP en la carpeta correcta
     */
    private String saveThumbnailAsWebP(BufferedImage image, String uniqueName, String originalExtension) throws IOException {
        Path directoryPath = Paths.get(thumbnailPath, "colors");
        
        // Asegurar que el directorio existe
        Files.createDirectories(directoryPath);
        
        try {
            // Convertir BufferedImage a byte array con alta calidad
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            // Usar PNG con compresión mínima para mejor calidad
            ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.85f);
            }
            
            writer.setOutput(ImageIO.createImageOutputStream(baos));
            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();
            
            byte[] imageBytes = baos.toByteArray();
            
            // Intentar convertir a WebP
            byte[] webpBytes = webPConversionService.convertToWebP(imageBytes, originalExtension);
            
            if (webpBytes != null) {
                // Guardar como WebP
                String webpFilename = uniqueName + ".webp";
                Path webpPath = directoryPath.resolve(webpFilename);
                Files.write(webpPath, webpBytes);
                System.out.println("✅ Thumbnail WebP guardado en: " + webpPath.toString());
                return "thumbnails/colors/" + webpFilename;
            } else {
                // Fallback a PNG
                String pngFilename = uniqueName + ".png";
                Path pngPath = directoryPath.resolve(pngFilename);
                Files.write(pngPath, imageBytes);
                System.out.println("✅ Thumbnail PNG guardado en: " + pngPath.toString());
                return "thumbnails/colors/" + pngFilename;
            }
            
        } catch (Exception e) {
            System.out.println("⚠️ Error en conversión WebP del thumbnail, usando PNG: " + e.getMessage());
            // Si todo falla, guardar como PNG
            String pngFilename = uniqueName + ".png";
            Path pngPath = directoryPath.resolve(pngFilename);
            ImageIO.write(image, "png", pngPath.toFile());
            System.out.println("✅ Thumbnail PNG de fallback guardado en: " + pngPath.toString());
            return "thumbnails/colors/" + pngFilename;
        }
    }
    
    /**
     * Guarda la imagen como WebP
     */
    private String saveAsWebP(BufferedImage image, String uniqueName, String subdirectory, String originalExtension) throws IOException {
        Path directoryPath = Paths.get(uploadPath, subdirectory);
        
        // Asegurar que el directorio existe
        Files.createDirectories(directoryPath);
        
        try {
            // Convertir BufferedImage a byte array con alta calidad
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            // Usar PNG con compresión mínima para mejor calidad
            ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.85f);
            }
            
            writer.setOutput(ImageIO.createImageOutputStream(baos));
            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();
            
            byte[] imageBytes = baos.toByteArray();
            
            // Intentar convertir a WebP
            byte[] webpBytes = webPConversionService.convertToWebP(imageBytes, originalExtension);
            
            if (webpBytes != null) {
                // Guardar como WebP
                String webpFilename = uniqueName + ".webp";
                Path webpPath = directoryPath.resolve(webpFilename);
                Files.write(webpPath, webpBytes);
                System.out.println("✅ WebP guardado en: " + webpPath.toString());
                return subdirectory + "/" + webpFilename;
            } else {
                // Fallback a PNG
                String pngFilename = uniqueName + ".png";
                Path pngPath = directoryPath.resolve(pngFilename);
                Files.write(pngPath, imageBytes);
                System.out.println("✅ PNG guardado en: " + pngPath.toString());
                return subdirectory + "/" + pngFilename;
            }
            
        } catch (Exception e) {
            System.out.println("⚠️ Error en conversión WebP, usando PNG: " + e.getMessage());
            // Si todo falla, guardar como PNG
            String pngFilename = uniqueName + ".png";
            Path pngPath = directoryPath.resolve(pngFilename);
            ImageIO.write(image, "png", pngPath.toFile());
            System.out.println("✅ PNG de fallback guardado en: " + pngPath.toString());
            return subdirectory + "/" + pngFilename;
        }
    }
    
    /**
     * Elimina una imagen de color del sistema de archivos
     * Elimina tanto la imagen principal como el thumbnail
     */
    public boolean deleteColorImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                System.out.println("⚠️ No se puede eliminar: imagePath es null o vacío");
                return false;
            }
            
            boolean deletedMain = false;
            boolean deletedThumbnail = false;
            
            // Eliminar imagen principal
            Path mainImagePath = Paths.get(uploadPath, imagePath);
            if (Files.exists(mainImagePath)) {
                Files.delete(mainImagePath);
                deletedMain = true;
                System.out.println("✅ Imagen principal eliminada: " + mainImagePath);
            } else {
                System.out.println("⚠️ Imagen principal no encontrada (puede que ya fue eliminada): " + mainImagePath);
            }
            
            // Eliminar thumbnail
            String filename = imagePath.substring(imagePath.lastIndexOf('/') + 1);
            Path thumbnailPathFile = Paths.get(thumbnailPath, "colors", filename);
            if (Files.exists(thumbnailPathFile)) {
                Files.delete(thumbnailPathFile);
                deletedThumbnail = true;
                System.out.println("✅ Thumbnail eliminado: " + thumbnailPathFile);
            } else {
                System.out.println("⚠️ Thumbnail no encontrado (puede que ya fue eliminado): " + thumbnailPathFile);
            }
            
            // Retornar true si se eliminó al menos un archivo, o si ambos no existían (ya estaban eliminados)
            // Si ambos archivos no existían, consideramos la operación exitosa (no hay nada que eliminar)
            if (!deletedMain && !deletedThumbnail) {
                // Verificar si realmente no existían (no fueron eliminados porque no existían)
                boolean mainNotExists = !Files.exists(mainImagePath);
                boolean thumbnailNotExists = !Files.exists(thumbnailPathFile);
                if (mainNotExists && thumbnailNotExists) {
                    System.out.println("ℹ️ Ambos archivos ya no existían, operación considerada exitosa");
                    return true;
                }
            }
            return deletedMain || deletedThumbnail;
        } catch (IOException e) {
            System.err.println("❌ Error eliminando imagen de color: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error inesperado eliminando imagen de color: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

