package com.bjfu.paperSystem.reviewer.service;

import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.Review;

import java.time.LocalDateTime;
import java.util.List;

public interface reviewerService {
    // 利用时间来筛选邀请列表
    public List<Review> filterByTime(LocalDateTime startTime, LocalDateTime endTime);
}
