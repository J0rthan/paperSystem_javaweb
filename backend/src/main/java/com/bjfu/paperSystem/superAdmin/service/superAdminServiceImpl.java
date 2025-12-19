package com.bjfu.paperSystem.superAdmin.service;

import com.bjfu.paperSystem.Login.dao.UserDao;
import com.bjfu.paperSystem.Login.javabeans.User;
import com.bjfu.paperSystem.superAdmin.dao.superAdminDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        // 设置状态为exist
        user.setStatus("exist");

        // 执行插入
        suAdminDao.save(user);
    }

    @Override
    public List<User> findAllUsers() {
        List<User> userList = suAdminDao.findAll();

        return userList;
    }

    @Override
    public List<User> findAllExistUsers() {
        List<User> userList = suAdminDao.findByStatus("exist");

        return userList;
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = suAdminDao.findById(userId).orElse(null);
        if (user != null) {
            user.setStatus("not_exist");
            suAdminDao.save(user);
        }
    }

    @Override
    public User findUserById(Integer userId) {
        return suAdminDao.findById(userId).orElse(null);
    }

    @Override
    public void modifyUser(User user) {
        User dbUser = suAdminDao.findById(user.getUserId()).orElse(null);
        dbUser.setUserName(user.getUserName());
        // 密码留空则不修改
        if (!user.getPassword().equals("")) {
            dbUser.setPassword(user.getPassword());
        }
        dbUser.setFullName(user.getFullName());
        dbUser.setEmail(user.getEmail());
        dbUser.setUserType(user.getUserType());
        dbUser.setCompany(user.getCompany());
        dbUser.setInvestigationDirection(user.getInvestigationDirection());
        dbUser.setRegisterTime(user.getRegisterTime());
        dbUser.setStatus(user.getStatus());

        suAdminDao.save(dbUser);
    }
}
