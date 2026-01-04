package com.bjfu.paperSystem.clientMessageUtils.Service;

import com.bjfu.paperSystem.clientMessageUtils.Dao.clientMessageDao;
import com.bjfu.paperSystem.javabeans.ClientMessage;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class clientMessageServiceImpl implements clientMessageService {
    @Autowired
    clientMessageDao clienDao;

    @Override
    public List<ClientMessage> findMessageBySender(Integer uid) {
        List<ClientMessage> list = clienDao.findSentWithUsers(uid);



        return list;
    }

    @Override
    public List<ClientMessage> findMessageByReceiver(Integer uid){
        List<ClientMessage> list = clienDao.findReceivedWithUsers(uid);

        return list;
    }

    @Override
    @Transactional
    public String insertMessage(Integer senderId, Integer receiverId, String messageBody, LocalDateTime sendingTime, Integer manuId) {
        ClientMessage message = new ClientMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setMessageBody(messageBody);
        message.setSendingTime(sendingTime);
        message.setManuId(manuId);

        clienDao.save(message);

        return "ok";
    }
}
