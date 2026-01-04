package com.bjfu.paperSystem.editor.dao;

import com.bjfu.paperSystem.javabeans.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EditorReviewDao extends JpaRepository<Review, Integer> {

    // 查找某篇稿件的所有审稿记录
    List<Review> findByManuId(int manuId);

    // 查找特定关系（用于旧代码，建议用下面的 exists 替代）
    Review findByManuIdAndReviewerId(int manuscriptId, int reviewerId);

    // 统计某稿件下特定状态的审稿人数量 (用于判断 "凑齐3人")
    long countByManuIdAndStatus(int manuId, String status);

    // 检查是否存在有效的邀请 (忽略已撤销 cancelled 的记录，允许重新邀请)
    boolean existsByManuIdAndReviewerIdAndStatusNot(int manuId, int reviewerId, String status);
}