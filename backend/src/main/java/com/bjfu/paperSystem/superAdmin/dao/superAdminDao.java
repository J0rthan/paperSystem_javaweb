package com.bjfu.paperSystem.superAdmin.dao;

import com.bjfu.paperSystem.Login.javabeans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface superAdminDao extends JpaRepository<User, Integer> {
}