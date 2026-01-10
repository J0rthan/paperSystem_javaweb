package com.bjfu.paperSystem.editor.dao;

import com.bjfu.paperSystem.javabeans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EditorDao extends JpaRepository<User, Integer> {
    List<User> findByStatus(String status);
}