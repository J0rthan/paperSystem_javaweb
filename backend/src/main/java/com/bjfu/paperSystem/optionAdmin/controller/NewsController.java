package com.bjfu.paperSystem.optionAdmin.controller;

import com.bjfu.paperSystem.javabeans.News;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.optionAdmin.service.NewsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/optionadmin/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    // 新闻列表页面
    @GetMapping
    public String newsList(Model model, @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) String startDate,
                          @RequestParam(required = false) String endDate) {
        List<News> newsList;
        
        // 根据条件查询新闻
        if (keyword != null && !keyword.isEmpty()) {
            newsList = newsService.searchNews(keyword);
        } else if (status != null && !status.isEmpty()) {
            newsList = newsService.getNewsByStatus(status);
        } else if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            newsList = newsService.getNewsByTimeRange(start, end);
        } else {
            newsList = newsService.getAllNews();
        }
        
        model.addAttribute("newsList", newsList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "optionadmin/news/newsList";
    }

    // 新增新闻页面
    @GetMapping("/add")
    public String addNews(Model model) {
        News news = new News();
        model.addAttribute("news", news);
        return "optionadmin/news/addNews";
    }

    // 编辑新闻页面
    @GetMapping("/edit/{id}")
    public String editNews(@PathVariable("id") Integer newsId, Model model) {
        News news = newsService.getNewsById(newsId);
        model.addAttribute("news", news);
        return "optionadmin/news/editNews";
    }

    // 保存新闻（新增和编辑）
    @PostMapping("/save")
    public String saveNews(@ModelAttribute News news, @RequestParam(required = false) MultipartFile attachment,
                          @RequestParam String action, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/optionadmin/login";
        
        // 设置创建者ID
        news.setCreatorId(loginUser.getUserId());
        
        // 处理附件上传
        if (attachment != null && !attachment.isEmpty()) {
            String attachmentPath = saveFile(attachment, "news_attachments");
            news.setAttachmentPath(attachmentPath);
            news.setAttachmentName(attachment.getOriginalFilename());
        } else if (news.getNewsId() > 0) {
            // 如果是编辑且没有上传新附件，保留原有附件信息
            News existingNews = newsService.getNewsById(news.getNewsId());
            if (existingNews != null) {
                news.setAttachmentPath(existingNews.getAttachmentPath());
                news.setAttachmentName(existingNews.getAttachmentName());
            }
        }
        
        // 根据操作类型处理
        if (action.equals("saveAndPublish")) {
            // 保存并发布
            if (news.getNewsId() > 0) {
                // 编辑新闻
                News existingNews = newsService.getNewsById(news.getNewsId());
                if (existingNews != null) {
                    news.setCreateTime(existingNews.getCreateTime());
                }
                newsService.updateNews(news);
                newsService.publishNews(news.getNewsId());
            } else {
                // 新增新闻
                newsService.saveNews(news);
            }
        } else if (action.equals("saveAsDraft")) {
            // 保存为草稿
            if (news.getNewsId() > 0) {
                News existingNews = newsService.getNewsById(news.getNewsId());
                if (existingNews != null) {
                    news.setCreateTime(existingNews.getCreateTime());
                }
                news.setStatus("draft");
                newsService.updateNews(news);
            } else {
                newsService.saveDraft(news);
            }
        } else if (action.equals("schedule")) {
            // 定时发布
            if (news.getNewsId() > 0) {
                News existingNews = newsService.getNewsById(news.getNewsId());
                if (existingNews != null) {
                    news.setCreateTime(existingNews.getCreateTime());
                }
                newsService.updateNews(news);
                newsService.scheduleNews(news.getNewsId(), news.getPublishTime());
            } else {
                newsService.saveDraft(news);
                newsService.scheduleNews(news.getNewsId(), news.getPublishTime());
            }
        }
        
        return "redirect:/optionadmin/news";
    }

    // 删除新闻
    @PostMapping("/delete/{id}")
    public String deleteNews(@PathVariable("id") Integer newsId) {
        newsService.deleteNews(newsId);
        return "redirect:/optionadmin/news";
    }

    // 文件上传处理
    private String saveFile(MultipartFile file, String subDir) {
        try {
            // 获取上传文件的原始名称
            String originalFileName = file.getOriginalFilename();
            // 使用 UUID 重命名防止中文乱码和文件名冲突
            String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + suffix;

            // 确定物理存储路径
            String uploadDir = "D:/java_uploads/" + subDir + "/";
            File destDir = new File(uploadDir);
            if (!destDir.exists()) destDir.mkdirs();

            File destFile = new File(destDir, fileName);
            file.transferTo(destFile);

            // 返回给数据库存的相对路径
            return "/uploads/" + subDir + "/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}