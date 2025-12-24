package com.bjfu.paperSystem.author.controller;

import com.bjfu.paperSystem.author.service.authorService;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.javabeans.ManuscriptAuthor;
import com.bjfu.paperSystem.javabeans.RecommendedReviewer;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/author/manuscript")
public class manuscriptSystemController {

    @Autowired
    private authorService authorService;

    // 稿件列表展示
    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        Map<String, List<Manuscript>> data = authorService.getCategorizedManuscripts(loginUser.getUserId());
        model.addAllAttributes(data);
        return "author/list";
    }

    // 进入新建投稿页面
    @GetMapping("/submit")
    public String toSubmit(Model model) {
        Manuscript manuscript = new Manuscript();
        manuscript.setManuscriptId(0);
        // 初始化一个作者位，方便前端显示第一行
        manuscript.getAuthors().add(new ManuscriptAuthor());
        manuscript.getReviewers().add(new RecommendedReviewer());

        model.addAttribute("manuscript", manuscript);
        return "author/submit";
    }

    // 处理投稿/保存草稿
    @PostMapping("/doSubmit")
    public String doSubmit(@ModelAttribute Manuscript manuscript,
                           @RequestParam("action") String action,
                           @RequestParam("msFile") MultipartFile msFile,
                           @RequestParam("clFile") MultipartFile clFile,
                           HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // 1. 处理文件上传：手稿 (Manuscript)
        if (msFile != null && !msFile.isEmpty()) {
            String path = saveFile(msFile, "manuscripts");
            manuscript.setManuscriptPath(path);
        }

        // 2. 处理文件上传：附信 (Cover Letter)
        if (clFile != null && !clFile.isEmpty()) {
            String path = saveFile(clFile, "covers");
            manuscript.setCoverLetterPath(path);
        }

        // 3. 调用重写的级联保存服务
        authorService.fullSubmit(manuscript, action, loginUser);

        return "redirect:/author/manuscript/list";
    }
    // 进入编辑草稿页面
    @GetMapping("/edit")
    public String toEdit(@RequestParam("id") int id, Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // getManuscriptByIdAndAuthor 已在 Service 中重写，会带出作者和审稿人列表
        Manuscript manuscript = authorService.getManuscriptByIdAndAuthor(id, loginUser.getUserId());
        if (manuscript == null) return "redirect:/author/manuscript/list";

        model.addAttribute("manuscript", manuscript);
        return "author/submit";
    }
    private String saveFile(MultipartFile file, String subDir) {
        try {
            // 获取上传文件的原始名称
            String originalFileName = file.getOriginalFilename();
            // 使用 UUID 重命名防止中文乱码和文件名冲突
            String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + suffix;

            // 确定物理存储路径 (建议在本地磁盘创建一个固定目录，如 C:/uploads 或 /tmp/uploads)
            String uploadDir = "D:/java_uploads/" + subDir + "/";
            File destDir = new File(uploadDir);
            if (!destDir.exists()) destDir.mkdirs();

            File destFile = new File(destDir, fileName);
            file.transferTo(destFile);

            // 返回给数据库存的相对路径或标识
            return "/uploads/" + subDir + "/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}