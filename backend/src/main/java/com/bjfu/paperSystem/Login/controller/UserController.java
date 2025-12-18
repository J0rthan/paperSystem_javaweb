package com.bjfu.paperSystem.Login.controller;

import com.bjfu.paperSystem.Login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.bjfu.paperSystem.Login.javabeans.User;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

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

        // 如果是超级管理员(super_admin)
        if ("super_admin".equalsIgnoreCase(userType)) {
            return "redirect:/superadmin";
        }
        // 如果是系统管理员(sys_admin)
        else if ("sys_admin".equalsIgnoreCase(userType)) {
            return "redirect:/sysadmin";
        }
        // 如果两者都不是(与论文业务操作有关人员)
        else {
            return "redirect:/user/index";
        }
    }
}
