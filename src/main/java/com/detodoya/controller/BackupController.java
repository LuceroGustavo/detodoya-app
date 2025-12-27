package com.detodoya.controller;

import com.detodoya.entity.BackupInfo;
import com.detodoya.service.BackupService;
import com.detodoya.service.RestoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/backup")
@PreAuthorize("hasRole('ADMIN') or hasRole('DEVELOPER')")
public class BackupController {
    
    @Autowired
    private BackupService backupService;
    
    @Autowired
    private RestoreService restoreService;
    
    @GetMapping
    public String backupPage(Model model) {
        List<BackupInfo> backups = backupService.getAllBackups();
        model.addAttribute("backups", backups);
        return "admin/backup-management";
    }
    
    @PostMapping("/create/full")
    public String createFullBackup(
            @RequestParam(required = false) String description,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        
        try {
            String createdBy = principal != null ? principal.getName() : "system";
            String filePath = backupService.createFullBackup(createdBy, description);
            
            redirectAttributes.addFlashAttribute("success", 
                "Backup completo creado exitosamente: " + new File(filePath).getName());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al crear backup: " + e.getMessage());
        }
        
        return "redirect:/admin/backup";
    }
    
    @PostMapping("/create/data-only")
    public String createDataOnlyBackup(
            @RequestParam(required = false) String description,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        
        try {
            String createdBy = principal != null ? principal.getName() : "system";
            String filePath = backupService.createDataOnlyBackup(createdBy, description);
            
            redirectAttributes.addFlashAttribute("success", 
                "Backup de datos creado exitosamente: " + new File(filePath).getName());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al crear backup: " + e.getMessage());
        }
        
        return "redirect:/admin/backup";
    }
    
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadBackup(@PathVariable Long id) {
        try {
            File backupFile = backupService.getBackupFile(id);
            if (backupFile == null || !backupFile.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(backupFile);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + backupFile.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(backupFile.length())
                .body(resource);
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/restore")
    public String restoreBackup(
            @RequestParam("backupFile") MultipartFile backupFile,
            @RequestParam(defaultValue = "false") boolean clearExisting,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validar archivo
            if (!restoreService.validateBackupFile(backupFile)) {
                redirectAttributes.addFlashAttribute("error", 
                    "El archivo no es un backup válido");
                return "redirect:/admin/backup";
            }
            
            String restoredBy = principal != null ? principal.getName() : "system";
            String result = restoreService.restoreFromBackup(backupFile, restoredBy, clearExisting);
            
            redirectAttributes.addFlashAttribute("success", result);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al restaurar backup: " + e.getMessage());
        }
        
        return "redirect:/admin/backup";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteBackup(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            backupService.deleteBackup(id);
            redirectAttributes.addFlashAttribute("success", "Backup eliminado exitosamente");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al eliminar backup: " + e.getMessage());
        }
        
        return "redirect:/admin/backup";
    }
    
    // API REST endpoints
    @GetMapping("/api/list")
    @ResponseBody
    public List<BackupInfo> listBackups() {
        return backupService.getAllBackups();
    }
    
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<BackupInfo> getBackup(@PathVariable Long id) {
        Optional<BackupInfo> backup = backupService.getBackupById(id);
        return backup.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/api/create/full")
    @ResponseBody
    public ResponseEntity<String> createFullBackupApi(
            @RequestParam(required = false) String description,
            Principal principal) {
        
        try {
            String createdBy = principal != null ? principal.getName() : "system";
            String filePath = backupService.createFullBackup(createdBy, description);
            return ResponseEntity.ok("Backup creado: " + new File(filePath).getName());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/api/restore")
    @ResponseBody
    public ResponseEntity<String> restoreBackupApi(
            @RequestParam("backupFile") MultipartFile backupFile,
            @RequestParam(defaultValue = "false") boolean clearExisting,
            Principal principal) {
        
        try {
            if (!restoreService.validateBackupFile(backupFile)) {
                return ResponseEntity.badRequest().body("Archivo de backup inválido");
            }
            
            String restoredBy = principal != null ? principal.getName() : "system";
            String result = restoreService.restoreFromBackup(backupFile, restoredBy, clearExisting);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
