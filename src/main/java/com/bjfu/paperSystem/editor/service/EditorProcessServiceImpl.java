package com.bjfu.paperSystem.editor.service;

import com.bjfu.paperSystem.javabeans.*;
import com.bjfu.paperSystem.editor.dao.*;
import com.bjfu.paperSystem.author.service.logService;
import com.bjfu.paperSystem.mailUtils.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bjfu.paperSystem.editor.dao.DecisionHistoryDao;
import com.bjfu.paperSystem.javabeans.DecisionHistory;
import com.bjfu.paperSystem.javabeans.ClientMessage;
import com.bjfu.paperSystem.clientMessageUtils.Service.clientMessageService;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import com.bjfu.paperSystem.editor.dao.EditorRecordAllocationDao;

@Service
public class EditorProcessServiceImpl implements EditorProcessService {

    @Autowired private EditorManuscriptDao manuscriptDao;
    @Autowired private EditorUserDao userDao;
    @Autowired private EditorReviewDao reviewDao;
    @Autowired private logService logService;
    @Autowired private MailUtil mailUtil;
    @Autowired private DecisionHistoryDao decisionHistoryRepository;
    @Autowired private clientMessageService clientMsgService;
    @Autowired private EditorRecordAllocationDao recordAllocationDao;

    @Override
    public Manuscript getManuscriptDetail(int id, int editorId) {
        Manuscript manuscript = manuscriptDao.findById(id).orElse(null);
        if (manuscript == null) {
            return null;
        }

        // 权限验证：检查Editor是否被分配了该稿件
        // 方式1：通过 Record_Allocation 表验证
        List<Record_Allocation> allocations = recordAllocationDao.findByEditorId(editorId);
        boolean hasPermission = allocations.stream()
                .anyMatch(a -> a.getManuscriptId() == id);

        // 方式2：通过 manuscript.getEditorId() 验证（如果该字段有值）
        if (!hasPermission && (manuscript.getEditorId() == null || !manuscript.getEditorId().equals(editorId))) {
            return null; // 没有权限，返回null
        }

        return manuscript;
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
        // 2. 统计状态为 审稿中 或 已完成 的人数
        long validReviewerCount = reviews.stream()
                .filter(r -> "accepted".equalsIgnoreCase(r.getStatus())
                        || "finished".equalsIgnoreCase(r.getStatus()))
                .count();

        // 3. 获取稿件当前信息
        Manuscript manuscript = manuscriptDao.findById(manuscriptId).orElse(null);
        // 4. 核心判断逻辑：
        if (manuscript != null && validReviewerCount >= 3 && "With Editor".equalsIgnoreCase(manuscript.getStatus())) {
            manuscript.setStatus("Under Review");
            manuscriptDao.save(manuscript);
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

    @Override
    @Transactional
    public void sendMessageToAuthor(int manuscriptId, int authorId, String message, int editorId) {
        // 1. 权限验证：检查Editor是否被分配了该稿件
        Manuscript manuscript = manuscriptDao.findById(manuscriptId).orElse(null);
        if (manuscript == null || manuscript.getAuthorId() != authorId) {
            return; // 稿件不存在或作者ID不匹配，直接返回
        }

        // 2. 验证Editor权限（可选，如果已经在Controller层验证过可以省略）
        if (manuscript.getEditorId() == null || !manuscript.getEditorId().equals(editorId)) {
            // 也可以通过 Record_Allocation 表验证，这里简化处理
            return;
        }

        // 3. 保存消息到数据库
        clientMsgService.insertMessage(
                editorId,           // senderId: Editor的ID
                authorId,           // receiverId: 作者的ID
                message,            // messageBody: 消息内容
                LocalDateTime.now(), // sendingTime: 当前时间
                manuscriptId        // manuId: 关联的稿件ID
        );

        // 4. 记录日志（可选）
        logService.record(editorId, "MESSAGE_TO_AUTHOR: " + message, manuscriptId);
    }

    @Override
    public List<ClientMessage> getCommunicationHistory(int manuscriptId, int editorId) {
        // 权限验证：检查Editor是否被分配了该稿件
        Manuscript manuscript = manuscriptDao.findById(manuscriptId).orElse(null);
        if (manuscript == null) {
            return List.of();
        }

        // 权限验证：检查是否被分配（通过 editorId 字段或 Record_Allocation 表）
        // 这里简化处理，如果 manuscript.editorId 匹配就可以，否则需要查询 Record_Allocation
        if (manuscript.getEditorId() == null || !manuscript.getEditorId().equals(editorId)) {
            // 可以通过 Record_Allocation 表进一步验证，这里先返回空
            return List.of();
        }

        // 查询该稿件的所有消息
        List<ClientMessage> sent = clientMsgService.findMessageBySender(editorId);
        List<ClientMessage> received = clientMsgService.findMessageByReceiver(editorId);

        // 过滤出属于该稿件的消息（使用 Objects.equals 进行安全的比较）
        return java.util.stream.Stream.concat(sent.stream(), received.stream())
                .filter(msg -> msg.getManuId() != null && java.util.Objects.equals(msg.getManuId(), manuscriptId))
                .sorted((m1, m2) -> {
                    if (m1.getSendingTime() == null || m2.getSendingTime() == null) {
                        return 0;
                    }
                    return m1.getSendingTime().compareTo(m2.getSendingTime());
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getOverdueManuscripts(int editorId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取该编辑的所有稿件分配记录
        List<Record_Allocation> allocations = recordAllocationDao.findByEditorId(editorId);
        
        if (allocations.isEmpty()) {
            return result;
        }
        
        LocalDateTime now = LocalDateTime.now();
        Set<Integer> manuscriptIds = new HashSet<>();
        
        // 查找所有有逾期审稿人的稿件ID
        for (Record_Allocation allocation : allocations) {
            int manuId = allocation.getManuscriptId();
            List<Review> reviews = reviewDao.findByManuId(manuId);
            
            for (Review review : reviews) {
                if (review.getDeadline() != null &&
                        ("accepted".equalsIgnoreCase(review.getStatus()) || 
                         "pending".equalsIgnoreCase(review.getStatus()))) {
                    if (now.isAfter(review.getDeadline())) {
                        manuscriptIds.add(manuId);
                        break; // 该稿件至少有一个逾期审稿人，添加后退出内层循环
                    }
                }
            }
        }
        
        // 构建返回结果
        for (Integer manuId : manuscriptIds) {
            Manuscript manuscript = manuscriptDao.findById(manuId).orElse(null);
            if (manuscript != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("manuscriptId", manuId);
                item.put("title", manuscript.getTitle());
                result.add(item);
            }
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getOverdueReviewers(int manuscriptId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        List<Review> reviews = reviewDao.findByManuId(manuscriptId);
        LocalDateTime now = LocalDateTime.now();
        
        for (Review review : reviews) {
            // 只返回已逾期的审稿人
            if (review.getDeadline() != null &&
                    ("accepted".equalsIgnoreCase(review.getStatus()) || 
                     "pending".equalsIgnoreCase(review.getStatus()))) {
                if (now.isAfter(review.getDeadline())) {
                    User reviewer = userDao.findById(review.getReviewerId()).orElse(null);
                    
                    // 计算已逾期天数
                    long overdueDays = ChronoUnit.DAYS.between(
                            review.getDeadline().toLocalDate(), 
                            now.toLocalDate()
                    );
                    
                    Map<String, Object> item = new HashMap<>();
                    item.put("reviewId", review.getReviewId());
                    item.put("reviewerId", review.getReviewerId());
                    item.put("reviewerName", reviewer != null ? reviewer.getFullName() : "Unknown");
                    item.put("reviewerEmail", reviewer != null ? reviewer.getEmail() : "");
                    item.put("deadline", review.getDeadline());
                    item.put("overdueDays", overdueDays);
                    result.add(item);
                }
            }
        }
        
        return result;
    }

    @Override
    @Transactional
    public void sendCustomReminder(int reviewId, String subject, String customContent, int editorId) {
        // 1. 获取 Review 信息
        Review review = reviewDao.findById(reviewId).orElse(null);
        if (review == null) {
            throw new RuntimeException("审稿任务不存在");
        }

        // 2. 获取审稿人和稿件信息
        User reviewer = userDao.findById(review.getReviewerId()).orElse(null);
        Manuscript manuscript = manuscriptDao.findById(review.getManuId()).orElse(null);

        if (reviewer == null || manuscript == null) {
            throw new RuntimeException("审稿人或稿件信息不存在");
        }

        // 3. 替换占位符（如果需要）
        String finalContent = replacePlaceholders(customContent, reviewer, manuscript, review);
        String finalSubject = subject != null && !subject.trim().isEmpty() ? 
                subject : "[催审提醒] 请尽快完成稿件审阅 - " + manuscript.getTitle();

        // 4. 发送邮件
        try {
            mailUtil.sendTextMail(reviewer.getEmail(), finalSubject, finalContent);
            
            // 5. 记录日志
            logService.record(editorId, "SEND_CUSTOM_REMINDER: Sent to " + reviewer.getFullName(), review.getManuId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("催审邮件发送失败：" + e.getMessage());
        }
    }

    /**
     * 替换邮件内容中的占位符
     */
    private String replacePlaceholders(String content, User reviewer, Manuscript manuscript, Review review) {
        if (content == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        long overdueDays = 0;
        if (review.getDeadline() != null && now.isAfter(review.getDeadline())) {
            overdueDays = ChronoUnit.DAYS.between(
                    review.getDeadline().toLocalDate(), 
                    now.toLocalDate()
            );
        }
        
        String deadlineStr = review.getDeadline() != null ?
                review.getDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "未知";
        
        return content
                .replace("{审稿人姓名}", reviewer.getFullName() != null ? reviewer.getFullName() : "审稿人")
                .replace("{稿件标题}", manuscript.getTitle() != null ? manuscript.getTitle() : "未知稿件")
                .replace("{稿件ID}", String.valueOf(manuscript.getManuscriptId()))
                .replace("{截止日期}", deadlineStr)
                .replace("{逾期天数}", String.valueOf(overdueDays));
    }
}