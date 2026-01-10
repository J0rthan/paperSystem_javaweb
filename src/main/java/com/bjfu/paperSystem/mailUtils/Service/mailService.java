package com.bjfu.paperSystem.mailUtils.Service;

import com.bjfu.paperSystem.javabeans.ClientMessage;
import com.bjfu.paperSystem.javabeans.EmailMessage;
import com.bjfu.paperSystem.javabeans.Manuscript;

import java.util.List;

public interface mailService {
    public String insertRecord(String senderEmail, String receiverEmail, String messageBody, int manuId, Manuscript manu);

    List<EmailMessage> findMessagesBySenderMail(String sender_mail);

    List<EmailMessage> findMessagesByReceiverMail(String receiver_mail);
}
