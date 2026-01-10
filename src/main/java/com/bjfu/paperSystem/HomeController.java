package com.bjfu.paperSystem;

import com.bjfu.paperSystem.javabeans.Editorial_Board;
import com.bjfu.paperSystem.javabeans.Journal;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.News;
import com.bjfu.paperSystem.optionAdmin.service.JournalService;
import com.bjfu.paperSystem.optionAdmin.service.NewsService;
import com.bjfu.paperSystem.service.PublicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private JournalService journalService;

    @Autowired
    private PublicService publicService;

    @GetMapping("/")
    public String index(Model model) {
        try {
            // 获取期刊介绍（第一个已发布的期刊）
            List<Journal> journals = publicService.getPublishedJournals(1);
            if (journals != null && !journals.isEmpty()) {
                model.addAttribute("journal", journals.get(0));
            } else {
                model.addAttribute("journal", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("journal", null);
        }

        try {
            // 获取编委介绍
            List<Editorial_Board> editorialBoard = publicService.getAllEditorialBoard();
            model.addAttribute("editorialBoard", editorialBoard != null ? editorialBoard : new ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("editorialBoard", new ArrayList<>());
        }

        try {
            // 获取论文列表
            List<Manuscript> latestPublished = publicService.getLatestPublished(10);
            model.addAttribute("latestPublished", latestPublished != null ? latestPublished : new ArrayList<>());

            List<Manuscript> mostPopular = publicService.getMostPopular(10);
            model.addAttribute("mostPopular", mostPopular != null ? mostPopular : new ArrayList<>());

            // Top Cited 和 Most Downloaded 暂时使用相同数据
            model.addAttribute("topCited", latestPublished != null ? latestPublished : new ArrayList<>());
            model.addAttribute("mostDownloaded", mostPopular != null ? mostPopular : new ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("latestPublished", new ArrayList<>());
            model.addAttribute("mostPopular", new ArrayList<>());
            model.addAttribute("topCited", new ArrayList<>());
            model.addAttribute("mostDownloaded", new ArrayList<>());
        }

        try {
            // 获取新闻列表（保留原有功能，不修改）
            List<News> publishedNews = newsService.getPublishedNews(10);
            model.addAttribute("newsList", publishedNews != null ? publishedNews : new ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("newsList", new ArrayList<>());
        }

        try {
            // 获取征稿通知
            List<News> callForPapers = publicService.getCallForPapers(5);
            model.addAttribute("callForPapers", callForPapers != null ? callForPapers : new ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("callForPapers", new ArrayList<>());
        }

        // 获取已发布的期刊列表（编辑部管理员发布的期刊会在首页展示）
        try {
            List<Journal> publishedJournals = journalService.getPublishedJournal();
            if (publishedJournals != null && !publishedJournals.isEmpty()) {
                // 限制首页最多显示10条期刊
                if (publishedJournals.size() > 10) {
                    publishedJournals = publishedJournals.subList(0, 10);
                }
                model.addAttribute("journalList", publishedJournals);
            } else {
                model.addAttribute("journalList", new ArrayList<>());
            }
        } catch (Exception e) {
            model.addAttribute("journalList", new ArrayList<>());
        }

        return "index";
    }

    @GetMapping("/index")
    public String indexPage(Model model) {
        // 重定向到首页
        return index(model);
    }

    // 关于期刊页面
    @GetMapping("/about-journal")
    public String aboutJournal(Model model) {
        try {
            List<Journal> journals = publicService.getPublishedJournals(1);
            if (journals != null && !journals.isEmpty()) {
                model.addAttribute("journal", journals.get(0));
            }
        } catch (Exception e) {
            model.addAttribute("journal", null);
        }

        try {
            List<Editorial_Board> editorialBoard = publicService.getAllEditorialBoard();
            model.addAttribute("editorialBoard", editorialBoard != null ? editorialBoard : new ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("editorialBoard", new ArrayList<>());
        }

        try {
            List<News> news = publicService.getPublishedNews(10);
            model.addAttribute("newsList", news != null ? news : new ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("newsList", new ArrayList<>());
        }

        return "about-journal";
    }

    // 论文发表页面
    @GetMapping("/submit")
    public String submit() {
        return "submit";
    }

    // 文章与专刊页面
    @GetMapping("/articles")
    public String articles(Model model) {
        try {
            List<Manuscript> latestIssues = publicService.getLatestPublished(20);
            model.addAttribute("latestIssues", latestIssues != null ? latestIssues : new ArrayList<>());
            model.addAttribute("specialIssues", new ArrayList<>()); // 专刊功能待实现
            model.addAttribute("allIssues", latestIssues != null ? latestIssues : new ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("latestIssues", new ArrayList<>());
            model.addAttribute("specialIssues", new ArrayList<>());
            model.addAttribute("allIssues", new ArrayList<>());
        }
        return "articles";
    }

    // 用户指南页面
    @GetMapping("/guide")
    public String guide(Model model) {
        try {
            List<Journal> journals = publicService.getPublishedJournals(1);
            if (journals != null && !journals.isEmpty()) {
                model.addAttribute("journal", journals.get(0));
            }
        } catch (Exception e) {
            model.addAttribute("journal", null);
        }
        return "guide";
    }

    // 公开的新闻详情页面（用于主页显示）
    @GetMapping("/news/{id}")
    public String publicNewsDetail(@PathVariable("id") Integer newsId, Model model) {
        try {
            News news = newsService.getNewsById(newsId);
            if (news == null || !"published".equals(news.getStatus())) {
                return "redirect:/";
            }
            model.addAttribute("news", news);
            return "optionadmin/news/newsDetail";
        } catch (Exception e) {
            return "redirect:/";
        }
    }
}



