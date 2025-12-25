package com.bjfu.paperSystem.author.controller;

import com.bjfu.paperSystem.author.service.authorService;
import com.bjfu.paperSystem.author.service.logService; // 确保导入了logService
import com.bjfu.paperSystem.javabeans.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    @Autowired
    private logService logService; // 注入日志服务

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        Map<String, List<Manuscript>> data = authorService.getCategorizedManuscripts(loginUser.getUserId());
        model.addAllAttributes(data);
        return "author/list";
    }

    @GetMapping("/submit")
    public String toSubmit(Model model) {
        Manuscript manuscript = new Manuscript();
        manuscript.setManuscriptId(0);
        manuscript.getAuthors().add(new ManuscriptAuthor());
        manuscript.getReviewers().add(new RecommendedReviewer());
        model.addAttribute("manuscript", manuscript);
        return "author/submit";
    }

    @PostMapping("/doSubmit")
    public String doSubmit(@ModelAttribute Manuscript manuscript,
                           @RequestParam("action") String action,
                           @RequestParam(value = "msFile", required = false) MultipartFile msFile,
                           @RequestParam(value = "clFile", required = false) MultipartFile clFile,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        try {
            if (msFile != null && !msFile.isEmpty()) {
                manuscript.setManuscriptPath(saveFile(msFile, "manuscripts"));
            }
            if (clFile != null && !clFile.isEmpty()) {
                manuscript.setCoverLetterPath(saveFile(clFile, "covers"));
            }
            authorService.fullSubmit(manuscript, action, loginUser);
            String msg = "save".equals(action) ? "草稿保存成功" : "稿件提交成功";
            redirectAttributes.addFlashAttribute("message", msg);
            return "redirect:list";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "操作失败：" + e.getMessage());
            return "redirect:/author/manuscript/submit";
        }
    }

    @GetMapping("/edit")
    public String toEdit(@RequestParam("id") int id, Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        Manuscript manuscript = authorService.getManuscriptByIdAndAuthor(id, loginUser.getUserId());
        if (manuscript == null) return "redirect:/author/manuscript/list";
        model.addAttribute("manuscript", manuscript);
        return "author/submit";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id, HttpSession session, RedirectAttributes ra) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        Manuscript m = authorService.getManuscriptByIdAndAuthor(id, loginUser.getUserId());
        if (m != null) {
            authorService.deleteManuscript(id);
            ra.addFlashAttribute("message", "稿件草稿已成功删除");
        }
        return "redirect:/author/manuscript/list";
    }
    @GetMapping("/track")
    public String track(@RequestParam("id") int id, Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        Manuscript manuscript = authorService.getManuscriptByIdAndAuthor(id, loginUser.getUserId());
        if (manuscript == null) return "redirect:/author/manuscript/list";
        List<Logs> logs = logService.getLogsByManuscriptId(id);
        List<Versions> versions = authorService.getVersionsByManuscriptId(id);
        model.addAttribute("manuscript", manuscript);
        model.addAttribute("logs", logs);
        model.addAttribute("versions", versions);
        return "author/track";
    }
    private String saveFile(MultipartFile file, String subDir) {
        try {
            String originalFileName = file.getOriginalFilename();
            String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + suffix;

            // --- 1. 定位【源码目录】(用于持久保存) ---
            String projectPath = System.getProperty("user.dir");
            // 确保指向 backend 模块下的 src
            String srcPath = projectPath + "/backend/src/main/resources/static/uploads/" + subDir + "/";
            File srcDir = new File(srcPath);
            if (!srcDir.exists()) srcDir.mkdirs();
            File srcFile = new File(srcDir, fileName);

            String classPath = java.net.URLDecoder.decode(this.getClass().getClassLoader().getResource("").getPath(), "UTF-8");
            // 处理 Windows 路径
            if (System.getProperty("os.name").toLowerCase().contains("win") && classPath.startsWith("/")) {
                classPath = classPath.substring(1);
            }
            String targetPath = classPath + "static/uploads/" + subDir + "/";
            File targetDir = new File(targetPath);
            if (!targetDir.exists()) targetDir.mkdirs();
            File targetFile = new File(targetDir, fileName);

            file.transferTo(srcFile);
            // 再通过工具类拷贝到编译目录（因为 file.transferTo 只能执行一次）
            org.springframework.util.FileCopyUtils.copy(srcFile, targetFile);
            return "/uploads/" + subDir + "/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}