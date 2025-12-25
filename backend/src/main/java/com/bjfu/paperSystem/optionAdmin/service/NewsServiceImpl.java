package com.bjfu.paperSystem.optionAdmin.service;

import com.bjfu.paperSystem.javabeans.News;
import com.bjfu.paperSystem.optionAdmin.dao.NewsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsDao newsDao;

    @Override
    public void saveNews(News news) {
        news.setCreateTime(LocalDateTime.now());
        news.setUpdateTime(LocalDateTime.now());
        news.setStatus("published");
        newsDao.save(news);
    }

    @Override
    public void updateNews(News news) {
        news.setUpdateTime(LocalDateTime.now());
        newsDao.save(news);
    }

    @Override
    public void deleteNews(Integer newsId) {
        newsDao.deleteById(newsId);
    }

    @Override
    public News getNewsById(Integer newsId) {
        return newsDao.findById(newsId).orElse(null);
    }

    @Override
    public List<News> getAllNews() {
        return newsDao.findAllByOrderByCreateTimeDesc();
    }

    @Override
    public List<News> getNewsByStatus(String status) {
        return newsDao.findByStatusOrderByPublishTimeDesc(status);
    }

    @Override
    public List<News> searchNews(String keyword) {
        return newsDao.findByKeyword(keyword);
    }

    @Override
    public List<News> getNewsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return newsDao.findByPublishTimeBetweenOrderByPublishTimeDesc(start, end);
    }

    @Override
    public void publishNews(Integer newsId) {
        News news = newsDao.findById(newsId).orElse(null);
        if (news != null) {
            news.setStatus("published");
            news.setPublishTime(LocalDateTime.now());
            news.setUpdateTime(LocalDateTime.now());
            newsDao.save(news);
        }
    }

    @Override
    public void scheduleNews(Integer newsId, LocalDateTime publishTime) {
        News news = newsDao.findById(newsId).orElse(null);
        if (news != null) {
            news.setStatus("scheduled");
            news.setPublishTime(publishTime);
            news.setUpdateTime(LocalDateTime.now());
            newsDao.save(news);
        }
    }

    @Override
    public void saveDraft(News news) {
        news.setCreateTime(LocalDateTime.now());
        news.setUpdateTime(LocalDateTime.now());
        news.setStatus("draft");
        newsDao.save(news);
    }
}