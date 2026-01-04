package com.bjfu.paperSystem.mailUtils.Dao;

import com.bjfu.paperSystem.javabeans.EmailMessage;
import com.bjfu.paperSystem.javabeans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface mailDao extends JpaRepository<EmailMessage, Integer> {
}
