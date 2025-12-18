package com.bjfu.paperSystem.controller;

import com.bjfu.paperSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.bjfu.paperSystem.javabeans.User;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    // 重定向之后返回root.html
    @GetMapping("/root")
    public String toRootPage() {
        return "root";
    }

    // 处理登陆请求
    @PostMapping("/login")
    public String login(User user, Model model, HttpSession session) {
        // 先获取用户类型，调用Service层接口
        User dbUser = userService.login(user.getUserName(), user.getPassword());

        if (dbUser == null) {
            model.addAttribute("msg", "用户名或密码错误");
            return "login";
        }

        session.setAttribute("loginUser", dbUser);
        String userType = dbUser.getUserType();

        if ("root".equalsIgnoreCase(userType)) {
            return "redirect:/root";
        } else if ("teacher".equalsIgnoreCase(userType)) {
            return "redirect:/teacher/index";
        }
        return "redirect:/user/index";
    }
}
