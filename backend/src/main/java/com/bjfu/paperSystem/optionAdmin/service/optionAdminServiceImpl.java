package com.bjfu.paperSystem.optionAdmin.service;

import com.bjfu.paperSystem.Login.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class optionAdminServiceImpl implements optionAdminService{
    @Autowired
    private UserDao userDao;
}
