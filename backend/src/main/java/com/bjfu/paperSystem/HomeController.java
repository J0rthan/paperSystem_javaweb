package com.bjfu.paperSystem;

import com.bjfu.paperSystem.javabeans.News;
import com.bjfu.paperSystem.optionAdmin.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
//1
@Controller
public class HomeController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/")
    public String index(Model model) {
        try {
            // 获取最新发布的10条新闻
            List<News> publishedNews = newsService.getPublishedNews(10);
            model.addAttribute("newsList", publishedNews != null ? publishedNews : new ArrayList<>());
        } catch (Exception e) {
            // 如果查询出错，返回空列表
            model.addAttribute("newsList", new ArrayList<>());
        }
        return "index";
    }

    @GetMapping("/index")
    public String indexPage(Model model) {
        try {
            // 获取最新发布的10条新闻
            List<News> publishedNews = newsService.getPublishedNews(10);
            model.addAttribute("newsList", publishedNews != null ? publishedNews : new ArrayList<>());
        } catch (Exception e) {
            // 如果查询出错，返回空列表
            model.addAttribute("newsList", new ArrayList<>());
        }
        return "index";
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



