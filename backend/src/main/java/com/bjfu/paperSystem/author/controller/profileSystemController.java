package com.bjfu.paperSystem.author.controller;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.author.dao.authorDao;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Objects;
@Controller
@RequestMapping("/author/profile")
public class profileSystemController {
    @Autowired
    private authorDao authorDao;
    @GetMapping
    public String profile(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:../Login.html";

        User user = authorDao.findById(loginUser.getUserId()).orElse(null);
        model.addAttribute("user", user);
        return "author/profile";
    }
    @PostMapping("/update")
    public String update(User user, HttpSession session, RedirectAttributes ra) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:../Login.html";
        User existingUser = authorDao.findByUserName(user.getUserName());
        if (existingUser != null && !Objects.equals(existingUser.getUserId(), loginUser.getUserId())) {
            ra.addFlashAttribute("error", "该账号已存在，请换一个试试！");
            return "redirect:/author/profile";
        }
        User dbUser = authorDao.findById(loginUser.getUserId()).orElse(null);
        if (dbUser != null) {
            dbUser.setUserName(user.getUserName());
            dbUser.setFullName(user.getFullName());
            dbUser.setEmail(user.getEmail());
            dbUser.setCompany(user.getCompany());
            dbUser.setInvestigationDirection(user.getInvestigationDirection());

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                dbUser.setPassword(user.getPassword());
            }
            authorDao.save(dbUser);
            session.setAttribute("loginUser", dbUser);
            ra.addFlashAttribute("msg", "信息修改成功！");
        }
        return "redirect:/author/profile";
    }
}