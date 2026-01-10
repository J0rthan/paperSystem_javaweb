package com.bjfu.paperSystem.reviewer.service;

import com.bjfu.paperSystem.Login.dao.UserDao;
import com.bjfu.paperSystem.author.dao.LogsDao;
import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.javabeans.Logs;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.Review;
import com.bjfu.paperSystem.reviewer.dao.reviewerDao;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class reviewerServiceImpl implements reviewerService{
    @Autowired
    private reviewerDao revDao;

    @Autowired
    private ManuscriptDao manDao;

    @Autowired
    private LogsDao logsDao;

    // 通过时间范围筛选稿件
    @Override
    public List<Review> filterByTime(LocalDateTime startTime, LocalDateTime endTime, int reviewer_id) {
        List<Review> result = revDao.findWithManuscript(startTime, endTime);
        result.removeIf(r -> r.getReviewerId() != reviewer_id);
        return result;
    }

    // 通过manu_id查找稿件
    @Override
    public Manuscript findByManuId(int manu_id) {
        return manDao.findById(manu_id).orElse(null);
    }

    // 同意审稿
    @Override
    @Transactional
    public String acceptManu(int review_id, int manu_id) {
        Review review = revDao.findByReviewIdAndManuId(review_id, manu_id);
        review.setStatus("accepted");

        return "审稿任务已接受，截止日期：" + LocalDateTime.now();
    }

    // 拒绝审稿
    @Override
    @Transactional
    public String rejectManu(int review_id, int manu_id) {
        Review review = revDao.findByReviewIdAndManuId(review_id, manu_id);
        review.setStatus("rejected");

        return "时间冲突，无法审稿";
    }

    @Override
    @Transactional
    public List<Review> filterByStatus(String status, int reviewer_id) {
        List<Review> result = revDao.findWithStatus(status);
        result.removeIf(r -> r.getReviewerId() != reviewer_id);
        return result;
    }

    @Override
    @Transactional
    public Review findByRevId(int review_id) {
        return revDao.findByIdWithManuscript(review_id);
    }

    // 更新系统状态为“完成审稿”
    @Override
    @Transactional
    public String updateFinish(Integer review_id) {
        Review review = revDao.findById(review_id).orElse(null);
        if (review != null) {
            review.setStatus("finished");

            // 写入日志
            Integer reviewerId = review.getReviewerId();
            String action = "finish reviewing";
            Integer manuId = review.getManuId();
            LocalDateTime time = LocalDateTime.now();

            Logs log = new Logs();
            log.setOporId(reviewerId);
            log.setPaperId(manuId);
            log.setOpTime(time);
            log.setOpType(action);
            logsDao.save(log);

            return "ok";
        }

        // 未找到review
        return "false";
    }
}
