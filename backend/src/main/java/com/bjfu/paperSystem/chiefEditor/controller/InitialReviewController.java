package com.bjfu.paperSystem.chiefEditor.controller;

import com.bjfu.paperSystem.chiefEditor.service.InitialReviewService;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/chiefeditor")
public class InitialReviewController {

    @Autowired
    private InitialReviewService initialReviewService;

    // 初审列表页：显示所有待分配稿件（status=Pending Allocation）
    @GetMapping("/initial-review")
    public String initialReviewList(Model model) {
        List<Manuscript> list = initialReviewService.getManuscriptsByStatus("Pending Allocation");
        model.addAttribute("manuscripts", list);
        return "chiefeditor/initial-review";
    }

    // 提交初审决策：送审 or 拒稿
    @PostMapping("/initial-review/decision")
    public String doInitialDecision(@RequestParam int manuscriptId,
                                    @RequestParam String decision,
                                    @RequestParam String assignReason,
                                    HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/Login.html";
        }
        // 获取当前登录用户id
        int userId = loginUser.getUserId();
        
        // 调用service层方法处理决策
        initialReviewService.initialDecision(manuscriptId, decision, userId, assignReason);
        return "redirect:/chiefeditor/initial-review";
    }
}