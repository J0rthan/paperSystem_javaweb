package com.bjfu.paperSystem.editor.controller;

import com.bjfu.paperSystem.javabeans.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/editor")
public class EditorController {

    @GetMapping
    public String index(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/Login.html";

        model.addAttribute("username", loginUser.getFullName());
        // 注意：这里返回 "editor" 对应的是 templates/editor.html
        return "editor";
    }
}