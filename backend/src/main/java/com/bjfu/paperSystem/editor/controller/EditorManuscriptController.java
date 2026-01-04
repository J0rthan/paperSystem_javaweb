package com.bjfu.paperSystem.editor.controller;

import com.bjfu.paperSystem.dto.EditorManuscriptDTO;
import com.bjfu.paperSystem.editor.service.EditorManuscriptService;
import com.bjfu.paperSystem.javabeans.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/editor")
public class EditorManuscriptController {

    @Autowired
    private EditorManuscriptService manuscriptService;

    // 首页
    @GetMapping
    public String index(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/Login.html";
        model.addAttribute("username", loginUser.getFullName());
        return "editor";
    }

    // === 修改点：使用 DTO 加载稿件列表 ===
    @GetMapping("/manuscripts")
    public String assignedManuscripts(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/Login.html";

        // 调用 Service 获取 DTO 列表
        List<EditorManuscriptDTO> list = manuscriptService.getMyManuscripts(loginUser.getUserId());
        model.addAttribute("manuscripts", list);

        return "editor/manuscripts"; // 对应 templates/editor/manuscripts.html
    }
}