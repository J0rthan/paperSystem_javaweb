package com.bjfu.paperSystem.editor.service;

import com.bjfu.paperSystem.javabeans.Logs;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.Review;
import com.bjfu.paperSystem.javabeans.User;

import java.time.LocalDateTime;
import java.util.List;

public interface EditorProcessService {

    Manuscript getManuscriptDetail(int id, int editorId);

    List<User> getAvailableReviewers();

    List<User> searchReviewers(String keyword);

    List<Review> getCurrentReviews(int manuscriptId);

    void submitRecommendation(int manuscriptId, String recommendation, String comment, int editorId);

    void sendMessageToAuthor(int manuscriptId, String message, int editorId);

    List<Logs> getCommunicationHistory(int manuscriptId);

    void inviteReviewer(int manuscriptId, int reviewerId, LocalDateTime deadline, int currentEditorId);

    void revokeInvitation(int reviewId, int currentEditorId);
}