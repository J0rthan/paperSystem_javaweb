package com.bjfu.paperSystem.editor.controller;

import com.bjfu.paperSystem.javabeans.*;
import com.bjfu.paperSystem.editor.service.EditorProcessService;
import com.bjfu.paperSystem.editor.dao.EditorUserDao;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/editor")
public class EditorProcessController {

    @Autowired
    private EditorProcessService processService;

    @Autowired
    private EditorUserDao userDao;

    @GetMapping("/process/{manuscriptId}")
    public String processPage(@PathVariable int manuscriptId,
                              @RequestParam(required = false) String tab,
                              @RequestParam(required = false) String keyword,
                              Model model,
                              HttpSession session) {

        User currentUser = (User) session.getAttribute("loginUser");
        if (currentUser == null) return "redirect:/login";

        // 1. 获取稿件详情
        Manuscript manuscript = processService.getManuscriptDetail(manuscriptId, currentUser.getUserId());
        if (manuscript == null) return "redirect:/editor/manuscripts"; // 没找到则返回列表

        // 2. 获取作者名
        User author = userDao.findById(manuscript.getAuthorId()).orElse(new User());
        model.addAttribute("authorName", author.getFullName());

        // 3. 获取当前已存在的审稿记录 (用于右侧列表展示)
        List<Review> existingReviews = processService.getCurrentReviews(manuscriptId);
        // 手动补充审稿人名字 (如果Review实体没有关联User对象的话)
        for (Review r : existingReviews) {
            userDao.findById(r.getReviewerId()).ifPresent(u -> r.setOpinion(u.getFullName()));
            // 借用 opinion 字段暂存名字，或者你需要修改 Review 实体加个 @Transient 字段。
            // 建议：前端页面直接用 r.reviewerId 查不太方便，这里偷懒用 opinion 存名字传给前端
            // 如果 Review 实体有关联 @ManyToOne User reviewer，则前端直接 r.reviewer.fullName 即可
        }
        model.addAttribute("existingReviews", existingReviews);

        // 4. 处理搜索
        if ("review".equals(tab) || keyword != null) {
            List<User> reviewers = processService.searchReviewers(keyword);
            model.addAttribute("reviewers", reviewers);
            model.addAttribute("keyword", keyword);
        }

        model.addAttribute("manuscript", manuscript);
        model.addAttribute("activeTab", tab);

        // 1. 获取当前状态
        String status = manuscript.getStatus();

        // 将判断条件改为：如果是 "Under Review" 或者 "With Editor"，都进行检查
        if ("Under Review".equalsIgnoreCase(status) || "With Editor".equalsIgnoreCase(status)) {

            List<Review> reviews = processService.getCurrentReviews(manuscriptId);
            StringBuilder alertMsg = new StringBuilder();
            LocalDateTime now = LocalDateTime.now();

            for (Review r : reviews) {
                // 【修改点2】 安全检查：
                // 1. 必须有截止日期 (防止 null 报错)
                // 2. 状态必须是 ACCEPTED (审稿中) 或者 PENDING (等待回复)
                //    (如果你只关心已接单的，就把 || "PENDING"... 这一段删掉)
                if (r.getDeadline() != null &&
                        ("ACCEPTED".equalsIgnoreCase(r.getStatus()) || "PENDING".equalsIgnoreCase(r.getStatus()))) {

                    long hoursLeft = java.time.temporal.ChronoUnit.HOURS.between(now, r.getDeadline());

                    if (hoursLeft < 0) {
                        // 已逾期 - 红色提醒
                        alertMsg.append("<div class='text-danger mb-2'>")
                                .append("<i class='bi bi-exclamation-triangle-fill'></i> <strong>Reviewer (ID: ")
                                .append(r.getReviewerId()).append(")</strong> 已逾期!")
                                .append("</div>");
                    } else if (hoursLeft < 72) {
                        // 剩余不足3天 - 黄色提醒
                        alertMsg.append("<div class='text-warning mb-2'>")
                                .append("<i class='bi bi-clock-history'></i> <strong>Reviewer (ID: ")
                                .append(r.getReviewerId()).append(")</strong> 截止日期不足 3 天!")
                                .append("</div>");
                    }
                }
            }

            // 如果拼接出了消息，就放入 model
            if (alertMsg.length() > 0) {
                model.addAttribute("alertMessage", alertMsg.toString());
            }
        }

        // 设定默认 Tab
        if (tab == null) tab = "info";
        model.addAttribute("activeTab", tab);

        return "editor/process";
    }

    @PostMapping("/process/invite")
    public String inviteReviewer(
            @RequestParam("manuscriptId") int manuscriptId,
            @RequestParam("reviewerId") int reviewerId,
            @RequestParam("deadlineStr") String deadlineStr,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("loginUser");
        if (currentUser == null) return "redirect:/login";

        // 默认处理日期
        LocalDateTime deadline;
        try {
            deadline = LocalDate.parse(deadlineStr).atTime(LocalTime.MAX);
        } catch (Exception e) {
            deadline = LocalDateTime.now().plusDays(14);
        }

        processService.inviteReviewer(manuscriptId, reviewerId, deadline, currentUser.getUserId());

        // 保持在 review 标签页
        return "redirect:/editor/process/" + manuscriptId + "?tab=review";
    }

    /**
     * 【新增】撤销邀请接口
     */
    @PostMapping("/process/revoke")
    public String revokeInvitation(
            @RequestParam("reviewId") int reviewId,
            @RequestParam("manuscriptId") int manuscriptId,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("loginUser");
        if (currentUser == null) return "redirect:/login";

        processService.revokeInvitation(reviewId, currentUser.getUserId());

        return "redirect:/editor/process/" + manuscriptId + "?tab=review";
    }

    @GetMapping("/download/{manuscriptId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable int manuscriptId, HttpSession session) {
        User currentUser = (User) session.getAttribute("loginUser");
        if (currentUser == null) return ResponseEntity.status(403).build();

        // 获取稿件信息
        Manuscript manuscript = processService.getManuscriptDetail(manuscriptId, currentUser.getUserId());

        // 判空检查
        if (manuscript == null || manuscript.getManuscriptPath() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            String filePathString = manuscript.getManuscriptPath();
            File file;

            // 处理路径：如果路径是绝对路径（包含盘符或以/开头但不是相对web路劲），直接使用
            // 但根据你的 AuthorController 参考，看起来主要是存的相对路径 "/uploads/..."

            String projectPath = System.getProperty("user.dir");
            // 尝试在源码目录找 (开发环境)
            String staticPath = "/backend/src/main/resources/static";
            if (filePathString.startsWith("/")) {
                file = new File(projectPath + staticPath + filePathString);
            } else {
                file = new File(projectPath + staticPath + "/" + filePathString);
            }

            // 如果源码目录找不到，尝试在编译后的 target 目录找 (运行环境)
            if (!file.exists()) {
                String targetPath = "/target/classes/static";
                if (filePathString.startsWith("/")) {
                    file = new File(projectPath + targetPath + filePathString);
                } else {
                    file = new File(projectPath + targetPath + "/" + filePathString);
                }
            }

            // 最后确认文件是否存在
            if (!file.exists()) {
                System.out.println("File not found: " + file.getAbsolutePath());
                return ResponseEntity.notFound().build();
            }

            // 构造资源返回
            Resource resource = new UrlResource(file.toPath().toUri());
            if (resource.exists() || resource.isReadable()) {
                // 处理中文文件名乱码
                String originalFileName = file.getName();
                String encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/process/remind")
    public String remindReviewer(@RequestParam int reviewId,
                                 @RequestParam int manuscriptId,
                                 HttpSession session,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return "redirect:/Login.html";

        // 调用 Service 发送邮件
        processService.sendManualReminder(reviewId, user.getUserId());

        // 添加成功提示消息
        redirectAttributes.addFlashAttribute("message", "已向审稿人发送催审邮件！");

        // 重定向回当前页面
        return "redirect:/editor/process/" + manuscriptId + "?tab=reviewers";
    }

}