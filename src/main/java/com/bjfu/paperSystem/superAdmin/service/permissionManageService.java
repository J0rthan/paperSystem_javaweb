package com.bjfu.paperSystem.superAdmin.service;

import com.bjfu.paperSystem.javabeans.Permission;
import com.bjfu.paperSystem.javabeans.User;

public interface permissionManageService {
    public String insertPermission(User user);

    public Permission findByUserId(int userId);
}
