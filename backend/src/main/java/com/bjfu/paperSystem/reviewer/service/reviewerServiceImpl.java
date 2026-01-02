package com.bjfu.paperSystem.reviewer.service;

import com.bjfu.paperSystem.Login.dao.UserDao;
import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.javabeans.Review;
import com.bjfu.paperSystem.reviewer.dao.reviewerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class reviewerServiceImpl implements reviewerService{
    @Autowired
    private reviewerDao revDao;

    @Autowired
    private ManuscriptDao manDao;

    @Override
    public List<Review> filterByTime(LocalDateTime startTime, LocalDateTime endTime) {
        return revDao.findWithManuscript(startTime, endTime);
    }
}
