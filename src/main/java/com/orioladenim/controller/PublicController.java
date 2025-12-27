package com.orioladenim.controller;

import com.orioladenim.entity.Product;
import com.orioladenim.entity.Category;
import com.orioladenim.repo.ProductRepository;
import com.orioladenim.service.CategoryService;
import com.orioladenim.service.HistoriaService;
import com.orioladenim.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
public class PublicController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private HistoriaService historiaService;
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @GetMapping("/")
    public String home(Model model) {
        try {
            // Mostrar solo productos de novedades (esNuevo = true y activo = true)
            model.addAttribute("products", productRepository.findByEsNuevoTrueAndActivoTrue());
            
            // Agregar categorías para el dropdown (solo las que tienen productos)
            model.addAttribute("categories", categoryService.getCategoriesWithProducts());
            
            // Obtener categorías listas para el carrusel (solo si hay categorías válidas)
            List<Category> carouselCategories = categoryService.findReadyForCarousel();
            System.out.println("🔄 [INDEX] Categorías del carrusel encontradas: " + carouselCategories.size());
            for (Category cat : carouselCategories) {
                System.out.println("  - " + cat.getName() + " (showInCarousel: " + cat.getShowInCarousel() + ", imagePath: " + cat.getImagePath() + ")");
            }
            if (!carouselCategories.isEmpty()) {
                model.addAttribute("carouselCategories", carouselCategories);
                System.out.println("✅ [INDEX] Categorías del carrusel agregadas al modelo");
            } else {
                System.out.println("⚠️ [INDEX] No hay categorías para el carrusel");
            }
            
            // Obtener historia principal (la más reciente y activa)
            model.addAttribute("historiaPrincipal", historiaService.findActivaPrincipal().orElse(null));
            
            return "index";
        } catch (Exception e) {
            // En caso de error, mostrar página sin categorías ni historia
            model.addAttribute("products", productRepository.findByEsNuevoTrueAndActivoTrue());
            model.addAttribute("categories", new java.util.ArrayList<>());
            model.addAttribute("historiaPrincipal", null);
            return "index";
        }
    }
    
    @GetMapping("/catalog")
    public String catalog(@RequestParam(required = false) String category, 
                         @RequestParam(required = false) String search, 
                         Model model) {
        System.out.println("🔍 [CATALOG] Parámetros recibidos - category: '" + category + "', search: '" + search + "'");
        
        // Obtener solo productos activos
        List<Product> products = productRepository.findByActivoTrue();
        System.out.println("🔍 Productos activos encontrados: " + products.size());
        
        // Filtrar por categoría si se especifica
        if (category != null && !category.trim().isEmpty()) {
            System.out.println("🔍 [CATALOG] Filtrando por categoría: '" + category + "'");
            System.out.println("🔍 [CATALOG] Productos antes del filtro: " + products.size());
            
            // Mostrar todas las categorías de los productos para debug
            for (Product p : products) {
                System.out.println("  - Producto: " + p.getName() + " - Categorías: " + 
                    p.getCategories().stream().map(Category::getName).collect(java.util.stream.Collectors.joining(", ")));
            }
            
            products = products.stream()
                    .filter(p -> p.getCategories().stream()
                            .anyMatch(c -> c.getName().equalsIgnoreCase(category.trim())))
                    .collect(java.util.stream.Collectors.toList());
            System.out.println("🔍 [CATALOG] Productos filtrados por categoría '" + category + "': " + products.size());
        }
        
        // Filtrar por búsqueda si se especifica
        if (search != null && !search.trim().isEmpty()) {
            String searchTerm = search.trim().toLowerCase();
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(searchTerm) ||
                               p.getCategories().stream().anyMatch(c -> c.getName().toLowerCase().contains(searchTerm)))
                    .collect(java.util.stream.Collectors.toList());
            System.out.println("🔍 Productos filtrados por búsqueda '" + search + "': " + products.size());
        }
        
        // Obtener solo categorías que tienen productos activos
        List<Category> categories = categoryService.getCategoriesWithProducts();
        System.out.println("🔍 Categorías con productos activos: " + categories.size());
        for (Category cat : categories) {
            System.out.println("  - " + cat.getName() + " (productos: " + cat.getProductCount() + ")");
        }
        
        // Fallback: si no hay categorías con productos, mostrar todas las categorías activas
        if (categories.isEmpty()) {
            System.out.println("⚠️ No hay categorías con productos activos, mostrando todas las categorías activas");
            categories = categoryService.getActiveCategories();
            System.out.println("🔍 Categorías activas totales: " + categories.size());
        }
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("search", search);
        
        return "catalog";
    }
    
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Integer id, Model model, HttpServletRequest request) {
        // Cargar producto con colores usando consulta optimizada
        java.util.Optional<Product> productOpt = productRepository.findByIdWithColors(id);
        
        if (!productOpt.isPresent()) {
            throw new RuntimeException("Producto no encontrado");
        }
        
        Product product = productOpt.get();
        
        // 📊 TRACKING AUTOMÁTICO DE VISITAS
        analyticsService.trackProductView(id, request);
        
        // Debug: Verificar colores
        System.out.println("🔍 [PRODUCT-DETAIL] Producto: " + product.getName());
        if (product.getColores() != null) {
            System.out.println("🔍 [PRODUCT-DETAIL] Cantidad de colores: " + product.getColores().size());
            for (com.orioladenim.entity.Color color : product.getColores()) {
                System.out.println("  - Color: " + color.getName() + " (Hex: " + color.getHexCode() + ")");
            }
        } else {
            System.out.println("⚠️ [PRODUCT-DETAIL] Lista de colores es NULL");
        }
        
        // Forzar carga de imágenes (separada para evitar MultipleBagFetchException)
        java.util.Optional<Product> productWithImagesOpt = productRepository.findByIdWithImages(id);
        if (productWithImagesOpt.isPresent()) {
            product.setImages(productWithImagesOpt.get().getImages());
        }
        
        // Forzar carga de categorías si no están cargadas
        if (product.getCategories() != null) {
            product.getCategories().size(); // Force lazy loading
        }
        
        model.addAttribute("product", product);
        return "product-detail";
    }
    
    
    @GetMapping("/about")
    public String about() {
        return "about";
    }
}

