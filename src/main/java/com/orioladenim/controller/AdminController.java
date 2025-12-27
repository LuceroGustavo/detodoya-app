package com.orioladenim.controller;

import com.orioladenim.entity.User;
import com.orioladenim.repo.ProductRepository;
import com.orioladenim.service.ContactService;
import com.orioladenim.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ContactService contactService;
    
    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // Obtener estadísticas para el dashboard
        long totalProducts = productRepository.count();
        long productosDestacados = productRepository.findByEsDestacadoTrueAndActivoTrue().size();
        long productosActivos = productRepository.findByActivoTrue().size();
        long consultasNoLeidas = contactService.contarNoLeidas();
        long totalConsultas = contactService.contarTotal();
        
        // Obtener el usuario real de la base de datos
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        model.addAttribute("totalProductos", totalProducts);
        model.addAttribute("productosDestacados", productosDestacados);
        model.addAttribute("productosActivos", productosActivos);
        model.addAttribute("totalConsultas", totalConsultas);
        model.addAttribute("consultasNoLeidas", consultasNoLeidas);
        model.addAttribute("username", username);
        model.addAttribute("user", user);
        
        return "admin/dashboard";
    }
    
    @GetMapping("/help")
    public String help() {
        return "admin/help";
    }
}

