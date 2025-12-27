package com.detodoya.service;

import com.detodoya.entity.Color;
import com.detodoya.repo.ColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de colores
 * Contiene la lógica de negocio para operaciones de colores
 */
@Service
@Transactional
public class ColorService {
    
    @Autowired
    private ColorRepository colorRepository;
    
    @Autowired
    private ColorImageService colorImageService;
    
    /**
     * Obtener todos los colores activos ordenados
     */
    @Transactional(readOnly = true)
    public List<Color> getActiveColors() {
        return colorRepository.findActiveColorsOrdered();
    }
    
    /**
     * Obtener todos los colores (activos e inactivos) ordenados
     * NOTA: Se usa en formularios de productos para mostrar todos los colores disponibles
     */
    @Transactional(readOnly = true)
    public List<Color> getAllColors() {
        return colorRepository.findAll().stream()
                .sorted((c1, c2) -> {
                    // Primero por displayOrder, luego por nombre
                    int orderCompare = Integer.compare(
                        c1.getDisplayOrder() != null ? c1.getDisplayOrder() : Integer.MAX_VALUE,
                        c2.getDisplayOrder() != null ? c2.getDisplayOrder() : Integer.MAX_VALUE
                    );
                    if (orderCompare != 0) {
                        return orderCompare;
                    }
                    return c1.getName().compareToIgnoreCase(c2.getName());
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Obtener colores paginados
     */
    @Transactional(readOnly = true)
    public Page<Color> getColorsPaginated(Pageable pageable) {
        return colorRepository.findAll(pageable);
    }
    
    /**
     * Buscar color por ID
     */
    @Transactional(readOnly = true)
    public Optional<Color> getColorById(Long id) {
        return colorRepository.findById(id);
    }
    
    /**
     * Buscar color por nombre
     */
    @Transactional(readOnly = true)
    public Optional<Color> getColorByName(String name) {
        return colorRepository.findByNameIgnoreCase(name);
    }
    
    /**
     * Buscar color por código hexadecimal
     */
    @Transactional(readOnly = true)
    public Optional<Color> getColorByHexCode(String hexCode) {
        return colorRepository.findByHexCodeIgnoreCase(hexCode);
    }
    
    /**
     * Crear nuevo color
     */
    public Color createColor(Color color) {
        // Normalizar nombre primero (evitar strings vacíos que causan error de constraint único)
        if (color.getName() == null || color.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del color es obligatorio");
        }
        color.setName(color.getName().trim());
        
        // Limpiar y normalizar el código hexadecimal
        if (color.getHexCode() != null && !color.getHexCode().trim().isEmpty()) {
            String hexCode = color.getHexCode().trim();
            // Remover caracteres no válidos excepto #
            hexCode = hexCode.replaceAll("[^#0-9A-Fa-f]", "");
            // Asegurar que empiece con #
            if (!hexCode.startsWith("#")) {
                hexCode = "#" + hexCode;
            }
            // Limitar a 7 caracteres (#RRGGBB) y convertir a mayúsculas
            if (hexCode.length() > 7) {
                hexCode = hexCode.substring(0, 7);
            }
            color.setHexCode(hexCode.toUpperCase());
        } else {
            // Si hexCode está vacío o es null, establecerlo explícitamente como null
            // Esto evita problemas con constraints únicos en la base de datos
            color.setHexCode(null);
        }
        
        // Normalizar description (evitar strings vacíos)
        if (color.getDescription() != null && color.getDescription().trim().isEmpty()) {
            color.setDescription(null);
        } else if (color.getDescription() != null) {
            color.setDescription(color.getDescription().trim());
        }
        
        // Validar datos
        validateColor(color);
        
        // Validar que el nombre no exista
        if (colorRepository.findByNameIgnoreCase(color.getName()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un color con el nombre: " + color.getName());
        }
        
        // Validar que el código hexadecimal no exista (solo si está presente)
        if (color.getHexCode() != null && !color.getHexCode().isEmpty() &&
            colorRepository.findByHexCodeIgnoreCase(color.getHexCode()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un color con el código hexadecimal: " + color.getHexCode());
        }
        
        // Establecer valores por defecto
        color.setIsActive(true); // Siempre activo al crear
        color.setIsDefault(false); // Nunca es predeterminado si se crea manualmente
        
        // Asignar orden de visualización automáticamente
        // Asegurar que nunca sea 0 (el orden mínimo es 1)
        Integer nextOrder = colorRepository.getNextDisplayOrder();
        if (nextOrder == null || nextOrder <= 0) {
            nextOrder = 1;
        }
        color.setDisplayOrder(nextOrder);
        
        // Inicializar contador de productos
        if (color.getProductCount() == null) {
            color.setProductCount(0);
        }
        
        return colorRepository.save(color);
    }
    
    /**
     * Actualizar color existente
     * IMPORTANTE: El ID del color NO cambia al actualizar, ya que estamos actualizando
     * la entidad existente, no creando una nueva.
     */
    public Color updateColor(Long id, Color colorData) {
        Color existingColor = colorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Color no encontrado con ID: " + id));
        
        // No permitir editar colores predeterminados
        if (Boolean.TRUE.equals(existingColor.getIsDefault())) {
            throw new IllegalStateException("No se puede editar un color predeterminado del sistema");
        }
        
        // Normalizar nombre primero (evitar strings vacíos que causan error de constraint único)
        if (colorData.getName() == null || colorData.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del color es obligatorio");
        }
        colorData.setName(colorData.getName().trim());
        
        // Limpiar y normalizar el código hexadecimal
        if (colorData.getHexCode() != null && !colorData.getHexCode().trim().isEmpty()) {
            String hexCode = colorData.getHexCode().trim();
            // Remover caracteres no válidos excepto #
            hexCode = hexCode.replaceAll("[^#0-9A-Fa-f]", "");
            // Asegurar que empiece con #
            if (!hexCode.startsWith("#")) {
                hexCode = "#" + hexCode;
            }
            // Limitar a 7 caracteres (#RRGGBB) y convertir a mayúsculas
            if (hexCode.length() > 7) {
                hexCode = hexCode.substring(0, 7);
            }
            colorData.setHexCode(hexCode.toUpperCase());
        } else {
            // Si hexCode está vacío o es null, establecerlo explícitamente como null
            // Esto evita problemas con constraints únicos en la base de datos
            colorData.setHexCode(null);
        }
        
        // Normalizar description (evitar strings vacíos)
        if (colorData.getDescription() != null && colorData.getDescription().trim().isEmpty()) {
            colorData.setDescription(null);
        } else if (colorData.getDescription() != null) {
            colorData.setDescription(colorData.getDescription().trim());
        }
        
        // Validar datos
        validateColor(colorData);
        
        // Validar que el nuevo nombre no exista en otro color
        if (!existingColor.getName().equalsIgnoreCase(colorData.getName()) &&
            colorRepository.existsByNameIgnoreCaseAndIdNot(colorData.getName(), id)) {
            throw new IllegalArgumentException("Ya existe otro color con el nombre: " + colorData.getName());
        }
        
        // Validar que el nuevo código hexadecimal no exista en otro color (solo si está presente)
        if (colorData.getHexCode() != null && !colorData.getHexCode().isEmpty() &&
            !colorData.getHexCode().equalsIgnoreCase(existingColor.getHexCode()) &&
            colorRepository.existsByHexCodeIgnoreCaseAndIdNot(colorData.getHexCode(), id)) {
            throw new IllegalArgumentException("Ya existe otro color con el código hexadecimal: " + colorData.getHexCode());
        }
        
        // Actualizar campos (no permitir cambiar isDefault ni displayOrder)
        existingColor.setName(colorData.getName());
        existingColor.setDescription(colorData.getDescription());
        existingColor.setHexCode(colorData.getHexCode());
        existingColor.setImagePath(colorData.getImagePath()); // Actualizar ruta de imagen
        existingColor.setIsActive(colorData.getIsActive());
        // NO actualizar displayOrder - mantener el orden original del color
        // El displayOrder solo se asigna al crear, no al editar
        
        return colorRepository.save(existingColor);
    }
    
    /**
     * Eliminar color permanentemente
     * NOTA: Se eliminó el soft delete porque no se usa en esta aplicación
     */
    @Transactional
    public void deleteColor(Long id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Color no encontrado con ID: " + id));
        
        // No permitir eliminar colores predeterminados
        if (Boolean.TRUE.equals(color.getIsDefault())) {
            throw new IllegalStateException("No se puede eliminar un color predeterminado del sistema");
        }
        
        // Verificar si tiene productos asociados (como colorEntity o en la lista colores)
        Long productCount = colorRepository.countProductsByColorId(id);
        if (productCount != null && productCount > 0) {
            throw new IllegalStateException("No se puede eliminar el color porque tiene " + productCount + " producto(s) asociado(s)");
        }
        
        // Si no tiene productos, eliminar permanentemente
        // Primero eliminar la imagen asociada si existe
        if (color.getImagePath() != null && !color.getImagePath().isEmpty()) {
            colorImageService.deleteColorImage(color.getImagePath());
        }
        
        // JPA automáticamente eliminará las relaciones en product_colors (Many-to-Many)
        // cuando se elimine el color, ya que no hay productos que lo referencien
        colorRepository.delete(color);
    }
    
    /**
     * Eliminar color permanentemente
     */
    public void deleteColorPermanently(Long id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Color no encontrado con ID: " + id));
        
        // No permitir eliminar colores predeterminados
        if (Boolean.TRUE.equals(color.getIsDefault())) {
            throw new IllegalStateException("No se puede eliminar un color predeterminado del sistema");
        }
        
        // Verificar si tiene productos
        if (color.hasProducts()) {
            throw new IllegalStateException("No se puede eliminar el color porque tiene productos asociados");
        }
        
        colorRepository.delete(color);
    }
    
    
    /**
     * Buscar colores por texto
     */
    @Transactional(readOnly = true)
    public List<Color> searchColors(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getActiveColors();
        }
        return colorRepository.searchActiveColors(searchText.trim());
    }
    
    /**
     * Obtener colores con productos
     */
    @Transactional(readOnly = true)
    public List<Color> getUsedColors() {
        return colorRepository.findUsedColors();
    }
    
    /**
     * Obtener colores sin productos
     */
    @Transactional(readOnly = true)
    public List<Color> getUnusedColors() {
        return colorRepository.findUnusedColors();
    }
    
    /**
     * Obtener colores más utilizados
     */
    @Transactional(readOnly = true)
    public List<Color> getMostUsedColors() {
        return colorRepository.findMostUsedColors();
    }
    
    /**
     * Obtener colores menos utilizados
     */
    @Transactional(readOnly = true)
    public List<Color> getLeastUsedColors() {
        return colorRepository.findLeastUsedColors();
    }
    
    /**
     * Reordenar colores
     */
    public void reorderColors(List<Long> colorIds) {
        for (int i = 0; i < colorIds.size(); i++) {
            Long colorId = colorIds.get(i);
            Color color = colorRepository.findById(colorId)
                    .orElseThrow(() -> new IllegalArgumentException("Color no encontrado con ID: " + colorId));
            color.setDisplayOrder(i + 1);
            colorRepository.save(color);
        }
    }
    
    /**
     * Crear o actualizar colores por defecto
     * Este método se asegura de que los colores predeterminados existan y estén marcados como isDefault = true
     */
    public void createDefaultColors() {
        // Definir colores predeterminados del sistema
        String[][] defaultColorsData = {
            {"Blanco", "Color blanco clásico", "#FFFFFF"},
            {"Negro", "Color negro elegante", "#000000"},
            {"Azul", "Color azul marino", "#000080"},
            {"Rojo", "Color rojo vibrante", "#FF0000"},
            {"Verde", "Color verde natural", "#008000"},
            {"Gris", "Color gris neutro", "#808080"},
            {"Rosa", "Color rosa suave", "#FFC0CB"},
            {"Amarillo", "Color amarillo brillante", "#FFFF00"},
            {"Marrón", "Color marrón tierra", "#A52A2A"},
            {"Violeta", "Color violeta elegante", "#800080"}
        };
        
        // Procesar cada color predeterminado
        for (int i = 0; i < defaultColorsData.length; i++) {
            String name = defaultColorsData[i][0];
            String description = defaultColorsData[i][1];
            String hexCode = defaultColorsData[i][2];
            
            // Buscar si el color ya existe
            Optional<Color> existingColor = colorRepository.findByNameIgnoreCase(name);
            
            if (existingColor.isPresent()) {
                // Si existe, actualizarlo para marcarlo como predeterminado
                Color color = existingColor.get();
                color.setIsDefault(true);
                color.setDisplayOrder(i + 1);
                // Actualizar descripción y código hex si no están definidos
                if (color.getDescription() == null || color.getDescription().isEmpty()) {
                    color.setDescription(description);
                }
                if (color.getHexCode() == null || color.getHexCode().isEmpty()) {
                    color.setHexCode(hexCode);
                }
                colorRepository.save(color);
            } else {
                // Si no existe, crearlo como predeterminado
                Color newColor = new Color(name, description, hexCode);
                newColor.setDisplayOrder(i + 1);
                newColor.setIsDefault(true); // Marcar como predeterminado
                colorRepository.save(newColor);
            }
        }
    }
    
    /**
     * Validar datos de color
     */
    public void validateColor(Color color) {
        // Normalizar y validar nombre
        if (color.getName() == null || color.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del color es obligatorio");
        }
        
        // Limpiar espacios en blanco del nombre
        String cleanName = color.getName().trim();
        if (cleanName.isEmpty()) {
            throw new IllegalArgumentException("El nombre del color es obligatorio");
        }
        color.setName(cleanName);
        
        if (color.getName().length() < 2 || color.getName().length() > 50) {
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 50 caracteres");
        }
        
        if (color.getDescription() != null && color.getDescription().length() > 200) {
            throw new IllegalArgumentException("La descripción no puede exceder 200 caracteres");
        }
        
        // Validar código hexadecimal solo si está presente (es opcional para patrones)
        if (color.getHexCode() != null && !color.getHexCode().trim().isEmpty() && 
            !color.getHexCode().matches("^#[0-9A-Fa-f]{6}$")) {
            throw new IllegalArgumentException("El código hexadecimal debe tener formato #RRGGBB o estar vacío");
        }
    }
    
    /**
     * Obtener el número total de colores
     */
    @Transactional(readOnly = true)
    public long getColorCount() {
        return colorRepository.count();
    }
    
    /**
     * Actualizar contador de productos para un color específico
     */
    @Transactional
    public void updateProductCount(Long colorId) {
        Color color = colorRepository.findById(colorId)
                .orElseThrow(() -> new IllegalArgumentException("Color no encontrado con ID: " + colorId));
        
        Long count = colorRepository.countProductsByColorId(colorId);
        color.setProductCount(count != null ? count.intValue() : 0);
        colorRepository.save(color);
    }
    
    /**
     * Actualizar contadores de productos para todos los colores
     */
    @Transactional
    public void updateAllProductCounts() {
        List<Color> allColors = colorRepository.findAll();
        for (Color color : allColors) {
            Long count = colorRepository.countProductsByColorId(color.getId());
            color.setProductCount(count != null ? count.intValue() : 0);
        }
        colorRepository.saveAll(allColors);
    }
    
    /**
     * Corregir colores con displayOrder = 0 o null
     * Asigna un orden válido (mínimo 1) a los colores que tienen orden 0 o null
     */
    @Transactional
    public void fixColorsWithZeroOrder() {
        List<Color> colorsWithZeroOrder = colorRepository.findAll().stream()
                .filter(c -> c.getDisplayOrder() == null || c.getDisplayOrder() <= 0)
                .collect(java.util.stream.Collectors.toList());
        
        if (!colorsWithZeroOrder.isEmpty()) {
            // Obtener el máximo orden actual
            Integer maxOrder = colorRepository.findAll().stream()
                    .filter(c -> c.getDisplayOrder() != null && c.getDisplayOrder() > 0)
                    .mapToInt(Color::getDisplayOrder)
                    .max()
                    .orElse(0);
            
            // Asignar orden secuencial a los colores con orden 0 o null
            for (int i = 0; i < colorsWithZeroOrder.size(); i++) {
                colorsWithZeroOrder.get(i).setDisplayOrder(maxOrder + i + 1);
            }
            
            colorRepository.saveAll(colorsWithZeroOrder);
        }
    }
}
