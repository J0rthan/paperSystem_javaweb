package com.bjfu.paperSystem.Login.service;

import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.Login.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User login(String userName, String password) {
        return userDao.findByUserNameAndPassword(userName, password)
                .orElse(null);
    }
}