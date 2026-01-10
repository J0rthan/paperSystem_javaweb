package com.bjfu.paperSystem.editor.service;

import com.bjfu.paperSystem.javabeans.Logs;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.Review;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.javabeans.ClientMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EditorProcessService {

    Manuscript getManuscriptDetail(int id, int editorId);

    List<User> getAvailableReviewers();

    List<User> searchReviewers(String keyword);

    List<Review> getCurrentReviews(int manuscriptId);

    void submitRecommendation(int manuscriptId, String recommendation, String comment, int editorId);

    void sendMessageToAuthor(int manuscriptId, int authorId, String message, int editorId);

    List<ClientMessage> getCommunicationHistory(int manuscriptId, int editorId);

    void inviteReviewer(int manuscriptId, int reviewerId, LocalDateTime deadline, int currentEditorId);

    void revokeInvitation(int reviewId, int currentEditorId);

    void checkAndUpdateManuscriptStatus(int manuscriptId);

    void sendManualReminder(int reviewId, int editorId);

    /**
     * 获取有逾期审稿人的稿件列表
     * @param editorId 编辑ID
     * @return 稿件列表，每个稿件包含manuscriptId和title
     */
    List<Map<String, Object>> getOverdueManuscripts(int editorId);

    /**
     * 获取指定稿件的逾期审稿人列表
     * @param manuscriptId 稿件ID
     * @return 审稿人列表，每个审稿人包含reviewId、reviewerId、reviewerName、reviewerEmail、deadline、overdueDays
     */
    List<Map<String, Object>> getOverdueReviewers(int manuscriptId);

    /**
     * 发送自定义内容的催审邮件
     * @param reviewId 审稿任务ID
     * @param subject 邮件主题
     * @param customContent 自定义邮件内容
     * @param editorId 编辑ID
     */
    void sendCustomReminder(int reviewId, String subject, String customContent, int editorId);
}