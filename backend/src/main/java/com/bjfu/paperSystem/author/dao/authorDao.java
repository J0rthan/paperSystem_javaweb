package com.bjfu.paperSystem.author.dao;

import com.bjfu.paperSystem.javabeans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface authorDao extends JpaRepository<User, Integer> {
    User findByUserName(String username);
}