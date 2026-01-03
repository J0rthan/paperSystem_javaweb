package com.bjfu.paperSystem.mailUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailUtil {
    private final JavaMailSender mailSender;
    private final String from;

    public MailUtil(JavaMailSender mailSender,
                    @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void sendTextMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}
