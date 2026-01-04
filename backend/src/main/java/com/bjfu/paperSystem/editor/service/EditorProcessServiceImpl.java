package com.bjfu.paperSystem.editor.service;

import com.bjfu.paperSystem.javabeans.*;
import com.bjfu.paperSystem.editor.dao.*;
import com.bjfu.paperSystem.author.service.logService;
import com.bjfu.paperSystem.mailUtils.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void submitRecommendation(int manuscriptId, String recommendation, String comment, int editorId) {
        Manuscript m = manuscriptDao.findById(manuscriptId).orElse(null);
        if (m != null) {
            m.setDecision(recommendation); // 这里存的是编辑建议，最终决议可能在别处
            // 如果是最终决议，可以改状态： m.setStatus("Decision Made");
            manuscriptDao.save(m);
            logService.record(editorId, "EDITOR_DECISION: " + recommendation, manuscriptId);
        }
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
        // 1. 检查是否已经邀请过 (排除已撤销 CANCELLED 的记录)
        List<Review> existing = reviewDao.findByManuId(manuscriptId);
        boolean isAlreadyInvited = existing.stream()
                .anyMatch(r -> r.getReviewerId() == reviewerId && !"CANCELLED".equals(r.getStatus()));

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
        review.setStatus("PENDING"); // 初始状态
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

    /**
     * 核心功能：撤销邀请
     */
    @Override
    @Transactional
    public void revokeInvitation(int reviewId, int currentEditorId) {
        Review review = reviewDao.findById(reviewId).orElse(null);
        if (review == null) return;

        // 只能撤销 PENDING 或 ACCEPTED
        if ("PENDING".equals(review.getStatus()) || "ACCEPTED".equals(review.getStatus())) {

            String oldStatus = review.getStatus();
            review.setStatus("CANCELLED"); // 软删除状态
            reviewDao.save(review);

            // === 修复点：日志内容缩短，防止数据库报错 ===
            // 原来的写法太长了，超过了 op_type 的数据库限制
            // logService.record(currentEditorId, "REVOKE_INVITE: Revoked invitation...", review.getManuId());

            // 现在的写法：只传操作简码
            logService.record(currentEditorId, "REVOKE_INVITE", review.getManuId());
        }
    }
}