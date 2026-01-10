package com.bjfu.paperSystem.editor.service;

import com.bjfu.paperSystem.editor.dao.EditorManuscriptDao;
import com.bjfu.paperSystem.editor.dao.EditorReviewDao;
import com.bjfu.paperSystem.editor.dao.EditorUserDao;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.Review;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.mailUtils.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReviewReminderTask {

    @Autowired private EditorReviewDao reviewDao;
    @Autowired private EditorUserDao userDao;
    @Autowired private EditorManuscriptDao manuscriptDao;
    @Autowired private MailUtil mailUtil;

    // 每天凌晨 2 点执行
    @Scheduled(cron = "0 0 2 * * ?")
    public void autoRemindReviewers() {
        System.out.println("====== [自动催审任务开始] " + LocalDateTime.now() + " ======");

        List<Review> allReviews = reviewDao.findAll();

        for (Review review : allReviews) {
            // 筛选：状态为 accepted 且有截止时间
            if (!"accepted".equalsIgnoreCase(review.getStatus()) || review.getDeadline() == null) {
                continue;
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime deadline = review.getDeadline();

            boolean isOverdue = now.isAfter(deadline);
            boolean isApproaching = deadline.isAfter(now) && deadline.isBefore(now.plusDays(7));

            if (isOverdue || isApproaching) {
                sendAutoTextEmail(review, isOverdue);
            }
        }
        System.out.println("====== [自动催审任务结束] ======");
    }

    private void sendAutoTextEmail(Review review, boolean isOverdue) {
        try {
            User reviewer = userDao.findById(review.getReviewerId()).orElse(null);
            Manuscript manuscript = manuscriptDao.findById(review.getManuId()).orElse(null);

            if (reviewer != null && manuscript != null) {
                String typeStr = isOverdue ? "[逾期提醒]" : "[即将截止]";
                String subject = typeStr + " 审稿任务提醒 - " + manuscript.getTitle();

                String statusText = isOverdue ? "已逾期" : "即将截止";

                // 纯文本内容
                String content = "尊敬的 " + reviewer.getFullName() + " 老师：\n\n"
                        + "这是系统的自动提醒邮件。\n"
                        + "您负责审阅的稿件 《" + manuscript.getTitle() + "》 目前处于 " + statusText + " 状态。\n"
                        + "截止时间：" + review.getDeadline().toString().replace("T", " ") + "\n\n"
                        + "请您尽快登录系统提交审稿意见。\n\n"
                        + "Paper System 自动通知";

                // 使用 sendTextMail
                mailUtil.sendTextMail(reviewer.getEmail(), subject, content);

                System.out.println("已向 " + reviewer.getEmail() + " 发送自动催审邮件 (ReviewID: " + review.getReviewId() + ")");
            }
        } catch (Exception e) {
            System.err.println("自动催审邮件发送失败: " + e.getMessage());
        }
    }
}