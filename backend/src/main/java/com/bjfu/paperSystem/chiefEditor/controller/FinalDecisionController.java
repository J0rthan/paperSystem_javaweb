package com.bjfu.paperSystem.chiefEditor.controller;

import com.bjfu.paperSystem.chiefEditor.service.FinalDecisionService;
import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chiefeditor")
public class FinalDecisionController {

    @Autowired
    private FinalDecisionService finalDecisionService;

    // 终审决策列表页
    @GetMapping("/decisions")
    public String listDecisions(Model model) {
        List<Manuscript> manuscripts = finalDecisionService.listPendingFinalDecisions();
        model.addAttribute("manuscripts", manuscripts);
        return "chiefeditor/decisions";
    }

    // 某篇稿件的终审详情页（如果你想做详情页，可用）
    @GetMapping("/decisions/{id}")
    public String decisionDetail(@PathVariable("id") int manuscriptId, Model model) {
        Manuscript m = finalDecisionService.getManuscriptDetail(manuscriptId);
        model.addAttribute("manuscript", m);
        return "chiefeditor/decision-detail";
    }

    // 提交终审决策（从列表页或详情页表单提交）
    @PostMapping("/decisions/do")
    public String doDecision(@RequestParam int manuscriptId,
                             @RequestParam String decision,
                             @RequestParam(required = false) String comment) {
        finalDecisionService.makeFinalDecision(manuscriptId, decision, comment);
        return "redirect:/chiefeditor/decisions";
    }
}