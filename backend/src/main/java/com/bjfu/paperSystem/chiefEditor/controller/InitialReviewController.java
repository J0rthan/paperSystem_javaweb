package com.bjfu.paperSystem.chiefEditor.controller;

import com.bjfu.paperSystem.chiefEditor.service.InitialReviewService;
import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/chiefeditor")
public class InitialReviewController {

    @Autowired
    private InitialReviewService initialReviewService;

    // 初审列表页：显示所有待初审稿件（status=SUBMITTED）
    @GetMapping("/initial-review")
    public String initialReviewList(Model model) {
        List<Manuscript> list = initialReviewService.getSubmittedManuscripts();
        model.addAttribute("manuscripts", list);
        return "chiefeditor/initial-review";
    }

    // 提交初审决策：分配编辑 or 拒稿
    @PostMapping("/initial-review/decision")
    public String doInitialDecision(@RequestParam int manuscriptId,
                                    @RequestParam String decision,
                                    @RequestParam(required = false) Integer editorId) {
        initialReviewService.initialDecision(manuscriptId, decision, editorId);
        return "redirect:/chiefeditor/initial-review";
    }
}