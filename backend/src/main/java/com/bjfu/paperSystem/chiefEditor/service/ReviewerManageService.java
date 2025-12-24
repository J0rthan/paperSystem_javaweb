package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.javabeans.User;
import java.util.List;

public interface ReviewerManageService {
    List<User> listReviewers();                 // 列表
    void enableReviewer(int userId);            // 启用
    void disableReviewer(int userId);           // 禁用
    void deleteReviewer(int userId);            // 删除
    void createReviewer(String userName, String fullName, String email); // 简易新增
}