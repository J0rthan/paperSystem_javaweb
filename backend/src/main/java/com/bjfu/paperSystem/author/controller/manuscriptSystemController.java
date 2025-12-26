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
    private logService logService;
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
        java.util.Map<Integer, String> userNames = new java.util.HashMap<>();
        for (Logs log : logs) {
            User opUser = authorService.findUserById(log.getOporId());
            if (opUser != null) {
                String name = "Reviewer".equalsIgnoreCase(opUser.getUserType()) ? "匿名审稿人" : opUser.getFullName();
                userNames.put(log.getOporId(), name);
            } else {
                userNames.put(log.getOporId(), "系统用户");
            }
        }
        model.addAttribute("manuscript", manuscript);
        model.addAttribute("logs", logs);
        model.addAttribute("versions", versions);
        model.addAttribute("userNames", userNames);
        return "author/track";
    }
    @GetMapping("/revise")
    public String toRevise(@RequestParam("id") int id, Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        Manuscript manuscript = authorService.getManuscriptByIdAndAuthor(id, loginUser.getUserId());
        if (manuscript == null || !"Need Revision".equals(manuscript.getStatus())) {
            return "redirect:/author/manuscript/list";
        }
        model.addAttribute("manuscript", manuscript);
        return "author/revise";
    }
    @PostMapping("/doRevise")
    public String doRevise(@RequestParam("manuscriptId") int manuscriptId,
                           @RequestParam("cleanFile") MultipartFile cleanFile,
                           @RequestParam("markedFile") MultipartFile markedFile,
                           @RequestParam("replyFile") MultipartFile replyFile,
                           @RequestParam("responseText") String responseText,
                           HttpSession session, RedirectAttributes ra) {

        User loginUser = (User) session.getAttribute("loginUser");
        try {
            authorService.submitRevision(manuscriptId, cleanFile, markedFile, replyFile, responseText, loginUser);
            ra.addFlashAttribute("message", "修回已提交，进入下一轮审稿");
            return "redirect:/author/manuscript/list";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "提交失败：" + e.getMessage());
            return "redirect:/author/manuscript/revise?id=" + manuscriptId;
        }
    }
    private String saveFile(MultipartFile file, String subDir) {
        try {
            String originalFileName = file.getOriginalFilename();
            String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + suffix;
            String projectPath = System.getProperty("user.dir");
            String srcPath = projectPath + "/backend/src/main/resources/static/uploads/" + subDir + "/";
            File srcDir = new File(srcPath);
            if (!srcDir.exists()) srcDir.mkdirs();
            File srcFile = new File(srcDir, fileName);
            String classPath = java.net.URLDecoder.decode(this.getClass().getClassLoader().getResource("").getPath(), "UTF-8");
            if (System.getProperty("os.name").toLowerCase().contains("win") && classPath.startsWith("/")) {
                classPath = classPath.substring(1);
            }
            String targetPath = classPath + "static/uploads/" + subDir + "/";
            File targetDir = new File(targetPath);
            if (!targetDir.exists()) targetDir.mkdirs();
            File targetFile = new File(targetDir, fileName);
            file.transferTo(srcFile);
            org.springframework.util.FileCopyUtils.copy(srcFile, targetFile);
            return "/uploads/" + subDir + "/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}