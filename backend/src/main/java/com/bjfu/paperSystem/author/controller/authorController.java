package com.bjfu.paperSystem.author.controller;
import com.bjfu.paperSystem.javabeans.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/author")
public class authorController {
    @GetMapping
    public String index(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/Login.html";
        model.addAttribute("username", loginUser.getFullName());
        return "author";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/Login.html";
    }
}