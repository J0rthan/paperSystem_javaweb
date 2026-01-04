package com.bjfu.paperSystem.optionAdmin.controller;

import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.javabeans.EmailMessage;
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
    public String approveManuscript(@RequestParam("manuscriptId") Integer manuscriptId, HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        // 使用当前用户的ID作为操作ID
        Integer operatorId = loginUser.getUserId();
        service.approveManuscript(manuscriptId, operatorId);
        return "redirect:/optionadmin/manuscripts";
    }

    // 拒绝稿件审核
    @PostMapping("/manuscripts/reject")
    public String rejectManuscript(@RequestParam("manuscriptId") Integer manuscriptId, HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        // 使用当前用户的ID作为操作ID
        Integer operatorId = loginUser.getUserId();
        service.rejectManuscript(manuscriptId, operatorId);
        return "redirect:/optionadmin/manuscripts";
    }
    
    // 拒绝稿件并发送反馈
    @PostMapping("/manuscripts/rejectWithFeedback")
    public String rejectWithFeedback(@RequestParam("manuscriptId") Integer manuscriptId,
                                   @RequestParam("messageBody") String messageBody,
                                   HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        // 使用当前用户的ID作为操作ID
        Integer operatorId = loginUser.getUserId();
        
        // 调用服务层方法处理拒绝和发送反馈
        service.rejectManuscriptWithFeedback(manuscriptId, operatorId, messageBody, loginUser.getEmail());
        
        return "redirect:/optionadmin/manuscripts";
    }
}
