package com.bjfu.paperSystem.Login.service;

import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.Login.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User login(String userName, String password) {
        return userDao.findByUserNameAndPassword(userName, password)
                .orElse(null);
    }

    @Override
    public User register(String userName, String password, String userType) {
        // 检查用户名是否存在
        if (isUserNameExists(userName)) {
            return null;
        }
        // 只允许作者和审稿人注册
        if (!"author".equals(userType) && !"reviewer".equals(userType)) {
            return null;
        }
        // 创建新用户
        User user = new User();
        user.setUserName(userName);
        user.setPassword(password);
        user.setUserType(userType);
        user.setRegisterTime(LocalDateTime.now());
        user.setStatus("exist"); // 设置状态为活跃
        return userDao.save(user);
    }

    @Override
    public boolean isUserNameExists(String userName) {
        return userDao.findByUserName(userName).isPresent();
    }
}