package com.bjfu.paperSystem.mailUtils.Dao;

import com.bjfu.paperSystem.javabeans.EmailMessage;
import com.bjfu.paperSystem.javabeans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface mailDao extends JpaRepository<EmailMessage, Integer> {
    List<EmailMessage> findBySenderEmailOrderBySendingTimeDesc(String sender_mail);

    List<EmailMessage> findByReceiverEmailOrderBySendingTimeDesc(String receiverEmail);
}
