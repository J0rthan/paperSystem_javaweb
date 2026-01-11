package com.bjfu.paperSystem.editor.controller;

import com.bjfu.paperSystem.javabeans.*;
import com.bjfu.paperSystem.editor.service.EditorProcessService;
import com.bjfu.paperSystem.editor.dao.EditorUserDao;
import com.bjfu.paperSystem.javabeans.ClientMessage;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/editor")
public class EditorProcessController {

    @Autowired
    private EditorProcessService processService;

    @Autowired
    private EditorUserDao userDao;

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
     * AJAX接口：邀请审稿人
     */
    @PostMapping("/api/invite")
    @ResponseBody
    public Map<String, Object> inviteReviewerAjax(
            @RequestParam("manuscriptId") int manuscriptId,
            @RequestParam("reviewerId") int reviewerId,
            @RequestParam("deadlineStr") String deadlineStr,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();
        User currentUser = (User) session.getAttribute("loginUser");
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }

        try {
            LocalDateTime deadline;
            try {
                deadline = LocalDate.parse(deadlineStr).atTime(LocalTime.MAX);
            } catch (Exception e) {
                deadline = LocalDateTime.now().plusDays(14);
            }

            processService.inviteReviewer(manuscriptId, reviewerId, deadline, currentUser.getUserId());
            result.put("success", true);
            result.put("message", "邀请已发送");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "邀请失败：" + e.getMessage());
        }
        return result;
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

    /**
     * AJAX接口：撤销邀请
     */
    @PostMapping("/api/revoke")
    @ResponseBody
    public Map<String, Object> revokeInvitationAjax(
            @RequestParam("reviewId") int reviewId,
            @RequestParam("manuscriptId") int manuscriptId,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();
        User currentUser = (User) session.getAttribute("loginUser");
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }

        try {
            processService.revokeInvitation(reviewId, currentUser.getUserId());
            result.put("success", true);
            result.put("message", "邀请已撤销");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "撤销失败：" + e.getMessage());
        }
        return result;
    }

    @GetMapping("/download/{manuscriptId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable int manuscriptId,
            @RequestParam(required = false, defaultValue = "manuscript") String type, // 新增参数：manuscript 或 coverLetter
            HttpSession session) {

        User currentUser = (User) session.getAttribute("loginUser");
        if (currentUser == null) return ResponseEntity.status(403).build();

        Manuscript manuscript = processService.getManuscriptDetail(manuscriptId, currentUser.getUserId());
        if (manuscript == null) return ResponseEntity.notFound().build();

        // 根据type参数决定下载哪个文件
        String filePathString;
        if ("coverLetter".equals(type) && manuscript.getCoverLetterPath() != null) {
            filePathString = manuscript.getCoverLetterPath();
        } else if (manuscript.getManuscriptPath() != null) {
            filePathString = manuscript.getManuscriptPath();
        } else {
            return ResponseEntity.notFound().build();
        }

        try {
            String projectPath = System.getProperty("user.dir");
            String staticPath = "/backend/src/main/resources/static";
            File file;

            if (filePathString.startsWith("/")) {
                file = new File(projectPath + staticPath + filePathString);
            } else {
                file = new File(projectPath + staticPath + "/" + filePathString);
            }

            if (!file.exists()) {
                String targetPath = "/target/classes/static";
                if (filePathString.startsWith("/")) {
                    file = new File(projectPath + targetPath + filePathString);
                } else {
                    file = new File(projectPath + targetPath + "/" + filePathString);
                }
            }

            if (!file.exists()) {
                System.out.println("File not found: " + file.getAbsolutePath());
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(file.toPath().toUri());
            if (resource.exists() || resource.isReadable()) {
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

    @PostMapping("/submitRecommendation")
    @ResponseBody
    public Map<String, Object> submitRecommendation(
            @RequestParam("manuscriptId") int manuscriptId,
            @RequestParam("recommendation") String recommendation,
            @RequestParam("comment") String comment,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();
        
        // 1. 获取当前登录用户
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }

        try {
            // 2. 调用 Service 处理业务
            processService.submitRecommendation(manuscriptId, recommendation, comment, user.getUserId());

            // 3. 反馈成功信息
            result.put("success", true);
            result.put("message", "建议已成功提交，稿件已转交主编！");
        } catch (IllegalStateException | IllegalArgumentException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "提交失败：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "提交失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 发送消息给作者（AJAX接口）
     */
    @PostMapping("/process/sendMessage")
    @ResponseBody
    public Map<String, Object> sendMessageToAuthor(
            @RequestParam("manuscriptId") int manuscriptId,
            @RequestParam("messageBody") String messageBody,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();
        User currentUser = (User) session.getAttribute("loginUser");

        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "用户未登录");
            return result;
        }

        try {
            // 获取稿件信息以获取作者ID
            Manuscript manuscript = processService.getManuscriptDetail(manuscriptId, currentUser.getUserId());
            if (manuscript == null) {
                result.put("success", false);
                result.put("message", "稿件不存在或无权限");
                return result;
            }

            // 调用Service发送消息
            processService.sendMessageToAuthor(
                    manuscriptId,
                    manuscript.getAuthorId(), // 作者ID
                    messageBody,
                    currentUser.getUserId()
            );

            result.put("success", true);
            result.put("message", "消息发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "发送失败：" + e.getMessage());
        }

        return result;
    }
}