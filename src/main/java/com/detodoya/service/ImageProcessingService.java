package com.detodoya.service;

import com.detodoya.entity.ProductImage;
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
public class ImageProcessingService {
    
    @Autowired
    private WebPConversionService webPConversionService;
    
    @Value("${upload.path:uploads}")
    private String uploadPath;
    
    @Value("${upload.thumbnail.path:uploads/thumbnails}")
    private String thumbnailPath;
    private static final int MAX_WIDTH = 1920;
    private static final int MAX_HEIGHT = 1080;
    private static final int THUMBNAIL_SIZE = 300;
    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
    
    @PostConstruct
    public void init() {
        createDirectories();
    }
    
    private void createDirectories() {
        try {
            Files.createDirectories(Paths.get(uploadPath));
            Files.createDirectories(Paths.get(thumbnailPath));
        } catch (IOException e) {
            throw new RuntimeException("Error creando directorios de imágenes", e);
        }
    }
    
    /**
     * Procesa y guarda una imagen, convirtiéndola a WebP y creando thumbnail
     */
    public ProductImage processAndSaveImage(MultipartFile file, Integer productId, boolean isPrimary) {
        try {
            // Validar archivo
            validateFile(file);
            
            // Generar nombre único
            String originalName = file.getOriginalFilename();
            String extension = getFileExtension(originalName);
            String uniqueName = UUID.randomUUID().toString();
            
            // Si es WebP, manejarlo directamente
            if ("webp".equals(extension)) {
                return handleWebPFile(file, uniqueName, isPrimary);
            }
            
            // Procesar imagen original (otros formatos)
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new RuntimeException("No se pudo leer la imagen");
            }
            
            BufferedImage resizedImage = resizeImage(originalImage, MAX_WIDTH, MAX_HEIGHT);
            
            // Convertir a WebP y guardar imagen principal
            String imagePath = saveAsWebP(resizedImage, uniqueName, uploadPath, extension);
            
            // Crear y guardar thumbnail
            BufferedImage thumbnail = createThumbnail(resizedImage, THUMBNAIL_SIZE);
            saveAsWebP(thumbnail, uniqueName, thumbnailPath, extension);
            
            // Crear entidad ProductImage
            ProductImage productImage = new ProductImage();
            productImage.setImagePath(imagePath);
            productImage.setFileName(imagePath);
            productImage.setOriginalName(originalName);
            productImage.setFileSize(file.getSize());
            productImage.setIsPrimary(isPrimary);
            productImage.setDisplayOrder(0);
            
            return productImage;
            
        } catch (IOException e) {
            throw new RuntimeException("Error procesando imagen: " + e.getMessage(), e);
        }
    }
    
    /**
     * Maneja archivos WebP directamente sin conversión
     */
    private ProductImage handleWebPFile(MultipartFile file, String uniqueName, boolean isPrimary) throws IOException {
        // Guardar archivo WebP directamente
        String webpFilename = uniqueName + ".webp";
        Path webpPath = Paths.get(uploadPath, webpFilename);
        Files.write(webpPath, file.getBytes());
        
        // Crear thumbnail WebP (copiar el mismo archivo por ahora)
        Path thumbnailPathFile = Paths.get(thumbnailPath, webpFilename);
        Files.write(thumbnailPathFile, file.getBytes());
        
        // Crear entidad ProductImage
        ProductImage productImage = new ProductImage();
        productImage.setImagePath(webpFilename);
        productImage.setFileName(webpFilename);
        productImage.setOriginalName(file.getOriginalFilename());
        productImage.setFileSize(file.getSize());
        productImage.setIsPrimary(isPrimary);
        productImage.setDisplayOrder(0);
        
        return productImage;
    }
    
    /**
     * Valida el archivo de imagen
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo es demasiado grande. Máximo 5MB");
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
        if (lastDotIndex == -1) {
            return null;
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
    
    /**
     * Verifica si la extensión está permitida
     */
    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
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
        
        // Si la imagen es más pequeña que el máximo, no redimensionar
        if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
            return originalImage;
        }
        
        // Usar el mismo tipo de imagen que la original para mantener calidad
        int imageType = originalImage.getType();
        if (imageType == BufferedImage.TYPE_CUSTOM) {
            imageType = BufferedImage.TYPE_INT_ARGB; // Mejor calidad para imágenes con transparencia
        }
        
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, imageType);
        Graphics2D g2d = resizedImage.createGraphics();
        
        // Configurar balance entre calidad y rendimiento
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        
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
        
        // Calcular dimensiones para recorte cuadrado
        int cropSize = Math.min(originalWidth, originalHeight);
        int x = (originalWidth - cropSize) / 2;
        int y = (originalHeight - cropSize) / 2;
        
        // Recortar imagen a cuadrado
        BufferedImage croppedImage = originalImage.getSubimage(x, y, cropSize, cropSize);
        
        // Redimensionar a tamaño thumbnail con alta calidad
        return resizeImage(croppedImage, size, size);
    }
    
    /**
     * Guarda la imagen como WebP (o PNG como fallback)
     */
    private String saveAsWebP(BufferedImage image, String uniqueName, String directory, String originalExtension) throws IOException {
        Path directoryPath = Paths.get(directory);
        
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
                return webpFilename;
            } else {
                // Fallback a PNG
                String pngFilename = uniqueName + ".png";
                Path pngPath = directoryPath.resolve(pngFilename);
                Files.write(pngPath, imageBytes);
                return pngFilename;
            }
            
        } catch (Exception e) {
            // Si todo falla, guardar como PNG
            String pngFilename = uniqueName + ".png";
            Path pngPath = directoryPath.resolve(pngFilename);
            ImageIO.write(image, "png", pngPath.toFile());
            return pngFilename;
        }
    }
    
    /**
     * Elimina una imagen del sistema de archivos
     */
    public void deleteImage(String imagePath) {
        try {
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
        } catch (IOException e) {
            throw new RuntimeException("Error eliminando imagen: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene el tamaño del archivo en formato legible
     */
    public String getFileSizeString(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}

