package com.bjfu.paperSystem.chiefEditor.controller;

import com.bjfu.paperSystem.javabeans.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chiefeditor")
public class chiefEditorController {
    @GetMapping
    public String chiefEditor(HttpSession session, Model model) {
        // 检查登录状态
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/Login.html";
        }
        // 将用户信息放到 Model 中，方便页面访问
        model.addAttribute("username", loginUser.getUserName());
        return "chiefeditor";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/index";
    }
}
