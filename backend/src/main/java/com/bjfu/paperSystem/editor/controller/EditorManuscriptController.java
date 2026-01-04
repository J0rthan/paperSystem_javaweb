package com.bjfu.paperSystem.editor.controller;

import com.bjfu.paperSystem.editor.service.EditorManuscriptService;
import com.bjfu.paperSystem.javabeans.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/editor")
public class EditorManuscriptController {

    @Autowired
    private EditorManuscriptService manuscriptService;

    @GetMapping("/manuscripts")
    public String list(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        // 这里返回 "editor/manuscripts" 对应 templates/editor/manuscripts.html
        model.addAttribute("manuscripts", manuscriptService.getMyManuscripts(user.getUserId()));
        return "editor/manuscripts";
    }
}