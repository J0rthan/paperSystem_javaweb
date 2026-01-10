package com.bjfu.paperSystem.mailUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailUtil {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public MailUtil(JavaMailSender mailSender) {  // 构造注入（推荐）
        this.mailSender = mailSender;
    }

    public void sendTextMail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
        } catch (Exception e) {
            // 记录邮件发送失败的异常，但不向上抛出，避免影响主流程
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
