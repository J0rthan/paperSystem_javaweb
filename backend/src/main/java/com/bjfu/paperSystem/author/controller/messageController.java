package com.bjfu.paperSystem.author.controller;
import com.bjfu.paperSystem.author.service.messageService;
import com.bjfu.paperSystem.javabeans.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Map;
@Controller
@RequestMapping("/author/message")
public class messageController {
    @Autowired
    private messageService messageService;
    @GetMapping("/list")
    public String messageList(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            return "redirect:/index";
        }
        Map<String, Object> data = messageService.getAuthorMessageCenter(user);
        model.addAttribute("clientMsgs", data.get("clientMsgs"));
        model.addAttribute("emailMsgs", data.get("emailMsgs"));
        return "author/message_list";
    }
}