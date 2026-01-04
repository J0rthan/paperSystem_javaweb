package com.bjfu.paperSystem.clientMessageUtils.Dao;

import com.bjfu.paperSystem.javabeans.EmailMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface clientMessageDao extends JpaRepository<EmailMessage, Integer> {

}
