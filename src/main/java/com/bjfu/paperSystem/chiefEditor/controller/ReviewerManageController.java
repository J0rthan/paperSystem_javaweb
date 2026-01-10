package com.bjfu.paperSystem.chiefEditor.controller;

import com.bjfu.paperSystem.chiefEditor.service.ReviewerManageService;
import com.bjfu.paperSystem.javabeans.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chiefeditor")
public class ReviewerManageController {

    @Autowired
    private ReviewerManageService reviewerManageService;

    @GetMapping("/reviewers")
    public String list(Model model) {
        List<User> reviewers = reviewerManageService.listReviewers();
        model.addAttribute("reviewers", reviewers);
        return "chiefeditor/reviewers";
    }

    @PostMapping("/reviewers/enable")
    public String enable(@RequestParam int userId) {
        reviewerManageService.enableReviewer(userId);
        return "redirect:/chiefeditor/reviewers";
    }

    @PostMapping("/reviewers/disable")
    public String disable(@RequestParam int userId) {
        reviewerManageService.disableReviewer(userId);
        return "redirect:/chiefeditor/reviewers";
    }

    @PostMapping("/reviewers/delete")
    public String delete(@RequestParam int userId) {
        reviewerManageService.deleteReviewer(userId);
        return "redirect:/chiefeditor/reviewers";
    }

    @PostMapping("/reviewers/create")
    public String create(@RequestParam String userName,
                         @RequestParam String fullName,
                         @RequestParam String email) {
        reviewerManageService.createReviewer(userName, fullName, email);
        return "redirect:/chiefeditor/reviewers";
    }
}