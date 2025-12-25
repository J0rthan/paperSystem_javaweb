package com.bjfu.paperSystem.chiefEditor.dao;

import com.bjfu.paperSystem.javabeans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiefEditorUserDao extends JpaRepository<User, Integer> {
    List<User> findByStatus(String status);

    List<User> findByUserTypeIgnoreCase(String userType);
}




