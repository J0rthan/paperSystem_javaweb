package com.bjfu.paperSystem.superAdmin.controller;

import com.bjfu.paperSystem.superAdmin.service.superAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// 还未完成

@Controller
@RequestMapping("/superadmin/permissionManage")
public class permissionManageController {
    @Autowired
    private superAdminService suAdminService;

    @GetMapping
    public String permissionManage() {
        return "/superadmin/permissionManage";
    }
}
