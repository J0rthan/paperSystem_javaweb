package com.bjfu.paperSystem.superAdmin.service;

import com.bjfu.paperSystem.Login.dao.UserDao;
import com.bjfu.paperSystem.Login.javabeans.User;
import com.bjfu.paperSystem.superAdmin.dao.superAdminDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class superAdminServiceImpl implements superAdminService{
    @Autowired
    private superAdminDao suAdminDao;

    @Override
    public void createAccount(String username, String password, String user_type) {
        User user = new User();

        user.setUserName(username);
        user.setPassword(password);
        user.setUserType(user_type);

        // 执行插入
        suAdminDao.save(user);
    }
}
