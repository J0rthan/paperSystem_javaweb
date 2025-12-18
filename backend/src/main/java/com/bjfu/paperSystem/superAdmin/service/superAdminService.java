package com.bjfu.paperSystem.superAdmin.service;

import com.bjfu.paperSystem.Login.javabeans.User;

public interface superAdminService {
    public void createAccount(String username, String password, String user_type);
}
