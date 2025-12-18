package com.bjfu.paperSystem.dao;

import com.bjfu.paperSystem.javabeans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    Optional<User> findByUserNameAndPassword(String userName, String password);
}
