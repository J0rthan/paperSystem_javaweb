package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.chiefEditor.dao.ChiefEditorUserDao;
import com.bjfu.paperSystem.javabeans.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewerManageServiceImpl implements ReviewerManageService {

    @Autowired
    private ChiefEditorUserDao userDao;

    @Override
    public List<User> listReviewers() {
        return userDao.findByUserTypeIgnoreCase("reviewer");
    }

    @Override
    public void enableReviewer(int userId) {
        userDao.findById(userId).ifPresent(u -> {
            u.setStatus("exist");
            userDao.save(u);
        });
    }

    @Override
    public void disableReviewer(int userId) {
        userDao.findById(userId).ifPresent(u -> {
            u.setStatus("banned");
            userDao.save(u);
        });
    }

    @Override
    public void deleteReviewer(int userId) {
        userDao.deleteById(userId);
    }

    @Override
    public void createReviewer(String userName, String fullName, String email) {
        User u = new User();
        u.setUserName(userName);
        u.setFullName(fullName);
        u.setEmail(email);
        u.setPassword("123456");     // 默认密码，后续可要求首登修改
        u.setUserType("reviewer");
        u.setStatus("exist");
        userDao.save(u);
    }
}