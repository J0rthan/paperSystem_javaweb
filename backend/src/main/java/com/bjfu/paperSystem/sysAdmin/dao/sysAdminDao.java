package com.bjfu.paperSystem.sysAdmin.dao;

import com.bjfu.paperSystem.javabeans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface sysAdminDao extends JpaRepository<User, Integer> {
    List<User> findByStatus(String status);
}