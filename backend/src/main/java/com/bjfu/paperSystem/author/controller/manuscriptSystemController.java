package com.bjfu.paperSystem.author.controller;
import com.bjfu.paperSystem.author.service.logService;
import com.bjfu.paperSystem.author.service.authorService;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.javabeans.Manuscript;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Controller
@RequestMapping("/author/manuscript")
public class manuscriptSystemController {
    @Autowired
    private authorService authorService;
    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        Map<String, List<Manuscript>> data = authorService.getCategorizedManuscripts(loginUser.getUserId());
        model.addAllAttributes(data);
        return "author/list";
    }
    @PostMapping("/doSubmit")
    public String doSubmit(@ModelAttribute Manuscript manuscript,
                           @RequestParam("action") String action,
                           HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        authorService.handleSubmission(manuscript, action, loginUser);
        return "redirect:/author/manuscript/list";
    }
    @GetMapping("/edit")
    public String toEdit(@RequestParam("id") int id, Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        Manuscript manuscript = authorService.getManuscriptByIdAndAuthor(id, loginUser.getUserId());
        if (manuscript == null) return "redirect:/author/manuscript/list";
        model.addAttribute("manuscript", manuscript);
        return "author/submit";
    }
}