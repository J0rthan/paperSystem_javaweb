package com.bjfu.paperSystem.author.controller;
import com.bjfu.paperSystem.author.service.messageService;
import com.bjfu.paperSystem.clientMessageUtils.Service.clientMessageService; // 引入你刚才展示的接口
import com.bjfu.paperSystem.javabeans.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Controller
@RequestMapping("/author/message")
public class messageController {
    @Autowired
    private messageService messageService;
    @Autowired
    private clientMessageService clientMsgService;
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
    @PostMapping("/send")
    @ResponseBody
    public Map<String, Object> sendMessage(
            @RequestParam("receiverId") Integer receiverId,
            @RequestParam("manuId") Integer manuId,
            @RequestParam("body") String body,
            HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            result.put("success", false);
            return result;
        }
        try {
            String status = clientMsgService.insertMessage(
                    loginUser.getUserId(),
                    receiverId,
                    body,
                    LocalDateTime.now(),
                    manuId
            );
            result.put("success", "ok".equals(status));
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
        }
        return result;
    }
}