package com.bjfu.paperSystem.optionAdmin.service;

import com.bjfu.paperSystem.javabeans.News;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsService {
    // 新增新闻
    void saveNews(News news);

    // 更新新闻
    void updateNews(News news);

    // 删除新闻
    void deleteNews(Integer newsId);

    // 根据ID获取新闻
    News getNewsById(Integer newsId);

    // 获取所有新闻
    List<News> getAllNews();

    // 根据状态获取新闻
    List<News> getNewsByStatus(String status);

    // 搜索新闻（根据标题或内容）
    List<News> searchNews(String keyword);

    // 根据时间范围获取新闻
    List<News> getNewsByTimeRange(LocalDateTime start, LocalDateTime end);

    // 发布新闻
    void publishNews(Integer newsId);

    // 定时发布新闻
    void scheduleNews(Integer newsId, LocalDateTime publishTime);

    // 保存为草稿
    void saveDraft(News news);
}