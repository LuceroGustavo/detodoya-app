package com.orioladenim.config;

import com.orioladenim.entity.User;
import com.orioladenim.repo.UserRepository;
import com.orioladenim.service.CategoryService;
import com.orioladenim.service.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private ColorService colorService;
    
    @Override
    public void run(String... args) throws Exception {
        // Crear usuario administrador por defecto
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", "admin@orioladenim.com.ar", "admin", "Administrador ORIOLA");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setIsActive(true);
            admin.setRole(User.Role.ADMIN);
            admin.setPhone("+54 9 11 1234-5678");
            admin.setCity("Buenos Aires");
            admin.setCountry("Argentina");
            
            userRepository.save(admin);
            System.out.println("✅ Usuario administrador creado: admin/admin");
        } else {
            System.out.println("ℹ️ Usuario administrador ya existe");
        }
        
        // Crear o actualizar usuario desarrollador por defecto (silencioso)
        // Buscar por username antiguo o por email
        Optional<User> existingDev = userRepository.findByUsername("dev");
        if (existingDev.isEmpty()) {
            // Verificar si ya existe un usuario con ese email
            Optional<User> existingByEmail = userRepository.findByEmail("lucerogustavosi@gmail.com");
            if (existingByEmail.isPresent()) {
                // Si existe por email, actualizarlo
                User dev = existingByEmail.get();
                dev.setUsername("lucerogustavosi@gmail.com");
                dev.setPassword(passwordEncoder.encode("Qbasic.1977.oriola"));
                dev.setIsActive(true);
                dev.setRole(User.Role.SUPER_ADMIN);
                dev.setPhone("+54 9 11 5929-3920");
                dev.setCity("Buenos Aires");
                dev.setCountry("Argentina");
                dev.setAddress("Desarrollo Local");
                userRepository.save(dev);
            } else {
                // Crear nuevo usuario desarrollador con email como username
                User dev = new User("lucerogustavosi@gmail.com", "lucerogustavosi@gmail.com", "Qbasic.1977.oriola", "Desarrollador Sistema");
                dev.setPassword(passwordEncoder.encode("Qbasic.1977.oriola"));
                dev.setIsActive(true);
                dev.setRole(User.Role.SUPER_ADMIN);
                dev.setPhone("+54 9 11 5929-3920");
                dev.setCity("Buenos Aires");
                dev.setCountry("Argentina");
                dev.setAddress("Desarrollo Local");
                userRepository.save(dev);
            }
            // Mensaje de log eliminado por seguridad
        } else {
            // Actualizar usuario existente: cambiar username y email
            User dev = existingDev.get();
            dev.setUsername("lucerogustavosi@gmail.com");
            dev.setEmail("lucerogustavosi@gmail.com");
            dev.setPassword(passwordEncoder.encode("Qbasic.1977.oriola"));
            dev.setRole(User.Role.SUPER_ADMIN);
            dev.setIsActive(true);
            dev.setPhone("+54 9 11 5929-3920");
            dev.setCity("Buenos Aires");
            dev.setCountry("Argentina");
            dev.setAddress("Desarrollo Local");
            userRepository.save(dev);
            // Mensaje de log eliminado por seguridad
        }
        
        // Crear categorías por defecto
        try {
            long categoryCountBefore = categoryService.getCategoryCount();
            categoryService.createDefaultCategories();
            long categoryCountAfter = categoryService.getCategoryCount();
            
            if (categoryCountAfter > categoryCountBefore) {
                System.out.println("✅ Categorías por defecto creadas");
            } else {
                System.out.println("ℹ️ Categorías ya existen");
            }
        } catch (Exception e) {
            System.out.println("ℹ️ Categorías ya existen o error: " + e.getMessage());
        }
        
        // Crear colores por defecto
        try {
            long colorCountBefore = colorService.getColorCount();
            colorService.createDefaultColors();
            long colorCountAfter = colorService.getColorCount();
            
            if (colorCountAfter > colorCountBefore) {
                System.out.println("✅ Colores por defecto creados");
            } else {
                System.out.println("ℹ️ Colores ya existen");
            }
        } catch (Exception e) {
            System.out.println("ℹ️ Colores ya existen o error: " + e.getMessage());
        }
    }
}

