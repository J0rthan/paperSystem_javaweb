package com.bjfu.paperSystem.author.dao;
import com.bjfu.paperSystem.javabeans.EmailMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface EmailMessageDao extends JpaRepository<EmailMessage, Integer> {
    List<EmailMessage> findByReceiverEmailOrderByEmailMesIdDesc(String email);
}