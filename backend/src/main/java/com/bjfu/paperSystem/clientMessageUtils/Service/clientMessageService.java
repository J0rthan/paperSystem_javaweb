package com.bjfu.paperSystem.clientMessageUtils.Service;

import ch.qos.logback.core.net.server.Client;
import com.bjfu.paperSystem.javabeans.ClientMessage;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User;

import java.time.LocalDateTime;
import java.util.List;

public interface clientMessageService {
    List<ClientMessage> findMessageBySender(Integer uid);

    List<ClientMessage> findMessageByReceiver(Integer uid);

    // 插入消息，当有消息发送的时候
    String insertMessage(Integer clientMesId, Integer senderId, Integer receiverId, String messageBody, LocalDateTime sendingTime, Integer manuId);
}
