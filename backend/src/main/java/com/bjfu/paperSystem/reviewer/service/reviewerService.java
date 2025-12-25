package com.bjfu.paperSystem.reviewer.service;

import com.bjfu.paperSystem.javabeans.Review;

import java.time.LocalDateTime;
import java.util.List;

public interface reviewerService {
    public List<Review> filterByTime(LocalDateTime startTime, LocalDateTime endTime);
}
