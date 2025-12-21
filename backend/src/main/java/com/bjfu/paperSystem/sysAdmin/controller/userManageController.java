package com.bjfu.paperSystem.sysAdmin.controller;

import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.sysAdmin.service.sysAdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller("sysUserManageController")
@RequestMapping("/sysadmin/userManage")
public class userManageController {
    @Autowired
    private sysAdminService sysService;

    @GetMapping
    public String userManage() {
        return "/sysadmin/userManage";
    }

    @GetMapping("createAccountPage")
    public String toCreateAccountPage() {
        return "/sysadmin/userManage/createAccountPage";
    }

    @GetMapping("deleteAccountPage")
    public String toDeleteAccountPage(Model model) {
        List<User> users = sysService.findAllExistUsers();
        users.removeIf(user -> "super_admin".equals(user.getUserType()));
        model.addAttribute("userList", users);
        return "sysadmin/userManage/deleteAccountPage";
    }

    @GetMapping("modifyAccountPage")
    public String toModifyAccountPage(Model model) {
        List<User> users = sysService.findAllUsers();
        users.removeIf(user -> "super_admin".equals(user.getUserType()));
        model.addAttribute("userList", users);
        return "sysadmin/userManage/modifyAccountPage";
    }

    // 业务逻辑函数
    @PostMapping("createAccount")
    public String createAccount(User user) {
        sysService.createAccount(user.getUserName(), user.getPassword(), user.getUserType());
        return "sysadmin/userManage/createAccountPage";
    }

    @PostMapping("deleteAccount")
    public String deleteAccount(User user) {
        sysService.deleteUser(user.getUserId());
        return "redirect:/sysadmin/userManage/deleteAccountPage";
    }

    @GetMapping("edit")
    public String toEditPage(User user, Model model) {
        User temp = sysService.findUserById(user.getUserId());
        model.addAttribute("userInfo", temp);
        return "/sysadmin/userManage/edit";
    }
    @PostMapping("modifyAccount")
    public String modifyAccount(User user, Model model) {
        sysService.modifyUser(user);
        List<User> users = sysService.findAllUsers();
        model.addAttribute("userList", users);
        return "/sysadmin/userManage/modifyAccountPage";
    }
}
