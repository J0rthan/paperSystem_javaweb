package com.bjfu.paperSystem.reviewer.dao;

import com.bjfu.paperSystem.javabeans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface reviewerDao extends JpaRepository<User, Integer> {
    List<User> findByStatus(String status);
}