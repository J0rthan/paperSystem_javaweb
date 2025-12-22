package com.bjfu.paperSystem.reviewer.service;

import com.bjfu.paperSystem.Login.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class reviewerServiceImpl implements reviewerService{
    @Autowired
    private UserDao userDao;
}
