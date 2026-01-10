package com.bjfu.paperSystem.editor.dao;

import com.bjfu.paperSystem.javabeans.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EditorReviewDao extends JpaRepository<Review, Integer> {

    // 查找某篇稿件的所有审稿记录
    // 使用显式查询确保正确映射到数据库列名
    @Query("SELECT r FROM Review r WHERE r.manuId = :manuId")
    List<Review> findByManuId(@Param("manuId") int manuId);

    // 查找特定关系（用于旧代码，建议用下面的 exists 替代）
    @Query("SELECT r FROM Review r WHERE r.manuId = :manuId AND r.reviewerId = :reviewerId")
    Review findByManuIdAndReviewerId(@Param("manuId") int manuscriptId, @Param("reviewerId") int reviewerId);

    // 统计某稿件下特定状态的审稿人数量 (用于判断 "凑齐3人")
    @Query("SELECT COUNT(r) FROM Review r WHERE r.manuId = :manuId AND r.status = :status")
    long countByManuIdAndStatus(@Param("manuId") int manuId, @Param("status") String status);

    // 检查是否存在有效的邀请 (忽略已撤销 cancelled 的记录，允许重新邀请)
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r " +
           "WHERE r.manuId = :manuId AND r.reviewerId = :reviewerId AND r.status != :status")
    boolean existsByManuIdAndReviewerIdAndStatusNot(@Param("manuId") int manuId, 
                                                     @Param("reviewerId") int reviewerId, 
                                                     @Param("status") String status);
}