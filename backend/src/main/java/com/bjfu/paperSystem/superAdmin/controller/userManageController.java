package com.bjfu.paperSystem.superAdmin.controller;

import com.bjfu.paperSystem.Login.javabeans.User;
import com.bjfu.paperSystem.Login.service.UserService;
import com.bjfu.paperSystem.superAdmin.service.superAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String toDeleteAccountPage() {
        return "superadmin/userManage/deleteAccountPage";
    }

    @GetMapping("modifyAccountPage")
    public String toModifyAccountPage() {
        return "superadmin/userManage/modifyAccountPage";
    }

    // 业务逻辑函数
    @PostMapping("createAccount")
    public String createAccount(User user) {
       suAdminService.createAccount(user.getUserName(), user.getPassword(), user.getUserType());
       return "sysadmin";
    }
}
