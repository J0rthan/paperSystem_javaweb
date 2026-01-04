package com.bjfu.paperSystem.mailUtils.Service;

import com.bjfu.paperSystem.javabeans.Manuscript;

public interface mailService {
    public String insertRecord(String senderEmail, String receiverEmail, String messageBody, int manuId, Manuscript manu);
}
