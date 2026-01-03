package com.orioladenim.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orioladenim.entity.*;
import com.orioladenim.enums.Talle;
import com.orioladenim.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class BackupService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductImageRepository productImageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BackupInfoRepository backupInfoRepository;
    
    @Autowired
    private ColorRepository colorRepository;
    
    @Autowired
    private ContactRepository contactRepository;
    
    @Autowired
    private HistoriaRepository historiaRepository;
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    @Value("${backup.directory:backups}")
    private String backupDir;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Transactional
    public String createFullBackup(String createdBy, String description) throws IOException {
        // Crear directorio de backup si no existe
        Path backupPath = Paths.get(backupDir);
        if (!Files.exists(backupPath)) {
            Files.createDirectories(backupPath);
        }
        
        // Generar nombre de archivo único
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "backup_full_" + timestamp + ".zip";
        String filePath = backupPath.resolve(fileName).toString();
        
        // Crear archivo ZIP
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            
            // 1. Exportar datos de productos
            exportProducts(zos);
            
            // 2. Exportar categorías
            exportCategories(zos);
            
            // 3. Exportar colores
            exportColors(zos);
            
            // 4. Exportar contactos
            exportContacts(zos);
            
            // 5. Exportar historias
            exportHistorias(zos);
            
            // 6. Exportar imágenes de productos
            exportProductImages(zos);
            
            // 7. Exportar usuarios
            exportUsers(zos);
            
            // 8. Exportar relaciones Many-to-Many
            exportRelationships(zos);
            
            // 9. Crear archivo de metadatos
            createMetadataFile(zos, createdBy, description);
        }
        
        // Obtener tamaño del archivo
        File backupFile = new File(filePath);
        long fileSize = backupFile.length();
        
        // Contar elementos
        int productCount = (int) productRepository.count();
        int imageCount = (int) productImageRepository.count();
        int categoryCount = (int) categoryRepository.count();
        int colorCount = (int) colorRepository.count();
        int contactCount = (int) contactRepository.count();
        int historiaCount = (int) historiaRepository.count();
        int userCount = (int) userRepository.count();
        
        // Guardar información del backup
        BackupInfo backupInfo = new BackupInfo(fileName, filePath, fileSize, createdBy, "FULL");
        backupInfo.setDescription(description);
        backupInfo.setProductCount(productCount);
        backupInfo.setImageCount(imageCount);
        backupInfo.setCategoryCount(categoryCount);
        // Agregar contadores adicionales si BackupInfo los soporta
        backupInfoRepository.save(backupInfo);
        
        return filePath;
    }
    
    @Transactional
    public String createDataOnlyBackup(String createdBy, String description) throws IOException {
        Path backupPath = Paths.get(backupDir);
        if (!Files.exists(backupPath)) {
            Files.createDirectories(backupPath);
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "backup_data_" + timestamp + ".zip";
        String filePath = backupPath.resolve(fileName).toString();
        
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            
            exportProducts(zos);
            exportCategories(zos);
            exportUsers(zos);
            createMetadataFile(zos, createdBy, description);
        }
        
        File backupFile = new File(filePath);
        long fileSize = backupFile.length();
        
        int productCount = (int) productRepository.count();
        int categoryCount = (int) categoryRepository.count();
        
        BackupInfo backupInfo = new BackupInfo(fileName, filePath, fileSize, createdBy, "DATA_ONLY");
        backupInfo.setDescription(description);
        backupInfo.setProductCount(productCount);
        backupInfo.setCategoryCount(categoryCount);
        backupInfoRepository.save(backupInfo);
        
        return filePath;
    }
    
    private void exportProducts(ZipOutputStream zos) throws IOException {
        List<Product> products = productRepository.findAll();
        
        // Crear versiones simplificadas sin referencias circulares
        List<Map<String, Object>> simplifiedProducts = new ArrayList<>();
        for (Product product : products) {
            Map<String, Object> productData = new HashMap<>();
            productData.put("pId", product.getPId());
            productData.put("name", product.getName());
            productData.put("descripcion", product.getDescripcion());
            productData.put("price", product.getPrice());
            productData.put("medidas", product.getMedidas());
            productData.put("color", product.getColor());
            productData.put("activo", product.getActivo());
            productData.put("esDestacado", product.getEsDestacado());
            productData.put("esNuevo", product.getEsNuevo());
            productData.put("material", product.getMaterial());
            productData.put("cuidados", product.getCuidados());
            productData.put("edadRecomendada", product.getEdadRecomendada());
            productData.put("qty", product.getQty());
            productData.put("precioOriginal", product.getPrecioOriginal());
            productData.put("descuentoPorcentaje", product.getDescuentoPorcentaje());
            productData.put("tallasDisponibles", product.getTallasDisponibles());
            productData.put("coloresDisponibles", product.getColoresDisponibles());
            productData.put("fechaCreacion", product.getFechaCreacion());
            productData.put("fechaActualizacion", product.getFechaActualizacion());
            
            // Solo incluir IDs de colores, no las entidades completas
            if (product.getColores() != null) {
                List<Long> colorIds = product.getColores().stream()
                    .map(color -> color.getId())
                    .collect(java.util.stream.Collectors.toList());
                productData.put("colores", colorIds);
            }
            
            simplifiedProducts.add(productData);
        }
        
        String productsJson = objectMapper.writeValueAsString(simplifiedProducts);
        
        ZipEntry entry = new ZipEntry("data/products.json");
        zos.putNextEntry(entry);
        zos.write(productsJson.getBytes());
        zos.closeEntry();
    }
    
    private void exportCategories(ZipOutputStream zos) throws IOException {
        List<Category> categories = categoryRepository.findAll();
        
        // Crear versiones simplificadas sin referencias circulares
        List<Map<String, Object>> simplifiedCategories = new ArrayList<>();
        for (Category category : categories) {
            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("id", category.getId());
            categoryData.put("name", category.getName());
            categoryData.put("description", category.getDescription());
            categoryData.put("isActive", category.getIsActive());
            categoryData.put("displayOrder", category.getDisplayOrder());
            categoryData.put("productCount", category.getProductCount());
            categoryData.put("imagePath", category.getImagePath());
            categoryData.put("createdAt", category.getCreatedAt());
            categoryData.put("updatedAt", category.getUpdatedAt());
            
            // Solo incluir IDs de productos, no las entidades completas
            if (category.getProducts() != null) {
                List<Integer> productIds = category.getProducts().stream()
                    .map(product -> product.getPId())
                    .collect(java.util.stream.Collectors.toList());
                categoryData.put("productIds", productIds);
            } else {
                // Incluir lista vacía para compatibilidad
                categoryData.put("productIds", new ArrayList<>());
            }
            
            simplifiedCategories.add(categoryData);
        }
        
        String categoriesJson = objectMapper.writeValueAsString(simplifiedCategories);
        
        ZipEntry entry = new ZipEntry("data/categories.json");
        zos.putNextEntry(entry);
        zos.write(categoriesJson.getBytes());
        zos.closeEntry();
    }
    
    private void exportProductImages(ZipOutputStream zos) throws IOException {
        List<ProductImage> images = productImageRepository.findAll();
        
        // Crear versiones simplificadas sin referencias circulares
        List<Map<String, Object>> simplifiedImages = new ArrayList<>();
        for (ProductImage image : images) {
            Map<String, Object> imageData = new HashMap<>();
            imageData.put("id", image.getId());
            imageData.put("fileName", image.getFileName());
            imageData.put("imagePath", image.getImagePath());
            imageData.put("originalName", image.getOriginalName());
            imageData.put("fileSize", image.getFileSize());
            imageData.put("isPrimary", image.getIsPrimary());
            imageData.put("displayOrder", image.getDisplayOrder());
            
            // Solo incluir ID del producto, no la entidad completa
            if (image.getProduct() != null) {
                imageData.put("productId", image.getProduct().getPId());
                imageData.put("productName", image.getProduct().getName());
            }
            
            simplifiedImages.add(imageData);
        }
        
        String imagesJson = objectMapper.writeValueAsString(simplifiedImages);
        ZipEntry entry = new ZipEntry("data/product_images.json");
        zos.putNextEntry(entry);
        zos.write(imagesJson.getBytes());
        zos.closeEntry();
        
        // Exportar archivos de imágenes desde la carpeta uploads
        Path uploadPath = Paths.get(uploadDir);
        if (Files.exists(uploadPath)) {
            Files.walk(uploadPath)
                .filter(Files::isRegularFile)
                .forEach(imagePath -> {
                    try {
                        String relativePath = uploadPath.relativize(imagePath).toString();
                        ZipEntry imageEntry = new ZipEntry("images/" + relativePath);
                        zos.putNextEntry(imageEntry);
                        Files.copy(imagePath, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        // Log error but continue with other images
                        System.err.println("Error copying image: " + imagePath + " - " + e.getMessage());
                    }
                });
        }
    }
    
    private void exportColors(ZipOutputStream zos) throws IOException {
        List<Color> colors = colorRepository.findAll();
        
        List<Map<String, Object>> simplifiedColors = new ArrayList<>();
        for (Color color : colors) {
            Map<String, Object> colorData = new HashMap<>();
            colorData.put("id", color.getId());
            colorData.put("name", color.getName());
            colorData.put("description", color.getDescription());
            colorData.put("hexCode", color.getHexCode());
            colorData.put("isActive", color.getIsActive());
            colorData.put("displayOrder", color.getDisplayOrder());
            colorData.put("productCount", color.getProductCount());
            colorData.put("createdAt", color.getCreatedAt());
            colorData.put("updatedAt", color.getUpdatedAt());
            
            simplifiedColors.add(colorData);
        }
        
        String colorsJson = objectMapper.writeValueAsString(simplifiedColors);
        
        ZipEntry entry = new ZipEntry("data/colors.json");
        zos.putNextEntry(entry);
        zos.write(colorsJson.getBytes());
        zos.closeEntry();
    }
    
    private void exportContacts(ZipOutputStream zos) throws IOException {
        List<Contact> contacts = contactRepository.findAll();
        
        List<Map<String, Object>> simplifiedContacts = new ArrayList<>();
        for (Contact contact : contacts) {
            Map<String, Object> contactData = new HashMap<>();
            contactData.put("id", contact.getId());
            contactData.put("nombre", contact.getNombre());
            contactData.put("email", contact.getEmail());
            contactData.put("telefono", contact.getTelefono());
            contactData.put("asunto", contact.getAsunto());
            contactData.put("mensaje", contact.getMensaje());
            contactData.put("productoInteres", contact.getProductoInteres());
            contactData.put("fechaCreacion", contact.getFechaCreacion());
            contactData.put("fechaActualizacion", contact.getFechaActualizacion());
            contactData.put("leido", contact.isLeido());
            contactData.put("respondido", contact.isRespondido());
            contactData.put("respuesta", contact.getRespuesta());
            contactData.put("fechaRespuesta", contact.getFechaRespuesta());
            contactData.put("ipAddress", contact.getIpAddress());
            contactData.put("userAgent", contact.getUserAgent());
            contactData.put("ubicacion", contact.getUbicacion());
            contactData.put("activo", contact.isActivo());
            
            simplifiedContacts.add(contactData);
        }
        
        String contactsJson = objectMapper.writeValueAsString(simplifiedContacts);
        
        ZipEntry entry = new ZipEntry("data/contacts.json");
        zos.putNextEntry(entry);
        zos.write(contactsJson.getBytes());
        zos.closeEntry();
    }
    
    private void exportHistorias(ZipOutputStream zos) throws IOException {
        List<Historia> historias = historiaRepository.findAll();
        
        List<Map<String, Object>> simplifiedHistorias = new ArrayList<>();
        for (Historia historia : historias) {
            Map<String, Object> historiaData = new HashMap<>();
            historiaData.put("id", historia.getId());
            historiaData.put("titulo", historia.getTitulo());
            historiaData.put("descripcion", historia.getDescripcion());
            historiaData.put("videoPath", historia.getVideoPath());
            historiaData.put("videoThumbnail", historia.getVideoThumbnail());
            historiaData.put("duracionSegundos", historia.getDuracionSegundos());
            historiaData.put("pesoArchivo", historia.getPesoArchivo());
            historiaData.put("activa", historia.getActiva());
            historiaData.put("fechaCreacion", historia.getFechaCreacion());
            historiaData.put("fechaActualizacion", historia.getFechaActualizacion());
            
            simplifiedHistorias.add(historiaData);
        }
        
        String historiasJson = objectMapper.writeValueAsString(simplifiedHistorias);
        
        ZipEntry entry = new ZipEntry("data/historias.json");
        zos.putNextEntry(entry);
        zos.write(historiasJson.getBytes());
        zos.closeEntry();
    }
    
    private void exportRelationships(ZipOutputStream zos) throws IOException {
        // Exportar relaciones Many-to-Many
        Map<String, Object> relationships = new HashMap<>();
        
        // Product-Colors relationships
        List<Map<String, Object>> productColors = new ArrayList<>();
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            if (product.getColores() != null) {
                for (Color color : product.getColores()) {
                    Map<String, Object> rel = new HashMap<>();
                    rel.put("productId", product.getPId());
                    rel.put("colorId", color.getId());
                    productColors.add(rel);
                }
            }
        }
        relationships.put("product_colors", productColors);
        
        // Product-Categories relationships
        List<Map<String, Object>> productCategories = new ArrayList<>();
        for (Product product : products) {
            if (product.getCategories() != null) {
                for (Category category : product.getCategories()) {
                    Map<String, Object> rel = new HashMap<>();
                    rel.put("productId", product.getPId());
                    rel.put("categoryId", category.getId());
                    productCategories.add(rel);
                }
            }
        }
        relationships.put("product_categories", productCategories);
        
        // Product-Talles relationships
        List<Map<String, Object>> productTalles = new ArrayList<>();
        for (Product product : products) {
            if (product.getTalles() != null) {
                for (Talle talle : product.getTalles()) {
                    Map<String, Object> rel = new HashMap<>();
                    rel.put("productId", product.getPId());
                    rel.put("talle", talle.toString());
                    productTalles.add(rel);
                }
            }
        }
        relationships.put("product_talles", productTalles);
        
        String relationshipsJson = objectMapper.writeValueAsString(relationships);
        
        ZipEntry entry = new ZipEntry("data/relationships.json");
        zos.putNextEntry(entry);
        zos.write(relationshipsJson.getBytes());
        zos.closeEntry();
    }
    
    private void exportUsers(ZipOutputStream zos) throws IOException {
        List<User> users = userRepository.findAll();
        
        // Filtrar información sensible
        List<Map<String, Object>> safeUsers = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> safeUser = new HashMap<>();
            safeUser.put("id", user.getId());
            safeUser.put("username", user.getUsername());
            safeUser.put("email", user.getEmail());
            safeUser.put("role", user.getRole());
            safeUser.put("enabled", user.getIsActive());
            safeUser.put("createdAt", user.getCreatedAt());
            // No incluir password por seguridad
            safeUsers.add(safeUser);
        }
        
        String usersJson = objectMapper.writeValueAsString(safeUsers);
        
        ZipEntry entry = new ZipEntry("data/users.json");
        zos.putNextEntry(entry);
        zos.write(usersJson.getBytes());
        zos.closeEntry();
    }
    
    private void createMetadataFile(ZipOutputStream zos, String createdBy, String description) throws IOException {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("createdAt", LocalDateTime.now().toString());
        metadata.put("createdBy", createdBy);
        metadata.put("description", description);
        metadata.put("version", "2.0");
        metadata.put("backupType", "FULL");
        metadata.put("productCount", productRepository.count());
        metadata.put("categoryCount", categoryRepository.count());
        metadata.put("colorCount", colorRepository.count());
        metadata.put("contactCount", contactRepository.count());
        metadata.put("historiaCount", historiaRepository.count());
        metadata.put("imageCount", productImageRepository.count());
        metadata.put("userCount", userRepository.count());
        metadata.put("totalTables", 8); // products, categories, colors, contacts, historias, product_images, users, backup_info
        
        String metadataJson = objectMapper.writeValueAsString(metadata);
        
        ZipEntry entry = new ZipEntry("metadata.json");
        zos.putNextEntry(entry);
        zos.write(metadataJson.getBytes());
        zos.closeEntry();
    }
    
    public List<BackupInfo> getAllBackups() {
        return backupInfoRepository.findAllOrderByCreatedAtDesc();
    }
    
    public List<BackupInfo> getBackupsByUser(String username) {
        return backupInfoRepository.findByCreatedByOrderByCreatedAtDesc(username);
    }
    
    public Optional<BackupInfo> getBackupById(Long id) {
        return backupInfoRepository.findById(id);
    }
    
    public void deleteBackup(Long id) throws IOException {
        Optional<BackupInfo> backupInfo = backupInfoRepository.findById(id);
        if (backupInfo.isPresent()) {
            // Eliminar archivo físico
            File backupFile = new File(backupInfo.get().getFilePath());
            if (backupFile.exists()) {
                backupFile.delete();
            }
            
            // Eliminar registro de la base de datos
            backupInfoRepository.deleteById(id);
        }
    }
    
    public File getBackupFile(Long id) {
        Optional<BackupInfo> backupInfo = backupInfoRepository.findById(id);
        if (backupInfo.isPresent()) {
            File backupFile = new File(backupInfo.get().getFilePath());
            if (backupFile.exists()) {
                return backupFile;
            }
        }
        return null;
    }
}
