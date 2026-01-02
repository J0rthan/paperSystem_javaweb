package com.bjfu.paperSystem.Login.service;

import com.bjfu.paperSystem.javabeans.User;

public interface UserService {
    User login(String userName, String password);
    User register(String userName, String password, String userType);
    boolean isUserNameExists(String userName);
}