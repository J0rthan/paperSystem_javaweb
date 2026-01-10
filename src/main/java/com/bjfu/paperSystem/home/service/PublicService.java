package com.bjfu.paperSystem.home.service;

import com.bjfu.paperSystem.javabeans.Editorial_Board;
import com.bjfu.paperSystem.javabeans.Journal;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.News;
import com.bjfu.paperSystem.chiefEditor.dao.ChiefEditorEditorial_BoardDao;
import com.bjfu.paperSystem.optionAdmin.dao.JournalDao;
import com.bjfu.paperSystem.optionAdmin.service.JournalService;
import com.bjfu.paperSystem.optionAdmin.service.NewsService;
import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicService {

    @Autowired
    private JournalService journalService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private ChiefEditorEditorial_BoardDao editorialBoardDao;

    @Autowired
    private ManuscriptDao manuscriptDao;

    /**
     * 获取已发布的期刊列表
     */
    public List<Journal> getPublishedJournals(int limit) {
        List<Journal> journals = journalService.getPublishedJournal();
        if (journals != null && journals.size() > limit) {
            return journals.subList(0, limit);
        }
        return journals;
    }

    /**
     * 获取最新发布的新闻
     */
    public List<News> getPublishedNews(int limit) {
        return newsService.getPublishedNews(limit);
    }

    /**
     * 获取所有编委成员
     */
    public List<Editorial_Board> getAllEditorialBoard() {
        return editorialBoardDao.findAll();
    }

    /**
     * 获取已发表的论文（decision为accepted或published）
     */
    public List<Manuscript> getPublishedManuscripts(int limit) {
        List<Manuscript> allManuscripts = manuscriptDao.findAll();
        return allManuscripts.stream()
                .filter(m -> m.getDecision() != null &&
                        ("accepted".equalsIgnoreCase(m.getDecision()) ||
                                "published".equalsIgnoreCase(m.getDecision())))
                .sorted((m1, m2) -> {
                    if (m1.getSubmitTime() != null && m2.getSubmitTime() != null) {
                        return m2.getSubmitTime().compareTo(m1.getSubmitTime());
                    }
                    return 0;
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 获取最新发表的论文
     */
    public List<Manuscript> getLatestPublished(int limit) {
        return getPublishedManuscripts(limit);
    }

    /**
     * 获取最受欢迎的论文（这里用提交时间作为代理，实际应该用下载量或浏览量）
     */
    public List<Manuscript> getMostPopular(int limit) {
        List<Manuscript> allManuscripts = manuscriptDao.findAll();
        return allManuscripts.stream()
                .filter(m -> m.getDecision() != null &&
                        ("accepted".equalsIgnoreCase(m.getDecision()) ||
                                "published".equalsIgnoreCase(m.getDecision())))
                .sorted((m1, m2) -> {
                    if (m1.getSubmitTime() != null && m2.getSubmitTime() != null) {
                        return m2.getSubmitTime().compareTo(m1.getSubmitTime());
                    }
                    return 0;
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 获取征稿通知（从新闻中筛选）
     */
    public List<News> getCallForPapers(int limit) {
        List<News> allNews = newsService.getPublishedNews(100);
        if (allNews == null) {
            return new ArrayList<>();
        }
        return allNews.stream()
                .filter(news -> news.getTitle() != null &&
                        (news.getTitle().contains("征稿") ||
                                news.getTitle().contains("Call for Papers") ||
                                news.getTitle().contains("投稿")))
                .limit(limit)
                .collect(Collectors.toList());
    }
}

