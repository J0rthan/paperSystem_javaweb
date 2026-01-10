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

    // 1. 终审列表页
    @GetMapping("/decisions")
    public String listDecisions(Model model) {
        // 获取所有等待终审的稿件
        List<Manuscript> manuscripts = finalDecisionService.listPendingFinalDecisions();
        model.addAttribute("manuscripts", manuscripts);
        return "chiefeditor/decisions";
    }

    // 2. 提交终审决策
    @PostMapping("/decisions/do")
    public String doDecision(@RequestParam int manuscriptId,
                             @RequestParam String decision,
                             @RequestParam(required = false) String comment) {
        // 调用 Service 执行逻辑
        finalDecisionService.makeFinalDecision(manuscriptId, decision, comment);

        // 处理完后重定向回列表，防止表单重复提交
        return "redirect:/chiefeditor/decisions";
    }
}