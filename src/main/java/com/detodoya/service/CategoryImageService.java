package com.detodoya.service;

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
public class CategoryImageService {
    
    @Autowired
    private WebPConversionService webPConversionService;
    
    @Value("${upload.path:uploads}")
    private String uploadPath;
    
    @Value("${upload.thumbnail.path:uploads/thumbnails}")
    private String thumbnailPath;
    
    private static final int MAX_WIDTH = 800;  // Más pequeño que productos
    private static final int MAX_HEIGHT = 600;
    private static final int THUMBNAIL_SIZE = 200;  // Thumbnail más pequeño
    private static final int MAX_FILE_SIZE = 3 * 1024 * 1024; // 3MB máximo
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
    
    @PostConstruct
    public void init() {
        createDirectories();
    }
    
    private void createDirectories() {
        try {
            // Crear directorios para categorías
            Files.createDirectories(Paths.get(uploadPath, "categories"));
            Files.createDirectories(Paths.get(thumbnailPath, "categories"));
        } catch (IOException e) {
            throw new RuntimeException("Error creando directorios de imágenes de categorías", e);
        }
    }
    
    /**
     * Procesa y guarda una imagen de categoría, convirtiéndola a WebP y creando thumbnail
     */
    public String saveCategoryImage(MultipartFile file, Long categoryId) {
        try {
            // Validar archivo
            validateFile(file);
            
            // Generar nombre único
            String originalName = file.getOriginalFilename();
            String extension = getFileExtension(originalName);
            String uniqueName = "category_" + categoryId + "_" + UUID.randomUUID().toString();
            
            // Crear directorios si no existen
            Path categoryUploadDir = Paths.get(uploadPath, "categories");
            Path categoryThumbnailDir = Paths.get(thumbnailPath, "categories");
            Files.createDirectories(categoryUploadDir);
            Files.createDirectories(categoryThumbnailDir);
            
            // Procesar imagen para WebP
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new RuntimeException("No se pudo leer la imagen");
            }
            
            BufferedImage resizedImage = resizeImage(originalImage, MAX_WIDTH, MAX_HEIGHT);
            
            // Convertir a WebP y guardar imagen principal
            String imagePath = saveAsWebP(resizedImage, uniqueName, "categories", extension);
            
            // Crear y guardar thumbnail en la carpeta correcta
            BufferedImage thumbnail = createThumbnail(resizedImage, THUMBNAIL_SIZE);
            String thumbnailPath = saveThumbnailAsWebP(thumbnail, uniqueName, extension);
            
            System.out.println("✅ Imagen de categoría guardada: " + imagePath);
            System.out.println("✅ Thumbnail guardado: " + thumbnailPath);
            
            return imagePath;
            
        } catch (IOException e) {
            throw new RuntimeException("Error procesando imagen de categoría: " + e.getMessage(), e);
        }
    }
    
    /**
     * Maneja archivos WebP directamente sin conversión
     */
    private String handleWebPFile(MultipartFile file, String uniqueName) throws IOException {
        // Guardar archivo WebP directamente
        String webpFilename = uniqueName + ".webp";
        Path webpPath = Paths.get(uploadPath, "categories", webpFilename);
        Files.write(webpPath, file.getBytes());
        
        // Crear thumbnail WebP (copiar el mismo archivo por ahora)
        Path thumbnailPathFile = Paths.get(thumbnailPath, "categories", webpFilename);
        Files.write(thumbnailPathFile, file.getBytes());
        
        return "categories/" + webpFilename;
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
     * Crea un thumbnail cuadrado de la imagen
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
        Path directoryPath = Paths.get(thumbnailPath, "categories");
        
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
                param.setCompressionQuality(0.85f); // Balance calidad/rendimiento
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
                return "thumbnails/categories/" + webpFilename;
            } else {
                // Fallback a PNG
                String pngFilename = uniqueName + ".png";
                Path pngPath = directoryPath.resolve(pngFilename);
                Files.write(pngPath, imageBytes);
                System.out.println("✅ Thumbnail PNG guardado en: " + pngPath.toString());
                return "thumbnails/categories/" + pngFilename;
            }
            
        } catch (Exception e) {
            System.out.println("⚠️ Error en conversión WebP del thumbnail, usando PNG: " + e.getMessage());
            // Si todo falla, guardar como PNG
            String pngFilename = uniqueName + ".png";
            Path pngPath = directoryPath.resolve(pngFilename);
            ImageIO.write(image, "png", pngPath.toFile());
            System.out.println("✅ Thumbnail PNG de fallback guardado en: " + pngPath.toString());
            return "thumbnails/categories/" + pngFilename;
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
                param.setCompressionQuality(0.85f); // Balance calidad/rendimiento
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
     * Elimina una imagen de categoría del sistema de archivos
     */
    public boolean deleteCategoryImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                return false;
            }
            
            // Eliminar imagen principal
            Path mainImagePath = Paths.get(uploadPath, imagePath);
            if (Files.exists(mainImagePath)) {
                Files.delete(mainImagePath);
            }
            
            // Eliminar thumbnail
            Path thumbnailPathFile = Paths.get(thumbnailPath, imagePath);
            if (Files.exists(thumbnailPathFile)) {
                Files.delete(thumbnailPathFile);
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error eliminando imagen de categoría: " + e.getMessage());
            return false;
        }
    }
}
