//稿件管理：查询、审核、拒绝并发送邮件反馈
//编辑管理：查询、保存、删除编辑
//用户状态管理：按照状态查询用户
//个人资料管理：查询、更新
package com.bjfu.paperSystem.optionAdmin.service;

import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User;

import java.util.List;

public interface optionAdminService {
    // 稿件管理相关方法
    List<Manuscript> getAllManuscripts();
    Manuscript getManuscriptById(Integer manuscriptId);
    void updateManuscriptStatus(Integer manuscriptId, String status);
    void approveManuscript(Integer manuscriptId, Integer operatorId);// 拒绝稿件审核
    void rejectManuscript(Integer manuscriptId, Integer operatorId);
    
    // 拒绝稿件并发送反馈
    void rejectManuscriptWithFeedback(Integer manuscriptId, Integer operatorId, String messageBody, String senderEmail);
    
    // 编辑管理相关方法
    List<User> getAllEditors();
    User getEditorById(Integer userId);
    void saveEditor(User user);
    void deleteEditor(Integer userId);
    
    // 状态管理方法
    List<User> findUsersByStatus(String status);
    
    // 个人信息管理方法
    User getUserById(Integer userId);
    String updateProfile(User user, Integer loginUserId);
}
