package com.bjfu.paperSystem.Login.controller;

import com.bjfu.paperSystem.Login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import com.bjfu.paperSystem.javabeans.User;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    // 处理登陆请求
    @PostMapping("/login")
    public String login(User user, HttpSession session) {
        // 先获取用户类型，调用Service层接口
        User dbUser = userService.login(user.getUserName(), user.getPassword());

        if (dbUser == null) {
            return "redirect:/LoginAgain.html";
        }

        session.setAttribute("loginUser", dbUser);
        String userType = dbUser.getUserType();
        String status = dbUser.getStatus();

        // 首先要确保是存在的用户，如果用户被封禁则提示无法登陆
        if ("exist".equalsIgnoreCase(status)) {
            // 如果是超级管理员(super_admin)
            if ("super_admin".equalsIgnoreCase(userType)) {
                return "redirect:/superadmin";
            }
            // 如果是系统管理员(sys_admin)
            else if ("sys_admin".equalsIgnoreCase(userType)) {
                return "redirect:/sysadmin";
            }
            //如果是作者(author)
            else if ("author".equalsIgnoreCase(userType)) {
                return "redirect:/author";
            }
            //如果是主编(chief_editor)
            else if ("chief_editor".equalsIgnoreCase(userType)) {
                return "redirect:/chiefeditor";
            }
            //如果是编辑(editor)
            else if ("editor".equalsIgnoreCase(userType)) {
                return "redirect:/editor";
            }
            //如果是编辑部管理员(option_admin)
            else if ("option_admin".equalsIgnoreCase(userType)) {
                return "redirect:/optionadmin";
            }
            //如果是审稿人(reviewer)
            else if ("reviewer".equalsIgnoreCase(userType)) {
                return "redirect:/reviewer";
            }
            else {
                return "redirect:/LoginAgain.html";
            }
        }
        else {
            return "redirect:/LoginAgain.html";
        }
    }
}
