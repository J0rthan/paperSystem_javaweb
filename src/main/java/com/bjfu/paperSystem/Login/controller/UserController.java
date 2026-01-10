package com.bjfu.paperSystem.Login.controller;

import com.bjfu.paperSystem.Login.service.UserService;
import com.bjfu.paperSystem.superAdmin.service.superAdminService;
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

    @Autowired
    private superAdminService suService;

    // 处理登陆请求
    @PostMapping("/login")
    public String login(User user, HttpSession session) {
        try {
            // 检查用户名是否为空
            if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
                return "redirect:/login?error=username";
            }

            // 先获取用户类型，调用Service层接口
            User dbUser = userService.login(user.getUserName(), user.getPassword());

            if (dbUser == null) {
                // 检查用户名是否存在
                try {
                    boolean userNameExists = userDao.findByUserName(user.getUserName()).isPresent();
                    if (userNameExists) {
                        // 用户名存在但密码错误
                        return "redirect:/login?error=password";
                    } else {
                        // 用户名不存在
                        return "redirect:/login?error=username";
                    }
                } catch (Exception e) {
                    // 如果查询出错，默认认为用户名不存在
                    e.printStackTrace();
                    return "redirect:/login?error=username";
                }
            }

            // 登录成功，继续处理
            session.setAttribute("loginUser", dbUser);
            String userType = dbUser.getUserType();
            String status = dbUser.getStatus();

            // 首先要确保是存在的用户，如果用户被封禁则提示无法登陆
            if ("exist".equalsIgnoreCase(status)) {
                try {
                    // session传user_id
                    int user_id = suService.findUseIdByName(user.getUserName());
                    session.setAttribute("user_id", user_id);
                } catch (Exception e) {
                    e.printStackTrace();
                    // 如果获取user_id失败，继续登录流程
                }
                
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
                    return "redirect:/login?error=invalid_role";
                }
            }
            else {
                // 账户被封禁或状态异常
                return "redirect:/login?error=account_disabled";
            }
        } catch (Exception e) {
            // 捕获所有异常，避免跳转到空白页
            e.printStackTrace();
            return "redirect:/login?error=username";
        }
    }
    
    // 显示登录页面
    @GetMapping("/login")
    public String loginPage() {
        return "home/login";
    }

    // 显示注册页面
    @GetMapping("/register")
    public String registerPage() {
        return "home/register";
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
        return "home/login-error";
    }
    
    // 处理注册请求
    @PostMapping("/register")
    public String register(User user, HttpSession session) {
        // 检查用户名是否存在
        if (userService.isUserNameExists(user.getUserName())) {
            return "redirect:/register?error=username_exists";
        }
        // 调用服务层注册用户
        User newUser = userService.register(user);
        if (newUser == null) {
            return "redirect:/register?error=register_error";
        }
        // 注册成功，重定向到注册页面显示成功消息
        return "redirect:/register?success=registered";
    }
}
