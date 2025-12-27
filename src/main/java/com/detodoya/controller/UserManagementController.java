package com.detodoya.controller;

import com.detodoya.entity.User;
import com.detodoya.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class UserManagementController {

    @Autowired
    private UserService userService;

    /**
     * Lista todos los usuarios (solo para SUPER_ADMIN)
     */
    @GetMapping
    public String listUsers(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Solo SUPER_ADMIN puede ver la lista de usuarios
        if (!currentUser.getRole().equals(User.Role.SUPER_ADMIN)) {
            return "redirect:/admin/dashboard";
        }
        
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("currentUser", currentUser);
        
        return "admin/user-list";
    }

    /**
     * Formulario para cambiar contraseña del usuario actual
     */
    @GetMapping("/change-password")
    public String changePasswordForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        model.addAttribute("user", currentUser);
        
        // Verificar si es desarrollador (SUPER_ADMIN con email lucerogustavosi@gmail.com)
        boolean isDeveloper = currentUser.getRole().equals(User.Role.SUPER_ADMIN) && 
                             "lucerogustavosi@gmail.com".equals(currentUser.getEmail());
        model.addAttribute("isDeveloper", isDeveloper);
        
        // Si es desarrollador, obtener el usuario admin
        if (isDeveloper) {
            Optional<User> adminUser = userService.findByUsername("admin");
            adminUser.ifPresent(user -> model.addAttribute("adminUser", user));
        }
        
        return "admin/change-password";
    }

    /**
     * Procesar cambio de contraseña
     */
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        try {
            // Validar que las contraseñas coincidan
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/admin/users/change-password";
            }
            
            // Validar longitud mínima
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                return "redirect:/admin/users/change-password";
            }
            
            // Validar fortaleza de contraseña (letras, números y símbolos)
            if (!isPasswordStrong(newPassword)) {
                redirectAttributes.addFlashAttribute("error", 
                    "La contraseña debe combinar letras, números y símbolos para mayor seguridad");
                return "redirect:/admin/users/change-password";
            }
            
            // Cambiar contraseña
            try {
                userService.changePassword(currentUser.getId(), currentPassword, newPassword);
                redirectAttributes.addFlashAttribute("success", "Contraseña cambiada exitosamente");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Contraseña actual incorrecta");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la contraseña: " + e.getMessage());
        }
        
        return "redirect:/admin/users/change-password";
    }
    
    /**
     * Procesar cambio de contraseña del administrador (solo para desarrollador)
     */
    @PostMapping("/change-admin-password")
    public String changeAdminPassword(
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Solo SUPER_ADMIN con email lucerogustavosi@gmail.com puede cambiar contraseña del admin
        boolean isDeveloper = currentUser.getRole().equals(User.Role.SUPER_ADMIN) && 
                             "lucerogustavosi@gmail.com".equals(currentUser.getEmail());
        
        if (!isDeveloper) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para esta acción");
            return "redirect:/admin/users/change-password";
        }
        
        try {
            // Obtener usuario admin
            User adminUser = userService.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("Usuario administrador no encontrado"));
            
            // Validar que las contraseñas coincidan
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/admin/users/change-password";
            }
            
            // Validar longitud mínima
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                return "redirect:/admin/users/change-password";
            }
            
            // Validar fortaleza de contraseña (letras, números y símbolos)
            if (!isPasswordStrong(newPassword)) {
                redirectAttributes.addFlashAttribute("error", 
                    "La contraseña debe combinar letras, números y símbolos para mayor seguridad");
                return "redirect:/admin/users/change-password";
            }
            
            // Cambiar contraseña del admin
            userService.changePasswordByAdmin(adminUser.getId(), newPassword);
            redirectAttributes.addFlashAttribute("success", 
                "Contraseña del administrador cambiada exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la contraseña del admin: " + e.getMessage());
        }
        
        return "redirect:/admin/users/change-password";
    }
    
    /**
     * Validar fortaleza de contraseña
     * Debe tener al menos: letras, números y símbolos
     */
    private boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        boolean hasLetters = password.matches(".*[a-zA-Z].*");
        boolean hasNumbers = password.matches(".*[0-9].*");
        boolean hasSymbols = password.matches(".*[^a-zA-Z0-9].*");
        
        // Debe cumplir al menos 3 de 4 criterios (longitud ya está validada)
        int criteriaMet = 0;
        if (hasLetters) criteriaMet++;
        if (hasNumbers) criteriaMet++;
        if (hasSymbols) criteriaMet++;
        
        return criteriaMet >= 2; // Al menos 2 de 3 criterios (letras, números, símbolos)
    }

    /**
     * Resetear contraseña de otro usuario (solo SUPER_ADMIN)
     */
    @PostMapping("/reset-password/{userId}")
    public String resetPassword(
            @PathVariable Long userId,
            @RequestParam("newPassword") String newPassword,
            RedirectAttributes redirectAttributes) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Solo SUPER_ADMIN puede resetear contraseñas
        if (!currentUser.getRole().equals(User.Role.SUPER_ADMIN)) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para esta acción");
            return "redirect:/admin/users";
        }
        
        try {
            // Validar longitud mínima
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                return "redirect:/admin/users";
            }
            
            // Validar fortaleza de contraseña (letras, números y símbolos)
            if (!isPasswordStrong(newPassword)) {
                redirectAttributes.addFlashAttribute("error", 
                    "La contraseña debe combinar letras, números y símbolos para mayor seguridad");
                return "redirect:/admin/users";
            }
            
            // Resetear contraseña
            userService.changePasswordByAdmin(userId, newPassword);
            redirectAttributes.addFlashAttribute("success", "Contraseña reseteada exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al resetear la contraseña: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    /**
     * Activar/Desactivar usuario (solo SUPER_ADMIN)
     */
    @PostMapping("/toggle-status/{userId}")
    public String toggleUserStatus(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Solo SUPER_ADMIN puede cambiar estado de usuarios
        if (!currentUser.getRole().equals(User.Role.SUPER_ADMIN)) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para esta acción");
            return "redirect:/admin/users";
        }
        
        try {
            userService.toggleUserStatus(userId);
            redirectAttributes.addFlashAttribute("success", "Estado del usuario actualizado");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el usuario: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }
}
