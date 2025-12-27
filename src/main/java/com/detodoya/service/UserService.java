package com.detodoya.service;

import com.detodoya.entity.User;
import com.detodoya.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Configuración de seguridad
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int PASSWORD_EXPIRY_DAYS = 90;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        
        // Verificar si la cuenta está bloqueada
        if (user.getAccountLocked()) {
            throw new UsernameNotFoundException("Cuenta bloqueada: " + username);
        }
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities("ROLE_" + user.getRole().name())
            .accountExpired(false)
            .accountLocked(user.getAccountLocked())
            .credentialsExpired(user.getMustChangePassword())
            .disabled(!user.getIsActive())
            .build();
    }
    
    /**
     * Crear nuevo usuario
     */
    public User createUser(User user) {
        // Validar que el username y email no existan
        if (existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe: " + user.getUsername());
        }
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El email ya existe: " + user.getEmail());
        }
        
        // Encriptar contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setMustChangePassword(false);
        user.setLoginAttempts(0);
        user.setAccountLocked(false);
        
        return userRepository.save(user);
    }
    
    /**
     * Actualizar perfil de usuario
     */
    public User updateProfile(Long userId, User userData) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
        
        // Validar que el nuevo email no exista en otro usuario
        if (!existingUser.getEmail().equals(userData.getEmail()) && 
            existsByEmail(userData.getEmail())) {
            throw new IllegalArgumentException("El email ya existe: " + userData.getEmail());
        }
        
        // Actualizar campos permitidos
        existingUser.setFullName(userData.getFullName());
        existingUser.setEmail(userData.getEmail());
        existingUser.setPhone(userData.getPhone());
        existingUser.setAddress(userData.getAddress());
        existingUser.setCity(userData.getCity());
        existingUser.setCountry(userData.getCountry());
        
        return userRepository.save(existingUser);
    }
    
    /**
     * Cambiar contraseña
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
        
        // Verificar contraseña actual
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        
        // Validar nueva contraseña
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
        }
        
        // Cambiar contraseña
        user.changePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * Cambiar contraseña por administrador
     */
    public void changePasswordByAdmin(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
        
        // Validar nueva contraseña
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
        }
        
        // Cambiar contraseña
        user.changePassword(passwordEncoder.encode(newPassword));
        // NO establecer mustChangePassword = true porque esto bloquea el login
        // Si el desarrollador está restaurando acceso, el admin debe poder ingresar inmediatamente
        user.setMustChangePassword(false);
        userRepository.save(user);
    }
    
    /**
     * Actualizar último login
     */
    public void updateLastLogin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        
        user.updateLastLogin();
        userRepository.save(user);
    }
    
    /**
     * Incrementar intentos de login fallidos
     */
    public void incrementLoginAttempts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        
        user.incrementLoginAttempts();
        
        // Bloquear cuenta si excede el límite
        if (user.getLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
            user.lockAccount();
        }
        
        userRepository.save(user);
    }
    
    /**
     * Desbloquear cuenta
     */
    public void unlockAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
        
        user.unlockAccount();
        userRepository.save(user);
    }
    
    /**
     * Activar/Desactivar usuario
     */
    public User toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
        
        user.setIsActive(!user.getIsActive());
        return userRepository.save(user);
    }
    
    /**
     * Obtener usuario por ID
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Obtener usuario por username
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Obtener usuario por email
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Obtener todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Obtener usuarios paginados
     */
    @Transactional(readOnly = true)
    public Page<User> getUsersPaginated(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    /**
     * Obtener usuarios activos
     */
    @Transactional(readOnly = true)
    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }
    
    /**
     * Obtener usuarios bloqueados
     */
    @Transactional(readOnly = true)
    public List<User> getLockedUsers() {
        return userRepository.findByAccountLockedTrue();
    }
    
    /**
     * Verificar si existe username
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Verificar si existe email
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Verificar si existe username excluyendo un ID
     */
    @Transactional(readOnly = true)
    public boolean existsByUsernameAndIdNot(String username, Long id) {
        return userRepository.existsByUsernameAndIdNot(username, id);
    }
    
    /**
     * Verificar si existe email excluyendo un ID
     */
    @Transactional(readOnly = true)
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return userRepository.existsByEmailAndIdNot(email, id);
    }
    
    /**
     * Buscar usuarios por texto
     */
    @Transactional(readOnly = true)
    public List<User> searchUsers(String searchText) {
        return userRepository.searchUsers(searchText);
    }
    
    /**
     * Obtener estadísticas de usuarios
     */
    @Transactional(readOnly = true)
    public Object[] getUserStatistics() {
        return userRepository.getUserStatistics();
    }
    
    /**
     * Validar contraseña
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * Verificar si la contraseña ha expirado
     */
    public boolean isPasswordExpired(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
        
        return user.isPasswordExpired(PASSWORD_EXPIRY_DAYS);
    }
    
    /**
     * Crear usuario administrador por defecto
     */
    public void createDefaultAdmin() {
        if (userRepository.count() == 0) {
            User admin = new User("admin", "admin@orioladenim.com.ar", "admin123", "Administrador ORIOLA");
            admin.setRole(User.Role.SUPER_ADMIN);
            admin.setFullName("Administrador ORIOLA");
            admin.setPhone("+54 9 11 1234-5678");
            admin.setCity("Buenos Aires");
            admin.setCountry("Argentina");
            createUser(admin);
        }
    }
}

