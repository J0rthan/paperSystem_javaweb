package com.bjfu.paperSystem.Login.service;

import com.bjfu.paperSystem.Login.javabeans.User;

public interface UserService {
    User login(String userName, String password);
}