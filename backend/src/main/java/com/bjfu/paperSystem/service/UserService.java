package com.bjfu.paperSystem.service;

import com.bjfu.paperSystem.javabeans.User;

public interface UserService {
    User login(String userName, String password);
}