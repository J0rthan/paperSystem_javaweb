package com.bjfu.paperSystem.optionAdmin.service;

import com.bjfu.paperSystem.optionAdmin.dao.optionAdminDao;
import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.author.dao.LogsDao;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.javabeans.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class optionAdminServiceImpl implements optionAdminService {
    @Autowired
    private ManuscriptDao manuscriptDao;
    
    @Autowired
    private optionAdminDao optionAdminDao;
    
    @Autowired
    private LogsDao logsDao;

    // 稿件管理相关方法实现
    @Override
    public List<Manuscript> getAllManuscripts() {
        return manuscriptDao.findAll();
    }

    @Override
    public Manuscript getManuscriptById(Integer manuscriptId) {
        return manuscriptDao.findById(manuscriptId).orElse(null);
    }

    @Override
    public void updateManuscriptStatus(Integer manuscriptId, String status) {
        Manuscript manuscript = manuscriptDao.findById(manuscriptId).orElse(null);
        if (manuscript != null) {
            manuscript.setStatus(status);
            manuscriptDao.save(manuscript);
        }
    }

    @Override
    public void approveManuscript(Integer manuscriptId, Integer operatorId) {
        // 更新稿件状态为Pending Allocation
        updateManuscriptStatus(manuscriptId, "Pending Allocation");
        
        // 记录日志
        Logs log = new Logs();
        log.setOpTime(LocalDateTime.now());
        log.setOporId(operatorId);
        log.setOpType("through review");
        log.setPaperId(manuscriptId);
        logsDao.save(log);
    }

    @Override
    public void rejectManuscript(Integer manuscriptId, Integer operatorId) {
        // 更新稿件状态为Incomplete Submission
        updateManuscriptStatus(manuscriptId, "Incomplete Submission");
        
        // 记录日志
        Logs log = new Logs();
        log.setOpTime(LocalDateTime.now());
        log.setOporId(operatorId);
        log.setOpType("return");
        log.setPaperId(manuscriptId);
        logsDao.save(log);
    }

    // 编辑管理相关方法实现
    @Override
    public List<User> getAllEditors() {
        return optionAdminDao.findAll();
    }

    @Override
    public User getEditorById(Integer userId) {
        return optionAdminDao.findById(userId).orElse(null);
    }

    @Override
    public void saveEditor(User user) {
        optionAdminDao.save(user);
    }

    @Override
    public void deleteEditor(Integer userId) {
        optionAdminDao.deleteById(userId);
    }

    // 状态管理方法实现
    @Override
    public List<User> findUsersByStatus(String status) {
        return optionAdminDao.findByStatus(status);
    }
    
    // 个人信息管理方法实现
    @Override
    public User getUserById(Integer userId) {
        return optionAdminDao.findById(userId).orElse(null);
    }
    
    @Override
    public String updateProfile(User user, Integer loginUserId) {
        // 检查用户名是否被占用（除了当前用户自己）
        User duplicateUser = optionAdminDao.findByUserName(user.getUserName());
        if (duplicateUser != null && duplicateUser.getUserId() != loginUserId) {
            return "用户名已被占用，请更换其他账号名";
        }
        
        User existingUser = optionAdminDao.findById(loginUserId).orElse(null);
        if (existingUser == null) {
            return "用户不存在";
        }
        
        // 更新用户信息
        existingUser.setUserName(user.getUserName());
        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        existingUser.setCompany(user.getCompany());
        existingUser.setInvestigationDirection(user.getInvestigationDirection());
        
        // 如果密码不为空，则更新密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        
        // 保存更新后的用户信息
        optionAdminDao.save(existingUser);
        return null;
    }
}
