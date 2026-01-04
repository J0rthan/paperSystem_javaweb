package com.bjfu.paperSystem.editor.controller;

import com.bjfu.paperSystem.editor.service.RemindService;
import com.bjfu.paperSystem.javabeans.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/editor")
public class RemindController {

    @Autowired
    private RemindService remindService;

    // 进入“审稿监控/催审”页面
    @GetMapping("/reminders")
    public String monitor(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return "redirect:/Login.html";

        model.addAttribute("pendingList", remindService.getPendingReviews(user.getUserId()));
        return "editor/reminders";
    }

    // 执行催审
    @PostMapping("/remind/send")
    public String sendReminder(@RequestParam int reviewId, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        remindService.sendReminder(reviewId, user.getUserId());
        return "redirect:/editor/reminders";
    }
}