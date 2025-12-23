package com.bjfu.paperSystem.author.controller;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.author.dao.authorDao;
import com.bjfu.paperSystem.author.service.authorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Objects;
import com.bjfu.paperSystem.author.service.logService;
@Controller
@RequestMapping("/author/profile")
public class profileSystemController {
    @Autowired
    private authorService authorService;
    @GetMapping
    public String profile(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/Login.html";

        model.addAttribute("user", authorService.getUserById(loginUser.getUserId()));
        return "author/profile";
    }
    @PostMapping("/update")
    public String update(User user, HttpSession session, RedirectAttributes ra) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/Login.html";
        String errorMsg = authorService.updateProfile(user, loginUser.getUserId());

        if (errorMsg != null) {
            ra.addFlashAttribute("error", errorMsg);
            return "redirect:/author/profile";
        }
        session.setAttribute("loginUser", authorService.getUserById(loginUser.getUserId()));
        ra.addFlashAttribute("msg", "信息修改成功！");

        return "redirect:/author/profile";
    }
}