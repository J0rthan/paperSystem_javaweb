package com.bjfu.paperSystem.reviewer.service;

import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.Review;

import java.time.LocalDateTime;
import java.util.List;

public interface reviewerService {
    // 利用时间来筛选邀请列表
    public List<Review> filterByTime(LocalDateTime startTime, LocalDateTime endTime, int reviewer_id);

    // 通过manu_id查找稿件
    public Manuscript findByManuId(int manu_id);

    // 同意审稿
    public String acceptManu(int review_id, int manu_id);

    // 拒绝审稿
    public String rejectManu(int review_id, int manu_id);

    // 查找已经接受的稿件
    public List<Review> filterByStatus(String status, int reviewer_id);

    // 根据主键查稿件
    public Review findByRevId(int review_id);

    // 更新系统状态为“完成审稿”
    public String updateFinish(Integer review_id);
}
