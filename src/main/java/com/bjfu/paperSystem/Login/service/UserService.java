package com.bjfu.paperSystem.Login.service;

import com.bjfu.paperSystem.javabeans.User;

public interface UserService {
    User login(String userName, String password);
    User register(User user);
    boolean isUserNameExists(String userName);
}