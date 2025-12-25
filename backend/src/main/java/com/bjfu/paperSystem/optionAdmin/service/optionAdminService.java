package com.bjfu.paperSystem.optionAdmin.service;

import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User;

import java.util.List;

public interface optionAdminService {
    // 稿件管理相关方法
    List<Manuscript> getAllManuscripts();
    Manuscript getManuscriptById(Integer manuscriptId);
    void updateManuscriptStatus(Integer manuscriptId, String status);
    void approveManuscript(Integer manuscriptId, Integer operatorId);
    void rejectManuscript(Integer manuscriptId, Integer operatorId);
    
    // 编辑管理相关方法
    List<User> getAllEditors();
    User getEditorById(Integer userId);
    void saveEditor(User user);
    void deleteEditor(Integer userId);
    
    // 状态管理方法
    List<User> findUsersByStatus(String status);
}
