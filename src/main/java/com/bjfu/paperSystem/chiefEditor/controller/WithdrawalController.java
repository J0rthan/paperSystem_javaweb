package com.bjfu.paperSystem.chiefEditor.controller;

import com.bjfu.paperSystem.chiefEditor.service.WithdrawalService;
import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chiefeditor")
public class WithdrawalController {

    @Autowired
    private WithdrawalService withdrawalService;

    // 撤稿列表页
    @GetMapping("/withdrawals")
    public String listWithdrawals(Model model) {
        List<Manuscript> manuscripts = withdrawalService.listWithdrawalCandidates();
        model.addAttribute("manuscripts", manuscripts);
        return "chiefeditor/withdrawals";
    }

    // 执行撤稿
    @PostMapping("/withdrawals/do")
    public String doWithdraw(@RequestParam int manuscriptId,
                             @RequestParam String reason) {
        withdrawalService.withdrawManuscript(manuscriptId, reason);
        return "redirect:/chiefeditor/withdrawals";
    }
}