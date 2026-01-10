package com.bjfu.paperSystem.optionAdmin.dao;

import com.bjfu.paperSystem.javabeans.News;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsDao extends JpaRepository<News, Integer>, JpaSpecificationExecutor<News> {

    // 根据状态查询新闻
    List<News> findByStatusOrderByPublishTimeDesc(String status);

    // 根据标题或内容模糊查询
    @Query("SELECT n FROM News n WHERE n.title LIKE %:keyword% OR n.content LIKE %:keyword%")
    List<News> findByKeyword(@Param("keyword") String keyword);

    // 根据发布时间范围查询
    List<News> findByPublishTimeBetweenOrderByPublishTimeDesc(LocalDateTime start, LocalDateTime end);

    // 查询已发布或定时发布的新闻
    List<News> findByStatusInOrderByPublishTimeDesc(List<String> statuses);

    // 分页查询所有新闻（按创建时间倒序）
    List<News> findAllByOrderByCreateTimeDesc();

    // 查询已发布的新闻（按发布时间倒序，限制数量）
    @Query("SELECT n FROM News n WHERE n.status = 'published' AND n.publishTime IS NOT NULL ORDER BY n.publishTime DESC")
    List<News> findPublishedNews(Pageable pageable);
}