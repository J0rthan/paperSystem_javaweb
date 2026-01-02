package com.bjfu.paperSystem.Login.controller;

import com.bjfu.paperSystem.Login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.Login.dao.UserDao;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserDao userDao;

    // 处理登陆请求
    @PostMapping("/login")
    public String login(User user, HttpSession session) {
        // 先获取用户类型，调用Service层接口
        User dbUser = userService.login(user.getUserName(), user.getPassword());

        if (dbUser == null) {
            // 检查用户名是否存在
            boolean userNameExists = userDao.findByUserName(user.getUserName()).isPresent();
            if (userNameExists) {
                // 用户名存在但密码错误
                return "redirect:/login-error?error=password";
            } else {
                // 用户名不存在
                return "redirect:/login-error?error=username";
            }
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
                return "redirect:/login-error?error=invalid_role";
            }
        }
        else {
            // 账户被封禁或状态异常
            return "redirect:/login-error?error=account_disabled";
        }
    }
    
    // 显示登录错误页面
    @GetMapping("/login-error")
    public String loginError(@RequestParam(value = "error", defaultValue = "general") String error, Model model) {
        String errorMessage = "";
        String errorTitle = "";
        
        switch (error) {
            case "username":
                errorTitle = "用户名不存在";
                errorMessage = "您输入的用户名不存在，请检查后重试。";
                break;
            case "password":
                errorTitle = "密码错误";
                errorMessage = "您输入的密码不正确，请检查后重试。";
                break;
            case "account_disabled":
                errorTitle = "账户已被禁用";
                errorMessage = "您的账户已被禁用，无法登录。请联系系统管理员。";
                break;
            case "invalid_role":
                errorTitle = "用户角色异常";
                errorMessage = "您的用户角色异常，无法登录。请联系系统管理员。";
                break;
            default:
                errorTitle = "登录失败";
                errorMessage = "登录过程中出现错误，请重试。";
                break;
        }
        
        model.addAttribute("errorTitle", errorTitle);
        model.addAttribute("errorMessage", errorMessage);
        return "login-error";
    }
    
    // 处理注册请求
    @PostMapping("/register")
    public String register(User user, HttpSession session) {
        // 检查用户名是否存在
        if (userService.isUserNameExists(user.getUserName())) {
            return "redirect:/?error=username_exists";
        }
        // 调用服务层注册用户
        User newUser = userService.register(user.getUserName(), user.getPassword(), user.getUserType());
        if (newUser == null) {
            return "redirect:/?error=register_error";
        }
        // 注册成功后自动登录
        session.setAttribute("loginUser", newUser);
        // 根据角色重定向
        String userType = newUser.getUserType();
        if ("author".equalsIgnoreCase(userType)) {
            return "redirect:/author";
        } else if ("reviewer".equalsIgnoreCase(userType)) {
            return "redirect:/reviewer";
        } else {
            return "redirect:/?error=register_error";
        }
    }
}
