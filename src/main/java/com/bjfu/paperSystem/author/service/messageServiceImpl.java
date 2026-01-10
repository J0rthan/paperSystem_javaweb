package com.bjfu.paperSystem.author.service;
import com.bjfu.paperSystem.author.dao.EmailMessageDao;
import com.bjfu.paperSystem.clientMessageUtils.Service.clientMessageService;
import com.bjfu.paperSystem.javabeans.ClientMessage;
import com.bjfu.paperSystem.javabeans.EmailMessage;
import com.bjfu.paperSystem.javabeans.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
@Service
public class messageServiceImpl implements messageService {
    @Autowired
    private EmailMessageDao emailMessageDao;
    @Autowired
    private clientMessageService clientMsgService;
    @Override
    public Map<String, Object> getAuthorMessageCenter(User loginUser) {
        Map<String, Object> data = new HashMap<>();
        List<ClientMessage> receivedMsgs = clientMsgService.findMessageByReceiver(loginUser.getUserId());
        List<ClientMessage> sentMsgs = clientMsgService.findMessageBySender(loginUser.getUserId());
        List<ClientMessage> allClientMsgs = new ArrayList<>();
        if (receivedMsgs != null) allClientMsgs.addAll(receivedMsgs);
        if (sentMsgs != null) allClientMsgs.addAll(sentMsgs);
        List<ClientMessage> sortedClientMsgs = allClientMsgs.stream()
                .filter(msg -> msg.getSendingTime() != null)
                .sorted((m1, m2) -> m2.getSendingTime().compareTo(m1.getSendingTime()))
                .collect(Collectors.toList());
        List<EmailMessage> allEmailMsgs = emailMessageDao.findByReceiverEmailWithManuscript(loginUser.getEmail());
        List<EmailMessage> filteredEmails = allEmailMsgs.stream()
                .filter(email -> {
                    if (email.getManuscript() == null) return false;
                    return Objects.equals(email.getManuscript().getAuthorId(), loginUser.getUserId());
                })
                .collect(Collectors.toList());
        data.put("clientMsgs", sortedClientMsgs);
        data.put("emailMsgs", filteredEmails);
        return data;
    }
}