
package com.bjfu.paperSystem.optionAdmin.controller;

import com.bjfu.paperSystem.javabeans.Journal;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.optionAdmin.service.JournalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/optionadmin/journal")
public class JournalController {

    @Autowired
    private JournalService journalService;

    // 期刊列表页面
    @GetMapping
    public String journalList(Model model, @RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String status) {
        List<Journal> journalList;
        
        // 根据条件查询期刊
        if (keyword != null && !keyword.isEmpty()) {
            journalList = journalService.searchJournal(keyword);
        } else if (status != null && !status.isEmpty()) {
            journalList = journalService.getJournalByStatus(status);
        } else {
            journalList = journalService.getAllJournal();
        }
        
        model.addAttribute("journalList", journalList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "optionadmin/journal/journalList";
    }

    // 新增期刊页面
    @GetMapping("/add")
    public String addJournal(Model model) {
        Journal journal = new Journal();
        model.addAttribute("journal", journal);
        return "optionadmin/journal/addJournal";
    }

    // 编辑期刊页面
    @GetMapping("/edit/{id}")
    public String editJournal(@PathVariable("id") Integer journalId, Model model) {
        Journal journal = journalService.getJournalById(journalId);
        model.addAttribute("journal", journal);
        return "optionadmin/journal/editJournal";
    }

    // 保存期刊（新增和编辑）
    @PostMapping("/save")
    public String saveJournal(@ModelAttribute Journal journal, @RequestParam String action, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/optionadmin/login";
        
        // 设置创建者ID
        journal.setCreatorId(loginUser.getUserId());
        
        // 根据操作类型处理
        if (action.equals("saveAndPublish")) {
            // 保存并发布
            if (journal.getJournalId() > 0) {
                // 编辑期刊
                Journal existingJournal = journalService.getJournalById(journal.getJournalId());
                if (existingJournal != null) {
                    journal.setCreateTime(existingJournal.getCreateTime());
                }
                journalService.updateJournal(journal);
                journalService.publishJournal(journal.getJournalId());
            } else {
                // 新增期刊
                journalService.saveJournal(journal);
            }
        } else if (action.equals("saveAsDraft")) {
            // 保存为草稿
            if (journal.getJournalId() > 0) {
                Journal existingJournal = journalService.getJournalById(journal.getJournalId());
                if (existingJournal != null) {
                    journal.setCreateTime(existingJournal.getCreateTime());
                }
                journal.setStatus("draft");
                journalService.updateJournal(journal);
            } else {
                journalService.saveDraft(journal);
            }
        }
        
        return "redirect:/optionadmin/journal";
    }

    // 删除期刊
    @PostMapping("/delete/{id}")
    public String deleteJournal(@PathVariable("id") Integer journalId) {
        journalService.deleteJournal(journalId);
        return "redirect:/optionadmin/journal";
    }
}