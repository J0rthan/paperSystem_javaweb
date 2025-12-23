package com.bjfu.paperSystem.author.controller;
import com.bjfu.paperSystem.author.service.logService;
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
import java.time.LocalDateTime;
@Controller
@RequestMapping("/author/manuscript")
public class manuscriptSystemController {
    @Autowired
    private authorService authorService;
    @Autowired
    private logService logService;
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
    public String doSubmit(@ModelAttribute Manuscript manuscript,
                           @RequestParam("action") String action,
                           HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        manuscript.setAuthorId(loginUser.getUserId());
        if ("save".equals(action)) {
            manuscript.setStatus("Incomplete");
        } else {
            manuscript.setStatus("待形式审查");
            manuscript.setSubmitTime(LocalDateTime.now().withNano(0));
        }
        manuscriptRepository.save(manuscript);
        logService.record(loginUser.getUserId(), "提交稿件", manuscript.getManuscriptId());
        return "redirect:/author/manuscript/list";
    }
}