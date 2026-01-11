package com.bjfu.paperSystem.author.dao;
import com.bjfu.paperSystem.javabeans.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface PermissionDao extends JpaRepository<Permission, Integer> {
    Optional<Permission> findByUser_UserId(int userId);
}