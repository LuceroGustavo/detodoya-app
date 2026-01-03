package com.orioladenim.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orioladenim.entity.*;
import com.orioladenim.enums.Talle;
import com.orioladenim.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class RestoreService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductImageRepository productImageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ColorRepository colorRepository;
    
    @Autowired
    private ContactRepository contactRepository;
    
    @Autowired
    private HistoriaRepository historiaRepository;
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Transactional
    public String restoreFromBackup(MultipartFile backupFile, String restoredBy, boolean clearExisting) throws IOException {
        // Validar archivo
        if (backupFile.isEmpty()) {
            throw new IllegalArgumentException("El archivo de backup está vacío");
        }
        
        if (!backupFile.getOriginalFilename().endsWith(".zip")) {
            throw new IllegalArgumentException("El archivo debe ser un ZIP");
        }
        
        // Crear directorio temporal para extraer
        Path tempDir = Files.createTempDirectory("backup_restore");
        
        try {
            // Asegurar que todas las carpetas necesarias existan
            ensureRequiredDirectories();
            
            // Extraer archivo ZIP
            extractZipFile(backupFile, tempDir);
            System.out.println("📁 Directorio temporal creado: " + tempDir.toAbsolutePath());
            
            // Validar estructura del backup
            validateBackupStructure(tempDir);
            
            // Verificar contenido del directorio temporal
            System.out.println("🔍 Contenido del directorio temporal:");
            Files.walk(tempDir)
                .forEach(path -> {
                    try {
                        if (Files.isDirectory(path)) {
                            System.out.println("📁 " + tempDir.relativize(path));
                        } else {
                            System.out.println("📄 " + tempDir.relativize(path));
                        }
                    } catch (Exception e) {
                        System.err.println("Error listando: " + e.getMessage());
                    }
                });
            
            // Verificar específicamente la carpeta images
            Path imagesDir = tempDir.resolve("images");
            if (Files.exists(imagesDir)) {
                System.out.println("✅ Carpeta images encontrada en: " + imagesDir.toAbsolutePath());
                try {
                    Files.list(imagesDir).forEach(file -> {
                        try {
                            if (Files.isDirectory(file)) {
                                System.out.println("📁 images/" + file.getFileName());
                            } else {
                                System.out.println("📄 images/" + file.getFileName());
                            }
                        } catch (Exception e) {
                            System.err.println("Error listando archivo: " + e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error listando carpeta images: " + e.getMessage());
                }
            } else {
                System.err.println("❌ Carpeta images NO encontrada en: " + imagesDir.toAbsolutePath());
            }
            
            // Leer metadatos
            Map<String, Object> metadata = readMetadata(tempDir);
            
            if (clearExisting) {
                // Limpiar datos existentes
                clearExistingData();
            }
            
            // Restaurar datos en orden correcto (dependencias primero)
            restoreColors(tempDir);
            restoreCategories(tempDir);
            restoreProducts(tempDir);
            restoreProductImages(tempDir); // Restaurar imágenes DESPUÉS de productos
            restoreRelationships(tempDir);
            restoreContacts(tempDir);
            restoreHistorias(tempDir);
            restoreUsers(tempDir);
            
            return "Backup restaurado exitosamente. " + 
                   "Productos: " + metadata.get("productCount") + 
                   ", Categorías: " + metadata.get("categoryCount") + 
                   ", Colores: " + metadata.get("colorCount") +
                   ", Contactos: " + metadata.get("contactCount") +
                   ", Historias: " + metadata.get("historiaCount") +
                   ", Imágenes: " + metadata.get("imageCount") +
                   ", Usuarios: " + metadata.get("userCount");
            
        } finally {
            // Limpiar directorio temporal
            deleteDirectory(tempDir);
        }
    }
    
    private void extractZipFile(MultipartFile backupFile, Path tempDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(backupFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path filePath = tempDir.resolve(entry.getName());
                
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
    
    private void validateBackupStructure(Path tempDir) throws IOException {
        Path metadataFile = tempDir.resolve("metadata.json");
        if (!Files.exists(metadataFile)) {
            throw new IllegalArgumentException("El archivo de backup no contiene metadatos válidos");
        }
        
        Path dataDir = tempDir.resolve("data");
        if (!Files.exists(dataDir)) {
            throw new IllegalArgumentException("El archivo de backup no contiene datos válidos");
        }
    }
    
    private Map<String, Object> readMetadata(Path tempDir) throws IOException {
        Path metadataFile = tempDir.resolve("metadata.json");
        String metadataJson = Files.readString(metadataFile);
        return objectMapper.readValue(metadataJson, new TypeReference<Map<String, Object>>() {});
    }
    
    @Transactional
    public void clearExistingData() {
        // Eliminar en orden correcto para respetar foreign keys
        // Limpiar en orden correcto (dependencias primero)
        productImageRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        colorRepository.deleteAll();
        contactRepository.deleteAll();
        historiaRepository.deleteAll();
        // No eliminar usuarios por seguridad
        
        // Limpiar archivos físicos de uploads
        clearPhysicalFiles();
    }
    
    private void clearPhysicalFiles() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (Files.exists(uploadPath)) {
                // Borrar todos los archivos de uploads (excepto .gitkeep)
                Files.walk(uploadPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.getFileName().toString().equals(".gitkeep"))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            System.out.println("Archivo eliminado: " + path.getFileName());
                        } catch (IOException e) {
                            System.err.println("Error eliminando archivo " + path + ": " + e.getMessage());
                        }
                    });
                
                // Borrar subdirectorios vacíos (excepto el directorio principal)
                Files.walk(uploadPath)
                    .sorted(Comparator.reverseOrder())
                    .filter(Files::isDirectory)
                    .filter(path -> !path.equals(uploadPath))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            System.out.println("Directorio eliminado: " + path.getFileName());
                        } catch (IOException e) {
                            System.err.println("Error eliminando directorio " + path + ": " + e.getMessage());
                        }
                    });
                
                System.out.println("Archivos físicos de uploads eliminados correctamente");
            }
        } catch (IOException e) {
            System.err.println("Error limpiando archivos físicos: " + e.getMessage());
        }
    }
    
    private void restoreCategories(Path tempDir) throws IOException {
        Path categoriesFile = tempDir.resolve("data/categories.json");
        if (Files.exists(categoriesFile)) {
            String categoriesJson = Files.readString(categoriesFile);
            List<Map<String, Object>> categoriesData = objectMapper.readValue(categoriesJson, new TypeReference<List<Map<String, Object>>>() {});
            
            for (Map<String, Object> categoryData : categoriesData) {
                // Verificar si la categoría ya existe
                String categoryName = (String) categoryData.get("name");
                Optional<Category> existingCategory = categoryRepository.findByNameIgnoreCase(categoryName);
                if (existingCategory.isEmpty()) {
                    Category category = new Category();
                    category.setName(categoryName);
                    category.setDescription((String) categoryData.get("description"));
                    category.setImagePath((String) categoryData.get("imagePath"));
                    category.setIsActive((Boolean) categoryData.get("isActive"));
                    category.setDisplayOrder(((Number) categoryData.get("displayOrder")).intValue());
                    category.setProductCount(((Number) categoryData.get("productCount")).intValue());
                    
                    // Ignorar productIds del backup (se restaurarán las relaciones después)
                    categoryRepository.save(category);
                }
            }
        }
    }
    
    // Mapa para asociar IDs originales con IDs nuevos
    private Map<Integer, Integer> productIdMapping = new HashMap<>();
    
    private void restoreProducts(Path tempDir) throws IOException {
        Path productsFile = tempDir.resolve("data/products.json");
        if (Files.exists(productsFile)) {
            String productsJson = Files.readString(productsFile);
            List<Map<String, Object>> productsData = objectMapper.readValue(productsJson, new TypeReference<List<Map<String, Object>>>() {});
            
            for (Map<String, Object> productData : productsData) {
                Product product = new Product();
                
                // Obtener ID original del backup
                Integer originalId = ((Number) productData.get("pId")).intValue();
                
                product.setName((String) productData.get("name"));
                product.setDescripcion((String) productData.get("descripcion"));
                product.setPrice((Double) productData.get("price"));
                product.setMedidas((String) productData.get("medidas"));
                product.setColor((String) productData.get("color"));
                product.setActivo((Boolean) productData.get("activo"));
                product.setEsDestacado((Boolean) productData.get("esDestacado"));
                product.setEsNuevo((Boolean) productData.get("esNuevo"));
                product.setMaterial((String) productData.get("material"));
                product.setCuidados((String) productData.get("cuidados"));
                product.setEdadRecomendada((String) productData.get("edadRecomendada"));
                product.setQty(((Number) productData.get("qty")).intValue());
                product.setPrecioOriginal((Double) productData.get("precioOriginal"));
                product.setDescuentoPorcentaje((Double) productData.get("descuentoPorcentaje"));
                product.setTallasDisponibles((String) productData.get("tallasDisponibles"));
                product.setColoresDisponibles((String) productData.get("coloresDisponibles"));
                
                // Restaurar relaciones con categorías si existen
                @SuppressWarnings("unchecked")
                List<Integer> colorIds = (List<Integer>) productData.get("colores");
                if (colorIds != null && !colorIds.isEmpty()) {
                    // Buscar colores por IDs (simplificado - asumiendo que existen)
                    // TODO: Implementar restauración de colores si es necesario
                }
                
                // Guardar producto y obtener el nuevo ID generado
                Product savedProduct = productRepository.save(product);
                Integer newId = savedProduct.getPId();
                
                // Mapear ID original -> ID nuevo
                productIdMapping.put(originalId, newId);
                
                System.out.println("✅ Producto restaurado: " + product.getName() + " (Original ID: " + originalId + " -> Nuevo ID: " + newId + ")");
            }
        }
    }
    
    private void restoreProductImages(Path tempDir) throws IOException {
        Path imagesFile = tempDir.resolve("data/product_images.json");
        if (Files.exists(imagesFile)) {
            String imagesJson = Files.readString(imagesFile);
            List<Map<String, Object>> imagesData = objectMapper.readValue(imagesJson, new TypeReference<List<Map<String, Object>>>() {});
            
            // Crear directorio de uploads si no existe
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);
                    System.out.println("Directorio uploads creado: " + uploadPath.toAbsolutePath());
                } catch (Exception e) {
                    System.err.println("Error creando directorio uploads: " + e.getMessage());
                    throw new IOException("No se pudo crear el directorio uploads", e);
                }
            }
            
            // Crear directorio de thumbnails si no existe
            Path thumbnailsDir = uploadPath.resolve("thumbnails");
            if (!Files.exists(thumbnailsDir)) {
                try {
                    Files.createDirectories(thumbnailsDir);
                    System.out.println("Directorio thumbnails creado: " + thumbnailsDir.toAbsolutePath());
                } catch (Exception e) {
                    System.err.println("Error creando directorio thumbnails: " + e.getMessage());
                    // No lanzar excepción, continuar sin thumbnails
                }
            }
            
            // Verificar permisos de escritura
            if (!Files.isWritable(uploadPath)) {
                throw new IOException("No hay permisos de escritura en el directorio uploads: " + uploadPath.toAbsolutePath());
            }
            
            for (Map<String, Object> imageData : imagesData) {
                try {
                    // Crear nueva instancia de ProductImage
                    ProductImage image = new ProductImage();
                    
                    // Configurar datos básicos
                    image.setFileName((String) imageData.get("fileName"));
                    image.setOriginalName((String) imageData.get("originalName"));
                    image.setFileSize(((Number) imageData.get("fileSize")).longValue());
                    image.setIsPrimary((Boolean) imageData.get("isPrimary"));
                    image.setDisplayOrder(((Number) imageData.get("displayOrder")).intValue());
                    
                    // Configurar imagePath correctamente (solo el nombre del archivo)
                    String fileName = (String) imageData.get("fileName");
                    image.setImagePath(fileName); // Solo el nombre del archivo, no la ruta completa
                    
                    // PRIMERO: Copiar archivo de imagen físicamente (independientemente del producto)
                    Path sourceImage = tempDir.resolve("images").resolve(fileName);
                    System.out.println("🔍 Buscando imagen en: " + sourceImage.toAbsolutePath());
                    
                    boolean imagenCopiada = false;
                    if (Files.exists(sourceImage)) {
                        Path targetImage = uploadPath.resolve(fileName);
                        Files.copy(sourceImage, targetImage, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("✅ Imagen copiada: " + fileName);
                        imagenCopiada = true;
                        
                        // Crear thumbnail si existe
                        Path sourceThumbnail = tempDir.resolve("images/thumbnails").resolve(fileName);
                        System.out.println("🔍 Buscando thumbnail en: " + sourceThumbnail.toAbsolutePath());
                        
                        if (Files.exists(sourceThumbnail)) {
                            // Usar la variable thumbnailsDir ya declarada arriba
                            if (!Files.exists(thumbnailsDir)) {
                                Files.createDirectories(thumbnailsDir);
                            }
                            Path targetThumbnail = thumbnailsDir.resolve(fileName);
                            Files.copy(sourceThumbnail, targetThumbnail, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("✅ Thumbnail copiado: " + fileName);
                        } else {
                            System.out.println("⚠️ Thumbnail no encontrado: " + fileName);
                        }
                    } else {
                        System.err.println("❌ Imagen no encontrada en backup: " + fileName);
                        continue; // Saltar si no existe el archivo físico
                    }
                    
                    // SEGUNDO: Intentar asociar con producto usando el mapeo de IDs
                    Integer originalProductId = ((Number) imageData.get("productId")).intValue();
                    Integer newProductId = productIdMapping.get(originalProductId);
                    
                    if (newProductId != null) {
                        Optional<Product> product = productRepository.findById(newProductId);
                        if (product.isPresent()) {
                            image.setProduct(product.get());
                            System.out.println("✅ Imagen asociada al producto: " + fileName + " (Original ID: " + originalProductId + " -> Nuevo ID: " + newProductId + ")");
                        } else {
                            System.err.println("⚠️ Producto no encontrado para imagen: " + fileName + " (Nuevo ID: " + newProductId + ") - Imagen copiada pero sin asociar");
                        }
                    } else {
                        System.err.println("⚠️ No se encontró mapeo para producto original ID: " + originalProductId + " - Imagen copiada pero sin asociar");
                    }
                    
                    // Guardar en base de datos (con o sin producto asociado)
                    if (imagenCopiada) {
                        productImageRepository.save(image);
                        System.out.println("✅ Imagen guardada en base de datos: " + fileName);
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error procesando imagen: " + e.getMessage());
                    e.printStackTrace();
                    // Continuar con la siguiente imagen
                }
            }
        }
    }
    
    private void restoreUsers(Path tempDir) throws IOException {
        Path usersFile = tempDir.resolve("data/users.json");
        if (Files.exists(usersFile)) {
            String usersJson = Files.readString(usersFile);
            List<Map<String, Object>> usersData = objectMapper.readValue(usersJson, new TypeReference<List<Map<String, Object>>>() {});
            
            for (Map<String, Object> userData : usersData) {
                String username = (String) userData.get("username");
                String email = (String) userData.get("email");
                
                // Verificar si el usuario ya existe
                Optional<User> existingUser = userRepository.findByUsername(username);
                if (existingUser.isEmpty()) {
                    User user = new User();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setRole(User.Role.valueOf((String) userData.get("role")));
                    user.setIsActive((Boolean) userData.get("enabled"));
                    // No restaurar password por seguridad
                    user.setPassword("$2a$10$defaultPasswordHash"); // Password por defecto
                    
                    userRepository.save(user);
                }
            }
        }
    }
    
    private void restoreColors(Path tempDir) throws IOException {
        Path colorsFile = tempDir.resolve("data/colors.json");
        if (Files.exists(colorsFile)) {
            String colorsJson = Files.readString(colorsFile);
            List<Map<String, Object>> colorsData = objectMapper.readValue(colorsJson, new TypeReference<List<Map<String, Object>>>() {});
            
            for (Map<String, Object> colorData : colorsData) {
                String colorName = (String) colorData.get("name");
                Optional<Color> existingColor = colorRepository.findByNameIgnoreCase(colorName);
                if (existingColor.isEmpty()) {
                    Color color = new Color();
                    color.setName(colorName);
                    color.setDescription((String) colorData.get("description"));
                    color.setHexCode((String) colorData.get("hexCode"));
                    color.setIsActive((Boolean) colorData.get("isActive"));
                    color.setDisplayOrder(((Number) colorData.get("displayOrder")).intValue());
                    color.setProductCount(((Number) colorData.get("productCount")).intValue());
                    colorRepository.save(color);
                }
            }
        }
    }
    
    private void restoreContacts(Path tempDir) throws IOException {
        Path contactsFile = tempDir.resolve("data/contacts.json");
        if (Files.exists(contactsFile)) {
            String contactsJson = Files.readString(contactsFile);
            List<Map<String, Object>> contactsData = objectMapper.readValue(contactsJson, new TypeReference<List<Map<String, Object>>>() {});
            
            for (Map<String, Object> contactData : contactsData) {
                Contact contact = new Contact();
                contact.setNombre((String) contactData.get("nombre"));
                contact.setEmail((String) contactData.get("email"));
                contact.setTelefono((String) contactData.get("telefono"));
                contact.setAsunto((String) contactData.get("asunto"));
                contact.setMensaje((String) contactData.get("mensaje"));
                contact.setProductoInteres((String) contactData.get("productoInteres"));
                contact.setLeido((Boolean) contactData.get("leido"));
                contact.setRespondido((Boolean) contactData.get("respondido"));
                contact.setRespuesta((String) contactData.get("respuesta"));
                contact.setIpAddress((String) contactData.get("ipAddress"));
                contact.setUserAgent((String) contactData.get("userAgent"));
                contact.setUbicacion((String) contactData.get("ubicacion"));
                contact.setActivo((Boolean) contactData.get("activo"));
                contactRepository.save(contact);
            }
        }
    }
    
    private void restoreHistorias(Path tempDir) throws IOException {
        Path historiasFile = tempDir.resolve("data/historias.json");
        if (Files.exists(historiasFile)) {
            String historiasJson = Files.readString(historiasFile);
            List<Map<String, Object>> historiasData = objectMapper.readValue(historiasJson, new TypeReference<List<Map<String, Object>>>() {});
            
            Path uploadPath = Paths.get(uploadDir);
            
            // Crear directorio de uploads si no existe
            if (!Files.exists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);
                    System.out.println("Directorio uploads creado: " + uploadPath.toAbsolutePath());
                } catch (Exception e) {
                    System.err.println("Error creando directorio uploads: " + e.getMessage());
                    throw new IOException("No se pudo crear el directorio uploads", e);
                }
            }
            
            // Crear directorio de historias si no existe
            Path historiasDir = uploadPath.resolve("historias");
            if (!Files.exists(historiasDir)) {
                try {
                    Files.createDirectories(historiasDir);
                    System.out.println("Directorio historias creado: " + historiasDir.toAbsolutePath());
                } catch (Exception e) {
                    System.err.println("Error creando directorio historias: " + e.getMessage());
                    throw new IOException("No se pudo crear el directorio historias", e);
                }
            }
            
            // Crear directorio de thumbnails de historias si no existe
            Path historiasThumbnailsDir = uploadPath.resolve("thumbnails").resolve("historias");
            if (!Files.exists(historiasThumbnailsDir)) {
                try {
                    Files.createDirectories(historiasThumbnailsDir);
                    System.out.println("Directorio thumbnails/historias creado: " + historiasThumbnailsDir.toAbsolutePath());
                } catch (Exception e) {
                    System.err.println("Error creando directorio thumbnails/historias: " + e.getMessage());
                    // No lanzar excepción, continuar sin thumbnails
                }
            }
            
            for (Map<String, Object> historiaData : historiasData) {
                try {
                    Historia historia = new Historia();
                    historia.setTitulo((String) historiaData.get("titulo"));
                    historia.setDescripcion((String) historiaData.get("descripcion"));
                    
                    // Configurar rutas de video correctamente
                    String videoPath = (String) historiaData.get("videoPath");
                    String videoThumbnail = (String) historiaData.get("videoThumbnail");
                    
                    // Extraer solo el nombre del archivo de la ruta completa
                    String videoFileName = videoPath != null ? 
                        Paths.get(videoPath).getFileName().toString() : null;
                    String thumbnailFileName = videoThumbnail != null ? 
                        Paths.get(videoThumbnail).getFileName().toString() : null;
                    
                    // Copiar archivo de video si existe
                    if (videoFileName != null) {
                        // Buscar video en diferentes ubicaciones posibles
                        Path sourceVideo = null;
                        
                        // 1. Buscar en images/historias/ (ruta principal)
                        Path historiasPath = tempDir.resolve("images").resolve("historias").resolve(videoFileName);
                        if (Files.exists(historiasPath)) {
                            sourceVideo = historiasPath;
                        }
                        
                        // 2. Buscar en images/ (fallback)
                        if (sourceVideo == null) {
                            Path fallbackPath = tempDir.resolve("images").resolve(videoFileName);
                            if (Files.exists(fallbackPath)) {
                                sourceVideo = fallbackPath;
                            }
                        }
                        
                        if (sourceVideo != null && Files.exists(sourceVideo)) {
                            // Copiar a uploads/historias/
                            Path targetVideo = historiasDir.resolve(videoFileName);
                            Files.copy(sourceVideo, targetVideo, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            historia.setVideoPath("historias/" + videoFileName); // Ruta relativa con subcarpeta
                            System.out.println("Video copiado: " + videoFileName + " -> historias/");
                        } else {
                            System.err.println("Video no encontrado en backup: " + videoFileName);
                            historia.setVideoPath(videoPath); // Usar ruta original como fallback
                        }
                    }
                    
                    // Copiar thumbnail si existe
                    if (thumbnailFileName != null) {
                        // Buscar thumbnail en diferentes ubicaciones posibles
                        Path sourceThumbnail = null;
                        
                        // 1. Buscar en images/thumbnails/historias/
                        Path thumbnailsPath = tempDir.resolve("images").resolve("thumbnails").resolve("historias").resolve(thumbnailFileName);
                        if (Files.exists(thumbnailsPath)) {
                            sourceThumbnail = thumbnailsPath;
                        }
                        
                        // 2. Buscar en images/ (fallback)
                        if (sourceThumbnail == null) {
                            Path fallbackPath = tempDir.resolve("images").resolve(thumbnailFileName);
                            if (Files.exists(fallbackPath)) {
                                sourceThumbnail = fallbackPath;
                            }
                        }
                        
                        if (sourceThumbnail != null && Files.exists(sourceThumbnail)) {
                            // Copiar a uploads/thumbnails/historias/
                            Path targetThumbnail = historiasThumbnailsDir.resolve(thumbnailFileName);
                            Files.copy(sourceThumbnail, targetThumbnail, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            historia.setVideoThumbnail("historias/" + thumbnailFileName); // Ruta relativa con subcarpeta
                            System.out.println("Thumbnail copiado: " + thumbnailFileName + " -> thumbnails/historias/");
                        } else {
                            System.err.println("Thumbnail no encontrado en backup: " + thumbnailFileName);
                            historia.setVideoThumbnail(videoThumbnail); // Usar ruta original como fallback
                        }
                    }
                    
                    historia.setDuracionSegundos(((Number) historiaData.get("duracionSegundos")).intValue());
                    historia.setPesoArchivo(((Number) historiaData.get("pesoArchivo")).longValue());
                    historia.setActiva((Boolean) historiaData.get("activa"));
                    historiaRepository.save(historia);
                    
                } catch (Exception e) {
                    System.err.println("Error procesando historia: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void restoreRelationships(Path tempDir) throws IOException {
        Path relationshipsFile = tempDir.resolve("data/relationships.json");
        if (Files.exists(relationshipsFile)) {
            String relationshipsJson = Files.readString(relationshipsFile);
            Map<String, Object> relationshipsData = objectMapper.readValue(relationshipsJson, new TypeReference<Map<String, Object>>() {});
            
            // Restaurar relaciones Product-Colors
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> productColors = (List<Map<String, Object>>) relationshipsData.get("product_colors");
            if (productColors != null) {
                for (Map<String, Object> rel : productColors) {
                    Integer productId = ((Number) rel.get("productId")).intValue();
                    Long colorId = ((Number) rel.get("colorId")).longValue();
                    
                    Optional<Product> product = productRepository.findById(productId);
                    Optional<Color> color = colorRepository.findById(colorId);
                    
                    if (product.isPresent() && color.isPresent()) {
                        if (product.get().getColores() == null) {
                            product.get().setColores(new ArrayList<>());
                        }
                        product.get().getColores().add(color.get());
                        productRepository.save(product.get());
                    }
                }
            }
            
            // Restaurar relaciones Product-Categories
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> productCategories = (List<Map<String, Object>>) relationshipsData.get("product_categories");
            if (productCategories != null) {
                for (Map<String, Object> rel : productCategories) {
                    Integer productId = ((Number) rel.get("productId")).intValue();
                    Long categoryId = ((Number) rel.get("categoryId")).longValue();
                    
                    Optional<Product> product = productRepository.findById(productId);
                    Optional<Category> category = categoryRepository.findById(categoryId);
                    
                    if (product.isPresent() && category.isPresent()) {
                        if (product.get().getCategories() == null) {
                            product.get().setCategories(new ArrayList<>());
                        }
                        product.get().getCategories().add(category.get());
                        productRepository.save(product.get());
                    }
                }
            }
            
            // Restaurar relaciones Product-Talles
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> productTalles = (List<Map<String, Object>>) relationshipsData.get("product_talles");
            if (productTalles != null) {
                for (Map<String, Object> rel : productTalles) {
                    Integer productId = ((Number) rel.get("productId")).intValue();
                    String talleStr = (String) rel.get("talle");
                    
                    Optional<Product> product = productRepository.findById(productId);
                    if (product.isPresent()) {
                        try {
                            Talle talle = Talle.valueOf(talleStr);
                            if (product.get().getTalles() == null) {
                                product.get().setTalles(new ArrayList<>());
                            }
                            product.get().getTalles().add(talle);
                            productRepository.save(product.get());
                        } catch (IllegalArgumentException e) {
                            // Ignorar talles inválidos
                        }
                    }
                }
            }
        }
    }
    
    private void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }
    
    public boolean validateBackupFile(MultipartFile file) {
        try {
            if (file.isEmpty() || !file.getOriginalFilename().endsWith(".zip")) {
                return false;
            }
            
            // Verificar que es un ZIP válido
            try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
                ZipEntry entry = zis.getNextEntry();
                return entry != null;
            }
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Asegura que todas las carpetas necesarias para el funcionamiento del sistema existan
     */
    private void ensureRequiredDirectories() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            
            // Crear directorio principal de uploads
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("✅ Directorio uploads creado: " + uploadPath.toAbsolutePath());
            }
            
            // Crear subdirectorios necesarios
            String[] requiredDirs = {
                "thumbnails",
                "historias", 
                "thumbnails/historias"
            };
            
            for (String dir : requiredDirs) {
                Path dirPath = uploadPath.resolve(dir);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                    System.out.println("✅ Directorio creado: " + dirPath.toAbsolutePath());
                }
            }
            
            System.out.println("✅ Todas las carpetas necesarias están disponibles");
            
        } catch (IOException e) {
            System.err.println("❌ Error creando directorios necesarios: " + e.getMessage());
            e.printStackTrace();
        }
    }
}