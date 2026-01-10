package com.bjfu.paperSystem.editor.dao;

import com.bjfu.paperSystem.javabeans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EditorUserDao extends JpaRepository<User, Integer> {
    // 查找特定角色的用户（用于找 Reviewer）
    List<User> findByUserTypeIgnoreCase(String userType);
    
    // 根据用户名查找用户（用于个人信息管理）
    User findByUserName(String userName);
}