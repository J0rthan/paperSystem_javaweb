package com.bjfu.paperSystem.author.dao;
import com.bjfu.paperSystem.javabeans.EmailMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface EmailMessageDao extends JpaRepository<EmailMessage, Integer> {
    @Query("SELECT e FROM EmailMessage e JOIN FETCH e.manuscript WHERE e.receiverEmail = :email ORDER BY e.emailMesId DESC")
    List<EmailMessage> findByReceiverEmailWithManuscript(@Param("email") String email);
}