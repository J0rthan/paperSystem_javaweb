package com.bjfu.paperSystem.superAdmin.service;

import com.bjfu.paperSystem.javabeans.Permission;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.superAdmin.dao.permissionManageDao;
import com.bjfu.paperSystem.superAdmin.dao.superAdminDao;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class permissionManageServiceImpl implements permissionManageService {
    @Autowired
    private superAdminDao suAdminDao;

    @Autowired
    private permissionManageDao permissionDao;

    @Override
    @Transactional
    public String insertPermission(User user) {
        String exit_code = "ok";

        Permission permission = new Permission();
        permission.setUser(user);
        String userType = user.getUserType();
        /*
        <option value="sys_admin" selected>系统管理员</option>
            <option value="option_admin">编辑部管理员</option>
            <option value="chief_editor">主编</option>
            <option value="editor">编辑</option>
            <option value="reviewer">审稿人</option>
            <option value="author">作者</option>
         */
        // 如果用户类型是作者
        if ("author".equals(userType)) {
            permission.setSubmitManuscript(true); // 可以提交新稿件
            permission.setViewAllManuscripts(false); // 不可以查看所有稿件
            permission.setInviteOrAssignPersonnel(false); // 不可以邀请/指派人员
            permission.setViewReviewerIdentity(false); // 不可以查看审稿人身份
            permission.setWriteReviewComment(false); // 不可以填写审稿意见
            permission.setMakeAcceptRejectDecision(false); // 不可以做出录用/拒稿决定
            permission.setModifySystemConfig(false); // 不可以修改系统配置
        }
        // 如果用户类型是审稿人
        else if ("reviewer".equals(userType)) {
            permission.setSubmitManuscript(false); // 不可以提交新稿件
            permission.setViewAllManuscripts(false); // 不可以查看所有稿件
            permission.setInviteOrAssignPersonnel(false); // 不可以邀请/指派人员
            permission.setViewReviewerIdentity(false); // 不可以查看审稿人身份
            permission.setWriteReviewComment(true); // 可以填写审稿意见
            permission.setMakeAcceptRejectDecision(false); // 不可以做出录用/拒稿决定
            permission.setModifySystemConfig(false); // 不可以修改系统配置
        }
        // 如果用户类型是编辑
        else if ("editor".equals(userType)) {
            permission.setSubmitManuscript(false); // 不可以提交新稿件
            permission.setViewAllManuscripts(false); // 不可以查看所有稿件
            permission.setInviteOrAssignPersonnel(true); // 可以邀请/指派人员
            permission.setViewReviewerIdentity(true); // 可以查看审稿人身份
            permission.setWriteReviewComment(false); // 不可以填写审稿意见
            permission.setMakeAcceptRejectDecision(false); // 不可以做出录用/拒稿决定
            permission.setModifySystemConfig(false); // 不可以修改系统配置
        }
        // 如果用户类型是主编
        else if ("chief_editor".equals(userType)) {
            permission.setSubmitManuscript(false); // 不可以提交新稿件
            permission.setViewAllManuscripts(true); // 可以查看所有稿件
            permission.setInviteOrAssignPersonnel(true); // 可以邀请/指派人员
            permission.setViewReviewerIdentity(true); // 可以查看审稿人身份
            permission.setWriteReviewComment(false); // 不可以填写审稿意见
            permission.setMakeAcceptRejectDecision(true); // 可以做出录用/拒稿决定
            permission.setModifySystemConfig(false); // 不可以修改系统配置
        }

        // 保存进数据库
        permissionDao.save(permission);

        return exit_code;
    }

    @Override
    public Permission findByUserId(int userId) {
        return permissionDao.findByUser_UserId(userId).orElse(null);
    }
}
