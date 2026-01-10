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
    public User register(User user) {
        // 检查用户名是否存在
        if (isUserNameExists(user.getUserName())) {
            return null;
        }
        // 只允许作者和审稿人注册
        String userType = user.getUserType();
        if (!"author".equals(userType) && !"reviewer".equals(userType)) {
            return null;
        }
        // 设置默认值
        user.setRegisterTime(LocalDateTime.now());
        // 若注册的是作者author，则设置状态Status为 not_exist
        if ("author".equals(userType)) {
            user.setStatus("not_exist");
        } else {
            user.setStatus("exist"); // 设置状态为活跃
        }
        return userDao.save(user);
    }

    @Override
    public boolean isUserNameExists(String userName) {
        return userDao.findByUserName(userName).isPresent();
    }
}