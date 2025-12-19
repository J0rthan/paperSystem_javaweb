package com.bjfu.paperSystem.superAdmin.controller;

import com.bjfu.paperSystem.Login.javabeans.User;
import com.bjfu.paperSystem.Login.service.UserService;
import com.bjfu.paperSystem.superAdmin.service.superAdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/superadmin/userManage")
public class userManageController {
    @Autowired
    private superAdminService suAdminService;

    // 返回页面函数
    @GetMapping
    public String userManage() {
        return "superadmin/userManage";
    }

    @GetMapping("createAccountPage")
    public String toCreateAccountPage() {
        return "superadmin/userManage/createAccountPage";
    }

    @GetMapping("deleteAccountPage")
    public String toDeleteAccountPage(Model model) {
        List<User> users = suAdminService.findAllExistUsers();
        model.addAttribute("userList", users);
        return "superadmin/userManage/deleteAccountPage";
    }

    @GetMapping("modifyAccountPage")
    public String toModifyAccountPage(Model model) {
        List<User> users = suAdminService.findAllUsers();
        model.addAttribute("userList", users);
        return "superadmin/userManage/modifyAccountPage";
    }

    // 业务逻辑函数
    @PostMapping("createAccount")
    public String createAccount(User user) {
       suAdminService.createAccount(user.getUserName(), user.getPassword(), user.getUserType());
       return "superadmin/userManage/createAccountPage";
    }

    @PostMapping("deleteAccount")
    public String deleteAccount(User user) {
        suAdminService.deleteUser(user.getUserId());
        return "redirect:/superadmin/userManage/deleteAccountPage";
    }

    @GetMapping("edit")
    public String toEditPage(User user, Model model) {
        User temp = suAdminService.findUserById(user.getUserId());
        model.addAttribute("userInfo", temp);
        return "/superadmin/userManage/edit";
    }
    @PostMapping("modifyAccount")
    public String modifyAccount(User user, Model model) {
        suAdminService.modifyUser(user);
        List<User> users = suAdminService.findAllUsers();
        model.addAttribute("userList", users);
        return "/superadmin/userManage/modifyAccountPage";
    }
}
