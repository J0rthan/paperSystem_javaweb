package com.bjfu.paperSystem.author.controller;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.author.service.authorService;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.Versions;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
@Controller
@RequestMapping("/author/manuscript")
public class manuscriptSystemController {
    @Autowired
    private authorService authorService;
    @Autowired
    private ManuscriptDao manuscriptRepository;
    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            List<Manuscript> papers = manuscriptRepository.findByAuthorId(loginUser.getUserId());
            model.addAttribute("papers", papers);
            return "author/list";
        }
        return "redirect:/login";
    }
    @GetMapping("/submit")
    public String toSubmit() {
        return "author/submit";
    }
    @PostMapping("/doSubmit")
    public String doSubmit(Manuscript manuscript, @RequestParam("file") MultipartFile file, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        manuscript.setAuthorId(loginUser.getUserId());
        authorService.submitPaper(manuscript, new Versions(), file);
        return "redirect:/author/manuscript/list";
    }
}