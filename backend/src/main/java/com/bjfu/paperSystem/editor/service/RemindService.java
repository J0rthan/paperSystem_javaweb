package com.bjfu.paperSystem.editor.service;

import com.bjfu.paperSystem.javabeans.Review;
import java.util.List;
import java.util.Map;

public interface RemindService {
    // 获取该编辑名下所有正在进行的审稿任务（包含稿件标题等信息）
    // 返回值 Map 包含 Review 对象和对应的 Manuscript Title
    List<Map<String, Object>> getPendingReviews(int editorId);

    // 发送催审邮件/消息
    void sendReminder(int reviewId, int editorId);
}