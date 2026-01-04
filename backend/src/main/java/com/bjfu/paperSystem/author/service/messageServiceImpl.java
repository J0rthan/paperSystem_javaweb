package com.bjfu.paperSystem.author.service;
import com.bjfu.paperSystem.author.dao.EmailMessageDao;
import com.bjfu.paperSystem.clientMessageUtils.Service.clientMessageService;
import com.bjfu.paperSystem.javabeans.ClientMessage;
import com.bjfu.paperSystem.javabeans.EmailMessage;
import com.bjfu.paperSystem.javabeans.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class messageServiceImpl implements messageService {
    @Autowired
    private EmailMessageDao emailMessageDao;
    @Autowired
    private clientMessageService clientMsgService;
    @Override
    public Map<String, Object> getAuthorMessageCenter(User loginUser) {
        Map<String, Object> data = new HashMap<>();
        List<ClientMessage> clientMsgs = clientMsgService.findMessageByReceiver(loginUser.getUserId());
        List<EmailMessage> allEmailMsgs = emailMessageDao.findByReceiverEmailWithManuscript(loginUser.getEmail());
        List<EmailMessage> filteredEmails = allEmailMsgs.stream()
                .filter(email -> email.getManuscript() != null &&
                        email.getManuscript().getAuthorId() == loginUser.getUserId())
                .toList();
        data.put("clientMsgs", clientMsgs);
        data.put("emailMsgs", filteredEmails);
        return data;
    }
}