package com.bjfu.paperSystem.editor.service;

import com.bjfu.paperSystem.editor.dao.*;
import com.bjfu.paperSystem.javabeans.*;
import com.bjfu.paperSystem.author.service.logService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RemindServiceImpl implements RemindService {

    @Autowired private EditorManuscriptDao manuscriptDao;
    @Autowired private EditorReviewDao reviewDao;
    @Autowired private EditorUserDao userDao; // 用于查审稿人邮箱
    @Autowired private logService logService;

    @Override
    public List<Map<String, Object>> getPendingReviews(int editorId) {
        List<Map<String, Object>> result = new ArrayList<>();

        // 1. 查出该编辑的所有稿件
        List<Manuscript> manuscripts = manuscriptDao.findByEditorId(editorId);

        for (Manuscript m : manuscripts) {
            // 2. 查出该稿件的所有审稿记录
            List<Review> reviews = reviewDao.findByManuId(m.getManuscriptId());
            for (Review r : reviews) {
                // 筛选条件：状态为 PENDING (未接受) 或 ACCEPTED (审稿中)，即未完成的任务
                if (!"COMPLETED".equals(r.getStatus()) && !"REJECTED".equals(r.getStatus())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("review", r);
                    map.put("manuscriptTitle", m.getTitle());
                    map.put("manuscriptId", m.getManuscriptId());

                    // 获取审稿人信息
                    User reviewer = userDao.findById(r.getReviewerId()).orElse(new User());
                    map.put("reviewerName", reviewer.getFullName());
                    map.put("reviewerEmail", reviewer.getEmail());

                    // 判断是否逾期 (假设 invitationTime + 14天为截止)
                    LocalDateTime deadline = r.getInvitationTime().plusDays(14);
                    boolean isOverdue = LocalDateTime.now().isAfter(deadline);
                    map.put("isOverdue", isOverdue);
                    map.put("deadline", deadline);

                    result.add(map);
                }
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void sendReminder(int reviewId, int editorId) {
        // 实际逻辑：发送邮件（此处模拟，仅写日志）
        Review r = reviewDao.findById(reviewId).orElse(null);
        if (r != null) {
            // 记录日志
            logService.record(editorId, "Sent Reminder to Reviewer " + r.getReviewerId(), r.getManuId());
        }
    }
}