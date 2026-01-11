package com.bjfu.paperSystem.mailUtils.Dao;

import com.bjfu.paperSystem.javabeans.EmailMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface mailDao extends JpaRepository<EmailMessage, Integer> {
    List<EmailMessage> findBySenderEmailOrderBySendingTimeDesc(String sender_mail);

    List<EmailMessage> findByReceiverEmailOrderBySendingTimeDesc(String receiverEmail);
    
    /**
     * 根据稿件ID和接收者邮箱查询审稿意见（审稿人发给编辑的意见）
     */
    @Query("SELECT e FROM EmailMessage e WHERE e.manuId = :manuId AND e.receiverEmail = :receiverEmail ORDER BY e.sendingTime DESC")
    List<EmailMessage> findByManuIdAndReceiverEmail(@Param("manuId") Integer manuId, @Param("receiverEmail") String receiverEmail);
}
