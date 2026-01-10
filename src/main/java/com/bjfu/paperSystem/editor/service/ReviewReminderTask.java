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
import java.time.temporal.ChronoUnit;
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
        int remindCount = 0;

        for (Review review : allReviews) {
            // 筛选：状态为 accepted 且有截止时间
            if (!"accepted".equalsIgnoreCase(review.getStatus()) || review.getDeadline() == null) {
                continue;
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime deadline = review.getDeadline();

            // 检查是否已逾期
            if (now.isAfter(deadline)) {
                // 计算逾期天数
                long overdueDays = ChronoUnit.DAYS.between(deadline.toLocalDate(), now.toLocalDate());
                
                // 只有在逾期天数 >= 7 天时才发送自动提醒
                if (overdueDays >= 7) {
                    sendAutoTextEmail(review, overdueDays);
                    remindCount++;
                }
            }
        }
        
        System.out.println("====== [自动催审任务结束] 共发送 " + remindCount + " 封提醒邮件 ======");
    }

    /**
     * 发送自动催审邮件（逾期7天及以上）
     * @param review 审稿任务
     * @param overdueDays 逾期天数
     */
    private void sendAutoTextEmail(Review review, long overdueDays) {
        try {
            User reviewer = userDao.findById(review.getReviewerId()).orElse(null);
            Manuscript manuscript = manuscriptDao.findById(review.getManuId()).orElse(null);

            if (reviewer != null && manuscript != null) {
                String subject = "[逾期7天自动提醒] 请尽快完成稿件审阅 - " + manuscript.getTitle();
                
                // 格式化截止日期
                String deadlineStr = review.getDeadline().toString().replace("T", " ");

                // 纯文本内容，包含逾期天数信息
                String content = "尊敬的 " + reviewer.getFullName() + " 老师：\n\n"
                        + "这是系统的自动提醒邮件（逾期7天自动提醒）。\n\n"
                        + "您负责审阅的稿件《" + manuscript.getTitle() + "》(ID: #MS-" + manuscript.getManuscriptId() + ")\n"
                        + "截止时间：" + deadlineStr + "\n"
                        + "目前已经逾期 " + overdueDays + " 天。\n\n"
                        + "编辑部注意到尚未收到您的审稿意见，请您在百忙之中抽出时间尽快登录系统完成审阅。\n\n"
                        + "感谢您的支持！\n\n"
                        + "此致\n"
                        + "敬礼\n\n"
                        + "Paper System 自动通知系统";

                // 使用 sendTextMail
                mailUtil.sendTextMail(reviewer.getEmail(), subject, content);

                System.out.println("已向 " + reviewer.getEmail() + " 发送自动催审邮件 (ReviewID: " + review.getReviewId() + ", 逾期天数: " + overdueDays + " 天)");
            }
        } catch (Exception e) {
            System.err.println("自动催审邮件发送失败 (ReviewID: " + review.getReviewId() + "): " + e.getMessage());
            e.printStackTrace();
        }
    }
}