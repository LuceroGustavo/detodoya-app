package com.detodoya.controller;

import com.detodoya.entity.Product;
import com.detodoya.entity.Category;
import com.detodoya.entity.Color;
import com.detodoya.entity.ProductImage;
import com.detodoya.enums.Talle;
import com.detodoya.enums.Genero;
import com.detodoya.enums.Temporada;
import com.detodoya.enums.TipoProducto;
import com.detodoya.repo.ProductRepository;
import com.detodoya.repo.ProductImageRepository;
import com.detodoya.service.CategoryService;
import com.detodoya.service.ColorService;
import com.detodoya.service.ImageProcessingService;
import com.detodoya.service.VideoProcessingService;
import com.detodoya.service.SubcategoriaService;
import com.detodoya.entity.Subcategoria;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Paths;

@Controller
@RequestMapping("/admin/products")
public class ProductController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductImageRepository productImageRepository;
    
    @Autowired
    private ImageProcessingService imageProcessingService;
    
    @Autowired
    private VideoProcessingService videoProcessingService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private ColorService colorService;
    
    @Autowired
    private SubcategoriaService subcategoriaService;

    @GetMapping
    public String listProducts(@RequestParam(required = false) String search,
                              @RequestParam(required = false) String category,
                              @RequestParam(required = false) String activo,
                              Model model) {
        List<Product> products = productRepository.findAll();
        
        // Debug: Verificar valores de esNuevo al cargar la lista
        System.out.println("📋 [listProducts] Cargando lista de productos, total: " + products.size());
        for (Product p : products) {
            System.out.println("  - Producto ID: " + p.getPId() + ", Nombre: " + p.getName() + ", esNuevo: " + p.getEsNuevo());
        }
        
        // Aplicar filtros
        if (search != null && !search.trim().isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(search.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        if (category != null && !category.trim().isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getCategories().stream()
                            .anyMatch(c -> c.getName().toLowerCase().contains(category.toLowerCase())))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        if (activo != null && !activo.trim().isEmpty()) {
            Boolean activoFiltro = Boolean.parseBoolean(activo);
            products = products.stream()
                    .filter(p -> p.getActivo().equals(activoFiltro))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getActiveCategories());
        model.addAttribute("search", search);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("activo", activo);
        return "admin/product-list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        Product product = new Product();
        product.setPId(null); // Asegurar que no tenga ID para detectar como nuevo
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getActiveCategories());
        model.addAttribute("subcategorias", subcategoriaService.getActiveSubcategorias());
        model.addAttribute("colors", colorService.getAllColors());
        model.addAttribute("tiposProducto", TipoProducto.values());
        model.addAttribute("talles", Talle.values());
        model.addAttribute("generos", Genero.values());
        model.addAttribute("temporadas", Temporada.values());
        return "admin/product-form";
    }

    @PostMapping("/save")
    public String addProduct(@Valid Product product, 
                           @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                           @RequestParam(value = "subcategoriaIds", required = false) List<Long> subcategoriaIds,
                           @RequestParam(value = "colorIds", required = false) List<Long> colorIds,
                           @RequestParam(value = "talleNames", required = false) List<String> talleNames,
                           @RequestParam(value = "generoNames", required = false) List<String> generoNames,
                           @RequestParam(value = "temporadaNames", required = false) List<String> temporadaNames,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.getActiveCategories());
            model.addAttribute("subcategorias", subcategoriaService.getActiveSubcategorias());
            model.addAttribute("colors", colorService.getAllColors());
            model.addAttribute("tiposProducto", TipoProducto.values());
            model.addAttribute("talles", Talle.values());
            model.addAttribute("generos", Genero.values());
            model.addAttribute("temporadas", Temporada.values());
            return "admin/product-form";
        }
        
        // Manejar categorías múltiples
        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Category> selectedCategories = new ArrayList<>();
            for (Long categoryId : categoryIds) {
                Category category = categoryService.findById(categoryId);
                if (category != null) {
                    selectedCategories.add(category);
                }
            }
            product.setCategories(selectedCategories);
        }
        
        // Manejar subcategorías múltiples (opcional)
        if (subcategoriaIds != null && !subcategoriaIds.isEmpty()) {
            List<Subcategoria> selectedSubcategorias = new ArrayList<>();
            for (Long subcategoriaId : subcategoriaIds) {
                subcategoriaService.getSubcategoriaById(subcategoriaId).ifPresent(selectedSubcategorias::add);
            }
            product.setSubcategorias(selectedSubcategorias);
        }
        
        // Manejar colores múltiples
        if (colorIds != null && !colorIds.isEmpty()) {
            List<Color> selectedColors = new ArrayList<>();
            for (Long colorId : colorIds) {
                colorService.getColorById(colorId).ifPresent(selectedColors::add);
            }
            product.setColores(selectedColors);
        }
        
        // Manejar talles múltiples (enum)
        if (talleNames != null && !talleNames.isEmpty()) {
            List<Talle> selectedTalles = new ArrayList<>();
            for (String talleName : talleNames) {
                try {
                    Talle talle = Talle.valueOf(talleName);
                    selectedTalles.add(talle);
                } catch (IllegalArgumentException e) {
                    // Ignorar talles inválidos
                }
            }
            product.setTalles(selectedTalles);
        }
        
        // Manejar géneros múltiples (enum)
        if (generoNames != null && !generoNames.isEmpty()) {
            List<Genero> selectedGeneros = new ArrayList<>();
            for (String generoName : generoNames) {
                try {
                    Genero genero = Genero.valueOf(generoName);
                    selectedGeneros.add(genero);
                } catch (IllegalArgumentException e) {
                    // Ignorar géneros inválidos
                }
            }
            product.setGeneros(selectedGeneros);
        }
        
        // Manejar temporadas múltiples (enum)
        if (temporadaNames != null && !temporadaNames.isEmpty()) {
            List<Temporada> selectedTemporadas = new ArrayList<>();
            for (String temporadaName : temporadaNames) {
                try {
                    Temporada temporada = Temporada.valueOf(temporadaName);
                    selectedTemporadas.add(temporada);
                } catch (IllegalArgumentException e) {
                    // Ignorar temporadas inválidas
                }
            }
            product.setTemporadas(selectedTemporadas);
        }
        
        product.setFechaCreacion(java.time.LocalDateTime.now());
        product.setFechaActualizacion(java.time.LocalDateTime.now());
        Product savedProduct = productRepository.save(product);
        return "redirect:/admin/products/edit/" + savedProduct.getPId();
    }

    @GetMapping("/edit/{pId}")
    public String editProduct(@PathVariable Integer pId, Model model) {
        Product product = productRepository.findById(pId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getActiveCategories());
        model.addAttribute("subcategorias", subcategoriaService.getActiveSubcategorias());
        model.addAttribute("colors", colorService.getAllColors());
        model.addAttribute("tiposProducto", TipoProducto.values());
        model.addAttribute("talles", Talle.values());
        model.addAttribute("generos", Genero.values());
        model.addAttribute("temporadas", Temporada.values());
        
        // Debug: Verificar qué datos tiene el producto
        System.out.println("📝 [editProduct] Producto ID: " + product.getPId());
        System.out.println("📝 [editProduct] Nombre: " + product.getName());
        System.out.println("📝 [editProduct] esNuevo: " + product.getEsNuevo());
        System.out.println("📝 [editProduct] Categorías del producto: " + product.getCategories().size());
        System.out.println("📝 [editProduct] Subcategorías del producto: " + (product.getSubcategorias() != null ? product.getSubcategorias().size() : 0));
        System.out.println("📝 [editProduct] Colores del producto: " + product.getColores().size());
        System.out.println("📝 [editProduct] Talles del producto: " + product.getTalles().size());
        System.out.println("📝 [editProduct] Géneros del producto: " + product.getGeneros().size());
        System.out.println("📝 [editProduct] Temporadas del producto: " + product.getTemporadas().size());
        
        return "admin/product-form";
    }

    @PostMapping("/edit/{pId}")
    public String updateProduct(@PathVariable Integer pId, 
                              @Valid Product product,
                              @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                              @RequestParam(value = "subcategoriaIds", required = false) List<Long> subcategoriaIds,
                              @RequestParam(value = "colorIds", required = false) List<Long> colorIds,
                              @RequestParam(value = "talleNames", required = false) List<String> talleNames,
                              @RequestParam(value = "generoNames", required = false) List<String> generoNames,
                              @RequestParam(value = "temporadaNames", required = false) List<String> temporadaNames,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.getActiveCategories());
            model.addAttribute("subcategorias", subcategoriaService.getActiveSubcategorias());
            model.addAttribute("colors", colorService.getAllColors());
            model.addAttribute("tiposProducto", TipoProducto.values());
            model.addAttribute("talles", Talle.values());
            model.addAttribute("generos", Genero.values());
            model.addAttribute("temporadas", Temporada.values());
            return "admin/product-form";
        }
        
        // Obtener el producto existente
        Product existingProduct = productRepository.findById(pId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        // Actualizar campos básicos
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQty(product.getQty());
        existingProduct.setDescripcion(product.getDescripcion());
        existingProduct.setMaterial(product.getMaterial());
        existingProduct.setCuidados(product.getCuidados());
        // Los géneros y temporadas se manejan por separado más abajo
        existingProduct.setEdadRecomendada(product.getEdadRecomendada());
        existingProduct.setTallasDisponibles(product.getTallasDisponibles());
        existingProduct.setColoresDisponibles(product.getColoresDisponibles());
        // Nuevos campos genéricos
        existingProduct.setEspecificaciones(product.getEspecificaciones());
        existingProduct.setMarca(product.getMarca());
        existingProduct.setModelo(product.getModelo());
        existingProduct.setGarantia(product.getGarantia());
        existingProduct.setTipoProducto(product.getTipoProducto());
        // Campos de integración marketplace
        existingProduct.setCodigoProducto(product.getCodigoProducto());
        existingProduct.setLinkVenta(product.getLinkVenta());
        existingProduct.setContactoVendedor(product.getContactoVendedor());
        existingProduct.setUbicacion(product.getUbicacion());
        // Nuevos campos para libros
        existingProduct.setAutor(product.getAutor());
        existingProduct.setEditorial(product.getEditorial());
        existingProduct.setIsbn(product.getIsbn());
        existingProduct.setPaginas(product.getPaginas());
        // Nuevos campos genéricos
        existingProduct.setPeso(product.getPeso());
        existingProduct.setDimensiones(product.getDimensiones());
        // Nuevos campos para electrónica
        existingProduct.setPotencia(product.getPotencia());
        existingProduct.setConsumo(product.getConsumo());
        existingProduct.setEsDestacado(product.getEsDestacado());
        // NO actualizar esNuevo desde el formulario - se maneja mediante el endpoint toggle-vista-inicio
        // existingProduct.setEsNuevo(product.getEsNuevo());
        existingProduct.setEtiquetaPromocional(product.getEtiquetaPromocional());
        existingProduct.setDescuentoPorcentaje(product.getDescuentoPorcentaje());
        existingProduct.setPrecioOriginal(product.getPrecioOriginal());
        existingProduct.setActivo(product.getActivo());
        existingProduct.setColorEntity(product.getColorEntity());
        existingProduct.setMedidas(product.getMedidas());
        existingProduct.setColor(product.getColor());
        
        // Manejar categorías múltiples
        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Category> selectedCategories = new ArrayList<>();
            for (Long categoryId : categoryIds) {
                Category category = categoryService.findById(categoryId);
                if (category != null) {
                    selectedCategories.add(category);
                }
            }
            existingProduct.setCategories(selectedCategories);
        } else {
            existingProduct.setCategories(new ArrayList<>());
        }
        
        // Manejar subcategorías múltiples (opcional)
        if (subcategoriaIds != null && !subcategoriaIds.isEmpty()) {
            List<Subcategoria> selectedSubcategorias = new ArrayList<>();
            for (Long subcategoriaId : subcategoriaIds) {
                subcategoriaService.getSubcategoriaById(subcategoriaId).ifPresent(selectedSubcategorias::add);
            }
            existingProduct.setSubcategorias(selectedSubcategorias);
        } else {
            existingProduct.setSubcategorias(new ArrayList<>());
        }
        
        // Manejar colores múltiples
        if (colorIds != null && !colorIds.isEmpty()) {
            List<Color> selectedColors = new ArrayList<>();
            for (Long colorId : colorIds) {
                colorService.getColorById(colorId).ifPresent(selectedColors::add);
            }
            existingProduct.setColores(selectedColors);
        } else {
            existingProduct.setColores(new ArrayList<>());
        }
        
        // Manejar talles múltiples
        if (talleNames != null && !talleNames.isEmpty()) {
            List<Talle> selectedTalles = new ArrayList<>();
            for (String talleName : talleNames) {
                try {
                    Talle talle = Talle.valueOf(talleName);
                    selectedTalles.add(talle);
                } catch (IllegalArgumentException e) {
                    // Ignorar talles inválidos
                }
            }
            existingProduct.setTalles(selectedTalles);
        } else {
            existingProduct.setTalles(new ArrayList<>());
        }
        
        // Manejar géneros múltiples
        if (generoNames != null && !generoNames.isEmpty()) {
            List<Genero> selectedGeneros = new ArrayList<>();
            for (String generoName : generoNames) {
                try {
                    Genero genero = Genero.valueOf(generoName);
                    selectedGeneros.add(genero);
                } catch (IllegalArgumentException e) {
                    // Ignorar géneros inválidos
                }
            }
            existingProduct.setGeneros(selectedGeneros);
        } else {
            existingProduct.setGeneros(new ArrayList<>());
        }
        
        // Manejar temporadas múltiples
        if (temporadaNames != null && !temporadaNames.isEmpty()) {
            List<Temporada> selectedTemporadas = new ArrayList<>();
            for (String temporadaName : temporadaNames) {
                try {
                    Temporada temporada = Temporada.valueOf(temporadaName);
                    selectedTemporadas.add(temporada);
                } catch (IllegalArgumentException e) {
                    // Ignorar temporadas inválidas
                }
            }
            existingProduct.setTemporadas(selectedTemporadas);
        } else {
            existingProduct.setTemporadas(new ArrayList<>());
        }
        
        existingProduct.setFechaActualizacion(java.time.LocalDateTime.now());
        
        // IMPORTANTE: Preservar el valor de esNuevo que fue establecido mediante toggle-vista-inicio
        // No se debe sobrescribir con el valor del formulario
        System.out.println("💾 [updateProduct] Guardando producto ID: " + existingProduct.getPId());
        System.out.println("💾 [updateProduct] esNuevo antes de guardar: " + existingProduct.getEsNuevo());
        System.out.println("💾 [updateProduct] esNuevo del formulario (ignorado): " + product.getEsNuevo());
        
        productRepository.save(existingProduct);
        productRepository.flush(); // Asegurar persistencia
        
        System.out.println("✅ [updateProduct] Producto guardado, esNuevo preservado: " + existingProduct.getEsNuevo());
        
        return "redirect:/admin/products";
    }

    @PostMapping("/delete/{pId}")
    @ResponseBody
    public java.util.Map<String, Object> deleteProduct(@PathVariable Integer pId) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            Product product = productRepository.findById(pId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            // Verificar si tiene relaciones que impidan la eliminación
            // (esto se manejará automáticamente por la base de datos, pero podemos intentar eliminar)
            productRepository.deleteById(pId);
            
            response.put("success", true);
            response.put("message", "Producto eliminado correctamente");
            return response;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            response.put("success", false);
            response.put("message", "No se puede eliminar el producto porque tiene datos asociados (vistas, imágenes, etc.). Primero elimina o desasocia estos datos.");
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar el producto: " + e.getMessage());
            return response;
        }
    }

    @GetMapping("/{pId}/images")
    public String manageImages(@PathVariable Integer pId, Model model) {
        Product product = productRepository.findById(pId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        model.addAttribute("product", product);
        return "admin/product-images";
    }

    @PostMapping("/{pId}/images/upload")
    @ResponseBody
    public java.util.Map<String, Object> uploadImages(@PathVariable Integer pId, 
                              @RequestParam("images") MultipartFile[] images) {
        try {
            Product product = productRepository.findById(pId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            // Si hay imágenes nuevas, desactivar todas las imágenes principales existentes
            boolean shouldClearPrimary = false;
            if (images.length > 0 && !images[0].isEmpty()) {
                shouldClearPrimary = true;
            }
            
            int savedCount = 0;
            boolean isFirstImage = true;
            
            for (int i = 0; i < images.length; i++) {
                MultipartFile file = images[i];
                if (!file.isEmpty()) {
                    // Detectar si es video
                    boolean isVideo = file.getContentType() != null && 
                                    (file.getContentType().startsWith("video/"));
                    
                    ProductImage productImage;
                    
                    if (isVideo) {
                        // Procesar video
                        String videoPath = videoProcessingService.procesarVideoProducto(file, pId);
                        @SuppressWarnings("unused")
                        String _thumbnailPath = videoProcessingService.generarThumbnailProducto(videoPath, pId);
                        
                        productImage = new ProductImage();
                        productImage.setFileName(Paths.get(videoPath).getFileName().toString());
                        productImage.setImagePath(videoPath);
                        productImage.setOriginalName(file.getOriginalFilename());
                        productImage.setFileSize(file.getSize());
                        productImage.setIsVideo(true);
                    } else {
                        // Procesar imagen normalmente
                        productImage = imageProcessingService.processAndSaveImage(file, pId, i == 0);
                        productImage.setIsVideo(false);
                    }
                    
                    productImage.setProduct(product);
                    
                    // Si es la primera imagen de la nueva subida, limpiar todas las principales existentes y marcar esta como principal
                    if (isFirstImage && shouldClearPrimary) {
                        // Desmarcar todas las imágenes principales existentes
                        java.util.List<ProductImage> existingImages = productImageRepository.findByProductIdOrderByDisplayOrderAsc(pId);
                        for (ProductImage existingImage : existingImages) {
                            if (existingImage.getIsPrimary()) {
                                existingImage.setIsPrimary(false);
                                productImageRepository.save(existingImage);
                            }
                        }
                        // Marcar esta como principal
                        productImage.setIsPrimary(true);
                        isFirstImage = false;
                    } else {
                        productImage.setIsPrimary(false);
                    }
                    
                    productImage.setDisplayOrder(i);
                    
                    productImageRepository.save(productImage);
                    savedCount++;
                }
            }
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", "Archivos asociados correctamente");
            response.put("count", savedCount);
            
            return response;
        } catch (Exception e) {
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", false);
            response.put("message", "Error al procesar los archivos: " + e.getMessage());
            return response;
        }
    }
    
    @PostMapping("/{pId}/toggle-status")
    @ResponseBody
    public java.util.Map<String, Object> toggleProductStatus(@PathVariable Integer pId) {
        try {
            Product product = productRepository.findById(pId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            // Cambiar el estado
            product.setActivo(!product.getActivo());
            productRepository.save(product);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", product.getActivo() ? "Producto activado y publicado" : "Producto desactivado y oculto del catálogo");
            response.put("activo", product.getActivo());
            
            return response;
        } catch (Exception e) {
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", false);
            response.put("message", "Error al cambiar el estado del producto: " + e.getMessage());
            return response;
        }
    }
    
    @PostMapping("/{pId}/toggle-vista-inicio")
    @ResponseBody
    @Transactional
    public java.util.Map<String, Object> toggleVistaInicio(@PathVariable Integer pId) {
        try {
            System.out.println("🔄 [toggleVistaInicio] Iniciando toggle para producto ID: " + pId);
            
            // Obtener el producto desde la base de datos
            Product product = productRepository.findById(pId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            System.out.println("📦 [toggleVistaInicio] Producto encontrado: " + product.getName());
            System.out.println("📦 [toggleVistaInicio] Valor actual de esNuevo: " + product.getEsNuevo());
            
            // Cambiar el estado de vista en inicio (manejar null como false)
            Boolean currentValue = product.getEsNuevo();
            if (currentValue == null) {
                currentValue = false;
                System.out.println("⚠️ [toggleVistaInicio] esNuevo era null, establecido a false");
            }
            
            Boolean newValue = !currentValue;
            System.out.println("🔄 [toggleVistaInicio] Cambiando esNuevo de " + currentValue + " a " + newValue);
            
            product.setEsNuevo(newValue);
            
            // Guardar el producto y hacer flush para asegurar persistencia inmediata
            Product savedProduct = productRepository.save(product);
            System.out.println("💾 [toggleVistaInicio] Producto guardado, esNuevo en objeto guardado: " + savedProduct.getEsNuevo());
            
            productRepository.flush(); // Forzar la escritura inmediata en la base de datos
            System.out.println("✅ [toggleVistaInicio] Flush completado");
            
            // Verificar que se guardó correctamente leyendo de nuevo desde la base de datos
            Product verifyProduct = productRepository.findById(pId).orElse(null);
            if (verifyProduct != null) {
                System.out.println("✅ [toggleVistaInicio] Verificación: esNuevo en BD: " + verifyProduct.getEsNuevo());
            }
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", newValue ? "Producto agregado a la vista de inicio" : "Producto removido de la vista de inicio");
            response.put("esNuevo", newValue);
            
            return response;
        } catch (Exception e) {
            System.err.println("❌ [toggleVistaInicio] Error: " + e.getMessage());
            e.printStackTrace();
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", false);
            response.put("message", "Error al cambiar la vista en inicio: " + e.getMessage());
            return response;
        }
    }
}

