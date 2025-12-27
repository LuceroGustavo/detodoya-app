package com.orioladenim.repo;

import com.orioladenim.entity.BackupInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BackupInfoRepository extends JpaRepository<BackupInfo, Long> {
    
    List<BackupInfo> findByCreatedByOrderByCreatedAtDesc(String createdBy);
    
    List<BackupInfo> findByBackupTypeOrderByCreatedAtDesc(String backupType);
    
    @Query("SELECT b FROM BackupInfo b ORDER BY b.createdAt DESC")
    List<BackupInfo> findAllOrderByCreatedAtDesc();
    
    @Query("SELECT b FROM BackupInfo b WHERE b.createdAt >= ?1 ORDER BY b.createdAt DESC")
    List<BackupInfo> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime date);
    
    void deleteByCreatedAtBefore(LocalDateTime date);
    
    long countByCreatedBy(String createdBy);
}
