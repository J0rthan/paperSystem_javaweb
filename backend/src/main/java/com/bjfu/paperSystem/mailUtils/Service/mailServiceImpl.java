package com.bjfu.paperSystem.mailUtils.Service;

import com.bjfu.paperSystem.javabeans.EmailMessage;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.mailUtils.Dao.mailDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class mailServiceImpl implements mailService{
    @Autowired
    mailDao mailDao1;

    public String insertRecord(String senderEmail, String receiverEmail, String messageBody, int manuId, Manuscript manu) {
        EmailMessage emailMes = new EmailMessage();
        emailMes.setSenderEmail(senderEmail);
        emailMes.setReceiverEmail(receiverEmail);
        emailMes.setMessageBody(messageBody);
        emailMes.setManuId(manuId);
        emailMes.setManuscript(manu);
        emailMes.setSendingTime(LocalDateTime.now());

        mailDao1.save(emailMes);

        return "ok";
    }
}
