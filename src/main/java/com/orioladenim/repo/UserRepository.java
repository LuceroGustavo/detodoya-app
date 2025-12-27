package com.orioladenim.repo;

import com.orioladenim.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Consultas básicas
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    // Consultas de existencia con exclusión
    boolean existsByUsernameAndIdNot(String username, Long id);
    
    boolean existsByEmailAndIdNot(String email, Long id);
    
    // Consultas de filtrado
    List<User> findByIsActiveTrue();
    
    List<User> findByIsActiveFalse();
    
    List<User> findByAccountLockedTrue();
    
    List<User> findByAccountLockedFalse();
    
    List<User> findByRole(User.Role role);
    
    List<User> findByMustChangePasswordTrue();
    
    // Búsqueda por texto
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY u.fullName ASC")
    List<User> searchUsers(@Param("search") String search);
    
    // Consultas de estadísticas
    @Query("SELECT " +
           "COUNT(u) as totalUsers, " +
           "COUNT(CASE WHEN u.isActive = true THEN 1 END) as activeUsers, " +
           "COUNT(CASE WHEN u.isActive = false THEN 1 END) as inactiveUsers, " +
           "COUNT(CASE WHEN u.accountLocked = true THEN 1 END) as lockedUsers, " +
           "COUNT(CASE WHEN u.mustChangePassword = true THEN 1 END) as mustChangePasswordUsers, " +
           "COUNT(CASE WHEN u.role = 'ADMIN' THEN 1 END) as adminUsers, " +
           "COUNT(CASE WHEN u.role = 'SUPER_ADMIN' THEN 1 END) as superAdminUsers " +
           "FROM User u")
    Object[] getUserStatistics();
    
    // Consultas de usuarios recientes
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findRecentUsers();
    
    @Query("SELECT u FROM User u WHERE u.lastLogin IS NOT NULL ORDER BY u.lastLogin DESC")
    List<User> findUsersByLastLogin();
    
    // Consultas de usuarios por fecha
    @Query("SELECT u FROM User u WHERE u.createdAt >= :startDate AND u.createdAt <= :endDate ORDER BY u.createdAt DESC")
    List<User> findUsersByDateRange(@Param("startDate") java.time.LocalDateTime startDate, 
                                   @Param("endDate") java.time.LocalDateTime endDate);
    
    // Consultas de usuarios con contraseña expirada
    @Query("SELECT u FROM User u WHERE u.passwordChangedAt < :expiryDate ORDER BY u.passwordChangedAt ASC")
    List<User> findUsersWithExpiredPassword(@Param("expiryDate") java.time.LocalDateTime expiryDate);
    
    // Consultas de usuarios por ubicación
    @Query("SELECT u FROM User u WHERE LOWER(u.city) = LOWER(:city) ORDER BY u.fullName ASC")
    List<User> findByCity(@Param("city") String city);
    
    @Query("SELECT u FROM User u WHERE LOWER(u.country) = LOWER(:country) ORDER BY u.fullName ASC")
    List<User> findByCountry(@Param("country") String country);
    
    // Consultas de conteo
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    Long countActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.accountLocked = true")
    Long countLockedUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.mustChangePassword = true")
    Long countUsersMustChangePassword();
    
    // Consultas de usuarios por rol
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true ORDER BY u.fullName ASC")
    List<User> findActiveUsersByRole(@Param("role") User.Role role);
    
    // Consultas de usuarios con intentos de login
    @Query("SELECT u FROM User u WHERE u.loginAttempts > 0 ORDER BY u.loginAttempts DESC")
    List<User> findUsersWithLoginAttempts();
    
    // Consultas de usuarios por último login
    @Query("SELECT u FROM User u WHERE u.lastLogin IS NULL ORDER BY u.createdAt ASC")
    List<User> findUsersNeverLoggedIn();
    
    @Query("SELECT u FROM User u WHERE u.lastLogin < :date ORDER BY u.lastLogin ASC")
    List<User> findUsersNotLoggedInSince(@Param("date") java.time.LocalDateTime date);
}

