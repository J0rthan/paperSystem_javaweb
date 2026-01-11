package com.bjfu.paperSystem.superAdmin.dao;

import com.bjfu.paperSystem.javabeans.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface permissionManageDao extends JpaRepository<Permission, Integer> {
    // 通过关联对象 user 的 userId 查 Permission
    Optional<Permission> findByUser_UserId(int userId);
}
