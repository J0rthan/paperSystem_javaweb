package com.bjfu.paperSystem.optionAdmin.controller;

import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.optionAdmin.service.optionAdminService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/optionadmin")
public class ManuscriptManagementController {
    @Autowired
    private optionAdminService service;

    // 查看所有稿件
    @GetMapping("/manuscripts")
    public String getAllManuscripts(HttpSession session, Model model) {
        List<Manuscript> list = service.getAllManuscripts();
        model.addAttribute("manuscripts", list);
        return "optionadmin/manuscripts";
    }

    // 查看单个稿件详情
    @GetMapping("/manuscripts/{id}")
    public String getManuscriptDetails(@PathVariable("id") Integer manuscriptId, Model model) {
        Manuscript manuscript = service.getManuscriptById(manuscriptId);
        model.addAttribute("manuscript", manuscript);
        return "optionadmin/manuscript-details";
    }

    

    // 通过稿件审核
    @PostMapping("/manuscripts/approve")
    public String approveManuscript(@RequestParam("manuscriptId") Integer manuscriptId) {
        // 获取稿件对象
        Manuscript manuscript = service.getManuscriptById(manuscriptId);
        // 使用稿件的authorId作为操作ID
        Integer operatorId = manuscript.getAuthorId();
        service.approveManuscript(manuscriptId, operatorId);
        return "redirect:/optionadmin/manuscripts";
    }

    // 拒绝稿件审核
    @PostMapping("/manuscripts/reject")
    public String rejectManuscript(@RequestParam("manuscriptId") Integer manuscriptId) {
        // 获取稿件对象
        Manuscript manuscript = service.getManuscriptById(manuscriptId);
        // 使用稿件的authorId作为操作ID
        Integer operatorId = manuscript.getAuthorId();
        service.rejectManuscript(manuscriptId, operatorId);
        return "redirect:/optionadmin/manuscripts";
    }
}
