package com.bjfu.paperSystem.editor.controller;

import com.bjfu.paperSystem.javabeans.*;
import com.bjfu.paperSystem.editor.service.EditorProcessService;
import com.bjfu.paperSystem.editor.dao.EditorUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

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

        // 3. 【新增】获取当前已存在的审稿记录 (用于右侧列表展示)
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
}