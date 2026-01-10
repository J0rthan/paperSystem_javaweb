package com.bjfu.paperSystem.editor.service;

import com.bjfu.paperSystem.author.dao.LogsDao;
import com.bjfu.paperSystem.javabeans.*;
import com.bjfu.paperSystem.editor.dao.*;
import com.bjfu.paperSystem.author.service.logService;
import com.bjfu.paperSystem.mailUtils.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bjfu.paperSystem.editor.dao.DecisionHistoryDao; // 确保引用路径正确
import com.bjfu.paperSystem.javabeans.DecisionHistory;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EditorProcessServiceImpl implements EditorProcessService {

    @Autowired private EditorManuscriptDao manuscriptDao;
    @Autowired private EditorUserDao userDao;
    @Autowired private EditorReviewDao reviewDao;
    @Autowired private logService logService;
    @Autowired private MailUtil mailUtil;
    @Autowired private DecisionHistoryDao decisionHistoryRepository;
    @Autowired private LogsDao logsDao;

    @Override
    public Manuscript getManuscriptDetail(int id, int editorId) {
        return manuscriptDao.findById(id).orElse(null);
    }

    @Override
    public List<User> getAvailableReviewers() {
        return userDao.findByUserTypeIgnoreCase("reviewer");
    }

    @Override
    public List<User> searchReviewers(String keyword) {
        List<User> all = userDao.findByUserTypeIgnoreCase("reviewer");
        if (keyword == null || keyword.trim().isEmpty()) {
            return all;
        }
        String key = keyword.toLowerCase();
        return all.stream()
                .filter(u -> (u.getFullName() != null && u.getFullName().toLowerCase().contains(key)) ||
                        (u.getInvestigationDirection() != null && u.getInvestigationDirection().toLowerCase().contains(key)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> getCurrentReviews(int manuscriptId) {
        // 获取该稿件所有的审稿记录
        return reviewDao.findByManuId(manuscriptId);
    }

    @Override
    public void sendMessageToAuthor(int manuscriptId, String message, int editorId) {
        // 简单实现：记录日志作为消息，或者你有专门的 Message 表
        logService.record(editorId, "MSG_TO_AUTHOR: " + message, manuscriptId);
    }

    @Override
    public List<Logs> getCommunicationHistory(int manuscriptId) {
        // 假设 logService 有获取日志的方法，或者返回空
        return Collections.emptyList();
    }

    /**
     * 核心功能：邀请审稿人
     */
    @Override
    @Transactional
    public void inviteReviewer(int manuscriptId, int reviewerId, LocalDateTime deadline, int currentEditorId) {
        // 1. 检查是否已经邀请过 (排除已撤销 undo 的记录)
        List<Review> existing = reviewDao.findByManuId(manuscriptId);
        boolean isAlreadyInvited = existing.stream()
                .anyMatch(r -> r.getReviewerId() == reviewerId && !"undo".equals(r.getStatus()));

        if (isAlreadyInvited) {
            return; // 已经邀请且有效，直接返回
        }

        Manuscript manuscript = manuscriptDao.findById(manuscriptId).orElse(null);
        User reviewer = userDao.findById(reviewerId).orElse(null);
        User editor = userDao.findById(currentEditorId).orElse(null);

        if (manuscript == null || reviewer == null || editor == null) return;

        // 2. 创建 Review 记录
        Review review = new Review();
        review.setManuId(manuscriptId);
        review.setReviewerId(reviewerId);
        review.setStatus("pending"); // 初始状态
        review.setScore(0);
        review.setDeadline(deadline);
        review.setInvitationTime(LocalDateTime.now());

        reviewDao.save(review);

        // 3. 发送邮件
        String dateStr = deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String subject = "【Review Invitation】" + manuscript.getTitle();
        String content = String.format(
                "Dear %s,\n\n" +
                        "We invite you to review the manuscript: \"%s\".\n" +
                        "Abstract: %s\n\n" +
                        "Please accept or decline by %s.\n\n" +
                        "Editor: %s",
                reviewer.getFullName(),
                manuscript.getTitle(),
                manuscript.getAbstractText(),
                dateStr,
                editor.getFullName()
        );

        // 异步发送
        new Thread(() -> {
            try {
                if (reviewer.getEmail() != null) mailUtil.sendTextMail(reviewer.getEmail(), subject, content);
            } catch (Exception e) { e.printStackTrace(); }
        }).start();

        // 4. 记录日志
        logService.record(currentEditorId, "INVITE_REVIEWER: Invited " + reviewer.getFullName(), manuscriptId);
    }

    @Override
    public void checkAndUpdateManuscriptStatus(int manuscriptId) {
        // 1. 获取该稿件所有的审稿记录
        List<Review> reviews = reviewDao.findByManuId(manuscriptId);
        // 2. 统计状态为已接受的人数
        long validReviewerCount = reviews.stream()
                .filter(r -> "accepted".equalsIgnoreCase(r.getStatus())).count();

        // 3. 获取稿件当前信息
        Manuscript manuscript = manuscriptDao.findById(manuscriptId).orElse(null);
        // 4. 核心判断逻辑：
        if (manuscript != null && validReviewerCount >= 3 && "With Editor".equalsIgnoreCase(manuscript.getStatus())) {
            manuscript.setStatus("Under Review");
            manuscriptDao.save(manuscript);

            int editorId = manuscript.getEditorId();
            String action = "editor completed";

            Logs log = new Logs();
            log.setOporId(editorId);
            log.setPaperId(manuscriptId);
            log.setOpTime(LocalDateTime.now());
            log.setOpType(action);
            logsDao.save(log);
        }
    }

    /**
     * 核心功能：撤销邀请
     */
    @Override
    @Transactional
    public void revokeInvitation(int reviewId, int currentEditorId) {
        Review review = reviewDao.findById(reviewId).orElse(null);
        if (review == null) return;

        // 只能撤销 pending
        if ("pending".equals(review.getStatus())) {

            String oldStatus = review.getStatus();
            review.setStatus("undo"); // 软删除状态
            reviewDao.save(review);
            // 现在的写法：只传操作简码
            logService.record(currentEditorId, "REVOKE_INVITE", review.getManuId());
        }
    }

    @Override
    @Transactional
    public void sendManualReminder(int reviewId, int editorId) {
        // 1. 获取 Review 信息
        Review review = reviewDao.findById(reviewId).orElse(null);
        if (review == null) return;

        // 2. 获取审稿人和稿件信息
        User reviewer = userDao.findById(review.getReviewerId()).orElse(null);
        Manuscript manuscript = manuscriptDao.findById(review.getManuId()).orElse(null);

        if (reviewer != null && manuscript != null) {
            // 3. 构建纯文本邮件内容 (使用 \n 换行)
            String deadlineStr = (review.getDeadline() != null) ?
                    review.getDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "近期";

            String subject = "[催审提醒] 请尽快完成稿件审阅 - " + manuscript.getTitle();

            String content = "尊敬的 " + reviewer.getFullName() + " 老师：\n\n"
                    + "您好！\n"
                    + "您接受审阅的稿件 《" + manuscript.getTitle() + "》 (ID: " + manuscript.getManuscriptId() + ") \n"
                    + "截止日期为： " + deadlineStr + "。\n"
                    + "编辑部注意到尚未收到您的审稿意见，请您在百忙之中抽出时间尽快登录系统完成审阅。\n\n"
                    + "感谢您的支持！\n"
                    + "此致\n"
                    + "编辑部";

            // 4. 发送邮件 (使用现有的 sendTextMail)
            try {
                mailUtil.sendTextMail(reviewer.getEmail(), subject, content);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("催审邮件发送失败: " + e.getMessage());
            }
        }
    }

    // 【实现该方法】
    @Override
    @Transactional // 务必加上事务注解
    public void submitRecommendation(int manuscriptId, String recommendation, String comment, int editorId) {
        // 1. 获取稿件
        Manuscript manu = manuscriptDao.findById(manuscriptId).orElse(null);
        if (manu == null) return;

        // 2. 获取编辑用户
        User editor = userDao.findById(editorId).orElse(null);

        // 3. 【核心逻辑】创建并保存历史记录 (DecisionHistory)
        DecisionHistory history = new DecisionHistory();
        history.setManuscript(manu);
        // 如果 manuscript 表里有 round 字段，用 manu.getRound()，没有则默认 1
        history.setRound(manu.getRound() == null ? 1 : manu.getRound());
        history.setEditorRecommendation(recommendation);
        history.setEditorComment(comment);
        history.setDecider(editor);
        history.setDecisionDate(new Date());

        // 保存历史存档
        decisionHistoryRepository.save(history);

        // 4. 【核心逻辑】更新 Manuscript 快照信息
        manu.setEditorRecommendation(recommendation);
        manu.setEditorComment(comment);

        // 关键：状态流转！
        // 建议提交后，稿件应该进入 "PENDING_DECISION" (待主编终审) 状态
        manu.setStatus("With Editor II");

        manuscriptDao.save(manu);
    }
}